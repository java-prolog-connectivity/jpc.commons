package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_QUERY_BROWSER_PANE;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_QUERY_BROWSER_SECTION;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_SECTION;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_TITLE;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.DISOLVING_PANE_ANIMATION_MILLIS;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.jpc.commons.prologbrowser.model.PrologDriverModel;
import org.jpc.commons.prologbrowser.model.PrologEngineOrganizer;
import org.jpc.query.QueryListener;
import org.minitoolbox.fx.FXUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main pane of the query browser
 * @author sergioc
 *
 */
public class QueryBrowserPane extends VBox {

	private static Logger logger = LoggerFactory.getLogger(QueryBrowserPane.class);
	
	private VBox logicConsoleSection;
	private Hyperlink logicConsoleTitle;
	private PrologDriverAndEngineManagerPane logicConsolePane;
	
	private VBox settingsSection;
	private Hyperlink settingsTitle;
	private SettingsPane settingsPane;
	
	private VBox querySection;
	private Hyperlink queryTitle;
	private MultiQueryPane queryPane;

	private ExecutorService executor;
	//This declaration is important. Although the variable is just assigned in the code, it is used as a change listener in a weak set (weak sets do not keep objects marked for garbage collection). 
	//If it would not have been declared as an instance variable it will be garbage collected and the controller will not work after a while.
	private MultiQueryController multiQueryController; 
	private ObservableSet<QueryListener> queryListeners;
	
	public QueryBrowserPane(Application app, Collection<PrologDriverModel> drivers) {
		executor = Executors.newCachedThreadPool();
		queryListeners = FXCollections.observableSet();
		
		settingsSection = new VBox();
		settingsTitle = new Hyperlink("Settings");
		settingsPane = new SettingsPane();
		togglePaneWhenHyperlinkClicked(settingsPane, settingsTitle);
		settingsSection.getChildren().addAll(settingsTitle, settingsPane);
		settingsSection.managedProperty().bind(settingsSection.visibleProperty());
		
		
		logicConsoleSection = new VBox();
		logicConsoleTitle = new Hyperlink("Prolog engines");
		logicConsolePane = new PrologDriverAndEngineManagerPane(drivers, settingsPane.getModel(), app, queryListeners, executor);
		togglePaneWhenHyperlinkClicked(logicConsolePane, logicConsoleTitle);
		logicConsoleSection.getChildren().addAll(logicConsoleTitle, logicConsolePane);
		logicConsoleSection.managedProperty().bind(logicConsoleSection.visibleProperty());
		
		querySection = new VBox();
		queryTitle = new Hyperlink("Query");
		queryPane = new MultiQueryPane();
		multiQueryController = new MultiQueryController(logicConsolePane.getPrologEngineChoiceModel(), queryPane);
		togglePaneWhenHyperlinkClicked(queryPane, queryTitle);
		//queryPane.setPrefWidth(getWidth());
		//queryPane.setMaxWidth(getWidth());
		//queryPane.setMaxWidth(Double.MAX_VALUE);
		querySection.getChildren().addAll(queryTitle, queryPane);
		querySection.managedProperty().bind(querySection.visibleProperty());
		
		getChildren().addAll(logicConsoleSection, settingsSection, querySection);
		setFocusTraversable(true);
		requestFocus();
		style();
	}

	public ObservableSet<QueryListener> queryListenersProperty() {
		return queryListeners;
	}
	
	public void showLogicConsole(boolean b) {
		logicConsoleSection.setVisible(b);
	}
	
	public PrologDriverAndEngineManagerPane getLogicConsolePane() {
		return logicConsolePane;
	}

	public void showSettings(boolean b) {
		settingsSection.setVisible(b);
	}
	
	public SettingsPane getSettingsPane() {
		return settingsPane;
	}

	public void showQuery(boolean b) {
		querySection.setVisible(b);
	}
	
	public MultiQueryPane getQueryPane() {
		return queryPane;
	}

	public ExecutorService getExecutor() {
		return executor;
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
		PrologEngineOrganizer organizer = logicConsolePane.getPrologEngineOrganizer();
//		if(organizer.nonAbortableQueriesInProgress())
//			logger.warn("Some executing queries cannot be stopped."); //TODO ask for confirmation
		if(!organizer.stopAllQueries())
			logger.warn("Some executing queries could not be stopped.");
		organizer.shutdownAll();
		executor.shutdown();
	}
	
	private void style() {
		getStyleClass().add(JPC_QUERY_BROWSER_PANE);
		logicConsoleSection.getStyleClass().add(JPC_QUERY_BROWSER_SECTION);
		settingsSection.getStyleClass().add(JPC_QUERY_BROWSER_SECTION);
		querySection.getStyleClass().add(JPC_QUERY_BROWSER_SECTION);
		logicConsoleTitle.getStyleClass().add(JPC_TITLE);
		logicConsolePane.getStyleClass().add(JPC_SECTION);
		settingsTitle.getStyleClass().add(JPC_TITLE);
		settingsPane.getStyleClass().add(JPC_SECTION);
		queryTitle.getStyleClass().add(JPC_TITLE);
		queryPane.getStyleClass().add(JPC_SECTION);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
