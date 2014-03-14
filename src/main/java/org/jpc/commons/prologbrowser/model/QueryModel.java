package org.jpc.commons.prologbrowser.model;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import org.jpc.error.PrologError;
import org.jpc.error.PrologParsingException;
import org.jpc.query.ObservableQuery;
import org.jpc.query.Query;
import org.jpc.query.QueryListener;
import org.jpc.query.Solution;
import org.minitoolbox.collections.CollectionsUtil;
import org.minitoolbox.fx.FXUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryModel implements QueryListener {

	private static final Logger logger = LoggerFactory.getLogger(QueryModel.class);
	
	//private File file; //a file in the file system where this query is persisted
	private Set<QueryListener> listeners;
	
	//private Executor abortQueryExecutor;
	private Executor queryExecutor; //the query executor
	
	//ENGINE PROPERTIES
	private BooleanProperty engineReady;
	private BooleanProperty engineIsQueried; //holds true if there is any query in progress in the same Prolog engine where this query is executed.
	private BooleanProperty engineIsMultiThreaded; //holds true if the Prolog engine where this query is executed is multithreaded.
	
	
	//QUERY PROPERTIES
	private StringProperty queryText;
	private BooleanProperty queryOpen; //holds true if the query is open
	private BooleanProperty showingResult; //holds true if the query has just finished and its results are being shown to the user
	private BooleanProperty queryInProgress;  //holds true if the query is in progress
	private BooleanProperty queryExhausted; //holds true if the query has been exhausted after successive calls to the next solution
	private LongProperty numberSolutions;
	private LongProperty queryMilliseconds;
	private LongProperty startTime;
	//DERIVED QUERY PROPERTIES
	private BooleanProperty queryTextEditable;
	private BooleanProperty queryTextAvailable;
	private BooleanProperty oneSolutionDisabled;
	private BooleanProperty allSolutionsDisabled;
	private BooleanProperty nextSolutionDisabled;
	private BooleanProperty cancelDisabled;
	private BooleanProperty abortable;

	private PrologEngineModel prologEngineModel;
	private ObservableQuery query;
	private QueryResultModel queryResultModel;

	private StringProperty statusMessage;
	
	
	public QueryModel(PrologEngineModel prologEngineModel, Executor queryExecutor) {
		this.prologEngineModel = prologEngineModel;
		this.queryExecutor = queryExecutor;
		//queryExecutor = Executors.newSingleThreadExecutor();
		//queryExecutor = Executors.newSingleThreadExecutor(new OneThreadFactory());
		//queryExecutor = new OneThreadExecutor();
		//this.abortQueryExecutor = abortQueryExecutor;
		this.engineReady = prologEngineModel.readyProperty();
		this.engineIsQueried = prologEngineModel.queryInProgressProperty();
		this.engineIsMultiThreaded = prologEngineModel.multiThreadedProperty();
		BooleanProperty nonMultiThreadedEngineIsQueried = new SimpleBooleanProperty(); //holds true if the engine that spawned the query is not multithreaded and there is aslready a query in progress.
		nonMultiThreadedEngineIsQueried.bind(engineIsQueried.and(Bindings.not(engineIsMultiThreaded)));
		
		listeners = CollectionsUtil.createWeakSet();
		listeners.add(this); //the query model is a listener of the observed query. More listeners can be registered.
		
		//INITIALIZING MAIN PROPERTIES
		queryText = new SimpleStringProperty("");
		queryOpen = new SimpleBooleanProperty(false);
		queryInProgress = new SimpleBooleanProperty(false);
		queryExhausted = new SimpleBooleanProperty(false);
		showingResult = new SimpleBooleanProperty(false);
		
		//SETTING DERIVED PROPERTIES
		queryTextEditable = new SimpleBooleanProperty(true); 
		queryTextEditable.bind(Bindings.not(Bindings.or(queryOpen, queryExhausted).or(showingResult)));
		queryTextAvailable = new SimpleBooleanProperty(false);
		queryText.addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				resetQuery();
				if(newValue.trim().isEmpty())
					queryTextAvailable.set(false);
				else
					queryTextAvailable.set(true);
			}
		});
		
		oneSolutionDisabled = new SimpleBooleanProperty(true);
		oneSolutionDisabled.bind(Bindings.not(engineReady).or(showingResult).or(nonMultiThreadedEngineIsQueried).or(Bindings.not(queryTextAvailable)).or(queryExhausted).or(queryOpen));
		allSolutionsDisabled = new SimpleBooleanProperty(true);
		allSolutionsDisabled.bind(Bindings.not(engineReady).or(showingResult).or(nonMultiThreadedEngineIsQueried).or(Bindings.not(queryTextAvailable)).or(queryExhausted).or(queryOpen));
		nextSolutionDisabled = new SimpleBooleanProperty(true);
		nextSolutionDisabled.bind(Bindings.not(engineReady).or(showingResult).or(nonMultiThreadedEngineIsQueried).or(Bindings.not(queryTextAvailable)).or(queryExhausted));
		abortable = new SimpleBooleanProperty(false); //this property will be updated when a query is created
		cancelDisabled = new SimpleBooleanProperty(true);
		//cancelDisabled.bind(Bindings.not(showingResult).and(Bindings.not(queryOpen).and(Bindings.not(queryInProgress).or(Bindings.not(abortable)))));
		cancelDisabled.bind(Bindings.not( //negation of the enabled condition seems to be easier to write
				showingResult.or(queryOpen).or(Bindings.and(queryInProgress, abortable)) 
		));
		numberSolutions = new SimpleLongProperty(0);
		queryMilliseconds = new SimpleLongProperty(0);
		startTime = new SimpleLongProperty(0);
		statusMessage = new SimpleStringProperty();
		
		this.queryResultModel = new QueryResultModel();
	}

	public PrologEngineModel getPrologEngineModel() {
		return prologEngineModel;
	}

	public QueryResultModel getQueryResultModel() {
		return queryResultModel;
	}

	public Executor getExecutor() {
		return queryExecutor;
	}
	
	private void updateStatus() {
		if(query == null)
			updateStatus("");
		else {
			StringBuilder sb = new StringBuilder();
			if(query.isExhausted())
				sb.append("Query Exhausted. ");
			
			if(numberSolutions.get() == 0)
				sb.append("No solutions");
			else {
				long nSolutions = numberSolutions.get();
				sb.append(nSolutions + " solution");
				if(nSolutions != 1)
					sb.append("s");
			}
			sb.append(" found in ");
			
			if(queryMilliseconds.get() < 1000)
				sb.append(queryMilliseconds.get() + " milliseconds");
			else if(queryMilliseconds.get() < 10000) //less than 10 seconds
				sb.append( (queryMilliseconds.get()/1000D) + " seconds"); //show it in seconds and preserve decimals
			else //more than 10 seconds
				sb.append( (queryMilliseconds.get()/1000) + " seconds"); //show it in seconds and do not preserve decimals
			
			sb.append(".");
			updateStatus(sb.toString());
		}
	}
	
	public void updateStatus(final String message) {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				statusMessage.set(message);
			}
		});
	}
	
	private void updateQueryMilliseconds() {
		long now = System.nanoTime();
		long deltaMilliSeconds = (now - startTime.get())/1000000;
		queryMilliseconds.set(queryMilliseconds.get() + deltaMilliSeconds);
	}
	
	private void resetQuery() {
		if(query != null) {
			query.close();
			query = null;
			queryResultModel.reset();
			resetQueryMetrics();
		}
		updateStatus();
	}
	
	private void resetQueryMetrics() {
		numberSolutions.set(0);
		startTime.set(0);
		queryMilliseconds.set(0);
		showingResult.set(false);
		queryResultModel.getQueryResult().clear();
	}
	
	public void resetState() {
		resetQueryMetrics();
		updateStatus("");
	}
	
	public StringProperty statusMessageProperty() {
		return statusMessage;
	}
	
	public void setQueryText(String text) {
		queryText.set(text);
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
		//queryExecutor.shutdownNow();
	}

	public void forceStop() {
		forceClose();
		//queryExecutor.shutdownNow();
	}
	
	public void close() {
		if(query != null) {
			if(query.isOpen() || query.isExhausted()) {
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
				queryExecutor.execute(new Runnable() {
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
		resetState();
		startTime.set(System.nanoTime());
		queryExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if(query == null)
					initializeQuery();
				try {
					if(query != null)
						query.oneSolution();
				} catch(PrologError e) {
					onException(e);
				}
			}
		});
	}
	
	public void allSolutions() {
		resetState();
		startTime.set(System.nanoTime());
		queryExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if(query == null) 
					initializeQuery();
				try {
					if(query != null)
						query.allSolutions();
				} catch(PrologError e) {
					onException(e);
				}
			}
		});
	}
	
	public void nextSolution() {
		if(query == null || query.isReady())
			resetState();
		else
			updateStatus(""); //clean the status but keep the state
		startTime.set(System.nanoTime());
		queryExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if(query == null)
					initializeQuery();
				try {
					if(query != null)
						if(query.hasNext()) //this check is necessary, otherwise a NoSuchElementException exception could be raised.
							query.next();
				} catch(PrologError e) {
					onException(e);
				}
			}
		});
	}
	
	private boolean initializeQuery() {
		final String text = queryText.get();
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				Property<ObservableList<String>> queryHistory = prologEngineModel.queryHistoryProperty();
				if(queryHistory.getValue().isEmpty() || !queryHistory.getValue().get(0).equals(text)) //add it to the history if it is not the same than the previous entry
					queryHistory.getValue().add(0, text);
			}
		});	
		Query observedQuery = null;
		try {
			observedQuery = prologEngineModel.query(text);
			//query = prologEngineModel.query(text);
		} catch(PrologParsingException e) {
			onException(e);
			return false;
		}
		//query = prologEngineModel.observe(observedQuery);
		query = new ObservableQuery(observedQuery);
		query.addQueryListeners(listeners); //query specific listeners
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				abortable.set(query.isAbortable());
				queryResultModel.setGoal(query.getGoal());
			}
		});
		return true;
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
	public void onQueryExhausted() {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				queryExhausted.set(true); //the query was exhausted when attempting to find the next solution
				updateQueryMilliseconds();
				updateStatus();
				showingResult.set(true);
			}
		});	
	}
	
	@Override
	public void onException(final Exception e) {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				//queryInProgress.set(false);
				//updateStatus("Exception while executing query: " + e.toString());
				close();
				StringBuilder sb = new StringBuilder();
				String message = e.getMessage();
				if(message != null && !message.isEmpty())
					sb.append(message);
				else
					sb.append(e.toString() + ".");
				if(e.getCause() != null) {
					sb.append(" Cause: ");
					sb.append(e.getCause());
				}
				updateStatus(sb.toString());
				showingResult.set(true);
			}
		});
	}
	
	@Override
	public void onNextSolutionFound(final Solution solution) {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				numberSolutions.set(numberSolutions.get() + 1);
				updateQueryMilliseconds();
				updateStatus();
				queryResultModel.getQueryResult().add(solution);
			}
		});
	}

	@Override
	public void onSolutionsFound(final List<Solution> solutions) {
		FXUtil.runInFXApplicationThread(new Runnable() {
			@Override
			public void run() {
				numberSolutions.set(solutions.size());
				updateQueryMilliseconds();
				updateStatus();
				showingResult.set(true);
				queryResultModel.getQueryResult().setAll(solutions);
			}
		});
	}
	
	@Override
	public void onQueryDisposed() {
	}

	private void notifyException(Exception e) {
		for(QueryListener listener : listeners) {
			listener.onException(e);
		}
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
