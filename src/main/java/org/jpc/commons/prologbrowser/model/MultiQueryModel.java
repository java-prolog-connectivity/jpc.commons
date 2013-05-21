package org.jpc.commons.prologbrowser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;


public class MultiQueryModel implements ListChangeListener<QueryModel> {

	private ObservableList<QueryModel> queries;
	private PrologEngineModel prologEngineModel;
	private BooleanProperty queryInProgress;
	private IntegerProperty focusedIndex;
	private Executor executor;
	
	public MultiQueryModel(PrologEngineModel prologEngineModel, Executor executor) {
		this.prologEngineModel = prologEngineModel;
		this.executor = executor;
		queries = FXCollections.observableArrayList();
		queries.addListener(this);
		queryInProgress = new SimpleBooleanProperty(false);
		focusedIndex = new SimpleIntegerProperty(0);
	}
	
	public QueryModel createSingleQueryModel() {
		QueryModel singleQueryModel = new QueryModel(prologEngineModel, executor);
		queries.add(singleQueryModel);
		return singleQueryModel;
	}
	
	public ObservableList<QueryModel> getQueries() {
		return queries;
	}
	
	public BooleanProperty queryInProgressProperty() {
		return queryInProgress;
	}
	
	public IntegerProperty focusedIndexProperty() {
		return focusedIndex;
	}
	
	private void refreshQueryInProgress() {
		BooleanProperty newQueryInProgress = new SimpleBooleanProperty(false);
		for(QueryModel query : queries) {
			BooleanProperty booleanPropertyAux = new SimpleBooleanProperty();
			booleanPropertyAux.bind(Bindings.or(newQueryInProgress, query.queryInProgressProperty()));
			newQueryInProgress = booleanPropertyAux;
		}
//		if(queryInProgress.isBound())
//			queryInProgress.unbind(); //not sure if this is really necessary
		queryInProgress.bind(newQueryInProgress);
	}
	
	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends QueryModel> change) {
		refreshQueryInProgress();
	}
	
	/**
	 * 
	 * @return true if at least one query is in progress. false otherwise.
	 */
	public boolean isQueryInProgress() {
		return queryInProgress.get();
	}
	
	/**
	 * 
	 * @return true if at least one query is non-abortable and in progress. false otherwise.
	 */
	public boolean isNonAbortableQueryInProgress() {
		for(QueryModel query : queries) {
			if(query.isQueryInProgress() && !query.isAbortable())
				return true;
		}
		return false;
	}
	
	/**
	 * Attempts to stop all the registered queries.
	 * @return true if all the queries were stopped. In-progress non-abortable queries cannot be stopped.
	 */
	public boolean stop() {
		boolean success = true;
		List<QueryModel> closedQueries = new ArrayList<>();
		for(QueryModel query : queries) {
			if(query.isQueryInProgress() && !query.isAbortable()) {
				success = false;
			} else {
				query.forceStop();
				closedQueries.add(query);
			}
		}
		//to avoid a ConcurrentModificationException the closed queries are eliminated from the query list in a second step
		queries.removeAll(closedQueries);
		return success;
	}
	
	public void dispose(QueryModel query) {
		query.forceStop();
		queries.remove(query);
	}
	
}

