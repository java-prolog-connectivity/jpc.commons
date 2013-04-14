package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_CUSTOM_CSS_FILE_NAME;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * An alternative to the QueryBrowserApp application
 * This class represents a window with a query browser
 * Its purpose is being able to have multiple query browser windows and executing queries at the same time open
 * @author sergioc
 *
 */
public class QueryBrowserStage extends Stage {

	public QueryBrowserStage(Window owner, Application app) {
		initOwner(owner);
		initModality(Modality.NONE);
		setTitle("Query Browser");
		Scene scene = new QueryBrowserScene(app, null);
		scene.getStylesheets().add(JpcLayout.class.getResource(JPC_CUSTOM_CSS_FILE_NAME).toExternalForm());
		//ScenicView.show(scene);
		setScene(scene);
	}
	
}
