package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.jpc.commons.prologbrowser.model.PrologDriverChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologDriverModel;
import org.jpc.commons.prologbrowser.model.PrologEngineChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologEngineModel;
import org.jpc.commons.prologbrowser.model.PrologEngineOrganizer;
import org.jpc.engine.profile.PrologEngineProfileFactory;

public class PrologDriverAndEngineManagerPane extends VBox {

	private Collection<PrologDriverModel> drivers;
	private PrologEngineProfileFactory profileFactory;
	private Application app;
	private Executor executor;
	
	private PrologEngineOrganizer prologEngineOrganizer;
	private PrologEngineChoiceModel prologEngineChoiceModel;
	
	private GridPane driverAndEnginePane;
	
	//private ToolBar toolbarPane;
	//private HBox lifeCycleButtonsPane;
	

	public PrologDriverAndEngineManagerPane(Collection<PrologDriverModel> drivers, PrologEngineProfileFactory profileFactory, Application app, Executor executor) {
		this.drivers = drivers;
		this.profileFactory = profileFactory;
		this.app = app;
		this.executor = executor;
		draw();
		style();
	}
	
	public PrologDriverAndEngineManagerPane(Map<PrologDriverModel, List<PrologEngineModel>> driverMap, PrologEngineProfileFactory profileFactory, Application app, Executor executor) {
		this(driverMap.keySet(), profileFactory, app, executor);
		for(Entry<PrologDriverModel, List<PrologEngineModel>> driverEntry : driverMap.entrySet()) {
			PrologDriverModel driver = driverEntry.getKey();
			for(PrologEngineModel prologEngine : driverEntry.getValue()) {
				prologEngineOrganizer.addPrologEngine(driver, prologEngine);
			}
		}
	}
	
	public PrologEngineChoiceModel getPrologEngineChoiceModel() {
		return prologEngineChoiceModel;
	}
	
	public PrologEngineOrganizer getPrologEngineOrganizer() {
		return prologEngineOrganizer;
	}
	
//	public void stop() {
//		prologEngineOrganizer.shutdownAll();
//	}
	
	private void draw() {
		PrologDriverChoicePane driverChooserPane = new PrologDriverChoicePane(drivers, app);
		PrologDriverChoiceModel driverChoiceModel = driverChooserPane.getModel();
		
		PrologEngineChoicePane prologEngineChoicePane = new PrologEngineChoicePane();
		prologEngineChoiceModel = prologEngineChoicePane.getModel();
		prologEngineOrganizer = new PrologEngineOrganizer(driverChoiceModel, prologEngineChoiceModel, profileFactory, executor); //will register itself as an observer of the driver choice model
		AddDriverPane addDriverPane = new AddDriverPane(driverChoiceModel);
		
		BooleanProperty createEngineDisabled = new SimpleBooleanProperty();
		createEngineDisabled.bind(Bindings.not(driverChoiceModel.selectedDriverEnabledProperty()));
		StartPrologEnginePane startPrologEnginePane = new StartPrologEnginePane(prologEngineOrganizer, createEngineDisabled);
		
		BooleanProperty shutdownEngineDisabled = new SimpleBooleanProperty();
		shutdownEngineDisabled.bind(Bindings.not(prologEngineChoiceModel.selectedEngineCloseableProperty()));
		ShutdownPrologEnginePane shutdownPrologEnginePane = new ShutdownPrologEnginePane(prologEngineChoiceModel, shutdownEngineDisabled);
		
		driverChoiceModel.selectFirst();


//		toolbarPane = new ToolBar();
//		lifeCycleButtonsPane = new HBox();
//		lifeCycleButtonsPane.getChildren().addAll(startPrologEnginePane, shutdownPrologEnginePane);
//		toolbarPane.getItems().addAll(lifeCycleButtonsPane);
				
	
		driverAndEnginePane = new GridPane();
		driverAndEnginePane.add(driverChooserPane, 0, 0, 2, 1);
		driverAndEnginePane.add(prologEngineChoicePane, 2, 0);
		
		HBox buttonsPane = new HBox();
		//buttonsPane.setAlignment(Pos.CENTER);
//		buttonsPane.setMaxWidth(Double.MAX_VALUE);
		
		HBox addDriverPaneHBox = new HBox();
		//addDriverPaneHBox.setMaxWidth(Double.MAX_VALUE);
		//addDriverPaneHBox.setAlignment(Pos.CENTER);
		addDriverPaneHBox.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(addDriverPaneHBox, Priority.SOMETIMES);
		addDriverPaneHBox.getChildren().add(addDriverPane);
		
		HBox startPrologEnginePaneHBox = new HBox();
		//startPrologEnginePaneHBox.setMaxWidth(Double.MAX_VALUE);
		//startPrologEnginePaneHBox.setAlignment(Pos.CENTER);
		startPrologEnginePaneHBox.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(startPrologEnginePaneHBox, Priority.SOMETIMES);
		startPrologEnginePaneHBox.getChildren().add(startPrologEnginePane);
		
		HBox shutdownPrologEnginePaneHBox = new HBox();
		//shutdownPrologEnginePaneHBox.setMaxWidth(Double.MAX_VALUE);
		//shutdownPrologEnginePaneHBox.setAlignment(Pos.CENTER);
		shutdownPrologEnginePaneHBox.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(shutdownPrologEnginePaneHBox, Priority.SOMETIMES);
		shutdownPrologEnginePaneHBox.getChildren().add(shutdownPrologEnginePane);
		
		buttonsPane.getChildren().addAll(addDriverPaneHBox, startPrologEnginePaneHBox, shutdownPrologEnginePaneHBox);
		
//		addDriverPane.setMaxWidth(Double.MAX_VALUE);
//		startPrologEnginePane.setMaxWidth(Double.MAX_VALUE);
//		shutdownPrologEnginePane.setMaxWidth(Double.MAX_VALUE);
//
//		HBox.setHgrow(addDriverPane, Priority.SOMETIMES);
//		HBox.setHgrow(startPrologEnginePane, Priority.SOMETIMES);
//		HBox.setHgrow(shutdownPrologEnginePane, Priority.SOMETIMES);
		
		
//		driverAndEnginePane.add(addDriverPane, 0,1);
//		driverAndEnginePane.add(startPrologEnginePane, 1,1);
//		driverAndEnginePane.add(shutdownPrologEnginePane, 2,1);
		
		//getChildren().addAll(toolbarPane, driverAndEnginePane);
		getChildren().addAll(driverAndEnginePane, buttonsPane);
	}
	
	private void style() {
		driverAndEnginePane.getStyleClass().addAll(JPC_GRID);
		//lifeCycleButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		//toolbarPane.getStyleClass().add(JPC_TOOLBAR);
		//setSpacing(15);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
	
}
