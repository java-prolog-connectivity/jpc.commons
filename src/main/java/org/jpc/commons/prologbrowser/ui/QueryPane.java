package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_QUERY_STATUS;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_TOOLBAR_CONTAINER;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_TOOLBAR_GROUP_PANE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.jpc.commons.prologbrowser.model.QueryModel;
import org.jpc.engine.logtalk.LogtalkLibrary;
import org.jpc.engine.logtalk.LogtalkLibraryItem;
import org.jpc.resource.LogicResource;
import org.jpc.resource.LogtalkResource;
import org.minitoolbox.fx.FXUtil;

import com.google.common.base.Joiner;

/**
 * @author sergioc
 *
 */
public class QueryPane extends VBox {

	private QueryModel model;

	private BooleanProperty busy;
	private BooleanProperty executingCommand;
	
	private ToolBar toolbarPane;
	//private HBox firstRowToolBar;
	private ComboBox<String> history;
	
	private HBox queryButtonsPane;
	private Button oneSolutionButton;
	private Button allSolutionsButton;
	private Button nextSolutionButton;
	private Button cancelQueryButton;
	
	private Button ensureLoadedButton;
	
	private HBox loaderShortcutsButtonsPane;
	private Button logtalkLoadLibraryButton;
	
	private HBox editionButtonsPane;
	private Button clearTextButton;
	private Button copyToClipboardButton;
	
	private HBox fileButtonsPane;
	private Button openButton;
	private Button saveButton;
	
	
	private TextField status;
	private TextArea queryTextArea;
	
	//private Hyperlink queryResultTitle;
	private QueryResultPane queryResultPane;
	
	
	public QueryPane() {
		draw();
		addListeners();
		style();
		resetModel();
		//disable();
		queryResultPane.managedProperty().bind(queryResultPane.visibleProperty());
	}
	
	public QueryPane(QueryModel model) {
		this();
		setModel(model);
	}
	
	public BooleanProperty showQueryResultProperty() {
		return queryResultPane.visibleProperty();
	}
	
