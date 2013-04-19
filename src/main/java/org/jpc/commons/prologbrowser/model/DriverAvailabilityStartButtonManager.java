package org.jpc.commons.prologbrowser.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.provider.PrologEngineFactoryProvider;
import org.minitoolbox.fx.FXUtil;

/**
 * A life cycle manager for the start engine button based on a driver availability
 * @author sergioc
 *
 */
public class DriverAvailabilityStartButtonManager implements PrologEngineFactoryInvalidatedListener {

	private BooleanProperty enabled;
	private PrologEngineFactoryProvider<PrologEngine> driverProvider;
	
	public DriverAvailabilityStartButtonManager(PrologDriverChoiceModel driverChoiceModel) {
		enabled = new SimpleBooleanProperty();
		this.driverProvider = driverChoiceModel;
		driverChoiceModel.addDriverSelectionObserver(this);
	}
	
	public BooleanProperty enabledProperty() {
		return enabled;
	}

	@Override
	public void onPrologEngineFactoryInvalidated() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				PrologEngineFactory driver = driverProvider.getPrologEngineFactory();
				if(driver != null && driver.isEnabled())
					enabled.set(true);
				else
					enabled.set(false);
			}
		});
		
	}

}
