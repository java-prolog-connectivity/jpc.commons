package org.jpc.commons.prologbrowser.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.jpc.engine.logtalk.driver.LogtalkEngineProfile;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.prolog.driver.PrologEngineProfile;
import org.jpc.engine.provider.PrologEngineFactoryProvider;

public class LogtalkProfileConfigurationModel implements PrologEngineFactoryProvider<PrologEngine> {

	PrologEngineFactoryProvider<? extends PrologEngine> factoryProvider;
	private BooleanProperty logtalkEnabledProperty;
	
	public LogtalkProfileConfigurationModel(PrologEngineFactoryProvider<? extends PrologEngine> factoryProvider//, BooleanProperty logtalkEnabledProperty
			) {
		this.factoryProvider = factoryProvider;
		this.logtalkEnabledProperty = new SimpleBooleanProperty();
	}
	
	public BooleanProperty logtalkEnabledProperty() {
		return logtalkEnabledProperty;
	}
	
	@Override
	public PrologEngineFactory<PrologEngine> getPrologEngineFactory() {
		PrologEngineProfile profile = null;
		PrologEngineFactory<? extends PrologEngine> engineFactory = factoryProvider.getPrologEngineFactory();
		if(engineFactory != null) {
			if(logtalkEnabledProperty.getValue())
				profile = new LogtalkEngineProfile(engineFactory);
			else
				profile = new PrologEngineProfile(engineFactory);
		}
		return profile;
	}
}
