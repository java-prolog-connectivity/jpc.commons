package org.jpc.commons.prologbrowser.ui;

import static java.util.Arrays.asList;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_CSS_FILE_NAME;
import static org.jpc.commons.prologbrowser.ui.JpcCss.JPC_IMAGE_BUTTON;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.jpc.commons.prologbrowser.model.PrologDriverChoiceModel;
import org.jpc.commons.prologbrowser.model.PrologDriverModel;
import org.jpc.engine.prolog.driver.PrologEngineDriver;
import org.jpc.util.DriverUtil;
import org.minitoolbox.fx.FXUtil;
import org.minitoolbox.reflection.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddDriverPane extends HBox{

	Logger logger = LoggerFactory.getLogger(AddDriverPane.class);
	
	private PrologDriverChoiceModel prologDriverChoiceModel;
	
	private Button addDriverButton;
	
	public AddDriverPane(PrologDriverChoiceModel prologDriverChoiceModel) {
		this.prologDriverChoiceModel = prologDriverChoiceModel;
		
		setMaxWidth(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_RIGHT);
		Image addDriverImage = BrowserImage.addDriverImage();
		addDriverButton = new Button("Add driver", new ImageView(addDriverImage));
		addDriverButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		addDriverButton.setTooltip(new Tooltip("Add engine driver"));
		addDriverButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				ExtensionFilter ef = FXUtil.createExtensionFilter("JPC drivers", asList("jar"));
				fc.getExtensionFilters().addAll(ef);
				fc.setTitle("Select JPC driver");
				//fc.setInitialDirectory(new File(System.getProperty("user.dir") + File.separator));
				List<File> selectedFiles = fc.showOpenMultipleDialog(AddDriverPane.this.getScene().getWindow());
				
				if(selectedFiles != null) {
					for(File selectedFile : selectedFiles) {
						if(ReflectionUtil.isFileLoaded(selectedFile, (URLClassLoader) getClass().getClassLoader())) { //this check is not working
							//logger.warn("The file " + selectedFile.getAbsolutePath() + " is already loaded"); //logs commented out since the check is not working
						} else {
							//logger.info("The file " + selectedFile.getAbsolutePath() + " has not been loaded before. Importing drivers ...");
							Set<PrologDriverModel> drivers = findNewDrivers(selectedFile);
							if(drivers.isEmpty())
								logger.warn("The file " + selectedFile.getAbsolutePath() + " does not contain new JPC drivers");
							else {
								for(PrologDriverModel driver : drivers) {
									AddDriverPane.this.prologDriverChoiceModel.addDriver(driver);
								}
							}
						}
					}
				}
			}
		});
		getChildren().addAll(addDriverButton);
		style();
	}
	
	private Set<PrologDriverModel> findNewDrivers(File file) {
		List<PrologDriverModel> newDrivers = new ArrayList<>();
		URL url = null;
		try {
			url = file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		//URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{url}, getClass().getClassLoader());
		URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{url});
		Collection<URL> urls = Arrays.asList(url);
		Collection<ClassLoader> classLoaders = Arrays.<ClassLoader>asList(urlClassLoader);
		Set<PrologEngineDriver> drivers = DriverUtil.findDrivers(classLoaders, urls);
		//System.out.println("Drivers: " + drivers.size());
		Set<PrologDriverModel> driversModels = new HashSet<>();
		for(PrologEngineDriver driver : drivers) {
			PrologDriverModel driverModel = new PrologDriverModel(driver);
//			System.out.println(driver.getEngineName());
//			System.out.println(driver.getLibraryName());
			driverModel.setName(driver.getLibraryName());
			driversModels.add(driverModel);
		}
		return driversModels;
	}
	
	private void style() {
		addDriverButton.getStyleClass().add(JPC_IMAGE_BUTTON);
		getStylesheets().add(JpcCss.class.getResource(JPC_CSS_FILE_NAME).toExternalForm());
	}

//	private class TestDriver extends PrologDriverModel {
//		public TestDriver() {
//			super(null);
//		}
//		
//		@Override
//		public String getName() {
//			return "RANDOM DRIVER NAME";
//		}
//		
//		@Override
//		public String getEngineName() {
//			return "SWI";
//		}
//		
//		@Override
//		public String getLibraryName() {
//			return "JPL";
//		}
//	}
	
}
