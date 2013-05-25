package org.jpc.commons.prologbrowser.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executor;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import org.jpc.engine.listener.PrologEngineLifeCycleListener;
import org.jpc.engine.profile.PrologEngineProfileFactory;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.prolog.driver.PrologEngineManager;
import org.jpc.util.naming.NamingUtil;
import org.minitoolbox.fx.FXUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages an observable list of Prolog engines according to a driver provider
 * @author sergioc
 *
 */
public class PrologEngineOrganizer implements PrologEngineManager<PrologEngine>, PrologEngineFactoryInvalidatedListener, PrologEngineLifeCycleListener {

	private static Logger logger = LoggerFactory.getLogger(PrologEngineOrganizer.class);
	
	private Map<PrologEngineFactory, ObservableList<PrologEngineModel>> driverMap; //a map from drivers to active Prolog engines
	private PrologDriverChoiceModel driverProvider; //knows the selected driver
	private PrologEngineFactory currentDriver; //the current selected driver
	private ObservableList<PrologEngineModel> currentDriverPrologEngines; //the available Prolog engines for the selected driver
	private PrologEngineChoiceModel prologEngineChoiceModel; //knows the selected Prolog engine
	
	
	private ObjectProperty<MultipleSelectionModel<PrologEngineModel>> prologEngineSelectionModelProperty;
	private PrologEngineProfileFactory profileFactory;
	private Map<String, Integer> namesPrologEngines;
	private Executor executor;
	
	public PrologEngineOrganizer(PrologDriverChoiceModel driverChoiceModel, PrologEngineChoiceModel prologEngineChoiceModel, PrologEngineProfileFactory profileFactory, Executor executor) {
		this.prologEngineChoiceModel = prologEngineChoiceModel;
		this.currentDriverPrologEngines = prologEngineChoiceModel.getAvailablePrologEngines();
		this.prologEngineSelectionModelProperty = prologEngineChoiceModel.selectionModelProperty();
		this.driverProvider = driverChoiceModel;
		this.profileFactory = profileFactory;
		this.executor = executor;
		driverMap = new HashMap<>();
		namesPrologEngines = new HashMap<>();
//		for(PrologDriverModel driver : driverChoiceModel.getAllDrivers()) {
//			driverMap.put(driver, FXCollections.<PrologEngineModel>observableArrayList());
//		}
		driverChoiceModel.addDriverSelectionObserver(this);
	}
	
	public Set<PrologEngineModel> getPrologEngines() {
		Set<PrologEngineModel> prologEngines = new HashSet<>();
		for(Entry<PrologEngineFactory, ObservableList<PrologEngineModel>> entry : driverMap.entrySet()) {
			prologEngines.addAll(entry.getValue());
		}
		return prologEngines;
	}
	
	public PrologDriverModel getPrologEngineDriver() {
		return driverProvider.getPrologEngineFactory();
	}
	
	/**
	 * @param driver the driver to set
	 */
	private void setDriverSelection(PrologEngineFactory driver) {
		resetDriverSelection();
		ObservableList<PrologEngineModel> prologEngines = driverMap.get(driver);
		if(prologEngines == null) {
			prologEngines = FXCollections.<PrologEngineModel>observableArrayList();
			driverMap.put(driver, prologEngines);
		}
			
		currentDriverPrologEngines.setAll(prologEngines);
		driverMap.put(driver, currentDriverPrologEngines);
		currentDriver = driver;
		
		if(!prologEngines.isEmpty()) //there are elements in the list
			prologEngineSelectionModelProperty.get().select(0);
	}
	
	private void resetDriverSelection() {
		if(currentDriver != null) {
			ObservableList<PrologEngineModel> clonedListEnginesCurrentDriver = FXCollections.<PrologEngineModel>observableArrayList(currentDriverPrologEngines);
			driverMap.put(currentDriver, clonedListEnginesCurrentDriver);
			currentDriverPrologEngines.clear();
			currentDriver = null;
		}
	}

