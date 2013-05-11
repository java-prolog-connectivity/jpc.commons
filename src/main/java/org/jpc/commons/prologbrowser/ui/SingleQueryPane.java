package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_TOOLBAR_CONTAINER;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_TOOLBAR_GROUP_PANE;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.jpc.commons.prologbrowser.model.SingleQueryModel;

/**
 * @author sergioc
 *
 */
public class SingleQueryPane extends VBox {
	
	private SingleQueryModel model;

	private ToolBar toolbarPane;
	private HBox firstRowToolBar;
	private ComboBox<String> history;
	
	private HBox queryButtonsPane;
	private Button allSolutionsButton;
	private Button oneSolutionButton;
	private Button nextSolutionButton;
	private Button cancelQueryButton; //if there is an executing query aborts it
	
	private HBox fileLoaderButtonsPane;
	private Button consultButton;
	private Button ensureLoadedButton;
	private Button logtalkLoadButton;
	
	private HBox editionButtonsPane;
	private Button copyToClipboardButton;
	private Button clearTextButton;
	
	private TextArea queryTextArea;
	
	public SingleQueryPane() {
		draw();
		addListeners();
		disable();
		style();
	}
	
	private void draw() {
		history = new ComboBox<>();
		history.setPromptText("History");
		//history.setPrefWidth(JPC_QUERY_HISTORY_PREFERRED_WIDTH);
		history.setMaxWidth(Double.MAX_VALUE);
		//HBox.setHgrow(history, Priority.SOMETIMES); //to make it grow to use the available horizontal space
		
		queryButtonsPane = new HBox();
		
		Image allSolutionsImage = new Image(getClass().getResourceAsStream("all_solutions.png"));
		allSolutionsButton = new Button("All", new ImageView(allSolutionsImage));
		allSolutionsButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		allSolutionsButton.setTooltip(new Tooltip("All solutions"));
		
		Image oneSolutionImage = new Image(getClass().getResourceAsStream("one_solution.png"));
		oneSolutionButton = new Button("One", new ImageView(oneSolutionImage));
		oneSolutionButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		oneSolutionButton.setTooltip(new Tooltip("One solution"));
		
		Image nextSolutionImage = new Image(getClass().getResourceAsStream("next_solution.png"));
		nextSolutionButton = new Button("Next", new ImageView(nextSolutionImage));
		nextSolutionButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		nextSolutionButton.setTooltip(new Tooltip("One solution"));
		
		Image cancelImage = new Image(getClass().getResourceAsStream("cancel.png"));
		cancelQueryButton = new Button("Cancel", new ImageView(cancelImage));
		cancelQueryButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		cancelQueryButton.setTooltip(new Tooltip("Cancel running query"));
		
		
		fileLoaderButtonsPane = new HBox();
		
		Image consultImage = new Image(getClass().getResourceAsStream("consult.png"));
		consultButton = new Button("Consult", new ImageView(consultImage));
		consultButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		consultButton.setTooltip(new Tooltip("Consult"));
		
		Image ensureLoadedImage = new Image(getClass().getResourceAsStream("ensure_loaded.png"));
		ensureLoadedButton = new Button("Ensure Loaded", new ImageView(ensureLoadedImage));
		ensureLoadedButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		ensureLoadedButton.setTooltip(new Tooltip("Ensure loaded"));
		
		Image logtalk_loadImage = new Image(getClass().getResourceAsStream("logtalk_load.png"));
		logtalkLoadButton = new Button("[⊨]", new ImageView(logtalk_loadImage));
		logtalkLoadButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		logtalkLoadButton.setTooltip(new Tooltip("Logtalk load"));
		
		
		editionButtonsPane = new HBox();
		
		Image copyToClipboardImage = new Image(getClass().getResourceAsStream("clipboard.png"));
		copyToClipboardButton = new Button("Copy", new ImageView(copyToClipboardImage));
		copyToClipboardButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		copyToClipboardButton.setTooltip(new Tooltip("Copy to clipboard"));
		
		Image clearTextImage = new Image(getClass().getResourceAsStream("clear.png"));
		clearTextButton = new Button("Clear", new ImageView(clearTextImage));
		clearTextButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		clearTextButton.setTooltip(new Tooltip("Clear"));
		
		toolbarPane = new ToolBar();

		queryTextArea = new TextArea();
		
		queryButtonsPane.getChildren().addAll(oneSolutionButton, allSolutionsButton, nextSolutionButton, cancelQueryButton);
		editionButtonsPane.getChildren().addAll(copyToClipboardButton, clearTextButton);
		fileLoaderButtonsPane.getChildren().addAll(consultButton, ensureLoadedButton, logtalkLoadButton);
		
		
		//EnsureLoadedPane ensureLoadedPane = new EnsureLoadedPane(prologEngineChoiceModel, prologEngineChoiceModel.prologEngineSelectedProperty(), executor);
		//LogtalkLoadPane logtalkLoadPane = new LogtalkLoadPane(prologEngineChoiceModel, prologEngineChoiceModel.prologEngineSelectedProperty(), executor);
		//loadButtonsPane.getChildren().addAll(ensureLoadedPane, logtalkLoadPane);
		
		//toolbarPane.getItems().addAll(queryButtonsPane, /*history,*/ editionButtonsPane, fileLoaderButtonsPane);

		VBox vBoxToolBar = new VBox();
		firstRowToolBar = new HBox();
		firstRowToolBar.getChildren().addAll(queryButtonsPane, fileLoaderButtonsPane, editionButtonsPane);
		vBoxToolBar.getChildren().addAll(firstRowToolBar, history);
		toolbarPane.getItems().addAll(vBoxToolBar);
		
		getChildren().addAll(toolbarPane, queryTextArea);
	}

	
	public void setModel(SingleQueryModel model) {
		resetModel();
		this.model = model;
		queryTextArea.textProperty().bindBidirectional(model.queryTextProperty());
		queryTextArea.disableProperty().bind(Bindings.not(model.queryTextEditableProperty()));
		history.disableProperty().bind(Bindings.not(model.queryTextEditableProperty()));
		history.itemsProperty().bind(model.queryHistoryProperty());
		clearTextButton.disableProperty().bind(Bindings.not(model.queryTextEditableProperty()));
		nextSolutionButton.disableProperty().bind(Bindings.not(model.nextSolutionEnabledProperty()));
		allSolutionsButton.disableProperty().bind(Bindings.not(model.allSolutionsEnabledProperty()));
		cancelQueryButton.disableProperty().bind(Bindings.not(model.cancelEnabledProperty()));
		copyToClipboardButton.disableProperty().bind(Bindings.not(model.queryTextAvailableProperty()));
	}
	
