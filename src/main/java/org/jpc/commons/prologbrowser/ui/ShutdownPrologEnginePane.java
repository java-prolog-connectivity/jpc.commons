package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_IMAGE_BUTTON;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineManager;
import org.jpc.engine.provider.PrologEngineProvider;

public class ShutdownPrologEnginePane extends HBox {

	private Button shutdownEngineButton;
	
	public ShutdownPrologEnginePane(final PrologEngineProvider prologEngineProvider, final PrologEngineManager prologEngineManager, BooleanProperty disabled) {
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		Image shutdownImage = new Image(getClass().getResourceAsStream("shutdown.png"));
		shutdownEngineButton = new Button("Shutdown", new ImageView(shutdownImage));
		shutdownEngineButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		shutdownEngineButton.setTooltip(new Tooltip("Shutdown engine"));
		shutdownEngineButton.disableProperty().bind(disabled);
		shutdownEngineButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				PrologEngine prologEngine = prologEngineProvider.getPrologEngine();
				prologEngineManager.shutdownPrologEngine(prologEngine);
			}
		});
		getChildren().addAll(shutdownEngineButton);
		style();
	}

	private void style() {
		shutdownEngineButton.getStyleClass().add(JPC_IMAGE_BUTTON);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
