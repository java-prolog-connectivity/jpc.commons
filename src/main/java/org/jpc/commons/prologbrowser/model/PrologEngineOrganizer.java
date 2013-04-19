package org.jpc.commons.prologbrowser.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineDriver;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.provider.PrologEngineFactoryProvider;
import org.minitoolbox.fx.FXUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages an observable list of Prolog engines according to a driver provider
 * @author sergioc
 *
 */
public class PrologEngineOrganizer implements PrologEngineFactoryInvalidatedListener {

	private static Logger logger = LoggerFactory.getLogger(PrologEngineOrganizer.class);
	
	private Map<PrologEngineFactory<PrologEngine>, ObservableList<PrologEngineModel>> driverMap;
	private PrologEngineFactoryProvider<PrologEngine> driverProvider; //knows the selected driver

	private ObservableList<PrologEngineModel> selectedDriverPrologEngines;
	private PrologEngineFactory<PrologEngine> currentDriver;
	
	ObjectProperty<MultipleSelectionModel<PrologEngineModel>> prologEngineSelectionModelProperty;
	
	public PrologEngineOrganizer(PrologEngineChoiceModel prologEngineChoiceModel, PrologDriverChoiceModel driverChoiceModel) {
		this.selectedDriverPrologEngines = prologEngineChoiceModel.getPrologEnginesList();
		prologEngineSelectionModelProperty = prologEngineChoiceModel.selectionModelProperty();
		this.driverProvider = driverChoiceModel;
		driverMap = new HashMap<>();
		for(PrologEngineDriver driver : driverChoiceModel.getDrivers()) {
			driverMap.put(driver, FXCollections.<PrologEngineModel>observableArrayList());
		}
		driverChoiceModel.addDriverSelectionObserver(this);
	}

	public Set<PrologEngineModel> getPrologEngines() {
		Set<PrologEngineModel> prologEngines = new HashSet<>();
		for(Entry<PrologEngineFactory<PrologEngine>, ObservableList<PrologEngineModel>> entry : driverMap.entrySet()) {
			prologEngines.addAll(entry.getValue());
		}
		return prologEngines;
	}
	
	public PrologEngineFactory<PrologEngine> getPrologEngineDriver() {
		return driverProvider.getPrologEngineFactory();
	}
	
	/**
	 * @param driver the driver to set
	 */
	private void setDriverSelection(PrologEngineFactory<PrologEngine> driver) {
		resetDriverSelection();
		ObservableList<PrologEngineModel> prologEngines = driverMap.get(driver);
		selectedDriverPrologEngines.setAll(prologEngines);
		driverMap.put(driver, selectedDriverPrologEngines);
		currentDriver = driver;
		
		if(!prologEngines.isEmpty()) //there are elements in the list
			prologEngineSelectionModelProperty.get().select(0);
	}
	
	private void resetDriverSelection() {
		if(currentDriver != null) {
			ObservableList<PrologEngineModel> clonedListEnginesCurrentDriver = FXCollections.<PrologEngineModel>observableArrayList(selectedDriverPrologEngines);
			driverMap.put(currentDriver, clonedListEnginesCurrentDriver);
			selectedDriverPrologEngines.clear();
			currentDriver = null;
		}
	}

	@Override
	public void onPrologEngineFactoryInvalidated() { //no driver has been selected
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				PrologEngineFactory<PrologEngine> driver = getPrologEngineDriver();
				if(driver == null) {
					resetDriverSelection();
				} else {
					setDriverSelection(driver);
				}
			}
		});
		
	}

	public void shutdownAll() {
		for(PrologEngineModel prologEngine : getPrologEngines()) {
			if(prologEngine.isCloseable()) {
				try {
					prologEngine.close();
				} catch(Exception e) {
					logger.warn("Impossible to close Prolog engine " + prologEngine.getName());
				}
			}
		}
	}

}
