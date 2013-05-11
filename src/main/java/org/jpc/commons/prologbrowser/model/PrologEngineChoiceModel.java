package org.jpc.commons.prologbrowser.model;

import java.util.Collection;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import org.jpc.engine.provider.PrologEngineProvider;
import org.minitoolbox.CollectionsUtil;

public class PrologEngineChoiceModel implements PrologEngineProvider {

	private BooleanProperty selectedEngineAvailable; //a property indicating if an engine is currently selected and it is available (not starting or shutting down)
	private BooleanProperty selectedEngineCloseable; //a property indicating if the current selected engine can be stopped
	private ObservableList<PrologEngineModel> availablePrologEngines;
	private ObjectProperty<MultipleSelectionModel<PrologEngineModel>> selectionModelProperty;

	private Collection<PrologEngineInvalidatedListener> engineSelectionListeners;

	public PrologEngineChoiceModel(ObservableList<PrologEngineModel> availablePrologEngines, ObjectProperty<MultipleSelectionModel<PrologEngineModel>> selectionModelProperty) {
		this.availablePrologEngines = availablePrologEngines;
		this.selectionModelProperty = selectionModelProperty;
		this.engineSelectionListeners = CollectionsUtil.createWeakSet();
		//selectionModelProperty.get().setSelectionMode(SelectionMode.MULTIPLE);
		selectedEngineAvailable = new SimpleBooleanProperty(false);
		selectedEngineCloseable = new SimpleBooleanProperty(false);
		addListeners();
	}
	
	public BooleanProperty selectedEngineAvailableProperty() {
		return selectedEngineAvailable;
	}
	
	public BooleanProperty selectedEngineCloseableProperty() {
		return selectedEngineCloseable;
	}
	
	public ObjectProperty<MultipleSelectionModel<PrologEngineModel>> selectionModelProperty() {
		return selectionModelProperty;
	}
	
	private void addListeners() {
		selectionModelProperty.get().selectedItemProperty().addListener(new ChangeListener<PrologEngineModel>(){
			@Override
			public void changed(ObservableValue<? extends PrologEngineModel> observable,
					PrologEngineModel oldValue, PrologEngineModel newValue) {
				PrologEngineModel prologEngineModel = getPrologEngine();
				if(prologEngineModel != null) {
					selectedEngineAvailable.bind(prologEngineModel.availableProperty());
					selectedEngineCloseable.bind(prologEngineModel.closeableProperty());
				} else {
					selectedEngineAvailable.unbind();
					selectedEngineAvailable.set(false);
					selectedEngineCloseable.unbind();
					selectedEngineCloseable.set(false);
				}
				notifyPrologEngineInvalidated();
			}});
	}
	
	@Override
	public PrologEngineModel getPrologEngine() {
		PrologEngineModel prologEngineModel = getFirstSelectedPrologEngine();
//		if(prologEngineModel == null || !prologEngineModel.isAvailable())
//			return null;
//		else
			return prologEngineModel;
	}

	
	private PrologEngineModel getFirstSelectedPrologEngine() {
		List<PrologEngineModel> prologEnginesModels = getSelectedPrologEngines();
		if(prologEnginesModels.isEmpty())
			return null;
		else
			return prologEnginesModels.get(0);
	}
	
	public ObservableList<PrologEngineModel> getSelectedPrologEngines() {
		return selectionModelProperty.get().getSelectedItems();
	}
	
	public ObservableList<PrologEngineModel> getAvailablePrologEngines() {
		return availablePrologEngines;
	}
	

	public void addEngineSelectionListener(PrologEngineInvalidatedListener listener) {
		engineSelectionListeners.add(listener);
	}

	public void removeEngineSelectionListener(PrologEngineInvalidatedListener listener) {
		engineSelectionListeners.remove(listener);
	}

	void notifyPrologEngineInvalidated() {
		for(PrologEngineInvalidatedListener listener : engineSelectionListeners) {
			listener.onPrologEngineInvalidated();
		}
	}

}

