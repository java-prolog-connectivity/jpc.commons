package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_LIST;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_LIST_LABEL;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PREFERRED_HEIGHT_LIST;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PREFERRED_WIDTH_LIST;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Callback;

import org.jpc.commons.prologbrowser.model.PrologDriverChoiceModel;
import org.jpc.engine.prolog.driver.PrologEngineDriver;

/**
 * Allows a user to select a Prolog driver
 * @author sergioc
 *
 */
public class PrologDriverChoicePane extends GridPane {

	private final PrologDriverChoiceModel model;
	private final Application app; //the application using this pane
	
	private Label engineTypesLabel;
	private Label prologDriversLabel;
	
	private ListView<String> engineTypes;
	private ListView<PrologEngineDriver> prologDrivers;
	
	
	public PrologDriverChoicePane(Application app, Iterable<PrologEngineDriver> drivers) {
		this.app = app;
		draw();
		model = new PrologDriverChoiceModel(drivers, engineTypes.selectionModelProperty(), prologDrivers.selectionModelProperty());
		engineTypes.setItems(model.getEnginesNames());
		prologDrivers.setItems(model.getFilteredDrivers());
		//model.selectFirst(); //it is not a good idea to select the first here, since some state listeners may have not been added yet (e.g., a button for starting a Prolog engine)
		style();
	}

	
	
	public PrologDriverChoiceModel getModel() {
		return model;
	}

	private void style() {
		getStyleClass().addAll(JPC_GRID);
		engineTypesLabel.getStyleClass().add(JPC_LIST_LABEL);
		engineTypes.getStyleClass().add(JPC_LIST);
		engineTypes.setPrefSize(JPC_PREFERRED_WIDTH_LIST, JPC_PREFERRED_HEIGHT_LIST);
		prologDriversLabel.getStyleClass().add(JPC_LIST_LABEL);
		prologDrivers.getStyleClass().add(JPC_LIST);
		prologDrivers.setPrefSize(JPC_PREFERRED_WIDTH_LIST, JPC_PREFERRED_HEIGHT_LIST);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
	
	private void draw() {
		engineTypes = new ListView<>();
		prologDrivers = new ListView<>();

		prologDrivers.setCellFactory(new Callback<ListView<PrologEngineDriver>, ListCell<PrologEngineDriver>>() {
			@Override
			public ListCell<PrologEngineDriver> call(ListView<PrologEngineDriver> list) {
				return new PrologEngineConfigurationCell();
			}
		});

		engineTypesLabel = new Label("Engine Type");
		engineTypesLabel.setMaxWidth(Double.MAX_VALUE);
		engineTypesLabel.setAlignment(Pos.CENTER);
		
		prologDriversLabel = new Label("Drivers");
		prologDriversLabel.setMaxWidth(Double.MAX_VALUE);
		prologDriversLabel.setAlignment(Pos.CENTER);
		
		
		add(engineTypesLabel, 0, 0);
		add(engineTypes, 0, 1);
		add(prologDriversLabel, 1, 0);
		add(prologDrivers, 1, 1);
	}
	


	
	
	public void disableEngineConfigurationOptions() {
		engineTypes.setDisable(true);
		prologDrivers.setDisable(true);
	}
	


	private class PrologEngineConfigurationCell extends ListCell<PrologEngineDriver> {
		
		public PrologEngineConfigurationCell() {
			setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
			            if(mouseEvent.getClickCount() == 2){
			            	Window owner = getScene().getWindow();
			            	PrologEngineDriver config = ((PrologEngineConfigurationCell)mouseEvent.getSource()).getItem();
			            	if(config != null) {
			            		//Stage stage = new Stage();
			            		new AboutDriverDialogStage(owner, app, config).show();
			            	}
			            	
			            }
			        }
				}
			});
		}
		
		@Override protected void updateItem(PrologEngineDriver item, boolean empty) {
			super.updateItem(item, empty);
			setText(item == null ? "" : item.getName());
		}
	}
	


}
