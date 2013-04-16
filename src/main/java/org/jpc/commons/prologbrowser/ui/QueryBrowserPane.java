package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_QUERY_BROWSER_PANE;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_TITLE;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.DISOLVING_PANE_ANIMATION_MILLIS;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.jpc.commons.prologbrowser.model.DriverAvailabilityStartButtonManager;
import org.jpc.commons.prologbrowser.model.EngineAvailabilityShutdownButtonManager;
import org.jpc.commons.prologbrowser.model.LogtalkProfileConfigurationModel;
import org.jpc.commons.prologbrowser.model.PrologDriverChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologEngineChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologEngineOrganizer;
import org.jpc.engine.prolog.driver.PrologEngineDriver;
import org.minitoolbox.fx.FXUtility;

public class QueryBrowserPane extends VBox {

	private Hyperlink logicConsoleTitle;
	private GridPane logicConsolePane;
	
	private Hyperlink fileLoadingTitle;
	private GridPane fileLoadingPane;
	
	private Application app;
	private Iterable<PrologEngineDriver> drivers;
	
	private ExecutorService executor;
	private PrologEngineOrganizer prologEngineOrganizer;
	
	private PrologEngineChoiceModel prologEngineChoiceModel;
	
	public QueryBrowserPane(Application app, Iterable<PrologEngineDriver> drivers) {
		this.app = app;
		this.drivers = drivers;
		executor = Executors.newCachedThreadPool();

		logicConsoleTitle = new Hyperlink("Prolog engine settings");
		logicConsolePane = createLogicConsolePane();
		logicConsolePane.managedProperty().bind(logicConsolePane.visibleProperty());
		logicConsoleTitle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	FXUtility.toggleNode(logicConsolePane, DISOLVING_PANE_ANIMATION_MILLIS);
            }
        });
		
		fileLoadingTitle = new Hyperlink("File loading");
		fileLoadingPane = createFileLoadingPane();
		fileLoadingPane.managedProperty().bind(fileLoadingPane.visibleProperty());
		fileLoadingTitle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	FXUtility.toggleNode(fileLoadingPane, DISOLVING_PANE_ANIMATION_MILLIS);
            }
        });
		
		getChildren().addAll(logicConsoleTitle, logicConsolePane, fileLoadingTitle, fileLoadingPane);
		this.setSpacing(10);
		setFocusTraversable(true);
		requestFocus();
		style();
	}
	
	private GridPane createLogicConsolePane() {
		PrologDriverChoicePane driverChooserPane = new PrologDriverChoicePane(app, drivers);
		PrologDriverChoiceModel driverChoiceModel = driverChooserPane.getModel();
		LogtalkProfileConfigurationPane profileConfigurationPane = new LogtalkProfileConfigurationPane(driverChoiceModel);
		LogtalkProfileConfigurationModel profileConfigurationModel = profileConfigurationPane.getModel();
		PrologEngineChoicePane prologEngineChoicePane = new PrologEngineChoicePane();
		prologEngineChoiceModel = prologEngineChoicePane.getModel();
		prologEngineOrganizer = new PrologEngineOrganizer(prologEngineChoiceModel, driverChoiceModel); //will register itself as an observer of the driver choice model
		
		executor = Executors.newSingleThreadExecutor();
		DriverAvailabilityStartButtonManager startButtonLifeCycleManager = new DriverAvailabilityStartButtonManager(driverChoiceModel);
		StartPrologEnginePane startPrologEnginePane = new StartPrologEnginePane(profileConfigurationModel, 
				prologEngineChoiceModel, startButtonLifeCycleManager.enabledProperty(), executor);
		
		EngineAvailabilityShutdownButtonManager shutdownButtonLifeCycleManager = new EngineAvailabilityShutdownButtonManager(prologEngineChoiceModel);
		ShutdownPrologEnginePane shutdownPrologEnginePane = new ShutdownPrologEnginePane(prologEngineChoiceModel, 
				prologEngineChoiceModel, shutdownButtonLifeCycleManager.enabledProperty(), executor);
		
		driverChoiceModel.selectFirst();
		
		GridPane pane = new GridPane();
		pane.add(driverChooserPane, 0, 0, 2, 1);
		pane.add(prologEngineChoicePane, 2, 0);
		pane.add(profileConfigurationPane, 0, 1);
		pane.add(startPrologEnginePane, 1, 1);
		pane.add(shutdownPrologEnginePane, 2, 1);
		
		return pane;
	}
	
	private GridPane createFileLoadingPane() {
		LogtalkLoadPane logtalkLoadPane = new LogtalkLoadPane(prologEngineChoiceModel, prologEngineChoiceModel.prologEngineSelectedProperty(), executor);
		EnsureLoadedPane ensureLoadedPane = new EnsureLoadedPane(prologEngineChoiceModel, prologEngineChoiceModel.prologEngineSelectedProperty(), executor);
		GridPane pane = new GridPane();
		pane.add(ensureLoadedPane, 0, 0);
		pane.add(logtalkLoadPane, 1,0);
		return pane;
	}
	
	
	public void stop() {
		prologEngineOrganizer.shutdownAll();
		executor.shutdownNow();
	}
	
	private void style() {
		getStyleClass().add(JPC_QUERY_BROWSER_PANE);
		logicConsoleTitle.getStyleClass().add(JPC_TITLE);
		logicConsolePane.getStyleClass().addAll(JPC_GRID);
		fileLoadingTitle.getStyleClass().add(JPC_TITLE);
		fileLoadingPane.getStyleClass().addAll(JPC_GRID);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
