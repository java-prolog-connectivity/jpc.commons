package org.jpc.commons.prologbrowser.ui;

import java.util.concurrent.ExecutorService;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

import org.jpc.commons.prologbrowser.model.ShutdownPrologEngineModel;

public class ShutdownPrologEnginePane extends HBox {

	private ShutdownPrologEngineModel model;
	private Button shutdownEngineButton;
	private ProgressIndicator progress;
	private ExecutorService executorService;
	
	
	
	public ShutdownPrologEngineModel getModel() {
		return model;
	}
	
}
