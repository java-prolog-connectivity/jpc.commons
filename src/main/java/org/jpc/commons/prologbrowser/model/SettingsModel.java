package org.jpc.commons.prologbrowser.model;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

import org.jpc.engine.profile.LogtalkEngineProfile;
import org.jpc.engine.profile.PreloaderEngineProfile;
import org.jpc.engine.profile.PrologEngineProfile;
import org.jpc.engine.profile.PrologEngineProfileFactory;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;

public class SettingsModel implements PrologEngineProfileFactory<PrologEngine> {

	private BooleanProperty preloadLogtalk;
	private StringProperty entryFilePath;
	private List<PrologEngineProfileFactory<PrologEngine>> customProfileFactories;
	
	public SettingsModel(BooleanProperty preloadLogtalkProperty, StringProperty entryFilePathProperty) {
		this.preloadLogtalk = preloadLogtalkProperty;
		this.entryFilePath = entryFilePathProperty;
		customProfileFactories = new ArrayList<>();
	}

	public BooleanProperty preloadLogtalkProperty() {
		return preloadLogtalk;
	}
	
	public StringProperty entryFilePathProperty() {
		return entryFilePath;
	}
	
	public void addProfileFactory(PrologEngineProfileFactory<PrologEngine> profileFactory) {
		customProfileFactories.add(profileFactory);
	}
	
	@Override
	public PrologEngineProfile<PrologEngine> createPrologEngineProfile(PrologEngineFactory<PrologEngine> engineFactory) {
		PrologEngineProfile<PrologEngine> prologEngineProfile = defaultPrologEngineProfile(engineFactory);
		for(PrologEngineProfileFactory<PrologEngine> customProfileFactory : customProfileFactories) {
			prologEngineProfile = customProfileFactory.createPrologEngineProfile(prologEngineProfile);
		}
		return prologEngineProfile;
	}
	
	public PrologEngineProfile<PrologEngine> defaultPrologEngineProfile(PrologEngineFactory<PrologEngine> engineFactory) {
		PrologEngineProfile profile = null;
		if(preloadLogtalk.getValue())
			profile = new LogtalkEngineProfile(engineFactory);
		else
			profile = new PrologEngineProfile(engineFactory);
		String entryFilePath = entryFilePathProperty().get().trim();
		if(!entryFilePath.isEmpty()) {
			profile = new PreloaderEngineProfile(profile, asList(entryFilePath));
		}
		return profile;
	}

}
