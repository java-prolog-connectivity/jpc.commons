package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_GRID;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_QUERY_BROWSER_PANE;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_TITLE;
import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import org.jpc.commons.prologbrowser.model.DriverBasedPrologEnginePresenter;
import org.jpc.commons.prologbrowser.model.LogtalkProfileConfigurationModel;
import org.jpc.commons.prologbrowser.model.PrologDriverChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologEngineChoiceModel;
import org.jpc.commons.prologbrowser.model.StartPrologEngineModel;
import org.jpc.engine.prolog.driver.PrologEngineDriver;

public class QueryBrowserPane extends GridPane {

	private Label logicConsoleTitle;
	
	public QueryBrowserPane(Application app, Iterable<PrologEngineDriver> drivers) {
		logicConsoleTitle = new Label("Prolog engine settings");
		PrologDriverChoicePane driverChooserPane = new PrologDriverChoicePane(app, drivers);
		PrologDriverChoiceModel driverChoiceModel = driverChooserPane.getModel();
		LogtalkProfileConfigurationPane profileConfigurationPane = new LogtalkProfileConfigurationPane(driverChoiceModel);
		LogtalkProfileConfigurationModel profileConfigurationModel = profileConfigurationPane.getModel();
		
		
		PrologEngineChoicePane prologEngineChoicePane = new PrologEngineChoicePane();
		PrologEngineChoiceModel prologEngineChoiceModel = prologEngineChoicePane.getModel();
		DriverBasedPrologEnginePresenter prologEngineManager = new DriverBasedPrologEnginePresenter(prologEngineChoiceModel.getPrologEngines(), driverChoiceModel.getDrivers(), driverChoiceModel);
		StartPrologEnginePane startPrologEnginePane = new StartPrologEnginePane(profileConfigurationModel, prologEngineChoiceModel);
		StartPrologEngineModel startPrologEngineModel = startPrologEnginePane.getModel();
		driverChoiceModel.addDriverSelectionObserver(startPrologEngineModel);
		add(logicConsoleTitle, 0, 0);
		add(driverChooserPane, 0, 1, 2, 1);
		add(prologEngineChoicePane, 2, 1);
		add(profileConfigurationPane, 0, 2);
		add(startPrologEnginePane, 1, 2);
		//driverChoiceModel.selectFirst();
		style();
	}
	
	private void style() {
		logicConsoleTitle.getStyleClass().add(JPC_TITLE);
		getStyleClass().addAll(JPC_QUERY_BROWSER_PANE, JPC_GRID);
		getStylesheets().add(JpcLayout.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
}
