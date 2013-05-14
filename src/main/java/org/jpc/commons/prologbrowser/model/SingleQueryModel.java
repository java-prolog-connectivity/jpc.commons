package org.jpc.commons.prologbrowser.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jpc.query.ObservableQuery;
import org.jpc.query.Query;
import org.jpc.query.QueryListener;
import org.jpc.term.Term;
import org.minitoolbox.CollectionsUtil;
import org.minitoolbox.fx.FXUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleQueryModel implements QueryListener {

	private static Logger logger = LoggerFactory.getLogger(SingleQueryModel.class);
	
	//private File file; //a file in the file system where this query is persisted
	private Collection<QueryListener> listeners;
	
	private Executor abortQueryExecutor;
	private ExecutorService queryExecutor; //a dedicated executor
	//MAIN PROPERTIES
	private BooleanProperty engineReady;
	private Property<ObservableList<String>> queryHistory;
	private StringProperty queryText;
	private BooleanProperty queryOpen;
	private BooleanProperty queryInProgress;
	private BooleanProperty queryExhausted;

	//DERIVED PROPERTIES
	private BooleanProperty queryTextEditable;
	private BooleanProperty queryTextAvailable;
	private BooleanProperty oneSolutionDisabled;
	private BooleanProperty allSolutionsDisabled;
	private BooleanProperty nextSolutionDisabled;
	private BooleanProperty cancelDisabled;
	private BooleanProperty abortable;

	private ObservableQuery query;
	private PrologEngineModel prologEngineModel;

	public SingleQueryModel(PrologEngineModel prologEngineModel, Executor abortQueryExecutor) {
		this.abortQueryExecutor = abortQueryExecutor;
		this.prologEngineModel = prologEngineModel;
		engineReady = prologEngineModel.readyProperty();
		queryExecutor = Executors.newSingleThreadExecutor();
		//queryExecutor = Executors.newSingleThreadExecutor(new OneThreadFactory());
		//queryExecutor = new OneThreadExecutor();
				
		queryHistory = new SimpleObjectProperty<>(FXCollections.<String>observableArrayList());
		listeners = CollectionsUtil.createWeakSet();
		listeners.add(this); //the query model is a listener of the observed query. More listeners can be registered.
		
		//INITIALIZING MAIN PROPERTIES
		queryText = new SimpleStringProperty();
		queryOpen = new SimpleBooleanProperty(false);
		queryInProgress = new SimpleBooleanProperty(false);
		queryExhausted = new SimpleBooleanProperty(false);
		
		//SETTING DERIVED PROPERTIES
		queryTextEditable = new SimpleBooleanProperty(true); 
		queryTextEditable.bind(Bindings.not(Bindings.or(queryOpen, queryExhausted)));
		queryTextAvailable = new SimpleBooleanProperty(false);
		queryText.addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				query = null;
				if(newValue.trim().isEmpty())
					queryTextAvailable.set(false);
				else
					queryTextAvailable.set(true);
			}
		});
		oneSolutionDisabled = new SimpleBooleanProperty(true);
		oneSolutionDisabled.bind(Bindings.not(engineReady).or(Bindings.not(queryTextAvailable).or(queryExhausted).or(queryOpen)));
		allSolutionsDisabled = new SimpleBooleanProperty(true);
		allSolutionsDisabled.bind(Bindings.not(engineReady).or(Bindings.not(queryTextAvailable).or(queryExhausted).or(queryOpen)));
		nextSolutionDisabled = new SimpleBooleanProperty(true);
		nextSolutionDisabled.bind(Bindings.not(engineReady).or(Bindings.not(queryTextAvailable).or(queryExhausted)));
		abortable = new SimpleBooleanProperty(false); //this property will be updated when a query is created
		cancelDisabled = new SimpleBooleanProperty(true);
		cancelDisabled.bind(Bindings.not(queryOpen).and(Bindings.not(queryInProgress).or(Bindings.not(abortable))));
	}

	
	
	public Property<ObservableList<String>> queryHistoryProperty() {
		return queryHistory;
	}
	
	public StringProperty queryTextProperty() {
		return queryText;
	}
	
	public BooleanProperty queryTextAvailableProperty() {
		return queryTextAvailable;
	}
	
	public BooleanProperty queryTextEditableProperty() {
		return queryTextEditable;
	}
	
	public BooleanProperty abortableProperty() {
		return abortable;
	}
	
	public boolean isAbortable() {
		return abortable.get();
	}
	
	public BooleanProperty queryOpenProperty() {
		return queryOpen;
	}
	
	public BooleanProperty queryInProgressProperty() {
		return queryInProgress;
	}
	
	public boolean isQueryInProgress() {
		return queryInProgress.get();
	}
	
	public BooleanProperty oneSolutionDisabledProperty() {
		return oneSolutionDisabled;
	}
	
	public BooleanProperty allSolutionsDisabledProperty() {
		return allSolutionsDisabled;
	}
	
	public BooleanProperty nextSolutionDisabledProperty() {
		return nextSolutionDisabled;
	}
	
	public BooleanProperty cancelDisabledProperty() {
		return cancelDisabled;
	}
	
	public void stop() {
		close();
		queryExecutor.shutdownNow();
	}

	public void forceStop() {
		forceClose();
		queryExecutor.shutdownNow();
	}
	
	public void close() {
		if(query != null) {
			if(query.isOpen()) {
				queryExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							query.close();
						} catch(Exception e) {
							logger.warn("Impossible to close running query in Prolog engine " + prologEngineModel.getName());
						}
					}
				});
			}
		}
	}
	
	public void forceClose() {
		if(query != null) {
			if(isQueryInProgress() && isAbortable()) {
				abort();
			}
			else {
				close();
			}
		}
	}
	
	public void abort() {
		if(query != null) {
			if(queryInProgress.get()) {
				abortQueryExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							query.abort();
						} catch(Exception e) {
							logger.warn("Impossible to abort running query in Prolog engine " + prologEngineModel.getName());
						}
					}
				});
			}
		}
	}
	
	public void oneSolution() {
		queryExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if(query == null)
					query = createQuery();
				query.oneSolution();
				
			}
		});
	}
	
	public void allSolutions() {
		queryExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if(query == null)
					query = createQuery();
				query.allSolutions();
			}
		});
	}
	
	public void nextSolution() {
		queryExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if(query == null)
					query = createQuery();
				query.next();
			}
		});
	}
	
	
	private ObservableQuery createQuery() {
		String text = queryText.get();
		queryHistory.getValue().add(0, text);
		Query query = prologEngineModel.query(text);
		ObservableQuery observedQuery = new ObservableQuery(query, listeners);
		abortable.set(query.isAbortable());
		return observedQuery;
	}

	
	@Override
	public void onQueryReady() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				queryOpen.set(false);
				queryExhausted.set(false);
			}
		});	
	}

	@Override
	public void onQueryOpened() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				queryOpen.set(true);
			}
		});	
	}

	@Override
	public void onQueryExhausted() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				queryExhausted.set(true); //the query was exhausted when attempting to find the next solution
			}
		});	
	}

	@Override
	public void onQueryInProgress() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				queryInProgress.set(true);
			}
		});
	}

	@Override
	public void onQueryFinished() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				queryInProgress.set(false);
			}
		});
	}

	@Override
	public void onException(Exception e) {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				queryInProgress.set(false);
			}
		});
	}
	
	@Override
	public void onNextSolutionFound(Map<String, Term> solution) {
	}

	@Override
	public void onSolutionsFound(List<Map<String, Term>> solutions) {
	}

	/**
	 * Add a listener to the list of objects listening for query events.
	 * @param listener
	 */
	public void addQueryListener(QueryListener listener) {
		listeners.add(listener);
		if(query != null)
			query.addQueryListener(listener);
	}
	
	public void removeQueryListener(QueryListener listener) {
		listeners.remove(listener);
		if(query != null)
			query.removeQueryListener(listener);
	}



}
