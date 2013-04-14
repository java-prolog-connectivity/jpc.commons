package org.jpc.commons.prologbrowser.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.jpc.engine.logtalk.driver.LogtalkEngineProfile;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.prolog.driver.PrologEngineProfile;
import org.jpc.engine.provider.PrologEngineFactoryProvider;

public class LogtalkProfileConfigurationModel implements PrologEngineFactoryProvider<PrologEngineFactory> {

	PrologEngineFactoryProvider<? extends PrologEngineFactory> factoryProvider;
	private BooleanProperty logtalkEnabledProperty;
	
	public LogtalkProfileConfigurationModel(PrologEngineFactoryProvider<? extends PrologEngineFactory> factoryProvider//, BooleanProperty logtalkEnabledProperty
			) {
		this.factoryProvider = factoryProvider;
		this.logtalkEnabledProperty = new SimpleBooleanProperty();
	}
	
	public BooleanProperty logtalkEnabledProperty() {
		return logtalkEnabledProperty;
	}
	
	@Override
	public PrologEngineProfile getPrologEngineFactory() {
		PrologEngineProfile profile = null;
		PrologEngineFactory engineFactory = factoryProvider.getPrologEngineFactory();
		if(engineFactory != null) {
			if(logtalkEnabledProperty.getValue())
				profile = new LogtalkEngineProfile(engineFactory);
			else
				profile = new PrologEngineProfile(engineFactory);
		}
		return profile;
	}
}
