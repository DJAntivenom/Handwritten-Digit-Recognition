package ch.elste.hdr;

import java.io.IOException;
import java.util.List;

import ch.elste.NeuralNetwork.SupervisedNeuralNetwork;
import ch.elste.hdr.GUI.AlertBox;
import ch.elste.hdr.GUI.SceneMainController;
import ch.elste.hdr.util.NetworkUtils;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	// Variables
	public static final int IMAGE_WIDTH = 28;
	public static final int IMAGE_HEIGHT = 28;
	public static String CURRENT_THEME;
	public static int CURRENT_INDEX;
	public static Thread TRAIN_THREAD;

	private FXMLLoader loader;
	private static SceneMainController controller;
	private BorderPane root;

	public static SupervisedNeuralNetwork CURRENT_NETWORK;

	/**
	 * The current network's current guess.
	 * 
	 * @see #guess
	 */
	@Deprecated
	private static DoubleProperty guessProperty;

	/**
	 * The current network's current guess.
	 */
	private static double guess;
	private static IntegerProperty[] labelProperties;

	/**
	 * The label of the number that is tested at the moment.
	 * 
	 * @see #currentLabel
	 */
	@Deprecated
	private static IntegerProperty currentLabelProperty;

	/**
	 * The label of the number that is tested at the moment.
	 */
	private static int currentLabel;
	private static List<int[][]> images;

	private static Stage primaryStage;
	private Scene scene;
	// End Variables

	@Override
	public void start(Stage primaryStage) {
		setupNetworkLogic();

		Main.primaryStage = primaryStage;

		setupFXML();

		updateCurrentLabel();

		CURRENT_THEME = getClass().getResource("GUI/light.css").toExternalForm();

		scene = new Scene(root);
		scene.getStylesheets().add(CURRENT_THEME);

		Main.primaryStage.setOnCloseRequest(e -> {
			e.consume();
			closeRequest();
		});

		TRAIN_THREAD = new Thread(new NetworkLogicRunnable());
		TRAIN_THREAD.start();

		Main.primaryStage.setScene(scene);
		Main.primaryStage.setTitle("Handwritten Digit Recognition");
		Main.primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Closes the window.
	 */
	private static void close() {
		TRAIN_THREAD.interrupt();
		primaryStage.close();
	}

	/**
	 * Train the network
	 */
	public static void train() {
		TRAIN_THREAD.run();
	}

	/**
	 * Return the scene.
	 * 
	 * @return the scene
	 */
	public Scene getScene() {
		return scene;
	}

	/**
	 * Set the theme of the program.
	 * 
	 * @param theme
	 *            of the program
	 */
	public void setTheme(SceneMainController.Themes theme) {
		CURRENT_THEME = theme.getExternalFilePath();

		scene.getStylesheets().clear();
		scene.getStylesheets().add(CURRENT_THEME);
	}

	/**
	 * Returns the primary stage.
	 * 
	 * @return the primary stage.
	 */
	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Returns the label of the image shown at the moment.
	 * 
	 * @return the label of the image shown.
	 */
	@Deprecated
	public static IntegerProperty getCurrentLabelProperty() {
		return currentLabelProperty;
	}

	public static int getCurrentLabel() {
		return currentLabel;
	}

	/**
	 * Return the value of {@link #currentLabelProperty}.
	 * 
	 * @return the value of {@link #currentLabelProperty}
	 */
	@Deprecated
	public static int getCurrentLabelPropertyValue() {
		return currentLabelProperty.get();
	}

	/**
	 * Update the current label.
	 */
	public static void updateCurrentLabel() {
		currentLabel = labelProperties[CURRENT_INDEX].get();
	}

	/**
	 * Returns the {@link #guessProperty}.
	 * 
	 * @return the {@link #guessProperty}
	 */
	@Deprecated
	public static DoubleProperty guessProperty() {
		return guessProperty;
	}

	/**
	 * Returns the {@link #guess}.
	 * 
	 * @return the {@link #guess}
	 */
	public static double getGuess() {
		return guess;
	}

	/**
	 * Set the value of the {@link #guessProperty}.
	 * 
	 * @param value
	 *            set the guessProperty
	 */
	@Deprecated
	public static void setGuessPropertyValue(double value) {
		guessProperty.set(value);
	}

	/**
	 * Set the value of {@link #guess}.
	 * 
	 * @param guess
	 *            the double to set {@link #guess}
	 */
	public static void setGuess(double guess) {
		Main.guess = guess;
	}

	/**
	 * Returns the image shown at the moment.
	 * 
	 * @return the image shown.
	 */
	public static int[][] getCurrentImage() {
		return images.get(CURRENT_INDEX);
	}

	/**
	 * Increment the index by one
	 * 
	 * @return false if there are no more images
	 */
	public static boolean nextIndex() {
		if (CURRENT_INDEX == labelProperties.length - 1)
			return false;
		else
			CURRENT_INDEX++;

		return true;
	}

	/**
	 * Decrement the index by one
	 * 
	 * @return false if the index is 0
	 */
	public static boolean previousIndex() {
		if (CURRENT_INDEX == 0)
			return false;
		else
			CURRENT_INDEX--;

		return true;
	}

	/**
	 * Request a close of the application.
	 */
	public static void closeRequest() {
		if (NetworkUtils.CHANGED) {
			AlertBox closeBox = new AlertBox("Are you sure you want to close? There are unsaved files.", "Unsaved files");
			if (closeBox.display(Modality.APPLICATION_MODAL, "Yes", "No") == 0)
				close();
		} else {
			close();
		}
	}

	/**
	 * setup the network and stuff
	 */
	private void setupNetworkLogic() {
		CURRENT_NETWORK = new SupervisedNeuralNetwork(.1, (int) Math.round(Math.pow(28, 2)), 10, 16);

		guessProperty = new SimpleDoubleProperty(0d);
		guess = 0d;

		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().clear();
		chooser.getExtensionFilters().add(new ExtensionFilter("label file", ".idx1-ubyte"));
		int[] labelsTemp = MnistReader.getLabels(chooser.showOpenDialog(primaryStage).getAbsolutePath());
		labelProperties = new SimpleIntegerProperty[labelsTemp.length];
		for (int i = 0; i < labelsTemp.length; i++) {
			labelProperties[i] = new SimpleIntegerProperty(labelsTemp[i]);
		}

		currentLabelProperty = new SimpleIntegerProperty(labelProperties[0].get());
		currentLabel = labelProperties[0].get();
		
		chooser.getExtensionFilters().clear();
		chooser.getExtensionFilters().add(new ExtensionFilter("Image file", ".idx3-ubyte"));
		images = MnistReader.getImages(chooser.showOpenDialog(primaryStage).getAbsolutePath());
	}

	/**
	 * setup FXML
	 */
	private void setupFXML() {
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("GUI/SceneMain.fxml"));

		try {
			root = loader.load();
		} catch (IOException e) {
			System.out.println("Boi How:");
			e.printStackTrace();
		}

		controller = loader.getController();
		controller.setOwner(this);
		controller.setup();
	}
}
