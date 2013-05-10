package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_TOOLBAR;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_TOOLBAR_GROUP_PANE;
import static org.jpc.commons.prologbrowser.ui.JpcLayout.JPC_QUERY_HISTORY_PREFERRED_WIDTH;

import java.util.concurrent.Executor;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.jpc.commons.prologbrowser.model.QueryModel;

/**
 * @author sergioc
 *
 */
public class QueryPane extends VBox {
	
	
	private Executor executor;
	private QueryModel model;
	
	
	private TextArea queryTextArea;
	
	private ToolBar toolbarPane;
	private ComboBox<String> history;
	
	private HBox queryButtonsPane;
	private Button nextSolutionButton;
	public Button allSolutionsButton;
	private Button cancelQueryButton; //if there is an executing query aborts it
	
	private HBox editionButtonsPane;
	private Button copyToClipboardButton;
	private Button clearTextButton;
	
	private HBox loadButtonsPane;
	
	public QueryPane() {
		//this.executor = executor; //TODO FIX THIS
		draw();
		addListeners();
		disable();
		style();
	}
	
	private void draw() {
		queryTextArea = new TextArea();
		
		history = new ComboBox<>();
		history.setPromptText("History");
		history.setPrefWidth(JPC_QUERY_HISTORY_PREFERRED_WIDTH);
		
		nextSolutionButton = new Button("Next");
		allSolutionsButton = new Button("All Solutions");
		cancelQueryButton = new Button("Cancel");
		
		copyToClipboardButton = new Button("Copy");
		clearTextButton = new Button("Clear");

		toolbarPane = new ToolBar();
		queryButtonsPane = new HBox();
		editionButtonsPane = new HBox();
		loadButtonsPane = new HBox();
		
		
		
		queryButtonsPane.getChildren().addAll(nextSolutionButton, allSolutionsButton, cancelQueryButton);
		editionButtonsPane.getChildren().addAll(copyToClipboardButton, clearTextButton);
		
		
		//EnsureLoadedPane ensureLoadedPane = new EnsureLoadedPane(prologEngineChoiceModel, prologEngineChoiceModel.prologEngineSelectedProperty(), executor);
		//LogtalkLoadPane logtalkLoadPane = new LogtalkLoadPane(prologEngineChoiceModel, prologEngineChoiceModel.prologEngineSelectedProperty(), executor);
		
		loadButtonsPane.getChildren().addAll(ensureLoadedPane, logtalkLoadPane);
		toolbarPane.getItems().addAll(queryButtonsPane, history, editionButtonsPane, loadButtonsPane);

		getChildren().addAll(toolbarPane, queryTextArea);
	}

	
	public void setModel(QueryModel model) {
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
				executor.execute(new Runnable() {
					@Override
					public void run() {
						model.nextSolution();
					}
				});
			}
		});
		
		allSolutionsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						model.allSolutions();
					}
				});
			}
		});
		
		cancelQueryButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						model.forceClose();
					}
				});
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
		loadButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		toolbarPane.getStyleClass().add(JPC_TOOLBAR);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
