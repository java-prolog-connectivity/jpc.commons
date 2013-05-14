package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_IMAGE_BUTTON;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import org.jpc.commons.prologbrowser.model.PrologEngineOrganizer;

public class AddDriverPane extends HBox{

	private PrologEngineOrganizer prologEngineOrganizer;
	
	private Button addDriverButton;
	
	public AddDriverPane(PrologEngineOrganizer prologEngineOrganizer) {
		this.prologEngineOrganizer = prologEngineOrganizer;
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		Image startImage = new Image(getClass().getResourceAsStream("add_driver.png"));
		addDriverButton = new Button("Add driver", new ImageView(startImage));
		addDriverButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		addDriverButton.setTooltip(new Tooltip("Add engine driver"));
		addDriverButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//TODO
			}
		});
		getChildren().addAll(addDriverButton);

		style();
	}
	
	private void style() {
		addDriverButton.getStyleClass().add(JPC_IMAGE_BUTTON);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
}
