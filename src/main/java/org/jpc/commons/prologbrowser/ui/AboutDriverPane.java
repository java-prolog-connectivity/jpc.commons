package org.jpc.commons.prologbrowser.ui;

import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CONTAINER;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_GRID;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.jpc.engine.prolog.driver.PrologEngineDriver;

public class AboutDriverPane extends VBox {

	
	private final PrologEngineDriver driver;
	private Label libraryNameLabel;
	private Label descriptionLabel;
	private GridPane gridPane;
	private HBox footer;
	private Hyperlink licenseLink;
	private Hyperlink websiteLink;
	
	public AboutDriverPane(final Application app, final PrologEngineDriver driver) {
		this.driver = driver;
		setSpacing(20);
		libraryNameLabel = new Label(driver.getLibraryName());
		descriptionLabel = new Label(driver.getDescription());
		

		
		
		gridPane = new GridPane();
		gridPane.add(libraryNameLabel, 0, 0);
		gridPane.add(descriptionLabel, 0, 1, 2, 1);

		licenseLink = new Hyperlink("License");
		//licenseLink.setVisited(false); trying to remove the annoying rectangle surrounding the link but it does not work
		licenseLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	app.getHostServices().showDocument(driver.getLicenseUrl());
            }
        });
		
		websiteLink = new Hyperlink("Site");
		//websiteLink.setVisited(false);
		websiteLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	app.getHostServices().showDocument(driver.getSiteUrl());
            }
        });
		
		footer = new HBox();
		footer.setAlignment(Pos.CENTER);
		footer.setMaxWidth(Double.MAX_VALUE);

		HBox hBoxLicense = new HBox();
		hBoxLicense.setMaxWidth(Double.MAX_VALUE);
		hBoxLicense.setAlignment(Pos.CENTER);
		hBoxLicense.getChildren().addAll(licenseLink);
		
		HBox hBoxWebsite = new HBox();
		hBoxWebsite.setMaxWidth(Double.MAX_VALUE);
		hBoxWebsite.setAlignment(Pos.CENTER);
		hBoxWebsite.getChildren().addAll(websiteLink);
		
		footer.getChildren().addAll(hBoxLicense, hBoxWebsite);
		HBox.setHgrow(hBoxLicense, Priority.SOMETIMES);
		HBox.setHgrow(hBoxWebsite, Priority.SOMETIMES);
		
		getChildren().addAll(gridPane, footer);
		
		style();
		setFocusTraversable(true);
		requestFocus();
	}
	
	private void style() {
		gridPane.getStyleClass().addAll(JPC_GRID, JPC_CONTAINER);
		libraryNameLabel.getStyleClass().add(JpcCss.DRIVER_NAME_LABEL);
		descriptionLabel.getStyleClass().add(JpcCss.DRIVER_DESCRIPTION_LABEL);
		footer.getStyleClass().add(JpcCss.ABOUT_DRIVER_FOOTER);
		licenseLink.getStyleClass().add(JpcCss.ABOUT_DRIVER_FOOTER_LINK);
		websiteLink.getStyleClass().add(JpcCss.ABOUT_DRIVER_FOOTER_LINK);
		getStylesheets().add(JpcCss.class.getResource(JpcCss.JPC_CSS_FILE_NAME).toExternalForm());
	}
	
}
