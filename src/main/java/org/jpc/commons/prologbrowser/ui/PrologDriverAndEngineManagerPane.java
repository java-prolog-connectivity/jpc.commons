package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;

import java.util.concurrent.Executor;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import org.jpc.commons.prologbrowser.model.PrologDriverChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologEngineChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologEngineOrganizer;
import org.jpc.engine.profile.PrologEngineProfileFactory;
import org.jpc.engine.prolog.driver.PrologEngineDriver;

public class PrologDriverAndEngineManagerPane extends VBox {

	private Iterable<PrologEngineDriver> drivers;
	private PrologEngineProfileFactory profileFactory;
	private Application app;
	private Executor executor;
	
	private PrologEngineOrganizer prologEngineOrganizer;
	private PrologEngineChoiceModel prologEngineChoiceModel;
	
	private GridPane driverAndEnginePane;
	
	//private ToolBar toolbarPane;
	//private HBox lifeCycleButtonsPane;
	

	public PrologDriverAndEngineManagerPane(Iterable<PrologEngineDriver> drivers, PrologEngineProfileFactory profileFactory, Application app, Executor executor) {
		this.drivers = drivers;
		this.profileFactory = profileFactory;
		this.app = app;
		this.executor = executor;
		draw();
		style();
	}
	
	public void stop() {
		prologEngineOrganizer.shutdownAll();
	}
	
	private void draw() {
		PrologDriverChoicePane driverChooserPane = new PrologDriverChoicePane(drivers, app);
		PrologDriverChoiceModel driverChoiceModel = driverChooserPane.getModel();
		
		PrologEngineChoicePane prologEngineChoicePane = new PrologEngineChoicePane();
		prologEngineChoiceModel = prologEngineChoicePane.getModel();
		prologEngineOrganizer = new PrologEngineOrganizer(driverChoiceModel, prologEngineChoiceModel, profileFactory, executor); //will register itself as an observer of the driver choice model
		

		BooleanProperty createEngineDisabled = new SimpleBooleanProperty();
		createEngineDisabled.bind(Bindings.not(driverChoiceModel.selectedDriverEnabledProperty()));
		StartPrologEnginePane startPrologEnginePane = new StartPrologEnginePane(prologEngineOrganizer, createEngineDisabled);
		
		BooleanProperty shutdownEngineDisabled = new SimpleBooleanProperty();
		shutdownEngineDisabled.bind(Bindings.not(prologEngineChoiceModel.selectedEngineCloseableProperty()));
		ShutdownPrologEnginePane shutdownPrologEnginePane = new ShutdownPrologEnginePane(prologEngineChoiceModel, prologEngineOrganizer, shutdownEngineDisabled);
		
		driverChoiceModel.selectFirst();


//		toolbarPane = new ToolBar();
//		lifeCycleButtonsPane = new HBox();
//		lifeCycleButtonsPane.getChildren().addAll(startPrologEnginePane, shutdownPrologEnginePane);
//		toolbarPane.getItems().addAll(lifeCycleButtonsPane);
				
	
		driverAndEnginePane = new GridPane();
		driverAndEnginePane.add(driverChooserPane, 0, 0, 2, 1);
		driverAndEnginePane.add(prologEngineChoicePane, 2, 0);
		driverAndEnginePane.add(startPrologEnginePane, 1,1);
		driverAndEnginePane.add(shutdownPrologEnginePane, 2,1);
		
		//getChildren().addAll(toolbarPane, driverAndEnginePane);
		getChildren().addAll(driverAndEnginePane);
	}
	
	private void style() {
		driverAndEnginePane.getStyleClass().addAll(JPC_GRID);
		//lifeCycleButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		//toolbarPane.getStyleClass().add(JPC_TOOLBAR);
		//setSpacing(15);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
	
}
