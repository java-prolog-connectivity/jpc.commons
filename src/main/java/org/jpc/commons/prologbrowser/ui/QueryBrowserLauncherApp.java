package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CONTAINER;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CUSTOM_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_QUERY_BROWSER_LAUNCHER;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class QueryBrowserLauncherApp extends Application {
	
	private Scene scene;
	private HBox launcherPane;
	private Button launchButton;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Query Browser Launcher");
		launcherPane = new HBox();
		launcherPane.setPrefWidth(250);
		launcherPane.setAlignment(Pos.CENTER);
		launchButton = new Button("Launch");
		launcherPane.getChildren().add(launchButton);
		scene = new Scene(launcherPane);
		
		launchButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		    	Window owner = scene.getWindow();
		    	QueryBrowserStage queryBrowser = new QueryBrowserStage(owner, QueryBrowserLauncherApp.this);
		    	queryBrowser.addStyle(JpcCss.class.getResource(JPC_CUSTOM_CSS_FILE_NAME).toExternalForm());
		    	queryBrowser.show();
		    }
		});
		style();
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void style() {
		launcherPane.getStyleClass().addAll(JPC_QUERY_BROWSER_LAUNCHER, JPC_CONTAINER);
		launchButton.getStyleClass().add(JPC_BUTTON);
		launcherPane.getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
		//launcherPane.getStylesheets().add(JpcLayout.class.getResource(JPC_CUSTOM_CSS_FILE_NAME).toExternalForm());
	}
	
	public static void main(String[] args) {
        launch(args);
    }
	
}
