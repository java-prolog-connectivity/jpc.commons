package org.jpc.commons.prologbrowser.model;

import org.jpc.engine.prolog.PrologEngine;

public class ShutdownPrologEngineModel {

	
	

	
	public void shutdownPrologEngine(PrologEngine prologEngine) {
		prologEngine.shutdown();
		//call to listener
	}

	
}
