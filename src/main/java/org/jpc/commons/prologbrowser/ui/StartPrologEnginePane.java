package org.jpc.commons.prologbrowser.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

import org.jpc.commons.prologbrowser.model.StartPrologEngineModel;
import org.jpc.engine.listener.PrologEngineCreationListener;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.provider.PrologEngineFactoryProvider;

public class StartPrologEnginePane extends HBox {

	private StartPrologEngineModel model;
	private Button startEngineButton;
	private ProgressIndicator progress;
	private ExecutorService executorService;
	
	public StartPrologEnginePane(PrologEngineFactoryProvider<PrologEngineFactory> prologEngineFactoryProvider, PrologEngineCreationListener prologEngineCreationListener) {
		executorService = Executors.newSingleThreadExecutor();
		startEngineButton = new Button("Start");
		startEngineButton.disableProperty().set(true);
		model = new StartPrologEngineModel(startEngineButton.disableProperty(), prologEngineFactoryProvider, prologEngineCreationListener);
		progress = new ProgressIndicator();
		progress.setVisible(false);
		
		startEngineButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				progress.setVisible(true);
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						model.createPrologEngine();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								progress.setVisible(false);
							}
						});
					}
				});
			}
		});
		getChildren().addAll(startEngineButton, progress);
	}
	
	public StartPrologEngineModel getModel() {
		return model;
	}

}

