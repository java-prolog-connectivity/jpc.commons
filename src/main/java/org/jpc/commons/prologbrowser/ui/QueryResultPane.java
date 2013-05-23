package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PREFERRED_HEIGHT_SOLUTION_TABLE;

import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import org.jpc.commons.prologbrowser.model.QueryResultModel;
import org.jpc.term.Term;

public class QueryResultPane extends VBox {

	private TableView<Map<String, Term>> tableView;
	private QueryResultModel queryResultModel;
	
	public QueryResultPane() {
		draw();
	}

	private void draw() {
		tableView = new TableView<>();
		tableView.setPlaceholder(new Label("No results to show"));
		tableView.setPrefHeight(JPC_PREFERRED_HEIGHT_SOLUTION_TABLE);
		//tableView.setMaxHeight(Double.MAX_VALUE);
		//VBox.setVgrow(tableView, Priority.ALWAYS);
		getChildren().addAll(tableView);
	}
	
	public void setModel(QueryResultModel queryResultModel) {
		this.queryResultModel = queryResultModel;
		Bindings.bindContent(tableView.getColumns(), queryResultModel.getColumns());
		Bindings.bindContent(tableView.getItems(), queryResultModel.getQueryResult());
		
		tableView.getColumns().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable o) {
				int size = ((ObservableList)o).size();
				if(size == 0) {
//					setVisible(false);
//					setVisible(true);
//					System.out.println(((ObservableList)o).size());
//					tableView.setVisible(false);
//					tableView.setVisible(true);
//					tableView.layout();
//					tableView.requestLayout();
				}
				
				
				
			}
			
		});
	}

	public void resetModel() {
		if(queryResultModel != null) {
			Bindings.unbindContent(tableView.getItems(), queryResultModel.getQueryResult());
			tableView.getItems().clear();
			Bindings.unbindContent(tableView.getColumns(), queryResultModel.getColumns());
			tableView.getColumns().clear();
			this.queryResultModel = null;
		}
		
	}

}
