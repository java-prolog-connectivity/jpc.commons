package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import org.jpc.engine.prolog.driver.PrologEngineFactory;

public class StartPrologEnginePane extends HBox {

	private Button startEngineButton;
	
	public StartPrologEnginePane(final PrologEngineFactory prologEngineFactory, BooleanProperty disabled) {
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		startEngineButton = new Button("Start");
		startEngineButton.disableProperty().bind(disabled);
		startEngineButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				prologEngineFactory.createPrologEngine();
			}
		});
		getChildren().addAll(startEngineButton);
		style();
	}
	
	private void style() {
		startEngineButton.getStyleClass().add(JPC_BUTTON);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}


}
