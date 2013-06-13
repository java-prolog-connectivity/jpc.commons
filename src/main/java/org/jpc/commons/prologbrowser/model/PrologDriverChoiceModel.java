package org.jpc.commons.prologbrowser.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import org.jpc.engine.listener.DriverStateListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineDriver;
import org.jpc.engine.provider.PrologEngineFactoryProvider;
import org.jpc.util.DriverUtil;
import org.jpc.util.DriverUtil.PrologEngineDriverComparator;
import org.jpc.util.DriverUtil.PrologEngineTypeComparator;
import org.jpc.util.naming.NamingUtil;
import org.minitoolbox.CollectionsUtil;
import org.minitoolbox.fx.FXUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * 
 * @author sergioc
 *
 */
public class PrologDriverChoiceModel implements PrologEngineFactoryProvider<PrologEngine>, DriverStateListener {

	private static Logger logger = LoggerFactory.getLogger(PrologDriverChoiceModel.class);
	
	private Map<String, Integer> nameOccurrences;
	
	private BooleanProperty selectedDriver;
	private BooleanProperty selectedDriverEnabled;
	
	private ObjectProperty<MultipleSelectionModel<String>> engineTypesSelectionModelProperty;
	private ObjectProperty<MultipleSelectionModel<PrologDriverModel>> filteredDriversSelectionModelProperty;

	private Collection<PrologEngineFactoryInvalidatedListener> driverSelectionObservers; //a list of observers to be notified in the event of a new driver selected
	
	private ObservableList<String> engineTypes; //the list of Prolog engine names
	private ObservableList<PrologDriverModel> filteredDrivers; //a list of filtered drivers (according to a selected Prolog engine)
	
	private List<PrologDriverModel> allDrivers; //an (alphabetically) ordered list of all available Prolog drivers
	//private Multimap<String,PrologDriverModel> groupedDrivers; //a multimap mapping engine names to drivers
	private Map<String,Multimap<String, PrologDriverModel>> groupedDrivers; //a multimap mapping engine names to drivers

	
	private List<PrologDriverModel> asDriverModels(Iterable<PrologEngineDriver> drivers) {
		List<PrologDriverModel> driverModels = new ArrayList<>();
		for(PrologEngineDriver driver : drivers) {
			PrologDriverModel driverModel = new PrologDriverModel(driver);
			driverModel.setName(driver.getLibraryName());
			driverModels.add(driverModel);
		}
		return driverModels;
	}
	
	
	public PrologDriverChoiceModel(Collection<PrologDriverModel> allDrivers, 
			ObservableList<String> engineTypes,
			ObjectProperty<MultipleSelectionModel<String>> engineTypesSelectionModelProperty,
			ObservableList<PrologDriverModel> filteredDrivers,
			ObjectProperty<MultipleSelectionModel<PrologDriverModel>> filteredDriversSelectionModelProperty) {
		
		this.engineTypes = engineTypes;
		this.engineTypesSelectionModelProperty = engineTypesSelectionModelProperty;
		this.filteredDrivers = filteredDrivers;
		this.filteredDriversSelectionModelProperty = filteredDriversSelectionModelProperty;
		this.driverSelectionObservers = CollectionsUtil.createWeakSet();
		nameOccurrences = new HashMap<>();
		
		if(allDrivers == null || allDrivers.isEmpty()) {
			Set<PrologEngineDriver> allDriversClassPath = DriverUtil.findDrivers();
			allDrivers = asDriverModels(allDriversClassPath);
		}
		
		this.allDrivers = PrologDriverModel.order(allDrivers);
		NamingUtil.renameRepeatedNames(allDrivers, nameOccurrences);
		DriverUtil.registerListener(this, this.allDrivers); //so instances will be notified if a driver is not available anymore
		addSelectionListeners();
		//groupedDrivers = buildDriversMap();
		groupedDrivers = DriverUtil.groupByPrologEngineName(this.allDrivers);
		
		engineTypes.addAll(new TreeSet<>(groupedDrivers.keySet())); //TreeSet is an ordered set (by default uses alphabetical order)
		selectedDriver = new SimpleBooleanProperty(false);
		selectedDriverEnabled = new SimpleBooleanProperty(false);
	}
	

	private static PrologDriverModel findDriverByName(Iterable<PrologDriverModel> drivers, String name) {
		for(PrologDriverModel driver : drivers) {
			if(driver.getName().equals(name))
				return driver;
		}
		return null;
	}
	
