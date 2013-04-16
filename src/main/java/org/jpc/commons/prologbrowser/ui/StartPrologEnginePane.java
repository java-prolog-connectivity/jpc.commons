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

import org.jpc.commons.prologbrowser.model.StartPrologEngineModel;
import org.jpc.engine.listener.PrologEngineCreationListener;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.provider.PrologEngineFactoryProvider;
import org.minitoolbox.concurrent.DirectExecutorService;

public class StartPrologEnginePane extends HBox {

	private StartPrologEngineModel model;
	private Button startEngineButton;
	private ProgressIndicator progress;
	private Executor executor;
	
	public StartPrologEnginePane(PrologEngineFactoryProvider<PrologEngineFactory> prologEngineFactoryProvider, 
			PrologEngineCreationListener prologEngineCreationListener, 
			BooleanProperty enabled,
			final Executor executor) {
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		this.executor = executor;
		startEngineButton = new Button("Start");
		if(enabled != null)
			startEngineButton.disableProperty().bind(Bindings.not(enabled));
		
		model = new StartPrologEngineModel(prologEngineFactoryProvider, prologEngineCreationListener);
		progress = new ProgressIndicator();
		progress.setPrefSize(JPC_BUTTON_PROGRESS_INDICATOR, JPC_BUTTON_PROGRESS_INDICATOR);
		progress.setVisible(false);
		
		startEngineButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				progress.setVisible(true);
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							model.createPrologEngine();
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
		getChildren().addAll(progress, startEngineButton);
		style();
	}
	
	private void style() {
		startEngineButton.getStyleClass().add(JPC_BUTTON);
		getStyleClass().add(JPC_BUTTON_PANE);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
	
	public StartPrologEngineModel getModel() {
		return model;
	}

}

