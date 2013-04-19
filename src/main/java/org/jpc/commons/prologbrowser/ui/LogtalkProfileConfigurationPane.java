package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

import org.jpc.commons.prologbrowser.model.LogtalkProfileConfigurationModel;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.provider.PrologEngineFactoryProvider;

public class LogtalkProfileConfigurationPane extends GridPane  {

	private LogtalkProfileConfigurationModel model;
	private CheckBox logtalkEnabledCheckBox;
	
	public LogtalkProfileConfigurationPane(PrologEngineFactoryProvider<? extends PrologEngine> factoryProvider) {
		logtalkEnabledCheckBox = new CheckBox("Enable Logtalk");
		model = new LogtalkProfileConfigurationModel(factoryProvider);
		logtalkEnabledCheckBox.selectedProperty().bindBidirectional(model.logtalkEnabledProperty());
		add(logtalkEnabledCheckBox, 0, 0);
		style();
	}
	
	public LogtalkProfileConfigurationModel getModel() {
		return model;
	}
	
	private void style() {
		getStyleClass().add(JPC_GRID);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
