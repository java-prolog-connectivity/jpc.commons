package org.jpc.commons.prologbrowser.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.jpc.engine.prolog.PrologEngine;
import org.minitoolbox.fx.FXUtil;

/**
 * A life cycle manager for the shutdown engine button based on an engine availability
 * @author sergioc
 *
 */
public class EngineAvailabilityShutdownButtonManager implements PrologEngineInvalidatedListener {

	private BooleanProperty enabled;
	private PrologEngineChoiceModel engineChoiceModel;
	
	public EngineAvailabilityShutdownButtonManager(PrologEngineChoiceModel engineChoiceModel) {
		enabled = new SimpleBooleanProperty();
		this.engineChoiceModel = engineChoiceModel;
		engineChoiceModel.addEngineSelectionListener(this);
	}
	
	public BooleanProperty enabledProperty() {
		return enabled;
	}

	@Override
	public void onPrologEngineInvalidated() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				PrologEngine prologEngine = engineChoiceModel.getPrologEngine();
				if(prologEngine != null && prologEngine.isCloseable())
					enabled.set(true);
				else
					enabled.set(false);
			}
		});
	}
}
