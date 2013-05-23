package org.jpc.commons.prologbrowser.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import org.jpc.commons.prologbrowser.model.MultiQueryModel;
import org.jpc.commons.prologbrowser.model.QueryModel;

public class MultiQueryPane extends AnchorPane {

	private TabPane tabPane;
	private Button addButton;
	private MultiQueryModel multiQueryModel;
	private QueryTab defaultEmptyQueryTab;
	
	public MultiQueryPane() {
		draw();
		addListeners();
		reset();
	}
	
    private void draw() {
    	defaultEmptyQueryTab = new QueryTab();
    	tabPane = new TabPane();
		addButton = new Button("+");
		getChildren().addAll(tabPane, addButton);
		AnchorPane.setTopAnchor(tabPane, 5.0);
	    AnchorPane.setLeftAnchor(tabPane, 5.0);
	    //AnchorPane.setLeftAnchor(tabPane, 0.0);
	    AnchorPane.setRightAnchor(tabPane, 5.0);
	    AnchorPane.setTopAnchor(addButton, 10.0);
	    //AnchorPane.setTopAnchor(addButton, 5.0);
	    AnchorPane.setLeftAnchor(addButton, 10.0);
	    //AnchorPane.setLeftAnchor(addButton, 0.0);
    }
    
    private void addListeners() {
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent event) {
				QueryModel singleQueryModel = multiQueryModel.createSingleQueryModel();
				addTab(singleQueryModel);
		     }
		});
    }
    
    public void setMultiQueryModel(MultiQueryModel multiQueryModel) {
    	if(multiQueryModel.equals(this.multiQueryModel)) //this avoid some nasty flicking in the screen
    		return;
    	clear();
    	this.multiQueryModel = multiQueryModel;
    	for(QueryModel singleQueryModel : multiQueryModel.getQueries()) {
    		addTab(singleQueryModel);
    	}
    	addButton.disableProperty().set(false);
    	tabPane.getSelectionModel().select(multiQueryModel.focusedIndexProperty().get());
    	multiQueryModel.focusedIndexProperty().bind(tabPane.getSelectionModel().selectedIndexProperty());
    }

    public void clear() {
    	if(multiQueryModel != null) {
    		if(multiQueryModel.focusedIndexProperty().isBound())
    			multiQueryModel.focusedIndexProperty().unbind();
    		multiQueryModel = null;
    	}
    	tabPane.getTabs().clear();
    	if(addButton.disableProperty().isBound())
    		addButton.disableProperty().unbind();
    	addButton.disableProperty().set(true);
    }
    
    public void reset() {
    	clear();
    	addTab(defaultEmptyQueryTab);
    }
    
    private void addTab(QueryModel singleQueryModel) {
    	addTab(new QueryTab(singleQueryModel));
    }

    private void addTab(QueryTab tab) {
    	ObservableList<Tab> tabs = tabPane.getTabs();
    	tabs.add(tab);
    	tab.setOnClosed(new EventHandler<Event>(){
			@Override
			public void handle(Event event) {
				QueryTab closedTab = (QueryTab) event.getSource();
				multiQueryModel.getQueries().remove(closedTab.getModel());
			}
    	});
    	BooleanProperty queryOpenProperty;
    	if(tab.getModel() != null)
    		queryOpenProperty = tab.getModel().queryOpenProperty();
    	else
    		queryOpenProperty = new SimpleBooleanProperty(false);
	    tab.closableProperty().bind(Bindings.size(tabs).greaterThan(1).and(Bindings.not(queryOpenProperty)));
	    tabPane.getSelectionModel().select(tab);
    }
    
}


