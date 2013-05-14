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
	//private HBox firstRowToolBar;
	private ComboBox<String> history;
	
	private HBox queryButtonsPane;
	private Button oneSolutionButton;
	private Button allSolutionsButton;
	private Button nextSolutionButton;
	private Button cancelQueryButton; //if there is an executing query aborts it
	
	private HBox prologShortcutsButtonsPane;
	private Button consultButton;
	private Button ensureLoadedButton;
	
	private HBox logtalkShortcutsButtonsPane;
	private Button logtalkLoadButton;
	private Button logtalkLoadLibraryButton;
	
	private HBox editionButtonsPane;
	private Button openButton;
	private Button saveButton;
	private Button clearTextButton;
	private Button copyToClipboardButton;
	
	
	private TextArea queryTextArea;
	
	public SingleQueryPane() {
		draw();
		addListeners();
		style();
		disable();
	}
	
	public SingleQueryPane(SingleQueryModel model) {
		this();
		setModel(model);
	}
	
	private void draw() {
		history = new ComboBox<>();
		history.setPromptText("Query history");
		//history.setPrefWidth(JPC_QUERY_HISTORY_PREFERRED_WIDTH);
		history.setMaxWidth(Double.MAX_VALUE);
		
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
		nextSolutionButton.setTooltip(new Tooltip("Next solution"));
		
		Image cancelImage = new Image(getClass().getResourceAsStream("cancel.png"));
		cancelQueryButton = new Button("Cancel", new ImageView(cancelImage));
		cancelQueryButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		cancelQueryButton.setTooltip(new Tooltip("Cancel"));
		
		
		prologShortcutsButtonsPane = new HBox();
		
		Image consultImage = new Image(getClass().getResourceAsStream("consult.png"));
		consultButton = new Button("Consult", new ImageView(consultImage));
		consultButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		consultButton.setTooltip(new Tooltip("Consult"));
		
		Image ensureLoadedImage = new Image(getClass().getResourceAsStream("ensure_loaded.png"));
		ensureLoadedButton = new Button("Ensure Loaded", new ImageView(ensureLoadedImage));
		ensureLoadedButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		ensureLoadedButton.setTooltip(new Tooltip("Ensure loaded"));
		
		
		logtalkShortcutsButtonsPane = new HBox();
		
		Image logtalkLoadImage = new Image(getClass().getResourceAsStream("logtalk_load.png"));
		logtalkLoadButton = new Button("[⊨]", new ImageView(logtalkLoadImage));
		logtalkLoadButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		logtalkLoadButton.setTooltip(new Tooltip("Logtalk load"));
		
		Image logtalkLoadLibraryImage = new Image(getClass().getResourceAsStream("logtalk_load_library.png"));
		logtalkLoadLibraryButton = new Button("[⊨]", new ImageView(logtalkLoadLibraryImage));
		logtalkLoadLibraryButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		logtalkLoadLibraryButton.setTooltip(new Tooltip("Load Logtalk library"));
		
		
		editionButtonsPane = new HBox();
		
		Image openImage = new Image(getClass().getResourceAsStream("open.png"));
		openButton = new Button("Open", new ImageView(openImage));
		openButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		openButton.setTooltip(new Tooltip("Open"));
		
		Image saveImage = new Image(getClass().getResourceAsStream("save.png"));
		saveButton = new Button("Save", new ImageView(saveImage));
		saveButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		saveButton.setTooltip(new Tooltip("Save"));
		
		Image copyToClipboardImage = new Image(getClass().getResourceAsStream("clipboard.png"));
		copyToClipboardButton = new Button("Copy", new ImageView(copyToClipboardImage));
		copyToClipboardButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		copyToClipboardButton.setTooltip(new Tooltip("Copy"));
		
		Image clearTextImage = new Image(getClass().getResourceAsStream("clear.png"));
		clearTextButton = new Button("New", new ImageView(clearTextImage));
		clearTextButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		clearTextButton.setTooltip(new Tooltip("New"));
		
		toolbarPane = new ToolBar();

		queryTextArea = new TextArea();
		
		queryButtonsPane.getChildren().addAll(oneSolutionButton, allSolutionsButton, nextSolutionButton, cancelQueryButton);
		editionButtonsPane.getChildren().addAll(saveButton, openButton, clearTextButton, copyToClipboardButton);
		prologShortcutsButtonsPane.getChildren().addAll(consultButton, ensureLoadedButton);
		logtalkShortcutsButtonsPane.getChildren().addAll(logtalkLoadButton, logtalkLoadLibraryButton);
		
		
		
		//EnsureLoadedPane ensureLoadedPane = new EnsureLoadedPane(prologEngineChoiceModel, prologEngineChoiceModel.prologEngineSelectedProperty(), executor);
		//LogtalkLoadPane logtalkLoadPane = new LogtalkLoadPane(prologEngineChoiceModel, prologEngineChoiceModel.prologEngineSelectedProperty(), executor);
		//loadButtonsPane.getChildren().addAll(ensureLoadedPane, logtalkLoadPane);
		
		toolbarPane.getItems().addAll(queryButtonsPane, prologShortcutsButtonsPane, logtalkShortcutsButtonsPane, editionButtonsPane);
		//toolbarPane.setMaxWidth(Control.USE_PREF_SIZE);
		//HBox.setHgrow(toolbarPane, Priority.NEVER);
		
//		VBox vBoxToolBar = new VBox();
//		firstRowToolBar = new HBox();
//		firstRowToolBar.getChildren().addAll(queryButtonsPane, fileLoaderButtonsPane, editionButtonsPane);
//		vBoxToolBar.getChildren().addAll(firstRowToolBar, history);
//		toolbarPane.getItems().addAll(vBoxToolBar);
		
		getChildren().addAll(toolbarPane, queryTextArea, history);
		
		//toolbarPane.setMaxWidth(USE_PREF_SIZE);
		//HBox.setHgrow(toolbarPane, Priority.NEVER);
	}

	
	public void setModel(SingleQueryModel model) {
		resetModel();
		this.model = model;
		queryTextArea.textProperty().bindBidirectional(model.queryTextProperty());
		queryTextArea.disableProperty().bind(Bindings.not(model.queryTextEditableProperty()));
		history.itemsProperty().bind(model.queryHistoryProperty());
		history.disableProperty().bind(Bindings.not(model.queryTextEditableProperty()));
		
		oneSolutionButton.disableProperty().bind(model.oneSolutionDisabledProperty());
		allSolutionsButton.disableProperty().bind(model.allSolutionsDisabledProperty());
		nextSolutionButton.disableProperty().bind(model.nextSolutionDisabledProperty());
		cancelQueryButton.disableProperty().bind(model.cancelDisabledProperty());
		
		consultButton.disableProperty().bind(model.oneSolutionDisabledProperty());
		ensureLoadedButton.disableProperty().bind(model.oneSolutionDisabledProperty());
		logtalkLoadButton.disableProperty().bind(model.oneSolutionDisabledProperty());
		logtalkLoadLibraryButton.disableProperty().bind(model.oneSolutionDisabledProperty());
		
		openButton.disableProperty().bind(Bindings.or(Bindings.not(model.queryTextAvailableProperty()), Bindings.not(model.queryTextEditableProperty())));
		saveButton.disableProperty().bind(Bindings.not(model.queryTextAvailableProperty()));
		clearTextButton.disableProperty().bind(Bindings.or(Bindings.not(model.queryTextAvailableProperty()), Bindings.not(model.queryTextEditableProperty())));
		copyToClipboardButton.disableProperty().bind(Bindings.not(model.queryTextAvailableProperty()));
	}
	
	public void resetModel() {
		if(model != null)
			queryTextArea.textProperty().unbindBidirectional(model.queryTextProperty());
		queryTextArea.disableProperty().unbind();
		history.itemsProperty().unbind();
		history.disableProperty().unbind();
		
		oneSolutionButton.disableProperty().unbind();
		allSolutionsButton.disableProperty().unbind();
		nextSolutionButton.disableProperty().unbind();
		cancelQueryButton.disableProperty().unbind();
		
		consultButton.disableProperty().unbind();
		ensureLoadedButton.disableProperty().unbind();
		logtalkLoadButton.disableProperty().unbind();
		logtalkLoadLibraryButton.disableProperty().unbind();
		
		openButton.disableProperty().unbind();
		saveButton.disableProperty().unbind();
		clearTextButton.disableProperty().unbind();
		copyToClipboardButton.disableProperty().unbind();
		model = null;
	}
	
	public void disable() {
		queryTextArea.disableProperty().set(true);
		history.disableProperty().set(true);
		oneSolutionButton.disableProperty().set(true);
		allSolutionsButton.disableProperty().set(true);
		nextSolutionButton.disableProperty().set(true);
		cancelQueryButton.disableProperty().set(true);
		consultButton.disableProperty().set(true);
		ensureLoadedButton.disableProperty().set(true);
		logtalkLoadButton.disableProperty().set(true);
		logtalkLoadLibraryButton.disableProperty().set(true);
		openButton.disableProperty().set(true);
		saveButton.disableProperty().set(true);
		clearTextButton.disableProperty().set(true);
		copyToClipboardButton.disableProperty().set(true);
	}
	
	private void addListeners() {
		
		// QUERY BUTTONS
		oneSolutionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.oneSolution();
			}
		});
		
		allSolutionsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.allSolutions();
			}
		});
		
		nextSolutionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.nextSolution();
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
		prologShortcutsButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		logtalkShortcutsButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		editionButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		toolbarPane.getStyleClass().add(JPC_TOOLBAR_CONTAINER);
		//firstRowToolBar.getStyleClass().add(JPC_TOOLBAR_CONTAINER);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
