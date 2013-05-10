package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import org.jpc.commons.prologbrowser.model.StartPrologEngineModel;
import org.jpc.engine.prolog.driver.PrologEngineFactory;

public class StartPrologEnginePane extends HBox {

	private StartPrologEngineModel model;
	private Button startEngineButton;
	
	public StartPrologEnginePane(PrologEngineFactory prologEngineFactory) {
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		startEngineButton = new Button("Start");
		model = new StartPrologEngineModel(prologEngineFactory, startEngineButton.disableProperty());
		startEngineButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				model.createPrologEngine();
			}
		});
		getChildren().addAll(startEngineButton);
		style();
	}
	
	private void style() {
		startEngineButton.getStyleClass().add(JPC_BUTTON);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
	
	public StartPrologEngineModel getModel() {
		return model;
	}

}