	private void draw() {
		busy = new SimpleBooleanProperty(false);
		executingCommand = new SimpleBooleanProperty(false);
				
		history = new ComboBox<>();
		history.setPromptText("Query history");
		//history.setPrefWidth(JPC_QUERY_HISTORY_PREFERRED_WIDTH);
		history.setMaxWidth(Double.MAX_VALUE);
		
		queryButtonsPane = new HBox();
		
		Image allSolutionsImage = BrowserImage.allSolutionsImage();
		allSolutionsButton = new Button("All", new ImageView(allSolutionsImage));
		allSolutionsButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		allSolutionsButton.setTooltip(new Tooltip("All solutions"));
		
		Image oneSolutionImage = BrowserImage.oneSolutionImage();
		oneSolutionButton = new Button("One", new ImageView(oneSolutionImage));
		oneSolutionButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		oneSolutionButton.setTooltip(new Tooltip("One solution"));
		
		Image nextSolutionImage = BrowserImage.nextSolutionImage();
		nextSolutionButton = new Button("Next", new ImageView(nextSolutionImage));
		nextSolutionButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		nextSolutionButton.setTooltip(new Tooltip("Next solution"));
		
		Image cancelImage = BrowserImage.cancelImage();
		cancelQueryButton = new Button("Cancel", new ImageView(cancelImage));
		cancelQueryButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		cancelQueryButton.setTooltip(new Tooltip("Cancel"));
		
		
		loaderShortcutsButtonsPane = new HBox();

		Image ensureLoadedImage = BrowserImage.ensureLoadedImage();
		ensureLoadedButton = new Button("Ensure Loaded", new ImageView(ensureLoadedImage));
		ensureLoadedButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		ensureLoadedButton.setTooltip(new Tooltip("Ensure loaded"));

		Image logtalkLoadLibraryImage = BrowserImage.logtalkLoadLibraryImage();
		logtalkLoadLibraryButton = new Button("[⊨]", new ImageView(logtalkLoadLibraryImage));
		logtalkLoadLibraryButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		logtalkLoadLibraryButton.setTooltip(new Tooltip("Load Logtalk library"));
		
		
		editionButtonsPane = new HBox();
		
		Image copyToClipboardImage = BrowserImage.clipboardImage();
		copyToClipboardButton = new Button("Copy", new ImageView(copyToClipboardImage));
		copyToClipboardButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		copyToClipboardButton.setTooltip(new Tooltip("Copy"));
		
		Image clearTextImage = BrowserImage.clearImage();
		clearTextButton = new Button("New", new ImageView(clearTextImage));
		clearTextButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		clearTextButton.setTooltip(new Tooltip("New"));
		
		
		fileButtonsPane = new HBox();
		
		Image openImage = BrowserImage.openImage();
		openButton = new Button("Open", new ImageView(openImage));
		openButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		openButton.setTooltip(new Tooltip("Open"));
		
		Image saveImage = BrowserImage.saveImage();
		saveButton = new Button("Save", new ImageView(saveImage));
		saveButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		saveButton.setTooltip(new Tooltip("Save"));
		
		
		
		toolbarPane = new ToolBar();

		queryTextArea = new TextArea();
		
		queryButtonsPane.getChildren().addAll(allSolutionsButton, oneSolutionButton, nextSolutionButton, cancelQueryButton);
		editionButtonsPane.getChildren().addAll(clearTextButton, copyToClipboardButton);
		fileButtonsPane.getChildren().addAll(saveButton, openButton);
		loaderShortcutsButtonsPane.getChildren().addAll(ensureLoadedButton, logtalkLoadLibraryButton);
		
		
		
		//EnsureLoadedPane ensureLoadedPane = new EnsureLoadedPane(prologEngineChoiceModel, prologEngineChoiceModel.prologEngineSelectedProperty(), executor);
		//LogtalkLoadPane logtalkLoadPane = new LogtalkLoadPane(prologEngineChoiceModel, prologEngineChoiceModel.prologEngineSelectedProperty(), executor);
		//loadButtonsPane.getChildren().addAll(ensureLoadedPane, logtalkLoadPane);
		
		toolbarPane.getItems().addAll(queryButtonsPane, loaderShortcutsButtonsPane, editionButtonsPane, fileButtonsPane);
		//toolbarPane.setMaxWidth(Control.USE_PREF_SIZE);
		//HBox.setHgrow(toolbarPane, Priority.NEVER);
//		VBox vBoxToolBar = new VBox();
//		firstRowToolBar = new HBox();
//		firstRowToolBar.getChildren().addAll(queryButtonsPane, fileLoaderButtonsPane, editionButtonsPane);
//		vBoxToolBar.getChildren().addAll(firstRowToolBar, history);
//		toolbarPane.getItems().addAll(vBoxToolBar);

		toolbarPane.setPrefWidth(getWidth());
			
		//toolbarPane.setMaxWidth(Double.MAX_VALUE);
		//toolbarPane.setMaxWidth(getWidth());
		//toolbarPane.setMaxWidth(USE_PREF_SIZE);
		//HBox.setHgrow(toolbarPane, Priority.ALWAYS);		
				
		status = new TextField();
		status.setEditable(false);
		
		
		//queryResultTitle = new Hyperlink("Result");
		queryResultPane = new QueryResultPane();
		
		
		getChildren().addAll(toolbarPane, history, queryTextArea, status, queryResultPane);

		//saveButton.disableProperty().set(true);
		copyToClipboardButton.disableProperty().bind(Bindings.or(queryTextArea.textProperty().isEqualTo(""), queryTextArea.textProperty().isNull()));
		resetModel();
	}
	
	public BooleanProperty busyProperty() {
		return busy;
	}
	
