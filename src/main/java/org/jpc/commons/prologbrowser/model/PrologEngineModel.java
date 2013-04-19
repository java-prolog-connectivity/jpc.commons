package org.jpc.commons.prologbrowser.model;

import java.util.concurrent.Executor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.PrologEngineProxy;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.util.naming.Nameable;
import org.minitoolbox.fx.FXUtil;

public class PrologEngineModel extends PrologEngineProxy implements Nameable {

	private volatile PrologEngine prologEngine;
	private BooleanProperty busy;
	private volatile double startupTime; //the time the Prolog engine was created
	private StringProperty name;
	private Executor executor;
	
	public PrologEngineModel(Executor executor) {
		busy = new SimpleBooleanProperty(true); //it is busy until initialized
		name = new SimpleStringProperty();
		this.executor = executor;
		
	}

	public void setBusy(boolean b) {
		busy.set(b);
	}
	
	
	public BooleanProperty busyProperty() {
		return busy;
	}
	
	public StringProperty nameProperty() {
		return name;
	}
	
	@Override
	public String getName() {
		return name.get();
	}

	@Override
	public void setName(String name) {
		this.name.set(name);
	}

	public double getStartupTime() {
		return startupTime;
	}

	public void initialize(final PrologEngineFactory<? extends PrologEngine> prologEngineFactory) {
		if(prologEngine != null)
			throw new RuntimeException("Engine already initialized");
		executor.execute(new Runnable() {
			@Override
			public void run() {
				prologEngine = prologEngineFactory.createPrologEngine();
				startupTime = System.nanoTime();
				FXUtil.runInFXApplicationThread(new Runnable() {
					@Override
					public void run() {
						busy.set(false);
					}
				});
			}
		});
	}

	@Override
	public void close() {
		busy.set(true);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					PrologEngineModel.super.close();
				} finally {
					FXUtil.runInFXApplicationThread(new Runnable() {
						@Override
						public void run() {
							busy.set(false);
						}
					});
				}
			}
		});
	}

}
