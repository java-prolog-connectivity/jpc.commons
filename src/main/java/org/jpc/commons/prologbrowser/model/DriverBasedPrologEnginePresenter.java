package org.jpc.commons.prologbrowser.model;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineDriver;
import org.jpc.engine.provider.PrologEngineFactoryProvider;

/**
 * Manages an observable list of Prolog engines according to a driver provider
 * @author sergioc
 *
 */
public class DriverBasedPrologEnginePresenter implements PrologEngineFactorySelectionListener {

	private Map<PrologEngineDriver, ObservableList<PrologEngine>> driverMap;
	private PrologEngineFactoryProvider<PrologEngineDriver> driverProvider; //knows the selected driver

	private ObservableList<PrologEngine> selectedDriverPrologEngines;
	private PrologEngineDriver currentDriver;
	
	
	public DriverBasedPrologEnginePresenter(ObservableList<PrologEngine> selectedDriverPrologEngines,
			Iterable<PrologEngineDriver> drivers, 
			PrologEngineFactoryProvider<PrologEngineDriver> driverProvider
			) {
		this.selectedDriverPrologEngines = selectedDriverPrologEngines;
		this.driverProvider = driverProvider;
		selectedDriverPrologEngines = FXCollections.<PrologEngine>observableArrayList();
		driverMap = new HashMap<>();
		for(PrologEngineDriver driver : drivers) {
			driverMap.put(driver, FXCollections.<PrologEngine>observableArrayList());
		}
	}

	public PrologEngineDriver getPrologEngineDriver() {
		return driverProvider.getPrologEngineFactory();
	}
	
	/**
	 * @param driver the driver to set
	 */
	private void setDriverSelection(PrologEngineDriver driver) {
		resetDriverSelection();
		ObservableList<PrologEngine> prologEngines = driverMap.get(driver);
		selectedDriverPrologEngines.setAll(prologEngines);
		driverMap.put(driver, selectedDriverPrologEngines);
		currentDriver = driver;
	}
	
	private void resetDriverSelection() {
		if(currentDriver != null) {
			ObservableList<PrologEngine> clonedListEnginesCurrentDriver = FXCollections.<PrologEngine>observableArrayList(selectedDriverPrologEngines);
			driverMap.put(currentDriver, clonedListEnginesCurrentDriver);
			selectedDriverPrologEngines.clear();
			currentDriver = null;
		}
	}

	@Override
	public void onPrologEngineFactorySelected() { // a driver has been selected
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setDriverSelection(getPrologEngineDriver());
			}
		});
	}

	@Override
	public void onPrologEngineFactoryUnselected() { //no driver has been selected
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				resetDriverSelection();
			}
		});
		
	}
	
	@Override
	public void onSelectedPrologEngineFactoryDisabled() {
	}

}