	@Override
	public void onPrologEngineFactoryInvalidated() { //no driver has been selected
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				PrologEngineFactory driver = getPrologEngineDriver();
				if(driver == null) {
					resetDriverSelection();
				} else {
					setDriverSelection(driver);
				}
			}
		});
	}

	public boolean queriesInProgress() {
		for(PrologEngineModel prologEngineModel : getPrologEngines()) {
			if(prologEngineModel.isQueryInProgress())
				return true;
		}
		return false;
	}
	
	public boolean nonAbortableQueriesInProgress() {
		for(PrologEngineModel prologEngineModel : getPrologEngines()) {
			if(prologEngineModel.isNonAbortableQueryInProgress())
				return true;
		}
		return false;
	}
	
	public boolean stopAllQueries() {
		boolean success = true; 
		for(PrologEngineModel prologEngineModel : getPrologEngines()) {
			if(!prologEngineModel.stopQueries())
				success = false;
		}
		return success;
	}
	
	public void shutdownAll() {
		//stopAllQueries();
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
	
	/**
	 * 
	 * @param prologEngineModel
	 * @return the list of Prolog engines (associated to a particular driver) where a given engine is located
	 */
	private ObservableList<PrologEngineModel> findPrologEngineList(PrologEngineModel prologEngineModel) {
		for(ObservableList<PrologEngineModel> list : driverMap.values()) {
			if(list.contains(prologEngineModel))
				return list;
		}
		return null;
	}
	
	@Override
	public PrologEngineModel createPrologEngine() {
		PrologDriverModel driver = getPrologEngineDriver();
		PrologEngineFactory prologEngineFactory = profileFactory == null?driver : profileFactory.createPrologEngineProfile(driver);
		PrologEngineModel prologEngineModel = new PrologEngineModel(executor);
		prologEngineModel.setName(driver.getShortDescription());
		NamingUtil.renameIfRepeated(prologEngineModel, namesPrologEngines);
		prologEngineModel.addEngineLifeCycleListener(this);
		currentDriverPrologEngines.add(prologEngineModel);
		prologEngineSelectionModelProperty.get().select(prologEngineModel);
		prologEngineModel.initialize(prologEngineFactory);
		return prologEngineModel;
	}

	@Override
	public void shutdownPrologEngine(PrologEngine prologEngine) {
		PrologEngineModel prologEngineModel = prologEngineChoiceModel.getPrologEngine();
		prologEngineModel.close();
	}

	@Override
	public boolean isDisabled() {
		PrologEngineFactory factory = getPrologEngineDriver();
		if(factory == null || factory.isDisabled())
			return true;
		else
			return false;
	}

	@Override
	public void onPrologEngineCreation(PrologEngine prologEngine) {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				prologEngineChoiceModel.notifyPrologEngineInvalidated();
			}
		});
	}

	@Override
	public void onPrologEngineShutdown(final PrologEngine prologEngine) {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				PrologEngineModel prologEngineModel = (PrologEngineModel) prologEngine;
				ObservableList<PrologEngineModel> containerList = findPrologEngineList(prologEngineModel);
				int currentIndex = prologEngineSelectionModelProperty.get().getSelectedIndex();
				boolean isActiveList = containerList.equals(currentDriverPrologEngines);
				if(isActiveList) {
					//if this line is not present, for some reason a change selection event is not going to be fired when deleting the LAST prolog engine in the list
					prologEngineSelectionModelProperty.get().clearSelection(); 
				}
				containerList.remove(prologEngineModel); 
				if(isActiveList) {
					if(!currentDriverPrologEngines.isEmpty()) {
						if(currentIndex == currentDriverPrologEngines.size()) {
							currentIndex--;
						}
						prologEngineSelectionModelProperty.get().select(currentIndex);
					}
				}
			}
		});
	}

}
