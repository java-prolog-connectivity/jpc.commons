package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_LIST;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_LIST_LABEL;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_PROLOG_ENGINE_ITEM;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PREFERRED_HEIGHT_LIST;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PREFERRED_WIDTH_LIST;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PROGRESS_INDICATOR_SIZE;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import org.jpc.commons.prologbrowser.model.PrologEngineChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologEngineModel;

public class PrologEngineChoicePane extends GridPane {

	private PrologEngineChoiceModel model;
	private Label engineInstanceLabel;
	private ListView<PrologEngineModel> prologEngines;
	
	public PrologEngineChoicePane() {
		draw();
		model = new PrologEngineChoiceModel(prologEngines.getItems(), prologEngines.selectionModelProperty());
		style();
	}
	
	public PrologEngineChoiceModel getModel() {
		return model;
	}
	
	private void style() {
		getStyleClass().addAll(JPC_GRID);
		engineInstanceLabel.getStyleClass().add(JPC_LIST_LABEL);
		prologEngines.getStyleClass().add(JPC_LIST);
		prologEngines.setPrefSize(JPC_PREFERRED_WIDTH_LIST, JPC_PREFERRED_HEIGHT_LIST);
		prologEngines.setMinHeight(Control.USE_PREF_SIZE);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
	
	private void draw() {
		prologEngines = new ListView<>();
		prologEngines.setCellFactory(new Callback<ListView<PrologEngineModel>, ListCell<PrologEngineModel>>() {
			@Override
			public ListCell<PrologEngineModel> call(ListView<PrologEngineModel> list) {
				return new PrologEngineCell();
			}
		});
		
		engineInstanceLabel = new Label("Sessions");
		engineInstanceLabel.setMaxWidth(Double.MAX_VALUE);
		engineInstanceLabel.setAlignment(Pos.CENTER);
		
		add(engineInstanceLabel, 0, 0);
		add(prologEngines, 0, 1);
	}
	
	
	private class PrologEngineCell extends ListCell<PrologEngineModel> {
		@Override protected void updateItem(PrologEngineModel prologEngineModel, boolean empty) {
			super.updateItem(prologEngineModel, empty);
			if(prologEngineModel == null) {
				setText("");
			} else {
				HBox pane = new HBox();
				ProgressIndicator progress = new ProgressIndicator();
				progress.managedProperty().bind(progress.visibleProperty()); //so it will not use space when it is not visible
				progress.setPrefSize(JPC_PROGRESS_INDICATOR_SIZE, JPC_PROGRESS_INDICATOR_SIZE);
				progress.visibleProperty().bind(prologEngineModel.busyProperty());
				Label prologEngineNameText = new Label(prologEngineModel.getName());
				pane.getChildren().addAll(prologEngineNameText, progress);
				pane.getStyleClass().add(JPC_PROLOG_ENGINE_ITEM);
				setGraphic(pane);
			}
			//setText(prologEngineModel == null ? "" : prologEngineModel.getName());
		}
	}

}
