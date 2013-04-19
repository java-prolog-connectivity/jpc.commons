package org.jpc.commons.prologbrowser.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.query.ObservableQuery;
import org.jpc.query.Query;
import org.jpc.query.QueryListener;
import org.jpc.term.Term;
import org.minitoolbox.CollectionsUtil;
import org.minitoolbox.fx.FXUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryModel implements QueryListener {

	private static Logger logger = LoggerFactory.getLogger(QueryModel.class);
	
	private Collection<QueryListener> listeners;
	
	//MAIN PROPERTIES
	private Property<ObservableList<String>> queryHistory;
	private StringProperty queryText;
	private BooleanProperty queryOpen;
	private BooleanProperty queryInProgress;
	private BooleanProperty queryExhausted;
	
	//DERIVED PROPERTIES
	private BooleanProperty queryTextEditable;
	private BooleanProperty queryTextAvailable;
	private BooleanProperty nextSolutionEnabled;
	private BooleanProperty allSolutionsEnabled;
	private BooleanProperty cancelEnabled;
	private BooleanProperty isAbortable;
		
	private ObservableQuery query;
	private PrologEngine prologEngine;

	
	public QueryModel(PrologEngine prologEngine) {
		this.prologEngine = prologEngine;
		queryHistory = new SimpleObjectProperty<>(FXCollections.<String>observableArrayList());
		listeners = CollectionsUtil.createWeakSet();
		listeners.add(this); //the query model is a listener of the observed query. However, more listeners can be registered.
		
		//INITIALIZING MAIN PROPERTIES
		queryText = new SimpleStringProperty();
		queryOpen = new SimpleBooleanProperty(false);
		queryInProgress = new SimpleBooleanProperty(false);
		queryExhausted = new SimpleBooleanProperty(false);
		
		
		//SETTING DERIVED PROPERTIES
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
		nextSolutionEnabled.bind(Bindings.and(queryTextAvailable, Bindings.not(queryExhausted)));
		allSolutionsEnabled.bind(Bindings.and(queryTextAvailable, Bindings.not(Bindings.or(queryOpen, queryExhausted))));
		isAbortable = new SimpleBooleanProperty(false); //this property will be updated when a query is created
		cancelEnabled.bind(queryOpen.and(Bindings.or(Bindings.not(queryInProgress), isAbortable)));
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
	
	public BooleanProperty queryInProgressProperty() {
		return queryInProgress;
	}
	
	public BooleanProperty nextSolutionEnabledProperty() {
		return nextSolutionEnabled;
	}
	
	public BooleanProperty allSolutionsEnabledProperty() {
		return allSolutionsEnabled;
	}
	
	public BooleanProperty cancelEnabledProperty() {
		return cancelEnabled;
	}
	
	
	public void abort() {
		if(query != null) {
			try {
				if(queryInProgress.get()) {
					query.abort();
				}
			} catch(Exception e) {
				//logger.warn("Impossible to abort running query in Prolog engine " + prologEngine.getName());
			}
		}
	}
	
	public void close() {
		if(query != null) {
			try {
				if(query.isOpen()) {
					query.close();
				}
			} catch(Exception e) {
				//logger.warn("Impossible to close running query in Prolog engine " + prologEngine.getName());
			}
		}
	}
	
	public void forceClose() {
		if(query != null) {
			if(queryInProgress.get()) {
				abort();
			}
			else {
				close();
			}
		}
	}
	
	public void nextSolution() {
		if(query == null)
			query = createQuery();
		query.next();
	}
	
	public void allSolutions() {
		if(query == null)
			query = createQuery();
		query.allSolutions();
	}
	
	
	private ObservableQuery createQuery() {
		String text = queryText.get();
		queryHistory.getValue().add(0, text);
		Query query = prologEngine.query(text);
		ObservableQuery observedQuery = new ObservableQuery(query, listeners);
		isAbortable.set(query.isAbortable());
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
