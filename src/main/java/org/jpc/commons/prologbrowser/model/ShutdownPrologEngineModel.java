package org.jpc.commons.prologbrowser.model;

import org.jpc.engine.listener.PrologEngineShutdownListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.provider.PrologEngineProvider;

public class ShutdownPrologEngineModel {
	
	private PrologEngineProvider prologEngineProvider;
	private PrologEngineShutdownListener prologEngineShutdownListener;
	
	public ShutdownPrologEngineModel(PrologEngineProvider prologEngineProvider, PrologEngineShutdownListener prologEngineShutdownListener) {
		this.prologEngineProvider = prologEngineProvider;
		this.prologEngineShutdownListener = prologEngineShutdownListener;
	}
	
	public void shutdownPrologEngine() {
		PrologEngine prologEngine = prologEngineProvider.getPrologEngine();
		prologEngine.close();
		if(prologEngineShutdownListener != null)
			prologEngineShutdownListener.onPrologEngineShutdown(prologEngine);
	}

}