	public void setModel(QueryModel model) {
		resetModel();
		this.model = model;
		queryResultPane.setModel(model.getQueryResultModel());
		busy.unbind();
		busy.bind(Bindings.or(model.queryInProgressProperty(), executingCommand));
		queryTextArea.textProperty().bindBidirectional(model.queryTextProperty());
		queryTextArea.editableProperty().bind(model.queryTextEditableProperty());
		history.itemsProperty().bind(model.getPrologEngineModel().queryHistoryProperty());
		history.disableProperty().bind(Bindings.not(model.queryTextEditableProperty()));
		status.textProperty().bind(model.statusMessageProperty());
		oneSolutionButton.disableProperty().bind(model.oneSolutionDisabledProperty());
		allSolutionsButton.disableProperty().bind(model.allSolutionsDisabledProperty());
		nextSolutionButton.disableProperty().bind(model.nextSolutionDisabledProperty());
		cancelQueryButton.disableProperty().bind(model.cancelDisabledProperty());

		ensureLoadedButton.disableProperty().bind(Bindings.not(model.queryTextEditableProperty()));
		logtalkLoadLibraryButton.disableProperty().bind(Bindings.not(model.queryTextEditableProperty()));
		
		openButton.disableProperty().bind(Bindings.not(model.queryTextEditableProperty()));
		saveButton.disableProperty().set(false);
		clearTextButton.disableProperty().bind(Bindings.or(Bindings.not(model.queryTextAvailableProperty()), Bindings.not(model.queryTextEditableProperty())));
		
	}
	
	public void resetModel() {
		busy.unbind();
		busy.bind(executingCommand);
		if(model != null)
			queryTextArea.textProperty().unbindBidirectional(model.queryTextProperty());
		queryTextArea.editableProperty().unbind();
		history.itemsProperty().unbind();
		history.disableProperty().unbind();
		status.textProperty().unbind();
		
		oneSolutionButton.disableProperty().unbind();
		allSolutionsButton.disableProperty().unbind();
		nextSolutionButton.disableProperty().unbind();
		cancelQueryButton.disableProperty().unbind();

		ensureLoadedButton.disableProperty().unbind();
		logtalkLoadLibraryButton.disableProperty().unbind();
		
		openButton.disableProperty().unbind();
		saveButton.disableProperty().set(true);
		clearTextButton.disableProperty().unbind();
		//copyToClipboardButton.disableProperty().unbind();
		model = null;
		queryResultPane.resetModel();
		disable();
	}
	
	public void disable() {
		queryTextArea.editableProperty().set(false);
		history.disableProperty().set(true);
		oneSolutionButton.disableProperty().set(true);
		allSolutionsButton.disableProperty().set(true);
		nextSolutionButton.disableProperty().set(true);
		cancelQueryButton.disableProperty().set(true);
		ensureLoadedButton.disableProperty().set(true);
		logtalkLoadLibraryButton.disableProperty().set(true);
		openButton.disableProperty().set(true);
		//saveButton.disableProperty().set(true);
		clearTextButton.disableProperty().set(true);
		//copyToClipboardButton.disableProperty().set(true);
	}
	
	
	
//	private List<File> selectPrologFiles() {
//		FileChooser fc = new FileChooser();
//		ExtensionFilter ef = FXUtil.createExtensionFilter("Prolog files", PrologResource.getPrologExtensions());
//		fc.getExtensionFilters().addAll(ef);
//		fc.setTitle("Select Prolog files");
//		//fc.setInitialDirectory(new File(System.getProperty("user.dir") + File.separator));
//		return fc.showOpenMultipleDialog(QueryPane.this.getScene().getWindow());
//	}
//	
//	private List<File> selectLogtalkFiles() {
//		FileChooser fc = new FileChooser();
//		ExtensionFilter ef = FXUtil.createExtensionFilter("Logtalk files", LogtalkResource.getLogtalkExtensions());
//		fc.getExtensionFilters().addAll(ef);
//		fc.setTitle("Select Logtalk files");
//		//fc.setInitialDirectory(new File(System.getProperty("user.dir") + File.separator));
//		return fc.showOpenMultipleDialog(QueryPane.this.getScene().getWindow());
//	}
	
