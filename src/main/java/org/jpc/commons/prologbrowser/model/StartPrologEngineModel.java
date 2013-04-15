package org.jpc.commons.prologbrowser.model;

import java.util.HashMap;
import java.util.Map;

import org.jpc.engine.listener.PrologEngineCreationListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.provider.PrologEngineFactoryProvider;
import org.jpc.util.naming.NamingUtil;

public class StartPrologEngineModel {

	private PrologEngineCreationListener prologEngineCreationListener;
	private Map<String, Integer> namesPrologEngines;
	
	/*
	 *  //knows how to create a Prolog engine 
	 *  it is not necessarily the driver provided by DriverProvider, it may be a PrologEngineProfile (e.g., a Logtalk profile) based on a driver provider
	 */
	private PrologEngineFactoryProvider<PrologEngineFactory> prologEngineFactoryProvider;
	
	
	public StartPrologEngineModel(PrologEngineFactoryProvider<PrologEngineFactory> prologEngineFactoryProvider, PrologEngineCreationListener prologEngineCreationListener) {
		this.prologEngineFactoryProvider = prologEngineFactoryProvider;
		this.prologEngineCreationListener = prologEngineCreationListener;
		namesPrologEngines = new HashMap<>();
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
	
}
