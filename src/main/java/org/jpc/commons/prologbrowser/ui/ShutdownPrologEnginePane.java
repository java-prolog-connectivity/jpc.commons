package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON_PANE;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_BUTTON_PROGRESS_INDICATOR;

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

import org.jpc.commons.prologbrowser.model.ShutdownPrologEngineModel;
import org.jpc.engine.listener.PrologEngineShutdownListener;
import org.jpc.engine.provider.PrologEngineProvider;

public class ShutdownPrologEnginePane extends HBox {

	private ShutdownPrologEngineModel model;
	private Button shutdownEngineButton;
	private ProgressIndicator progress;
	private Executor executor;
	
	public ShutdownPrologEnginePane(PrologEngineProvider prologEngineProvider, 
			PrologEngineShutdownListener prologEngineShutdownListener, 
			BooleanProperty enabled,
			final Executor executor) {
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		this.executor = executor;
		shutdownEngineButton = new Button("Shutdown");
		if(enabled != null)
			shutdownEngineButton.disableProperty().bind(Bindings.not(enabled));
		
		model = new ShutdownPrologEngineModel(prologEngineProvider, prologEngineShutdownListener);
		progress = new ProgressIndicator();
		progress.setPrefSize(JPC_BUTTON_PROGRESS_INDICATOR, JPC_BUTTON_PROGRESS_INDICATOR);
		progress.setVisible(false);
		
		shutdownEngineButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				progress.setVisible(true);
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							model.shutdownPrologEngine();
//							try {
//								Thread.sleep(500);
//							} catch (InterruptedException e) {
//								throw new RuntimeException(e);
//							}
						} finally {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									progress.setVisible(false);
								}
							});
						}
					}
				});
			}
		});
		getChildren().addAll(progress, shutdownEngineButton);
		style();
	}
	
	private void style() {
		shutdownEngineButton.getStyleClass().add(JPC_BUTTON);
		getStyleClass().add(JPC_BUTTON_PANE);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
	
	public ShutdownPrologEngineModel getModel() {
		return model;
	}
	
}
