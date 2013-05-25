package org.jpc.commons.prologbrowser.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.jpc.commons.prologbrowser.model.PrologDriverModel;

public class AboutDriverStage extends Stage {

	private Scene scene;

	public AboutDriverStage(Window owner, Application app, PrologDriverModel driver) {
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
		setResizable(false);
		setTitle("Driver details");
		scene = new Scene(new AboutDriverPane(app, driver));
		setScene(scene);
	}

}
