package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_LIST;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_LIST_LABEL;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PREFERRED_HEIGHT_LIST;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PREFERRED_WIDTH_LIST;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import org.jpc.commons.prologbrowser.model.PrologEngineChoiceModel;
import org.jpc.engine.prolog.PrologEngine;

public class PrologEngineChoicePane extends GridPane {

	private PrologEngineChoiceModel model;
	private Label engineInstanceLabel;
	private ListView<PrologEngine> prologEngines;
	
	public PrologEngineChoicePane() {
		draw();
		model = new PrologEngineChoiceModel(prologEngines.selectionModelProperty());
		prologEngines.setItems(model.getPrologEnginesList());
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
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
	
	private void draw() {
		prologEngines = new ListView<>();
		prologEngines.setCellFactory(new Callback<ListView<PrologEngine>, ListCell<PrologEngine>>() {
			@Override
			public ListCell<PrologEngine> call(ListView<PrologEngine> list) {
				return new PrologEngineCell();
			}
		});
		
		engineInstanceLabel = new Label("Prolog Sessions");
		engineInstanceLabel.setMaxWidth(Double.MAX_VALUE);
		engineInstanceLabel.setAlignment(Pos.CENTER);
		
		add(engineInstanceLabel, 0, 0);
		add(prologEngines, 0, 1);
	}
	
	
	private class PrologEngineCell extends ListCell<PrologEngine> {
		@Override protected void updateItem(PrologEngine item, boolean empty) {
			super.updateItem(item, empty);
			setText(item == null ? "" : item.getName());
		}
	}

}
