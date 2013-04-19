package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;

import java.io.File;
import java.util.concurrent.Executor;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.jpc.commons.prologbrowser.model.PrologEngineModel;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.provider.PrologEngineProvider;

public class EnsureLoadedPane extends HBox {

	private Button ensureLoadedButton;
	
	public EnsureLoadedPane(final PrologEngineProvider<PrologEngineModel> prologEngineProvider, 
			BooleanProperty enabled,
			final Executor executor) {
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		ensureLoadedButton = new Button("Ensure Loaded");
		
		if(enabled != null)
			ensureLoadedButton.disableProperty().bind(Bindings.not(enabled));
		
		ensureLoadedButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				ExtensionFilter ef = new ExtensionFilter("Prolog files (*.pl, *.P, *.lgt)", "*.pl", "*.P", "*.lgt");
				fc.getExtensionFilters().addAll(ef);
				fc.setTitle("Select Logtalk file");
				//fc.setInitialDirectory(new File(System.getProperty("user.dir") + File.separator));
				File selectedFile = fc.showOpenDialog(EnsureLoadedPane.this.getScene().getWindow());
				if(selectedFile != null) {
					final String fileName = selectedFile.getAbsolutePath();
					//TODO NOTIFY START HERE
					executor.execute(new Runnable() {
						@Override
						public void run() {
							try {
								PrologEngine prologEngine = prologEngineProvider.getPrologEngine();
								if(!prologEngine.ensureLoaded(fileName))
									throw new RuntimeException("Impossible to load Prolog file " + fileName);
							} finally {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										//TODO NOTIFY STOP HERE
									}
								});
							}
						}
					});
				}
				
			}
		});
		getChildren().addAll(ensureLoadedButton);
		style();
	}
	
	private void style() {
		ensureLoadedButton.getStyleClass().add(JPC_BUTTON);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
}
