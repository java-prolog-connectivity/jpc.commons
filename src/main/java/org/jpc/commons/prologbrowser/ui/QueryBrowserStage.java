package org.jpc.commons.prologbrowser.ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * An alternative to the QueryBrowserApp application
 * This class represents a window with a query browser
 * Its purpose is being able to have multiple query browser windows and executing queries at the same time open
 * @author sergioc
 *
 */
public class QueryBrowserStage extends Stage {

	private QueryBrowserScene scene;
	
	public QueryBrowserStage(Window owner, Application app) {
		
		initOwner(owner);
		initModality(Modality.NONE);
		setTitle("Query Browser");
		scene = new QueryBrowserScene(app, null);
		//ScenicView.show(scene);
		setScene(scene);
		this.setOnCloseRequest(new EventHandler<WindowEvent>() {
		      @Override 
		      public void handle(WindowEvent e) {
		    	  scene.stop();
		      }
		});
	}
	
	public void addStyle(String style) {
		scene.getStylesheets().add(style);
	}
	
}
