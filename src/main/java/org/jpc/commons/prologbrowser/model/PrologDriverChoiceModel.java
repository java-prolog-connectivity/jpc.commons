package org.jpc.commons.prologbrowser.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import org.jpc.engine.listener.DriverStateListener;
import org.jpc.engine.prolog.driver.PrologEngineDriver;
import org.jpc.engine.provider.PrologEngineFactoryProvider;
import org.jpc.util.DriverUtil;
import org.jpc.util.naming.NamingUtil;
import org.minitoolbox.CollectionsUtil;
import org.minitoolbox.fx.FXUtil;

import com.google.common.collect.Multimap;

/**
 * 
 * @author sergioc
 *
 */
public class PrologDriverChoiceModel implements PrologEngineFactoryProvider, DriverStateListener {

	private ObjectProperty<MultipleSelectionModel<String>> engineTypesSelectionModelProperty;
	private ObjectProperty<MultipleSelectionModel<PrologEngineDriver>> filteredDriversSelectionModelProperty;

	private Collection<PrologEngineFactoryInvalidatedListener> driverSelectionObservers; //a list of observers to be notified in the event of a new driver selected
	
	private ObservableList<String> engineTypes; //the list of Prolog engine names
	private ObservableList<PrologEngineDriver> filteredDrivers; //a list of filtered drivers (according to a selected Prolog engine)
	
	private List<PrologEngineDriver> allDrivers; //an (alphabetically) ordered list of all available Prolog drivers
	private Multimap<String,Multimap<String, PrologEngineDriver>> groupedDrivers; //a multimap mapping engine names to drivers

	
	public static void setDefaultNames(Iterable<PrologEngineDriver> drivers) {
		for(PrologEngineDriver driver : drivers) {
			driver.setName(driver.getLibraryName());
		}
		NamingUtil.renameRepeatedNames(drivers);
	}
	
	
	public PrologDriverChoiceModel(Iterable<PrologEngineDriver> allDrivers, 
			ObservableList<String> engineTypes,
			ObjectProperty<MultipleSelectionModel<String>> engineTypesSelectionModelProperty,
			ObservableList<PrologEngineDriver> filteredDrivers,
			ObjectProperty<MultipleSelectionModel<PrologEngineDriver>> filteredDriversSelectionModelProperty) {
		
		this.engineTypes = engineTypes;
		this.engineTypesSelectionModelProperty = engineTypesSelectionModelProperty;
		this.filteredDrivers = filteredDrivers;
		this.filteredDriversSelectionModelProperty = filteredDriversSelectionModelProperty;
		this.driverSelectionObservers = CollectionsUtil.createWeakSet();
		
		if(allDrivers == null || !allDrivers.iterator().hasNext()) {
			allDrivers = DriverUtil.findConfigurations();
			setDefaultNames(allDrivers);
		}
		this.allDrivers = DriverUtil.order(allDrivers);

		DriverUtil.registerListener(this, allDrivers); //so instances will be notified if a driver is not available anymore
		addSelectionListeners();
		groupedDrivers = DriverUtil.groupByPrologEngineName(allDrivers);
		engineTypes.addAll(new TreeSet<>(groupedDrivers.keySet())); //TreeSet is an ordered set (by default uses alphabetical order)
	}
	
	public ObservableList<String> getEngineTypes() {
		return engineTypes;
	}
	
	public ObservableList<PrologEngineDriver> getFilteredDrivers() {
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
		
		getFilteredDriversSelectionModel().selectedItemProperty().addListener(new ChangeListener<PrologEngineDriver>() {
			@Override
			public void changed(ObservableValue<? extends PrologEngineDriver> observable, PrologEngineDriver oldDriver, PrologEngineDriver newDriver) {
				if(newDriver == null || !newDriver.equals(oldDriver)) {
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
		DriverUtil.orderByLibraryName(drivers);
		return drivers;
	}

	public List<PrologEngineDriver> getAllDrivers() {
		return allDrivers;
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
		return getFilteredDriversSelectionModel().getSelectedItem();
	}

	public void selectFirst() {
		if(!engineTypes.isEmpty())
			getEngineTypesSelectionModel().select(engineTypes.get(0));
	}

	public void selectPrologEngine(String engineName) {
		List<PrologEngineDriver> drivers = getFlattenCollectionDrivers(engineName);
		filteredDrivers.setAll(drivers);
		if(!drivers.isEmpty()) {
			PrologEngineDriver firstDriver = drivers.get(0);
			getFilteredDriversSelectionModel().select(firstDriver); //this should trigger the registered change listeners
		}	
	}
	
	private MultipleSelectionModel<String> getEngineTypesSelectionModel() {
		return engineTypesSelectionModelProperty.get();
	}
	
	private MultipleSelectionModel<PrologEngineDriver> getFilteredDriversSelectionModel() {
		return filteredDriversSelectionModelProperty.get();
	}
	
	@Override
	public void onDriverDisabled() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				PrologEngineDriver selectedDriver = getPrologEngineFactory();
				if(selectedDriver != null) {
					if(selectedDriver.isDisabled()) {
						notifyDriverInvalidated();
					}
				}
			}
		});
	}

}