	public boolean addDriver(PrologDriverModel driver) {
		Multimap<String, PrologDriverModel> libraryNamesToDriverMultimap = groupedDrivers.get(driver.getEngineDescription().getName());
		if(libraryNamesToDriverMultimap == null) {
			libraryNamesToDriverMultimap = TreeMultimap.create(new PrologEngineTypeComparator(), new PrologEngineDriverComparator());
			groupedDrivers.put(driver.getEngineDescription().getName(), libraryNamesToDriverMultimap);
			//libraryNameToDriversMultimap.put(driver.getLibraryName(), driver);
			//libraryNamesToDriverMultimaps.add(libraryNameToDriversMultimap);
			engineTypes.add(driver.getEngineDescription().getName());
			Collections.sort(engineTypes, new PrologEngineTypeComparator());
		}
		Collection<PrologDriverModel> drivers = libraryNamesToDriverMultimap.get(driver.getLibraryName());
		if(findDriverByName(drivers, driver.getName()) != null) {
			logger.warn("A driver with name " + driver.getName() + " for the " + driver.getEngineDescription().getName() + " Prolog engine already exists.");
			return false;
		} else { //this ugly code needs to be revisited
			drivers.add(driver);
			driver.addStateListener(this);
			String currentPrologEngineType = getSelectedPrologEngine();
			if(currentPrologEngineType != null && currentPrologEngineType.equals(driver.getEngineDescription().getName()))
				filteredDrivers.add(driver);
			return true;
		}
	}
	
	public String getSelectedPrologEngine() {
		return getEngineTypesSelectionModel().getSelectedItem();
	}
	
	public BooleanProperty selectedDriverProperty() {
		return selectedDriver;
	}
	
	public BooleanProperty selectedDriverEnabledProperty() {
		return selectedDriverEnabled;
	}
	
	public ObservableList<String> getEngineTypes() {
		return engineTypes;
	}
	
	public ObservableList<PrologDriverModel> getFilteredDrivers() {
		return filteredDrivers;
	}
	
	private void addSelectionListeners() {
		getEngineTypesSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldPrologEngineType, String newPrologEngineType) {
				if(!newPrologEngineType.equals(oldPrologEngineType)) {
					selectPrologEngine(newPrologEngineType);
				}
			}
		});
		
		getFilteredDriversSelectionModel().selectedItemProperty().addListener(new ChangeListener<PrologDriverModel>() {
			@Override
			public void changed(ObservableValue<? extends PrologDriverModel> observable, PrologDriverModel oldDriver, PrologDriverModel newDriver) {
				refreshSelectedDriverProperties();
				notifyDriverInvalidated();
//				if(newDriver == null || !newDriver.equals(oldDriver)) {
//					notifyDriverInvalidated();
//				}
			}
		});
	}
	
	private void refreshSelectedDriverProperties() {
		PrologDriverModel driver = getPrologEngineFactory();
		if(driver != null) {
			selectedDriver.set(true);
			if(driver.isDisabled())
				selectedDriverEnabled.set(false);
			else
				selectedDriverEnabled.set(true);
		} else {
			selectedDriver.set(false);
			selectedDriverEnabled.set(false);
		}
	}
	
	public List<PrologDriverModel> getFlattenCollectionDrivers(String engineName) {
		List<PrologDriverModel> drivers = new ArrayList<>();
		Multimap<String, PrologDriverModel> driversMultimap = groupedDrivers.get(engineName);
		for(PrologDriverModel driver : driversMultimap.values()) {
			drivers.add(driver);
		}
		DriverUtil.orderByLibraryName(drivers);
		return drivers;
	}

	public List<PrologDriverModel> getAllDrivers() {
		return allDrivers;
	}

//	public List<Multimap<String, PrologDriverModel>> getDrivers(String engineName) {
//		return new ArrayList<Multimap<String, PrologDriverModel>>(groupedDrivers.get(engineName));
//	}
	
	public void addDriverSelectionObserver(PrologEngineFactoryInvalidatedListener observer) {
		driverSelectionObservers.add(observer);
	}
	
	public void removeDriverSelectionObserver(PrologEngineFactoryInvalidatedListener observer) {
		driverSelectionObservers.remove(observer);
	}

	private void notifyDriverInvalidated() {
		for(PrologEngineFactoryInvalidatedListener listener : driverSelectionObservers) {
			listener.onPrologEngineFactoryInvalidated();
		}
	}

	@Override
	public PrologDriverModel getPrologEngineFactory() {
		return getFilteredDriversSelectionModel().getSelectedItem();
	}

	public void selectFirst() {
		if(!engineTypes.isEmpty())
			getEngineTypesSelectionModel().select(engineTypes.get(0));
	}

	public void selectPrologEngine(String engineName) {
		List<PrologDriverModel> drivers = getFlattenCollectionDrivers(engineName);
		filteredDrivers.setAll(drivers);
		if(!drivers.isEmpty()) {
			PrologDriverModel firstDriver = drivers.get(0);
			getFilteredDriversSelectionModel().select(firstDriver); //this should trigger the registered change listeners
		}	
	}
	
	private MultipleSelectionModel<String> getEngineTypesSelectionModel() {
		return engineTypesSelectionModelProperty.get();
	}
	
	private MultipleSelectionModel<PrologDriverModel> getFilteredDriversSelectionModel() {
		return filteredDriversSelectionModelProperty.get();
	}
	
	@Override
	public void onDriverDisabled() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				refreshSelectedDriverProperties();
//				PrologDriverModel selectedDriver = getPrologEngineFactory();
//				if(selectedDriver != null) {
//					if(selectedDriver.isDisabled()) {
//						notifyDriverInvalidated();
//					}
//				}
			}
		});
	}

}
