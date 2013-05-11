package org.jpc.commons.prologbrowser.model;

import java.util.Collection;
import java.util.concurrent.Executor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.jpc.engine.listener.PrologEngineLifeCycleListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.PrologEngineProxy;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.util.naming.Nameable;
import org.minitoolbox.CollectionsUtil;
import org.minitoolbox.fx.FXUtil;

public class PrologEngineModel extends PrologEngineProxy implements Nameable {

	private BooleanProperty available;
	private BooleanProperty closeable;
	private StringProperty name;
	private double startupTime; //the time the Prolog engine was created
	private Executor executor;
	private Collection<PrologEngineLifeCycleListener> engineLifeCycleListeners;
	
	public PrologEngineModel(Executor executor) {
		available = new SimpleBooleanProperty(false); //it is not yet available when just initialized
		closeable = new SimpleBooleanProperty(false);
		name = new SimpleStringProperty();
		this.executor = executor;
		this.engineLifeCycleListeners = CollectionsUtil.createWeakSet();
	}

	@Override
	protected synchronized void setPrologEngine(PrologEngine prologEngine) {
		super.setPrologEngine(prologEngine);
	}
	
	@Override
	public synchronized PrologEngine getPrologEngine() {
		return super.getPrologEngine();
	}
	
	protected synchronized void setStartupTime(double startupTime) {
		this.startupTime = startupTime;
	}

	public synchronized double getStartupTime() {
		return startupTime;
	}
	
	
	public void setAvailable(boolean b) {
		available.set(b);
	}
	
	public boolean isAvailable() {
		return available.get();
	}
	
	public BooleanProperty availableProperty() {
		return available;
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


	public synchronized void initialize(final PrologEngineFactory prologEngineFactory) {
		if(getPrologEngine() != null)
			throw new RuntimeException("Engine already initialized");
		executor.execute(new Runnable() {
			@Override
			public void run() {
				setPrologEngine(prologEngineFactory.createPrologEngine());
//				try {
//				Thread.sleep(500); //TODO delete
//				} catch (InterruptedException e) {
//					throw new RuntimeException(e);
//				}
				setStartupTime(System.nanoTime());
				FXUtil.runInFXApplicationThread(new Runnable() {
					@Override
					public void run() {
						available.set(true);
						closeable.set(PrologEngineModel.super.isCloseable());
						notifyOnCreation();
					}
				});
			}
		});
	}

	public BooleanProperty closeableProperty() {
		return closeable;
	}
	
	@Override
	public boolean isCloseable() {
		return closeable.get();
	}
	
	@Override
	public void close() {
		available.set(false);
		closeable.set(false);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				PrologEngineModel.super.close();
				notifyOnShutdown();
			}
		});
	}

	public void addEngineLifeCycleListener(PrologEngineLifeCycleListener listener) {
		engineLifeCycleListeners.add(listener);
	}

	public void removeEngineLifeCycleListener(PrologEngineLifeCycleListener listener) {
		engineLifeCycleListeners.remove(listener);
	}

	void notifyOnCreation() {
		for(PrologEngineLifeCycleListener listener : engineLifeCycleListeners) {
			listener.onPrologEngineCreation(this);
		}
	}
	
	void notifyOnShutdown() {
		for(PrologEngineLifeCycleListener listener : engineLifeCycleListeners) {
			listener.onPrologEngineShutdown(this);
		}
	}
	
}
