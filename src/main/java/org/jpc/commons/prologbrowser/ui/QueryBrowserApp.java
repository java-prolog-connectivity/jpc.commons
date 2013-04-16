package org.jpc.commons.prologbrowser.ui;

import javafx.application.Application;
import javafx.stage.Stage;

import com.javafx.experiments.scenicview.ScenicView;

public class QueryBrowserApp extends Application {

	private QueryBrowserScene scene;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Query Browser");
		scene = new QueryBrowserScene(this, null);
		//scene.getStylesheets().add(JpcLayout.class.getResource(JPC_CUSTOM_CSS_FILE_NAME).toExternalForm());
		//ScenicView.show(scene);
		primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
        launch(args);
    }

	@Override
	public void stop() {
		scene.stop();
	}
}
