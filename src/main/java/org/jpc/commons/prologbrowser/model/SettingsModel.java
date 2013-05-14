package org.jpc.commons.prologbrowser.model;

import static java.util.Arrays.asList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

import org.jpc.engine.profile.LogtalkEngineProfile;
import org.jpc.engine.profile.PreloaderEngineProfile;
import org.jpc.engine.profile.PrologEngineProfile;
import org.jpc.engine.profile.PrologEngineProfileFactory;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;

public class SettingsModel implements PrologEngineProfileFactory<PrologEngine> {

	private BooleanProperty preloadLogtalkProperty;
	private StringProperty entryFilePathProperty;
	
	public SettingsModel(BooleanProperty preloadLogtalkProperty, StringProperty entryFilePathProperty) {
		this.preloadLogtalkProperty = preloadLogtalkProperty;
		this.entryFilePathProperty = entryFilePathProperty;
	}

	@Override
	public PrologEngineProfile<PrologEngine> createPrologEngineProfile(PrologEngineFactory<PrologEngine> engineFactory) {
		PrologEngineProfile profile = null;
		if(preloadLogtalkProperty.getValue())
			profile = new LogtalkEngineProfile(engineFactory);
		else
			profile = new PrologEngineProfile(engineFactory);
		String entryFilePath = entryFilePathProperty.get().trim();
		if(!entryFilePath.isEmpty()) {
			profile = new PreloaderEngineProfile(profile, asList(entryFilePath));
		}
		return profile;
	}

}
