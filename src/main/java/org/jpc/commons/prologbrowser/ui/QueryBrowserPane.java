package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_QUERY_BROWSER_PANE;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_TITLE;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import org.jpc.commons.prologbrowser.model.DriverAvailabilityStartButtonManager;
import org.jpc.commons.prologbrowser.model.EngineAvailabilityShutdownButtonManager;
import org.jpc.commons.prologbrowser.model.LogtalkProfileConfigurationModel;
import org.jpc.commons.prologbrowser.model.PrologDriverChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologEngineChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologEngineOrganizer;
import org.jpc.engine.prolog.driver.PrologEngineDriver;

public class QueryBrowserPane extends GridPane {

	private Label logicConsoleTitle;
	private ExecutorService executor = Executors.newCachedThreadPool();
	private PrologEngineOrganizer prologEngineOrganizer;
	
	public QueryBrowserPane(Application app, Iterable<PrologEngineDriver> drivers) {
		logicConsoleTitle = new Label("Prolog engine settings");
		PrologDriverChoicePane driverChooserPane = new PrologDriverChoicePane(app, drivers);
		PrologDriverChoiceModel driverChoiceModel = driverChooserPane.getModel();
		LogtalkProfileConfigurationPane profileConfigurationPane = new LogtalkProfileConfigurationPane(driverChoiceModel);
		LogtalkProfileConfigurationModel profileConfigurationModel = profileConfigurationPane.getModel();
		PrologEngineChoicePane prologEngineChoicePane = new PrologEngineChoicePane();
		PrologEngineChoiceModel prologEngineChoiceModel = prologEngineChoicePane.getModel();
		prologEngineOrganizer = new PrologEngineOrganizer(prologEngineChoiceModel, driverChoiceModel); //will register itself as an observer of the driver choice model
		
		executor = Executors.newSingleThreadExecutor();
		DriverAvailabilityStartButtonManager startButtonLifeCycleManager = new DriverAvailabilityStartButtonManager(driverChoiceModel);
		StartPrologEnginePane startPrologEnginePane = new StartPrologEnginePane(profileConfigurationModel, 
				prologEngineChoiceModel, startButtonLifeCycleManager.enabledProperty(), executor);
		
		EngineAvailabilityShutdownButtonManager shutdownButtonLifeCycleManager = new EngineAvailabilityShutdownButtonManager(prologEngineChoiceModel);
		ShutdownPrologEnginePane shutdownPrologEnginePane = new ShutdownPrologEnginePane(prologEngineChoiceModel, 
				prologEngineChoiceModel, shutdownButtonLifeCycleManager.enabledProperty(), executor);
		
		add(logicConsoleTitle, 0, 0);
		add(driverChooserPane, 0, 1, 2, 1);
		add(prologEngineChoicePane, 2, 1);
		add(profileConfigurationPane, 0, 2);
		add(startPrologEnginePane, 1, 2);
		add(shutdownPrologEnginePane, 2, 2);
		driverChoiceModel.selectFirst();
		style();
	}
	
	public void stop() {
		prologEngineOrganizer.shutdownAll();
		executor.shutdownNow();
	}
	
	private void style() {
		logicConsoleTitle.getStyleClass().add(JPC_TITLE);
		getStyleClass().addAll(JPC_QUERY_BROWSER_PANE, JPC_GRID);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
