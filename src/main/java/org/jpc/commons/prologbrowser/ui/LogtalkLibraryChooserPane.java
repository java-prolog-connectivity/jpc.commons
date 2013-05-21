package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CONTAINER;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_FOOTER;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_VBOX;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.jpc.commons.prologbrowser.model.LogtalkLibraryTree;
import org.jpc.commons.prologbrowser.model.LogtalkLibraryTree.LogtalkLibraryDirNode;
import org.jpc.commons.prologbrowser.model.LogtalkLibraryTree.LogtalkLibraryFileLeaf;
import org.jpc.commons.prologbrowser.model.LogtalkLibraryTree.LogtalkLibraryTreeRoot;
import org.jpc.commons.prologbrowser.model.OkCancelListener;
import org.jpc.engine.logtalk.LogtalkLibrary;
import org.jpc.engine.logtalk.LogtalkLibraryItem;

public class LogtalkLibraryChooserPane extends VBox {

	private Map<String, LogtalkLibrary> logtalkLibrariesMap;
	private Set<LogtalkLibrary> logtalkLibraries;
	private List<LogtalkLibraryItem> chosenLibraryItems;
	
	private TreeView<LogtalkLibraryTree> logtalkLibrariesTreeView;
	private VBox mainChooserPane;
	private OkCancelPane okCancelPane;
	
	public LogtalkLibraryChooserPane(Map<String, LogtalkLibrary> logtalkLibrariesMap) {
		this.logtalkLibrariesMap = logtalkLibrariesMap;
		logtalkLibraries = new TreeSet<>(new Comparator<LogtalkLibrary>() {
			@Override
			public int compare(LogtalkLibrary l1, LogtalkLibrary l2) {
				return l1.getAlias().compareTo(l2.getAlias());
			}
		});
		logtalkLibraries.addAll(logtalkLibrariesMap.values());
		draw();
		style();
		setFocusTraversable(true);
		requestFocus();
	}
	
	public List<LogtalkLibraryItem> getChosenItems() {
		return chosenLibraryItems;
	}
	
	private void draw() {
		mainChooserPane = new VBox();
		logtalkLibrariesTreeView = new TreeView<>();
		//logtalkLibrariesTreeView.setCellFactory(CheckBoxTreeCell.<LogtalkLibraryTree>forTreeView());    
		logtalkLibrariesTreeView.setCellFactory(new Callback<TreeView<LogtalkLibraryTree>, TreeCell<LogtalkLibraryTree>>() {
			@Override
			public TreeCell<LogtalkLibraryTree> call(TreeView<LogtalkLibraryTree> list) {
				return new LogtalkLibraryCell();
			}
		});
		
		CheckBoxTreeItem<LogtalkLibraryTree> root = new CheckBoxTreeItem<>();
		root.setValue(new LogtalkLibraryTreeRoot());
		logtalkLibrariesTreeView.setRoot(root);
		
		for(LogtalkLibrary logtalkLibrary : logtalkLibraries) {
			CheckBoxTreeItem<LogtalkLibraryTree> libraryDirNode = new CheckBoxTreeItem<>();
			libraryDirNode.setValue(new LogtalkLibraryDirNode(logtalkLibrary));
			root.getChildren().add(libraryDirNode);
			for(LogtalkLibraryItem logtalkLibraryItem : logtalkLibrary.getItems()) {
				CheckBoxTreeItem<LogtalkLibraryTree> libraryFileLeaf = new CheckBoxTreeItem<>();
				libraryFileLeaf.setValue(new LogtalkLibraryFileLeaf(logtalkLibraryItem));
				libraryDirNode.getChildren().add(libraryFileLeaf);
			}
		}
		logtalkLibrariesTreeView.setShowRoot(false);
		
		
		okCancelPane = new OkCancelPane(new OkCancelListener() {
			@Override
			public void onOk() {
				collectSelectedLibraryItems();
				close();
			}

			@Override
			public void onCancel() {
				close();
			}
		});
		mainChooserPane.getChildren().add(logtalkLibrariesTreeView);
		getChildren().addAll(mainChooserPane, okCancelPane);
	}
	
	private void close() {
		Stage stage = (Stage) this.getScene().getWindow();
		stage.close();
	}
	
	private void collectSelectedLibraryItems() {
		List<LogtalkLibraryItem> chosenLibraryItems = new ArrayList<>();
		for(TreeItem<LogtalkLibraryTree> libraryDirNode : logtalkLibrariesTreeView.getRoot().getChildren()) {
			for(TreeItem<LogtalkLibraryTree> libraryFileLeaf : libraryDirNode.getChildren()) {
				CheckBoxTreeItem<LogtalkLibraryTree> libraryFileCheckBoxNode = (CheckBoxTreeItem<LogtalkLibraryTree>) libraryFileLeaf;
				if(libraryFileCheckBoxNode.isSelected()) {
					LogtalkLibraryFileLeaf logtalkLibraryFileLeaf = (LogtalkLibraryFileLeaf) libraryFileCheckBoxNode.getValue();
					chosenLibraryItems.add(logtalkLibraryFileLeaf.getLogtalkLibraryItem());
				}
			}
		}
		if(!chosenLibraryItems.isEmpty())
			this.chosenLibraryItems = chosenLibraryItems;
	}
	
	private class LogtalkLibraryCell extends CheckBoxTreeCell<LogtalkLibraryTree>  {
		@Override 
		public void updateItem(LogtalkLibraryTree logtalkLibraryNode, boolean empty) {
			super.updateItem(logtalkLibraryNode, empty);
			if(logtalkLibraryNode == null) {
				setText("");
				//setGraphic(null);
			} else {
				setText(logtalkLibraryNode.getName());
//				Label label = new Label(logtalkLibraryNode.getName());
//				HBox hBox = new HBox();
//				hBox.getChildren().add(label);
//				setGraphic(hBox);
			}
		}
	}
	
	private void style() {
		getStyleClass().addAll(JPC_VBOX);
		mainChooserPane.getStyleClass().addAll(JPC_CONTAINER);
		okCancelPane.getStyleClass().add(JPC_FOOTER);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
		
		
	}
	
}
