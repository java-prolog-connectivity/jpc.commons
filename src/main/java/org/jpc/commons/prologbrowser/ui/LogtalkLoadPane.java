package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON_PANE;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_BUTTON_PROGRESS_INDICATOR_SIZE;

import java.io.File;
import java.util.concurrent.Executor;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.jpc.engine.logtalk.LogtalkEngine;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.provider.PrologEngineProvider;

public class LogtalkLoadPane extends HBox {

	private Button logtalkLoadButton;
	private Executor executor;
	
	public LogtalkLoadPane(final PrologEngineProvider prologEngineProvider, 
			BooleanProperty enabled,
			final Executor executor) {
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		this.executor = executor;
		logtalkLoadButton = new Button("Logtalk Load");
		
		if(enabled != null)
			logtalkLoadButton.disableProperty().bind(Bindings.not(enabled));
		
		logtalkLoadButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				ExtensionFilter ef = new ExtensionFilter("Logtalk files (*.lgt)", "*.lgt");
				fc.getExtensionFilters().addAll(ef);
				fc.setTitle("Select Logtalk file");
				//fc.setInitialDirectory(new File(System.getProperty("user.dir") + File.separator));
				File selectedFile = fc.showOpenDialog(LogtalkLoadPane.this.getScene().getWindow());
				if(selectedFile != null) {
					final String fileName = selectedFile.getAbsolutePath();
					//TODO NOTIFY START HERE
					executor.execute(new Runnable() {
						@Override
						public void run() {
							try {
								PrologEngine prologEngine = prologEngineProvider.getPrologEngine();
								if(!new LogtalkEngine(prologEngine).logtalkLoad(fileName))
									throw new RuntimeException("Impossible to load Logtalk file " + fileName);
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
		getChildren().addAll(logtalkLoadButton);
		style();
	}
	
	private void style() {
		logtalkLoadButton.getStyleClass().add(JPC_BUTTON);
		getStyleClass().add(JPC_BUTTON_PANE);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
}
