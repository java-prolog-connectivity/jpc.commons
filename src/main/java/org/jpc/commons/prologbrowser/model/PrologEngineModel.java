package org.jpc.commons.prologbrowser.model;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jpc.engine.listener.PrologEngineLifeCycleListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.PrologEngineProxy;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.query.QueryListener;
import org.jpc.util.naming.Nameable;
import org.minitoolbox.CollectionsUtil;
import org.minitoolbox.fx.FXUtil;

public class PrologEngineModel extends PrologEngineProxy implements Nameable {

	private BooleanProperty ready;
	private BooleanProperty closeable;
	private BooleanProperty multiThreaded;
	private StringProperty name;
	private double startupTime; //the time the Prolog engine was created
	private Executor executor;
	private Collection<PrologEngineLifeCycleListener> engineLifeCycleListeners;
	private Property<ObservableList<String>> queryHistory;
	private MultiQueryModel multiQueryModel;
	private Set<QueryListener> queryListeners;

	public PrologEngineModel(Executor executor) {
		ready = new SimpleBooleanProperty(false); //it is not yet available when just initialized
		closeable = new SimpleBooleanProperty(false);
		multiThreaded = new SimpleBooleanProperty(false);
		name = new SimpleStringProperty();
		engineLifeCycleListeners = CollectionsUtil.createWeakSet();
		this.executor = executor;
		queryHistory = new SimpleObjectProperty<>(FXCollections.<String>observableArrayList());
		multiQueryModel = new MultiQueryModel(this, executor);
		queryListeners = CollectionsUtil.createWeakSet();
	}

	public PrologEngineModel(Executor executor, PrologEngine prologEngine) {
		this(executor);
		setPrologEngine(prologEngine);
	}
	
	public PrologEngineModel(Executor executor, PrologEngine prologEngine, String name) {
		this(executor, prologEngine);
		setName(name);
	}
	
	public MultiQueryModel getMultiQueryModel() {
		return multiQueryModel;
	}
	
	@Override
	protected void setPrologEngine(PrologEngine prologEngine) {
		super.setPrologEngine(prologEngine);
		setStartupTime(System.nanoTime());
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				ready.set(true);
				multiThreaded.set(PrologEngineModel.super.isMultiThreaded());
				refreshCloseable();
				notifyOnCreation();
			}
		});
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
	
	
	public void setReady(boolean b) {
		ready.set(b);
	}
	
	public boolean isReady() {
		return ready.get();
	}
	
	public BooleanProperty readyProperty() {
		return ready;
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
			}
		});
	}

	private void refreshCloseable() {
		if(!isReady() || !super.isCloseable()) {
			setCloseable(false);
		} else {
			closeable.bind(Bindings.not(multiQueryModel.queryInProgressProperty()));
		}
	}
	
	public BooleanProperty multiThreadedProperty() {
		return multiThreaded;
	}
	
	public Property<ObservableList<String>> queryHistoryProperty() {
		return queryHistory;
	}
	
	public BooleanProperty closeableProperty() {
		return closeable;
	}
	
	@Override
	public boolean isCloseable() {
		return closeable.get();
	}
	
	private void setCloseable(boolean b) {
		if(closeable.isBound())
			closeable.unbind();
		closeable.set(b);
	}

	@Override
	public void close() {
		ready.set(false);
		setCloseable(false);
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

	public BooleanProperty queryInProgressProperty() {
		return multiQueryModel.queryInProgressProperty();
	}
	
	public boolean isQueryInProgress() {
		return multiQueryModel.isQueryInProgress();
	}
	
	public boolean isNonAbortableQueryInProgress() {
		return multiQueryModel.isNonAbortableQueryInProgress();
	}
	
	public boolean stopQueries() {
		return multiQueryModel.stop();
	}

	public Set<QueryListener> getQueryListeners() {
		return queryListeners;
	}

	public void addQueryListener(QueryListener listener) {
		queryListeners.add(listener);
	}
	
	public void removeQueryListener(QueryListener listener) {
		queryListeners.remove(listener);
	}

}
