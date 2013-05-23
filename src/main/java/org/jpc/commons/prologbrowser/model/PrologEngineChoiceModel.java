package org.jpc.commons.prologbrowser.model;

import java.util.Collection;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import org.jpc.engine.provider.PrologEngineProvider;
import org.minitoolbox.CollectionsUtil;

public class PrologEngineChoiceModel implements PrologEngineProvider<PrologEngineModel> {

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
					PrologEngineModel oldPrologEngineModel, PrologEngineModel newPrologEngineModel) { 
				if(newPrologEngineModel != null) {
					selectedEngineAvailable.bind(newPrologEngineModel.readyProperty());
					selectedEngineCloseable.bind(newPrologEngineModel.closeableProperty());
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
		return selectionModelProperty.get().getSelectedItem();
		/*
		List<PrologEngineModel> prologEnginesModels = getSelectedPrologEngines(); //this method is buggy (a JavaFX problem) and should not be used
		if(prologEnginesModels.isEmpty())
			return null;
		else
			return prologEnginesModels.get(0);
		*/
	}
	
//	private ObservableList<PrologEngineModel> getSelectedPrologEngines() {
//		//Warning: the method getSelectedItems() seems to be buggy in JavaFX. 
//		//If it is called in a change listener triggered because its only selection has been deleted, getSelectedItems will still return a collection with the item that has been deleted,
//		//instead of returning an empty collection as it should.
//		return selectionModelProperty.get().getSelectedItems(); 
//	}
	
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

