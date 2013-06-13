package org.jpc.commons.prologbrowser.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;


public class MultiQueryPane2 extends HBox {

	private TabPane tabs;
	private Button addButton;
	private Region region;
	
	public MultiQueryPane2() {
		draw();
		addListeners();
	}
	
    private void draw() {
    	setAlignment(Pos.CENTER);
    	setSpacing(5);
    	
    	tabs = new TabPane();
		addButton = new Button("+");
		region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);
		getChildren().addAll(tabs, region, addButton);
		
//		setFillHeight(false);
//		setMinHeight(getHeight());

//		AnchorPane.setTopAnchor(tabs, 5.0);
//	    AnchorPane.setLeftAnchor(tabs, 5.0);
//	    AnchorPane.setRightAnchor(tabs, 5.0);
//	    AnchorPane.setTopAnchor(addButton, 10.0);
//	    AnchorPane.setLeftAnchor(addButton, 10.0);
	    
    }
    
    private void addListeners() {
		addButton.setOnAction(new EventHandler<ActionEvent>() {
		     @Override
		     public void handle(ActionEvent event) {
		       addTab();
		     }
		});
    }
    
    private void addTab() {
    	Tab tab = new Tab("Tab " + (tabs.getTabs().size() + 1));
    	QueryPane singleQueryPane = new QueryPane();
    	tab.setContent(singleQueryPane);
	    tabs.getTabs().add(tab);
	    tabs.getSelectionModel().select(tab);
    }

}
