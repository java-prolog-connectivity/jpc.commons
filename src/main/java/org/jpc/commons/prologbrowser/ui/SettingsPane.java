package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_HBOX;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_VBOX;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.jpc.commons.prologbrowser.model.SettingsModel;
import org.jpc.resource.LogicResource;
import org.minitoolbox.fx.FXUtil;

public class SettingsPane extends VBox {
	
	private SettingsModel model;
	
	//Logtalk controls
	private CheckBox preloadLogtalkCheckBox; 
	
	//Entry file configuration controls
	private HBox entryFileHBox;
	private Label entryFileLabel;
	private TextField entryFilePathText;
	private Button chooseEntryFileButton;
	
	
	public SettingsPane() {
		draw();
		model = new SettingsModel(preloadLogtalkCheckBox.selectedProperty(), entryFilePathText.textProperty());
		style();
	}
	
	public SettingsModel getModel() {
		return model;
	}

	private void draw() {
		preloadLogtalkCheckBox = new CheckBox("Preload Logtalk");
		entryFileLabel = new Label("Prolog Entry File:");
		entryFilePathText = new TextField();
		HBox.setHgrow(entryFilePathText, Priority.SOMETIMES); //to make it grow to use the available horizontal space
		chooseEntryFileButton = new Button("...");
		chooseEntryFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				ExtensionFilter ef = FXUtil.createExtensionFilter("Prolog files", LogicResource.getLogicResourceExtensions());
				fc.getExtensionFilters().addAll(ef);
				fc.setTitle("Select entry file");
				//fc.setInitialDirectory(new File(System.getProperty("user.dir") + File.separator));
				File selectedFile = fc.showOpenDialog(SettingsPane.this.getScene().getWindow());
				if(selectedFile != null) {
					entryFilePathText.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		entryFileHBox = new HBox();
		entryFileHBox.getChildren().addAll(entryFileLabel, entryFilePathText, chooseEntryFileButton);
		getChildren().addAll(entryFileHBox, preloadLogtalkCheckBox);
	}

	private void style() {
		getStyleClass().addAll(JPC_VBOX);
		entryFileHBox.getStyleClass().addAll(JPC_HBOX);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}

