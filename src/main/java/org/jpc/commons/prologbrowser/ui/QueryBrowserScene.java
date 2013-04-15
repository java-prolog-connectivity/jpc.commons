package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CONTAINER;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_QUERY_BROWSER;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

import org.jpc.engine.prolog.driver.PrologEngineDriver;

public class QueryBrowserScene extends Scene {

	private Application app;
	private HBox root;
	private QueryBrowserPane queryBrowserPane;
	
	public QueryBrowserScene(Application app, Iterable<PrologEngineDriver> drivers) {
		super(new HBox());
		this.app = app;
		root = (HBox) this.getRoot();
		queryBrowserPane = new QueryBrowserPane(app, drivers);
		
		root.getChildren().add(queryBrowserPane);
		style();
		
	}
	
	private void style() {
		root.getStyleClass().addAll(JPC_QUERY_BROWSER, JPC_CONTAINER);
		root.getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

	public void stop() {
		queryBrowserPane.stop();
	}
}
