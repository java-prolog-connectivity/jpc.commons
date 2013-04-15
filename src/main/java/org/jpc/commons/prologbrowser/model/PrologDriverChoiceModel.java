package org.jpc.commons.prologbrowser.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import org.jpc.engine.listener.DriverStateListener;
import org.jpc.engine.prolog.driver.PrologEngineDriver;
import org.jpc.engine.provider.PrologEngineFactoryProvider;
import org.jpc.util.DriverUtil;
import org.jpc.util.naming.NamingUtil;
import org.minitoolbox.CollectionsUtil;
import org.minitoolbox.fx.FXUtility;

import com.google.common.collect.Multimap;

/**
 * 
 * @author sergioc
 *
 */
public class PrologDriverChoiceModel implements PrologEngineFactoryProvider<PrologEngineDriver>, DriverStateListener {

	private ObjectProperty<MultipleSelectionModel<String>> prologEngineTypesSelectionModelProperty;
	private ObjectProperty<MultipleSelectionModel<PrologEngineDriver>> prologEngineDriversSelectionModelProperty;

	
	private Collection<PrologEngineFactoryInvalidatedListener> driverSelectionObservers;
	
	private ObservableList<String> enginesNames; //the list of Prolog engine names
	private ObservableList<PrologEngineDriver> filteredDrivers; //a list of filtered drivers (according to a selected Prolog engine)
	
	private List<PrologEngineDriver> drivers; //an (alphabetically) ordered list of all available Prolog drivers
	private Multimap<String,Multimap<String, PrologEngineDriver>> groupedDrivers; //a multimap mapping engine names to drivers

	
	public static void setDefaultNames(Iterable<PrologEngineDriver> drivers) {
		for(PrologEngineDriver driver : drivers) {
			driver.setName(driver.getLibraryName());
		}
		NamingUtil.renameRepeatedNames(drivers);
	}
	
	public PrologDriverChoiceModel(Iterable<PrologEngineDriver> drivers, 
			ObjectProperty<MultipleSelectionModel<String>> prologEngineTypesSelectionModelProperty, 
			ObjectProperty<MultipleSelectionModel<PrologEngineDriver>> prologEngineDriversSelectionModelProperty) {
		
		this.prologEngineTypesSelectionModelProperty = prologEngineTypesSelectionModelProperty;
		this.prologEngineDriversSelectionModelProperty = prologEngineDriversSelectionModelProperty;
		this.driverSelectionObservers = CollectionsUtil.createWeakSet();
		if(drivers == null || !drivers.iterator().hasNext()) {
			drivers = DriverUtil.findConfigurations();
			setDefaultNames(drivers);
		}
		this.drivers = DriverUtil.order(drivers);
		//this.drivers = drivers;
		
		DriverUtil.registerListener(this, drivers); //so instances will be notified if a driver is not available anymore
		addSelectionListeners();
		groupedDrivers = DriverUtil.groupByPrologEngineName(drivers);
		enginesNames = FXCollections.observableArrayList(new TreeSet<>(groupedDrivers.keySet())); //TreeSet is an ordered set (by default uses alphabetical order)
		filteredDrivers = FXCollections.observableArrayList();
	}
	
	public ObservableList<String> getEnginesNames() {
		return enginesNames;
	}
	
	public ObservableList<PrologEngineDriver> getFilteredDrivers() {
		return filteredDrivers;
	}
	
	private void addSelectionListeners() {
		getPrologEngineTypesSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldPrologEngineType, String newPrologEngineType) {
				if(!newPrologEngineType.equals(oldPrologEngineType)) {
					selectPrologEngine(newPrologEngineType);
				}
			}
		});
		
		getPrologEngineDriversSelectionModel().selectedItemProperty().addListener(new ChangeListener<PrologEngineDriver>() {
			@Override
			public void changed(ObservableValue<? extends PrologEngineDriver> observable, PrologEngineDriver oldDriver, PrologEngineDriver newDriver) {
				if(newDriver == null) {
					notifyDriverInvalidated();
				} else if(!newDriver.equals(oldDriver)) {
					if(newDriver.isEnabled()) {
					} else {
					}
					notifyDriverInvalidated();
				}
			}
		});
	}
	
	public List<PrologEngineDriver> getFlattenCollectionDrivers(String engineName) {
		List<PrologEngineDriver> drivers = new ArrayList<>();
		Collection<Multimap<String, PrologEngineDriver>> driversMap = groupedDrivers.get(engineName);
		for(Multimap<String, PrologEngineDriver> driverMap : driversMap) {
			for(PrologEngineDriver driver : driverMap.values()) {
				drivers.add(driver);
			}
		}
		return drivers;
	}

	public List<PrologEngineDriver> getDrivers() {
		return drivers;
	}

	public List<Multimap<String, PrologEngineDriver>> getDrivers(String engineName) {
		return new ArrayList<Multimap<String, PrologEngineDriver>>(groupedDrivers.get(engineName));
	}
	
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
	public PrologEngineDriver getPrologEngineFactory() {
		return getPrologEngineDriversSelectionModel().getSelectedItem();
	}

	public void selectFirst() {
		if(!enginesNames.isEmpty())
			getPrologEngineTypesSelectionModel().select(enginesNames.get(0));
	}

	public void selectPrologEngine(String engineName) {
		List<PrologEngineDriver> drivers = getFlattenCollectionDrivers(engineName);
		filteredDrivers.setAll(drivers);
		if(!drivers.isEmpty()) {
			PrologEngineDriver firstDriver = drivers.get(0);
			getPrologEngineDriversSelectionModel().select(firstDriver); //this should trigger the registered change listeners
		}	
	}
	
	private MultipleSelectionModel<String> getPrologEngineTypesSelectionModel() {
		return prologEngineTypesSelectionModelProperty.get();
	}
	
	private MultipleSelectionModel<PrologEngineDriver> getPrologEngineDriversSelectionModel() {
		return prologEngineDriversSelectionModelProperty.get();
	}
	
	@Override
	public void onDriverDisabled() {
		FXUtility.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				PrologEngineDriver selectedDriver = getPrologEngineFactory();
				if(selectedDriver != null) {
					if(!selectedDriver.isEnabled()) {
						notifyDriverInvalidated();
					}
				}
			}
		});
	}

}
