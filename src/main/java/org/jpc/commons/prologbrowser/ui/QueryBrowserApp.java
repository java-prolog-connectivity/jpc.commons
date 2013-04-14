package org.jpc.commons.prologbrowser.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QueryBrowserApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Query Browser");
		Scene scene = new QueryBrowserScene(this, null);
		//scene.getStylesheets().add(JpcLayout.class.getResource(JPC_CUSTOM_CSS_FILE_NAME).toExternalForm());
		//ScenicView.show(scene);
		primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
        launch(args);
    }

}
