package org.jpc.commons.prologbrowser.ui;

import java.util.List;
import java.util.Map;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.jpc.engine.logtalk.LogtalkLibrary;
import org.jpc.engine.logtalk.LogtalkLibraryItem;

public class LogtalkLibraryChooserStage extends Stage {

	private Scene scene;
	private LogtalkLibraryChooserPane logtalkLibraryChooserPane;
	
	public LogtalkLibraryChooserStage(Window owner, Map<String, LogtalkLibrary> logtalkLibraries) {
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
		setResizable(false);
		setTitle("Logtalk library browser");
		logtalkLibraryChooserPane = new LogtalkLibraryChooserPane(logtalkLibraries);
		scene = new Scene(logtalkLibraryChooserPane);
		setScene(scene);
	}

	public List<LogtalkLibraryItem> getChosenItems() {
		return logtalkLibraryChooserPane.getChosenItems();
	}
	
}
