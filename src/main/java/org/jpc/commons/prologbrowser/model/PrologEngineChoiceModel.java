package org.jpc.commons.prologbrowser.model;

import java.util.Collection;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import org.jpc.engine.listener.PrologEngineLifeCycleListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.provider.PrologEngineProvider;
import org.minitoolbox.CollectionsUtil;
import org.minitoolbox.fx.FXUtility;

public class PrologEngineChoiceModel implements PrologEngineLifeCycleListener, PrologEngineProvider {

	private ObservableList<PrologEngine> prologEngines;
	private ObjectProperty<MultipleSelectionModel<PrologEngine>> selectionModelProperty;

	private Collection<PrologEngineInvalidatedListener> engineSelectionObservers;
	
	public PrologEngineChoiceModel(ObjectProperty<MultipleSelectionModel<PrologEngine>> selectionModelProperty) {
		this.prologEngines = FXCollections.observableArrayList();
		this.selectionModelProperty = selectionModelProperty;
		this.engineSelectionObservers = CollectionsUtil.createWeakSet();
		//selectionModelProperty.get().setSelectionMode(SelectionMode.MULTIPLE);
		addListeners();
	}
	
	public ObjectProperty<MultipleSelectionModel<PrologEngine>> selectionModelProperty() {
		return selectionModelProperty;
	}
	
	public void addListeners() {
		selectionModelProperty.get().selectedItemProperty().addListener(new ChangeListener<PrologEngine>(){
			@Override
			public void changed(ObservableValue<? extends PrologEngine> observable,
					PrologEngine oldPrologEngine, PrologEngine newPrologEngine) {
				notifyDriverInvalidated();
			}});
	}
	
	@Override
	public PrologEngine getPrologEngine() {
		List<PrologEngine> prologEngines = getSelectedEngines();
		if(prologEngines.isEmpty())
			return null;
		else
			return prologEngines.get(0);
	}
	
	private ObservableList<PrologEngine> getSelectedEngines() {
		return selectionModelProperty.get().getSelectedItems();
	}
	
	public ObservableList<PrologEngine> getPrologEnginesList() {
		return prologEngines;
	}
	
	
	
	@Override
	public void onPrologEngineCreation(final PrologEngine prologEngine) {
		FXUtility.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				prologEngines.add(prologEngine);
			}
		});
	}

	@Override
	public void onPrologEngineShutdown(final PrologEngine prologEngine) {
		FXUtility.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				prologEngines.remove(prologEngine); //TODO select a new engine
			}
		});
	}


	public void addEngineSelectionObserver(PrologEngineInvalidatedListener observer) {
		engineSelectionObservers.add(observer);
	}

	public void removeEngineSelectionObserver(PrologEngineInvalidatedListener observer) {
		engineSelectionObservers.remove(observer);
	}

	private void notifyDriverInvalidated() {
		for(PrologEngineInvalidatedListener listener : engineSelectionObservers) {
			listener.onPrologEngineInvalidated();
		}
	}

}

