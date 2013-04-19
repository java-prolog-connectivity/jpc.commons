package org.jpc.commons.prologbrowser.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.prolog.driver.PrologEngineManager;
import org.jpc.engine.provider.PrologEngineFactoryProvider;
import org.jpc.engine.provider.PrologEngineProvider;
import org.jpc.util.naming.NamingUtil;
import org.minitoolbox.CollectionsUtil;
import org.minitoolbox.fx.FXUtil;

public class PrologEngineChoiceModel implements PrologEngineManager, PrologEngineProvider<PrologEngineModel> {

	private BooleanProperty prologEngineSelected;
	private ObservableList<PrologEngineModel> prologEngines;
	private ObjectProperty<MultipleSelectionModel<PrologEngineModel>> selectionModelProperty;

	private Collection<PrologEngineInvalidatedListener> engineSelectionListeners;
	private PrologEngineFactoryProvider<? extends PrologEngine> factoryProvider;
	private Executor executor;
	private Map<String, Integer> namesPrologEngines;
	
	public PrologEngineChoiceModel(
			ObservableList<PrologEngineModel> prologEngines,
			ObjectProperty<MultipleSelectionModel<PrologEngineModel>> selectionModelProperty, 
			PrologEngineFactoryProvider<? extends PrologEngine> factoryProvider, 
			Executor executor) {
		this.prologEngines = prologEngines;
		this.selectionModelProperty = selectionModelProperty;
		this.factoryProvider = factoryProvider;
		this.executor = executor;
		this.engineSelectionListeners = CollectionsUtil.createWeakSet();
		namesPrologEngines = new HashMap<>();
		//selectionModelProperty.get().setSelectionMode(SelectionMode.MULTIPLE);
		prologEngineSelected = new SimpleBooleanProperty();
		addListeners();
	}
	
	public BooleanProperty prologEngineSelectedProperty() {
		return prologEngineSelected;
	}
	
	public ObjectProperty<MultipleSelectionModel<PrologEngineModel>> selectionModelProperty() {
		return selectionModelProperty;
	}
	
	public void addListeners() {
		selectionModelProperty.get().selectedItemProperty().addListener(new ChangeListener<PrologEngineModel>(){
			@Override
			public void changed(ObservableValue<? extends PrologEngineModel> observable,
					PrologEngineModel oldValue, PrologEngineModel newValue) {
				if(prologEngines.isEmpty())
					prologEngineSelected.set(false);
				else
					prologEngineSelected.set(true);
				notifyPrologEngineInvalidated();
			}});
	}
	
	@Override
	public PrologEngineModel getPrologEngine() {
		List<PrologEngineModel> prologEnginesModels = getSelectedPrologEngines();
		if(prologEnginesModels.isEmpty())
			return null;
		else
			return prologEnginesModels.get(0);
	}

	
	private ObservableList<PrologEngineModel> getSelectedPrologEngines() {
		return selectionModelProperty.get().getSelectedItems();
	}
	
	public ObservableList<PrologEngineModel> getPrologEnginesList() {
		return prologEngines;
	}
	
	
	@Override
	public PrologEngineModel createPrologEngine() {
		final PrologEngineModel prologEngineModel = new PrologEngineModel(executor);
		NamingUtil.renameIfRepeated(prologEngineModel, namesPrologEngines);
		prologEngines.add(prologEngineModel);
		selectionModelProperty.get().select(prologEngineModel);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				prologEngineModel.initialize(factoryProvider.getPrologEngineFactory());
//				try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			}
			}
		});
		return prologEngineModel; //the Prolog engine returned is probably still in "busy" state
	}

	@Override
	public void shutdownPrologEngine(PrologEngine prologEngine) {
		final PrologEngineModel prologEngineModel = getPrologEngine();
		prologEngineModel.setBusy(true);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					prologEngineModel.close();
				} finally {
					FXUtil.runInFXApplicationThread(new Runnable() {
						@Override
						public void run() {
							prologEngineModel.setBusy(false);
							int currentIndex = selectionModelProperty.get().getSelectedIndex();
							prologEngines.remove(prologEngineModel); 
							if(!prologEngines.isEmpty()) {
								if(currentIndex == prologEngines.size())
									currentIndex--;
								selectionModelProperty.get().select(currentIndex);
							}
							notifyPrologEngineInvalidated();
						}
					});
				}
			}
		});
	}

	public void addEngineSelectionListener(PrologEngineInvalidatedListener listener) {
		engineSelectionListeners.add(listener);
	}

	public void removeEngineSelectionListener(PrologEngineInvalidatedListener listener) {
		engineSelectionListeners.remove(listener);
	}

	private void notifyPrologEngineInvalidated() {
		for(PrologEngineInvalidatedListener listener : engineSelectionListeners) {
			listener.onPrologEngineInvalidated();
		}
	}

	@Override
	public boolean isEnabled() {
		PrologEngineFactory<? extends PrologEngine> factory = factoryProvider.getPrologEngineFactory();
		if(factory != null && factory.isEnabled())
			return true;
		else
			return false;
	}



}