	public void resetModel() {
		queryTextArea.textProperty().unbindBidirectional(model.queryTextProperty());
		queryTextArea.disableProperty().unbind();
		history.disableProperty().unbind();
		history.itemsProperty().unbind();
		clearTextButton.disableProperty().unbind();
		nextSolutionButton.disableProperty().unbind();
		allSolutionsButton.disableProperty().unbind();
		cancelQueryButton.disableProperty().unbind();
		copyToClipboardButton.disableProperty().unbind();
		model = null;
	}
	
	private void disable() {
		queryTextArea.disableProperty().set(true);
		history.disableProperty().set(true);
		clearTextButton.disableProperty().set(true);
		nextSolutionButton.disableProperty().set(true);
		allSolutionsButton.disableProperty().set(true);
		cancelQueryButton.disableProperty().set(true);
		copyToClipboardButton.disableProperty().set(true);
	}
	
	
	private void addListeners() {
		
		// QUERY BUTTONS
		nextSolutionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.nextSolution();
			}
		});
		
		allSolutionsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.allSolutions();
			}
		});
		
		cancelQueryButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.forceClose();
			}
		});
		
		
		// EDITOR BUTTONS
		
		clearTextButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				queryTextArea.setText("");
			}
		});
		
		copyToClipboardButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String queryText = queryTextArea.getText();
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
			    content.putString(queryText);
			    clipboard.setContent(content);
			}
		});
		
		history.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				queryTextArea.textProperty().set(newValue);
			}
		});
	}
	
	private void style() {
		getStyleClass().addAll(JPC_GRID);
		queryButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		editionButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		fileLoaderButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		//toolbarPane.getStyleClass().add(JPC_TOOLBAR_CONTAINER);
		firstRowToolBar.getStyleClass().add(JPC_TOOLBAR_CONTAINER);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
