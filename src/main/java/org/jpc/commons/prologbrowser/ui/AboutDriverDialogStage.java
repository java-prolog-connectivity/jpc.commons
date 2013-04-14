package org.jpc.commons.prologbrowser.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.jpc.engine.prolog.driver.PrologEngineDriver;

public class AboutDriverDialogStage extends Stage {

	private Scene scene;

	public AboutDriverDialogStage(Window owner, Application app, PrologEngineDriver driver) {
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
		setResizable(false);
		setTitle("Driver details");
		scene = new Scene(new AboutDriverPane(app, driver));
		setScene(scene);
	}

}