	private List<File> selectPrologOrLogtalkFiles() {
		FileChooser fc = new FileChooser();
		ExtensionFilter ef = FXUtil.createExtensionFilter("Prolog files", LogicResource.getLogicResourceExtensions());
		fc.getExtensionFilters().addAll(ef);
		fc.setTitle("Select Prolog files");
		//fc.setInitialDirectory(new File(System.getProperty("user.dir") + File.separator));
		return fc.showOpenMultipleDialog(QueryPane.this.getScene().getWindow());
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
				model.resetState();
				model.forceClose();
			}
		});
		
		ensureLoadedButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				List<File> selectedFiles = selectPrologOrLogtalkFiles();
				if(selectedFiles != null) {
					queryTextArea.setText(loaderScript(selectedFiles));
					model.oneSolution();
				}
			}
		});
		
		logtalkLoadLibraryButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Executor executor = model.getExecutor();
				executingCommand.set(true);
				executor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							if(LogtalkLibrary.getDefaultLogtalkLibraries() == null) {
								LogtalkLibrary.setDefaultLogtalkLibraries(model.getPrologEngineModel().asLogtalkEngine().getLibraries());
							}
							final Map<String, LogtalkLibrary> logtalkLibraries = LogtalkLibrary.getDefaultLogtalkLibraries();
							FXUtil.runInFXApplicationThread(new Runnable() {
								@Override
								public void run() {
									LogtalkLibraryChooserStage libraryChooser = new LogtalkLibraryChooserStage(getScene().getWindow(), logtalkLibraries);
									libraryChooser.showAndWait();
									List<LogtalkLibraryItem> chosenItems = libraryChooser.getChosenItems();
									if(chosenItems != null) {
										List<String> chosenItemsStrings = new ArrayList<>();
										for(LogtalkLibraryItem logtalkLibraryItem : chosenItems) {
											chosenItemsStrings.add(logtalkLibraryItem.asTerm().toString());
										}
										StringBuilder sb = new StringBuilder();
										sb.append("logtalk_load([");
										sb.append(Joiner.on(", ").join(chosenItemsStrings));
										sb.append("])");
										queryTextArea.setText(sb.toString());
										model.oneSolution();
									}
								}
							});
						} catch(Exception e) {
							model.updateStatus(e.toString());
							e.printStackTrace();
							//System.out.println(e.getStackTrace());
						} finally {
							FXUtil.runInFXApplicationThread(new Runnable() {
								@Override
								public void run() {
									executingCommand.set(false);
								}
							});
						}
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
	
	private String loaderScript(List<File> files) {
		List<String> loadingDirectives = new ArrayList<>();
		for(File file : files) {
			if(LogtalkResource.hasLogtalkExtension(file.getName()))
				loadingDirectives.add("logtalk_load('"+file.getAbsolutePath()+"')");
			else
				loadingDirectives.add("ensure_loaded('"+file.getAbsolutePath()+"')");
		}
		return Joiner.on(", ").join(loadingDirectives);
	}
	
//	private String asFilesListString(List<File> files) {
//		StringBuilder sb = new StringBuilder();
//		List<String> filesNames = new ArrayList<>();
//		for(File file : files) {
//			filesNames.add("'" + file.getAbsolutePath() + "'");
//		}
//		return Joiner.on(", ").join(filesNames);
//	}
	
	private void style() {
		getStyleClass().addAll(JPC_GRID);
		status.getStyleClass().add(JPC_QUERY_STATUS);
		queryButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		loaderShortcutsButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		editionButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		fileButtonsPane.getStyleClass().add(JPC_TOOLBAR_GROUP_PANE);
		toolbarPane.getStyleClass().add(JPC_TOOLBAR_CONTAINER);
		//queryResultTitle.getStyleClass().add(JPC_TITLE);
		//firstRowToolBar.getStyleClass().add(JPC_TOOLBAR_CONTAINER);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
