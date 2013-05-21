package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_QUERY_TAB_HEADER;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PROGRESS_INDICATOR_SIZE;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

import org.jpc.commons.prologbrowser.model.QueryModel;

public class QueryTab extends Tab {

	public static final int MAX_LENGTH_TITLE = 7;
	
	private QueryModel queryModel;
	private Label titleLabel;
	private HBox tabHeader;
	QueryPane singleQueryPane;
	
	public QueryTab() {
		this(null);
	}
	
	public QueryTab(QueryModel queryModel) {
		this.queryModel = queryModel;
		this.titleLabel = new Label();
		singleQueryPane = new QueryPane();
		if(queryModel != null)
			singleQueryPane.setModel(queryModel);
    	setContent(singleQueryPane);
    	configureTitle();
    	resetTitle();
    	style();
	}
	
	public QueryModel getModel() {
		return queryModel;
	}
	
	private void style() {
		tabHeader.getStyleClass().add(JPC_QUERY_TAB_HEADER);
	}
	
	private void configureTitle() {
		tabHeader = new HBox();
		//tabHeader.setAlignment(Pos.BASELINE_CENTER);
		tabHeader.setAlignment(Pos.CENTER_LEFT);
		ProgressIndicator progress = new ProgressIndicator();
		//progress.managedProperty().bind(progress.visibleProperty()); //so it will not use space when it is not visible
		progress.setPrefSize(JPC_PROGRESS_INDICATOR_SIZE, JPC_PROGRESS_INDICATOR_SIZE);
		BooleanProperty progressVisibleProperty = new SimpleBooleanProperty(false);
		if(queryModel != null) {
			progressVisibleProperty = singleQueryPane.busyProperty();
			
			queryModel.queryTextProperty().addListener(new WeakChangeListener<String>(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
					resetTitle();
				}
			}));
		}
		progress.visibleProperty().bind(progressVisibleProperty);
		tabHeader.getChildren().addAll(progress, titleLabel);
		setGraphic(tabHeader);
	}
	
	private void resetTitle() {
		if (queryModel == null || queryModel.queryTextProperty().get().isEmpty())
			setEmptyTitle();
		else {
			String newValue = queryModel.queryTextProperty().get();
			if(newValue.length() <= MAX_LENGTH_TITLE)
				setTitle(newValue);
			else
				setTitle(newValue.substring(0, MAX_LENGTH_TITLE).concat("..."));
		}
	}
	
	private void setEmptyTitle() {
		setTitle("?-");
	}
	
	private void setTitle(String title) {
		titleLabel.textProperty().set(title);
	}

}
