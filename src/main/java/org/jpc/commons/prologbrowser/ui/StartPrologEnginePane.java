package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import org.jpc.commons.prologbrowser.model.StartPrologEngineModel;
import org.jpc.engine.listener.PrologEngineCreationListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineFactory;

public class StartPrologEnginePane extends HBox {

	private StartPrologEngineModel model;
	private Button startEngineButton;
	
	public StartPrologEnginePane(PrologEngineFactory<PrologEngine> prologEngineFactory, 
			PrologEngineCreationListener prologEngineCreationListener, 
			BooleanProperty enabled) {
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		startEngineButton = new Button("Start");
		if(enabled != null)
			startEngineButton.disableProperty().bind(Bindings.not(enabled));
		
		model = new StartPrologEngineModel(prologEngineFactory, prologEngineCreationListener);

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

