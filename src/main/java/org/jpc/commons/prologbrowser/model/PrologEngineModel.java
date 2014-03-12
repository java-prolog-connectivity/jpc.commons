package org.jpc.commons.prologbrowser.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jpc.Jpc;
import org.jpc.engine.listener.PrologEngineLifeCycleListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.PrologEngineProxy;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.query.ObservableQuery;
import org.jpc.query.Query;
import org.jpc.query.QueryListener;
import org.jpc.query.Solution;
import org.jpc.term.Term;
import org.minitoolbox.collections.CollectionsUtil;
import org.minitoolbox.fx.FXUtil;
import org.minitoolbox.naming.Nameable;

public class PrologEngineModel extends PrologEngineProxy implements Nameable, QueryListener  {

	private IntegerProperty numQueriesInProgress;
	private BooleanProperty queryInProgress;
	
	private BooleanProperty ready;
	private BooleanProperty busy;
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
		numQueriesInProgress = new SimpleIntegerProperty(0);
		queryInProgress = new SimpleBooleanProperty();
		queryInProgress.bind(numQueriesInProgress.greaterThan(0));
		ready = new SimpleBooleanProperty(false); //it is not yet available when just initialized
		closeable = new SimpleBooleanProperty(false);
		multiThreaded = new SimpleBooleanProperty(false);
		name = new SimpleStringProperty();
		engineLifeCycleListeners = CollectionsUtil.createWeakSet();
		this.executor = executor;
		queryHistory = new SimpleObjectProperty<>(FXCollections.<String>observableArrayList());
		multiQueryModel = new MultiQueryModel(this, executor);
		busy = new SimpleBooleanProperty();
		busy.bind(Bindings.or(Bindings.not(ready), queryInProgressProperty()));
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

	
	@Override
	public ObservableQuery basicQuery(Term term, boolean errorHandledQuery, Jpc context) {
		Query observedQuery = super.basicQuery(term, errorHandledQuery, context);
		ObservableQuery query = new ObservableQuery(observedQuery, getQueryListeners()); //creates an observable query with the default listeners of the Prolog engine
		query.addQueryListener(this); //also add the PrologEngine as one of the listeners
		return query;
	}
	
	/**
	 * 
	 * @return true if at least one query is in progress. false otherwise.
	 */
	public boolean isQueryInProgress() {
		return queryInProgressProperty().get();
	}
	
	public BooleanProperty queryInProgressProperty() {
		return queryInProgress;
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
	
	public BooleanProperty busyProperty() {
		return busy;
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
			closeable.bind(Bindings.not(queryInProgressProperty()));
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

	@Override
	public void onQueryReady() {
	}

	@Override
	public void onQueryOpened() {
	}

	@Override
	public void onQueryExhausted() {
	}

	@Override
	public void onQueryInProgress() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				numQueriesInProgress.set(numQueriesInProgress.get()+1);
			}
		});
	}

	@Override
	public void onQueryFinished() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				numQueriesInProgress.set(numQueriesInProgress.get()-1);
			}
		});
	}

	@Override
	public void onException(Exception e) {
	}

	@Override
	public void onNextSolutionFound(Solution solution) {
	}

	@Override
	public void onSolutionsFound(List<Solution> solutions) {
	}

	@Override
	public void onQueryDisposed() {
	}
	
}
