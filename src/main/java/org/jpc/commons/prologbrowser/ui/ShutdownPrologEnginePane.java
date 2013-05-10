package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import org.jpc.commons.prologbrowser.model.ShutdownPrologEngineModel;
import org.jpc.engine.prolog.driver.PrologEngineManager;
import org.jpc.engine.provider.PrologEngineProvider;

public class ShutdownPrologEnginePane extends HBox {

	private ShutdownPrologEngineModel model;
	private Button shutdownEngineButton;
	
	public ShutdownPrologEnginePane(PrologEngineProvider prologEngineProvider,
			PrologEngineManager prologEngineManager) {
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		shutdownEngineButton = new Button("Shutdown");
		model = new ShutdownPrologEngineModel(prologEngineProvider, prologEngineManager, shutdownEngineButton.disableProperty());		
		shutdownEngineButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				model.shutdownPrologEngine();
			}
		});
		getChildren().addAll(shutdownEngineButton);
		style();
	}
	
	public ShutdownPrologEngineModel getModel() {
		return model;
	}
	
	private void style() {
		shutdownEngineButton.getStyleClass().add(JPC_BUTTON);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
