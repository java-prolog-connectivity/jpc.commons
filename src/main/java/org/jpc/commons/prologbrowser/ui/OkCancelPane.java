package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_HBOX;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import org.jpc.commons.prologbrowser.model.OkCancelListener;

public class OkCancelPane extends HBox {

	private OkCancelListener okCancelListener;
	private Button okButton;
	private Button cancelButton;
	
	public OkCancelPane(OkCancelListener okCancelListener) {
		this.okCancelListener = okCancelListener;
		draw();
		style();
	}
	
	private void draw() {
		okButton = new Button("Ok");
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				okCancelListener.onOk();
			}
		});
		
		cancelButton = new Button("Cancel");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				okCancelListener.onCancel();
			}
		});
		
		getChildren().addAll(cancelButton, okButton);
	}
	
	private void style() {
		setAlignment(Pos.CENTER);
		okButton.getStyleClass().add(JPC_BUTTON);
		cancelButton.getStyleClass().add(JPC_BUTTON);
		getStyleClass().add(JPC_HBOX);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
	
}
