package org.jpc.commons.prologbrowser.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.Tab;

import org.jpc.commons.prologbrowser.model.SingleQueryModel;

public class SingleQueryTab extends Tab {

	private SingleQueryModel singleQueryModel;
	public final int MAX_LENGTH_TITLE = 7;
	
	public SingleQueryTab() {
		SingleQueryPane singleQueryPane = new SingleQueryPane();
    	setContent(singleQueryPane);
    	setEmptyTitle();
	}
	
	public SingleQueryTab(SingleQueryModel singleQueryModel) {
		this.singleQueryModel = singleQueryModel;
		SingleQueryPane singleQueryPane = new SingleQueryPane();
    	singleQueryPane.setModel(singleQueryModel);
    	setContent(singleQueryPane);
    	configureTitle();
    	setEmptyTitle();
	}
	
	public SingleQueryModel getModel() {
		return singleQueryModel;
	}
	
	private void configureTitle() {
		final StringProperty calculatedTextProperty = new SimpleStringProperty();
		singleQueryModel.queryTextProperty().addListener(new WeakChangeListener<String>(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				if(newValue.isEmpty()) {
					setEmptyTitle();
				} else {
					if(newValue.length() <= MAX_LENGTH_TITLE)
						calculatedTextProperty.set(newValue);
					else
						calculatedTextProperty.set(newValue.substring(0, MAX_LENGTH_TITLE).concat("..."));
					textProperty().bind(calculatedTextProperty);
				}
				
			}
		}));
	}
	
	private void setEmptyTitle() {
		if(textProperty().isBound())
			textProperty().unbind();
		textProperty().set("?-");
	}
}
