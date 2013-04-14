package org.jpc.commons.prologbrowser.model;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import org.jpc.engine.listener.PrologEngineLifeCycleListener;
import org.jpc.engine.prolog.PrologEngine;

public class PrologEngineChoiceModel implements PrologEngineLifeCycleListener {

	private ObservableList<PrologEngine> prologEngines;
	ObjectProperty<MultipleSelectionModel<PrologEngine>> selectionModelProperty;
	
	
	public PrologEngineChoiceModel(ObjectProperty<MultipleSelectionModel<PrologEngine>> selectionModelProperty) {
		this.selectionModelProperty = selectionModelProperty;
		this.prologEngines = FXCollections.observableArrayList();
	}
	
	public ObservableList<PrologEngine> getPrologEngines() {
		return prologEngines;
	}
	
	@Override
	public void onPrologEngineCreation(final PrologEngine prologEngine) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				prologEngines.add(prologEngine);
			}
		});
	}

	@Override
	public void onPrologEngineShutdown(final PrologEngine prologEngine) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				prologEngines.remove(prologEngine);
			}
		});
	}

}

