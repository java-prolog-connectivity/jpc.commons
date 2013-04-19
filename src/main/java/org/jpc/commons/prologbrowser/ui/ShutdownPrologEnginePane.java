package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_BUTTON;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;

import java.util.concurrent.Executor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import org.jpc.commons.prologbrowser.model.PrologEngineModel;
import org.jpc.engine.listener.PrologEngineShutdownListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.driver.PrologEngineManager;
import org.jpc.engine.provider.PrologEngineProvider;

public class ShutdownPrologEnginePane extends HBox {

	private Button shutdownEngineButton;
	
	public ShutdownPrologEnginePane(final PrologEngineProvider<PrologEngineModel> prologEngineProvider, 
			final PrologEngineManager prologEngineManager,
			final PrologEngineShutdownListener prologEngineShutdownListener, 
			BooleanProperty enabled,
			final Executor executor) {
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		shutdownEngineButton = new Button("Shutdown");
		if(enabled != null)
			shutdownEngineButton.disableProperty().bind(Bindings.not(enabled));
		
		shutdownEngineButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				PrologEngine prologEngine = prologEngineProvider.getPrologEngine();
				prologEngineManager.shutdownPrologEngine(prologEngine);
				if(prologEngineShutdownListener != null)
					prologEngineShutdownListener.onPrologEngineShutdown(prologEngine);
			}
		});
		getChildren().addAll(shutdownEngineButton);
		style();
	}
	
	private void style() {
		shutdownEngineButton.getStyleClass().add(JPC_BUTTON);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

}
