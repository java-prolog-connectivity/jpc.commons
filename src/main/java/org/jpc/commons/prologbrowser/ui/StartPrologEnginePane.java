package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_IMAGE_BUTTON;

import java.util.concurrent.Executor;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.minitoolbox.fx.FXUtil;

public class StartPrologEnginePane extends HBox {

	private Button startEngineButton;
	
	public StartPrologEnginePane(final PrologEngineFactory prologEngineFactory, BooleanProperty disabled) {
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		Image startImage = BrowserImage.startImage();
		startEngineButton = new Button("Start", new ImageView(startImage));
		startEngineButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		startEngineButton.setTooltip(new Tooltip("Start engine"));
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
		startEngineButton.getStyleClass().add(JPC_IMAGE_BUTTON);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
