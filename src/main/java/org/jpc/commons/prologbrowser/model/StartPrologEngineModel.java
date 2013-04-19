package org.jpc.commons.prologbrowser.model;

import org.jpc.engine.listener.PrologEngineCreationListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;

public class StartPrologEngineModel {

	private PrologEngineCreationListener prologEngineCreationListener;
	
	
	/*
	 *  //knows how to create a Prolog engine 
	 *  it is not necessarily the driver provided by DriverProvider, it may be a PrologEngineProfile (e.g., a Logtalk profile) based on a driver provider
	 */
	private PrologEngineFactory<PrologEngine> prologEngineFactory;
	
	
	public StartPrologEngineModel(PrologEngineFactory<PrologEngine> prologEngineFactory, PrologEngineCreationListener prologEngineCreationListener) {
		this.prologEngineFactory = prologEngineFactory;
		this.prologEngineCreationListener = prologEngineCreationListener;
		
	}


	public PrologEngine createPrologEngine() {
		PrologEngine prologEngine = getPrologEngineFactory().createPrologEngine();
		if(prologEngineCreationListener != null)
			prologEngineCreationListener.onPrologEngineCreation(prologEngine);
		return prologEngine;
	}

	public PrologEngineFactory getPrologEngineFactory() {
		return prologEngineFactory;
	}
	
}
