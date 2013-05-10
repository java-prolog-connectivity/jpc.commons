package org.jpc.commons.prologbrowser.model;

import javafx.beans.property.BooleanProperty;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineManager;
import org.jpc.engine.provider.PrologEngineProvider;
import org.minitoolbox.fx.FXUtil;

/**
 * A life cycle manager for the shutdown engine button based on an engine availability
 * @author sergioc
 *
 */
public class ShutdownPrologEngineModel implements PrologEngineInvalidatedListener {

	private BooleanProperty disabled;
	private PrologEngineProvider prologEngineProvider;
	private PrologEngineManager prologEngineManager;
	
	public ShutdownPrologEngineModel(PrologEngineProvider prologEngineProvider, PrologEngineManager prologEngineManager, BooleanProperty disabled) {
		this.prologEngineProvider = prologEngineProvider;
		this.prologEngineManager = prologEngineManager;
		this.disabled = disabled;
	}
	
	public BooleanProperty disabledProperty() {
		return disabled;
	}

	public void shutdownPrologEngine() {
		PrologEngine prologEngine = prologEngineProvider.getPrologEngine();
		prologEngineManager.shutdownPrologEngine(prologEngine);
	}
	
	@Override
	public void onPrologEngineInvalidated() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				PrologEngine prologEngine = prologEngineProvider.getPrologEngine();
				if(prologEngine != null && prologEngine.isCloseable())
					disabled.set(false);
				else
					disabled.set(true);
			}
		});
	}
}
