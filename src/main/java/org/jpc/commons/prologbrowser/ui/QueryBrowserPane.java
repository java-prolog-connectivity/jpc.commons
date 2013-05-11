package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_QUERY_BROWSER_PANE;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_SECTION;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_TITLE;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.DISOLVING_PANE_ANIMATION_MILLIS;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.jpc.engine.prolog.driver.PrologEngineDriver;
import org.minitoolbox.fx.FXUtil;

/**
 * The main pane of the query browser
 * @author sergioc
 *
 */
public class QueryBrowserPane extends VBox {

	private Hyperlink logicConsoleTitle;
	private PrologDriverAndEngineManagerPane logicConsolePane;
	
	private Hyperlink settingsTitle;
	private SettingsPane settingsPane;
	
	private Hyperlink queryTitle;
	private SingleQueryPane queryPane;

	private ExecutorService executor;
	
	public QueryBrowserPane(Application app, Iterable<PrologEngineDriver> drivers) {
		executor = Executors.newCachedThreadPool();
		
		settingsTitle = new Hyperlink("Settings");
		settingsPane = new SettingsPane();
		togglePaneWhenHyperlinkClicked(settingsPane, settingsTitle);
		
		logicConsoleTitle = new Hyperlink("Prolog engines");
		logicConsolePane = new PrologDriverAndEngineManagerPane(drivers, settingsPane.getModel(), app, executor);
		togglePaneWhenHyperlinkClicked(logicConsolePane, logicConsoleTitle);
		
		queryTitle = new Hyperlink("Query");
		queryPane = new SingleQueryPane();
		togglePaneWhenHyperlinkClicked(queryPane, queryTitle);
		
		getChildren().addAll(settingsTitle, settingsPane, logicConsoleTitle, logicConsolePane, queryTitle, queryPane);
		setFocusTraversable(true);
		requestFocus();
		style();
	}
	
	private void togglePaneWhenHyperlinkClicked(final Pane pane, Hyperlink hyperlink) {
		pane.managedProperty().bind(pane.visibleProperty());
		hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	FXUtil.toggleNode(pane, DISOLVING_PANE_ANIMATION_MILLIS);
            }
        });
	}

	public void stop() {
		logicConsolePane.stop();
		executor.shutdownNow();
	}
	
	private void style() {
		getStyleClass().add(JPC_QUERY_BROWSER_PANE);
		logicConsoleTitle.getStyleClass().add(JPC_TITLE);
		logicConsolePane.getStyleClass().add(JPC_SECTION);
		settingsTitle.getStyleClass().add(JPC_TITLE);
		settingsPane.getStyleClass().add(JPC_SECTION);
		queryTitle.getStyleClass().add(JPC_TITLE);
		queryPane.getStyleClass().add(JPC_SECTION);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
