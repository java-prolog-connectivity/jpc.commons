package org.jpc.commons.prologbrowser.ui;

import java.util.concurrent.Executor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.jpc.engine.provider.PrologEngineProvider;

/**
 * @author sergioc
 *
 */
public class QueryPane extends VBox {
	
	private PrologEngineProvider prologEngineProvider;
	private Executor executor;
	
	private Label queryLabel;
	private TextArea queryTextArea;
	
	private Button nextSolutionButton;
	public Button allSolutionsButton;
	private Button stopQueryButton;
	
	private Button copyToClipboardButton;
	private Button clearTextButton;
	

	public QueryPane(PrologEngineProvider prologEngineProvider, BooleanProperty prologEngineAvailable, Executor executor) {
		this.prologEngineProvider = prologEngineProvider;
		draw();
		if(prologEngineAvailable != null) {
			nextSolutionButton.disableProperty().bind(Bindings.not(prologEngineAvailable));
		}
	}
	
	private void draw() {
		queryTextArea = new TextArea();
		VBox vBoxSecondColumn = new VBox();
		vBoxSecondColumn.getChildren().add(queryTextArea);
		nextSolutionButton = new Button("Next");
		allSolutionsButton = new Button("All Solutions");
		clearTextButton = new Button("Clear");
		
		HBox hBoxButtons = new HBox();
		hBoxButtons.setSpacing(10);
		//hBoxButtons.getChildren().addAll(nextSolutionButton, allSolutionsButton, clearTextButton);
		hBoxButtons.getChildren().addAll(allSolutionsButton, clearTextButton);
		vBoxSecondColumn.getChildren().add(hBoxButtons);
		getChildren().add(vBoxSecondColumn);
	}

	private void addListeners() {
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
	}

}
