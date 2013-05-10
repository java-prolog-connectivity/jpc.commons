package org.jpc.commons.prologbrowser.model;

import javafx.beans.property.BooleanProperty;

import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.minitoolbox.fx.FXUtil;

public class StartPrologEngineModel implements PrologEngineFactoryInvalidatedListener {
	
	/*
	 *  knows how to create a Prolog engine 
	 *  it is not necessarily the driver provided by DriverProvider, it may be a PrologEngineProfile (e.g., a Logtalk profile) based on a driver provider
	 */
	private PrologEngineFactory prologEngineFactory;
	private BooleanProperty disabled;
	
	public StartPrologEngineModel(PrologEngineFactory prologEngineFactory, BooleanProperty disabled) {
		this.prologEngineFactory = prologEngineFactory;
		this.disabled = disabled;
	}

	public PrologEngineFactory getPrologEngineFactory() {
		return prologEngineFactory;
	}
	
	public BooleanProperty disabledProperty() {
		return disabled;
	}

	public void createPrologEngine() {
		prologEngineFactory.createPrologEngine();
	}

	@Override
	public void onPrologEngineFactoryInvalidated() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				if(prologEngineFactory.isDisabled())
					disabled.set(true);
				else
					disabled.set(false);
			}
		});
		
	}
	
}
