package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_LIST;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_LIST_LABEL;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PREFERRED_HEIGHT_LIST;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_PREFERRED_WIDTH_LIST;

import java.util.Collection;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Callback;

import org.jpc.commons.prologbrowser.model.PrologDriverChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologDriverModel;

/**
 * Allows a user to select a Prolog driver.
 * @author sergioc
 *
 */
public class PrologDriverChoicePane extends GridPane {

	private final PrologDriverChoiceModel model;
	private final Application app; //the application using this pane
	
	private Label engineTypesLabel;
	private Label prologDriversLabel;
	
	private ListView<String> engineTypes;
	private ListView<PrologDriverModel> prologDrivers;
	
	
	public PrologDriverChoicePane(Collection<PrologDriverModel> drivers, Application app) {
		this.app = app;
		draw();
		model = new PrologDriverChoiceModel(drivers, engineTypes.getItems(), engineTypes.selectionModelProperty(), prologDrivers.getItems(), prologDrivers.selectionModelProperty());
		//model.selectFirst(); //it is not a good idea to select the first driver here, since some state listeners may have not been added yet (e.g., a button for starting a Prolog engine)
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
		engineTypes.setMinHeight(Control.USE_PREF_SIZE); //apparently this can be set using css in javafx8, remember to change it when migrating
		prologDriversLabel.getStyleClass().add(JPC_LIST_LABEL);
		prologDrivers.getStyleClass().add(JPC_LIST);
		prologDrivers.setPrefSize(JPC_PREFERRED_WIDTH_LIST, JPC_PREFERRED_HEIGHT_LIST);
		prologDrivers.setMinHeight(Control.USE_PREF_SIZE);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}
	
	private void draw() {
		engineTypes = new ListView<>();
		prologDrivers = new ListView<>();

		prologDrivers.setCellFactory(new Callback<ListView<PrologDriverModel>, ListCell<PrologDriverModel>>() {
			@Override
			public ListCell<PrologDriverModel> call(ListView<PrologDriverModel> list) {
				return new PrologDriverCell();
			}
		});

		engineTypesLabel = new Label("Engines");
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
	
	
	public void disable() {
		engineTypes.setDisable(true);
		prologDrivers.setDisable(true);
	}

	private class PrologDriverCell extends ListCell<PrologDriverModel> {
		
		public PrologDriverCell() {
			setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
			            if(mouseEvent.getClickCount() == 2){
			            	Window owner = getScene().getWindow();
			            	PrologDriverModel driver = ((PrologDriverCell)mouseEvent.getSource()).getItem();
			            	if(driver != null) {
			            		new AboutDriverStage(owner, app, driver).show();
			            	}
			            }
			        }
				}
			});
		}
		
		@Override protected void updateItem(PrologDriverModel item, boolean empty) {
			super.updateItem(item, empty);
			setText(item == null ? "" : item.getName());
		}
	}
	

}
