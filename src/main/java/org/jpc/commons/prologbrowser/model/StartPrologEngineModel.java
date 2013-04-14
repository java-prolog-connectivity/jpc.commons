package org.jpc.commons.prologbrowser.model;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;

import org.jpc.engine.listener.PrologEngineCreationListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.provider.PrologEngineFactoryProvider;
import org.jpc.util.naming.NamingUtil;

public class StartPrologEngineModel implements PrologEngineFactorySelectionListener {

	private BooleanProperty disableProperty;
	private PrologEngineCreationListener prologEngineCreationListener;
	private Map<String, Integer> namesPrologEngines;
	
	/*
	 *  //knows how to create a Prolog engine 
	 *  it is not necessarily the driver provided by driverProvider, it may be a PrologEngineProfile (e.g., a Logtalk profile) based on the driver provider
	 */
	private PrologEngineFactoryProvider<PrologEngineFactory> prologEngineFactoryProvider;
	
	
	public StartPrologEngineModel(BooleanProperty disableProperty, PrologEngineFactoryProvider<PrologEngineFactory> prologEngineFactoryProvider, PrologEngineCreationListener prologEngineCreationListener) {
		this.disableProperty = disableProperty;
		this.prologEngineFactoryProvider = prologEngineFactoryProvider;
		this.prologEngineCreationListener = prologEngineCreationListener;
		namesPrologEngines = new HashMap<>();
	}
	
	public void disable(boolean value) {
		disableProperty.set(value);
	}

	public PrologEngine createPrologEngine() {
		PrologEngine prologEngine = getPrologEngineFactory().createPrologEngine();
		NamingUtil.renameIfRepeated(prologEngine, namesPrologEngines);
		if(prologEngineCreationListener != null)
			prologEngineCreationListener.onPrologEngineCreation(prologEngine);
		return prologEngine;
	}

	public PrologEngineFactory getPrologEngineFactory() {
		return prologEngineFactoryProvider.getPrologEngineFactory();
	}
	
	@Override
	public void onPrologEngineFactorySelected() {
		disable(false);
	}

	@Override
	public void onPrologEngineFactoryUnselected() {
		disable(true);
	}

	@Override
	public void onSelectedPrologEngineFactoryDisabled() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				disable(true);
			}
		});
	}
	
}
