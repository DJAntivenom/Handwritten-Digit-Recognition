package ch.elste.hdr.GUI;

import java.io.IOException;
import java.net.URL;

import org.jdom2.JDOMException;

import ch.elste.NeuralNetwork.SupervisedNeuralNetwork;
import ch.elste.NeuralNetwork.exceptions.NoHiddenWeightException;
import ch.elste.hdr.Main;
import ch.elste.hdr.IO.IODataLoader;
import ch.elste.hdr.IO.exceptions.FileNotSpecifiedException;
import ch.elste.hdr.util.NetworkUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;

/**
 * The controller of the main stage.
 * 
 * @author Dillon
 *
 */
public class SceneMainController extends Controller {
	// Variables

	@FXML
	private Canvas numberCanvas;

	@FXML
	private Canvas inputCanvas;

	@FXML
	private Label numberValueLabel;

	@FXML
	private Label numberValueLabelCopy;

	@FXML
	private Label networkGuessLabel;

	@FXML
	private Label networkErrorLabel;

	@FXML
	private Label errorRateValueLabel;

	@FXML
	private RadioMenuItem themeChoosableLight;

	@FXML
	private RadioMenuItem themeChoosableDark;

	@FXML
	private CheckBox numberDrawable;

	@FXML
	private Label nodeCountInput;

	@FXML
	private Label nodeCountHidden;

	@FXML
	private Label nodeCountOutput;

	@FXML
	private Label triesCountTotal;

	@FXML
	private Label triesCountTotal1;

	@FXML
	private Button animationPlayButton;

	@FXML
	private Button animationNextButton;

	@FXML
	private Button animationPrevButton;
	
	@FXML
	private TitledPane infoPane;

	// End Variables
	public SceneMainController() {
		try {
			loader = new IODataLoader();
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void newFile() {
		NewFileBox box = new NewFileBox("", "New File");
		String str = box.display("New File", Modality.WINDOW_MODAL);
		if (str != null && !str.equals("!")) {
			double learningRate = Double.parseDouble(str.split("!")[0]);
			str = str.split("!")[1];
			String[] strs = str.split("\\s*,+\\s*");
			int[] ints = new int[strs.length];
			for (int i = 0; i < strs.length; i++) {
				ints[i] = Integer.parseInt(strs[i]);
			}

			Main.CURRENT_NETWORK = new SupervisedNeuralNetwork(learningRate, (int) Math.round(Math.pow(28, 2)), 10,
					ints);
			setup();
		}
	}

	@FXML
	public void newDefault() {
		Main.CURRENT_NETWORK = new SupervisedNeuralNetwork(.1, (int) Math.round(Math.pow(28, 2)), 10, 16);
		setup();
	}

	@FXML
	public void save() {
		try {
			loader.save(Main.CURRENT_NETWORK.getInputWeights(), Main.CURRENT_NETWORK.getInputNodeSize(),
					Main.CURRENT_NETWORK.getHiddenWeights(), Main.CURRENT_NETWORK.getHiddenNodeSizes(),
					Main.CURRENT_NETWORK.getOutputWeights(), Main.CURRENT_NETWORK.getOutputNodeSize(),
					Main.CURRENT_NETWORK.getErrorRate(), Main.CURRENT_NETWORK.getLearningrate(),
					Main.CURRENT_NETWORK.getTotalLearningIterations());
		} catch (FileNotSpecifiedException e) {
			saveAs();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void openInfo() {
		if(!infoPane.isExpanded())
			infoPane.setExpanded(true);
	}

	@FXML
	public void saveAs() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("XML", "*.xml"));

		java.io.File location = chooser.showSaveDialog(Main.getPrimaryStage());

		if (location != null)
			loader.setSaveLocation(location.getAbsolutePath());
		else
			return;

		try {
			loader.save(Main.CURRENT_NETWORK.getInputWeights(), Main.CURRENT_NETWORK.getInputNodeSize(),
					Main.CURRENT_NETWORK.getHiddenWeights(), Main.CURRENT_NETWORK.getHiddenNodeSizes(),
					Main.CURRENT_NETWORK.getOutputWeights(), Main.CURRENT_NETWORK.getOutputNodeSize(),
					Main.CURRENT_NETWORK.getErrorRate(), Main.CURRENT_NETWORK.getLearningrate(),
					Main.CURRENT_NETWORK.getTotalLearningIterations());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void open() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("XML", "*.xml"));

		java.io.File location = chooser.showOpenDialog(Main.getPrimaryStage());

		if (location.equals(null))
			return;
		else {
			try {
				loader.setFileLocation(location.getAbsolutePath());
				Main.CURRENT_NETWORK = new SupervisedNeuralNetwork(loader.getLearningRate(), loader.getInputLayerSize(),
						loader.getOutputLayerSize(), loader.getHiddenLayerSizes());
				Main.CURRENT_NETWORK.setInputWeights(loader.getInputWeights());
				try {
					Main.CURRENT_NETWORK.setHiddenWeights(loader.getHiddenLayers());
				} catch (NoHiddenWeightException e) {
				}
				Main.CURRENT_NETWORK.setOutputWeights(loader.getOutputWeights());
			} catch (JDOMException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	public void close() {
		Main.closeRequest();
	}

	@FXML
	public void guessInputCanvas() {
		int stretchFactor = (int) Math.round(inputCanvas.getWidth() / Main.IMAGE_WIDTH);

		PixelReader pr = inputCanvas
				.snapshot(null, new WritableImage(Main.IMAGE_WIDTH * stretchFactor, Main.IMAGE_HEIGHT * stretchFactor))
				.getPixelReader();

		int values[][] = new int[Main.IMAGE_WIDTH][Main.IMAGE_HEIGHT];
		double[] temp = new double[stretchFactor * stretchFactor];

		for (int x = 0; x < Main.IMAGE_WIDTH; x++) {
			for (int y = 0; y < Main.IMAGE_HEIGHT; y++) {
				temp = new double[stretchFactor * stretchFactor];
				for (int i = 0; i < stretchFactor; i++) {
					for (int j = 0; j < stretchFactor; j++) {
						temp[i * stretchFactor + j] = pr.getColor(x * stretchFactor + i, y * stretchFactor + j)
								.getBrightness();
					}
				}
				values[y][x] = (int) Math.round(NetworkUtils.getAverageValue(temp) * 255);
			}
		}

		drawNumber(values);

		Main.setGuess(NetworkUtils.getGuessedValue(Main.CURRENT_NETWORK.feedForward(NetworkUtils.to1DArray(values))));
		
		updateUI();
	}

	@FXML
	public void resetInputCanvas() {
		final GraphicsContext gc = inputCanvas.getGraphicsContext2D();

		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, inputCanvas.getWidth(), inputCanvas.getHeight());

		inputCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
			gc.setFill(new Color(1, 1, 1, .05));
			gc.fillRect(e.getX(), e.getY(), 20, 20);
			gc.setFill(new Color(1, 1, 1, .25));
			gc.fillRect(e.getX(), e.getY(), 15, 15);
			gc.setFill(new Color(1, 1, 1, .5));
			gc.fillRect(e.getX(), e.getY(), 10, 10);
			gc.setFill(new Color(1, 1, 1, 1));
			gc.fillRect(e.getX(), e.getY(), 5, 5);
		});
	}

	/**
	 * Draw the current image to the screen.
	 */
	public void drawNumber() {
		int stretchFactor = (int) Math.round(numberCanvas.getWidth() / Main.IMAGE_WIDTH);
		PixelWriter px = numberCanvas.getGraphicsContext2D().getPixelWriter();

		for (int x = 0; x < Main.IMAGE_WIDTH; x++) {
			for (int y = 0; y < Main.IMAGE_HEIGHT; y++) {
				for (int i = 0; i < stretchFactor; i++) {
					for (int j = 0; j < stretchFactor; j++) {
						px.setColor(x * stretchFactor + i, y * stretchFactor + j,
								Color.hsb(0, 0, Main.getCurrentImage()[y][x] / 255d));
					}
				}
			}
		}
	}

	private void drawNumber(int[][] yeah) {
		int stretchFactor = (int) Math.round(numberCanvas.getWidth() / Main.IMAGE_WIDTH);
		PixelWriter px = numberCanvas.getGraphicsContext2D().getPixelWriter();

		for (int x = 0; x < Main.IMAGE_WIDTH; x++) {
			for (int y = 0; y < Main.IMAGE_HEIGHT; y++) {
				for (int i = 0; i < stretchFactor; i++) {
					for (int j = 0; j < stretchFactor; j++) {
						px.setColor(x * stretchFactor + i, y * stretchFactor + j, Color.hsb(0, 0, yeah[y][x] / 255d));
					}
				}
			}
		}
	}

	/**
	 * The setup method is to be called just after constructor.
	 */
	public void setup() {
		triesCountTotal1.textProperty().bind(triesCountTotal.textProperty());

		updateUI();

		nodeCountInput.setText("" + Main.CURRENT_NETWORK.getInputNodeSize());
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < Main.CURRENT_NETWORK.getHiddenNodeSizes().length; i++) {
			strBuilder.append(Main.CURRENT_NETWORK.getHiddenNodeSizes()[i]).append(", ");
		}
		strBuilder.delete(strBuilder.length() - 2, strBuilder.length());
		nodeCountHidden.setText(strBuilder.toString());
		nodeCountOutput.setText("" + Main.CURRENT_NETWORK.getOutputNodeSize());

		setupAnimationButtons();

		drawNumber();
		resetInputCanvas();
	}

	/**
	 * Update the UI.
	 */
	public void updateUI() {
		triesCountTotal.setText(Main.CURRENT_NETWORK.getTotalLearningIterations() + "");

		errorRateValueLabel.setText(Main.CURRENT_NETWORK.getErrorRate() + "");

		numberValueLabel.setText(Main.getCurrentLabel() + "");
		numberValueLabelCopy.textProperty().bind(numberValueLabel.textProperty());

		networkErrorLabel.setText(Main.CURRENT_NETWORK.getCost() + "");

		networkGuessLabel.setText(Main.getGuess() + "");
	}

	private void setupAnimationButtons() {
		animationPlayButton.setOnAction(e -> {
			if (!NetworkUtils.ANIMATED) {
				NetworkUtils.ANIMATED = true;
				animationPlayButton.setText("Stop");
				animationPrevButton.setDisable(true);
				animationNextButton.setDisable(true);
			} else {
				NetworkUtils.ANIMATED = false;
				animationPlayButton.setText("Play");
				animationPrevButton.setDisable(false);
				animationNextButton.setDisable(false);
			}
		});

		animationPrevButton.setOnAction(e -> {
			if (!Main.previousIndex()) {
				animationPrevButton.setDisable(true);
			} else {
				animationNextButton.setDisable(false);

				if (numberDrawable.isSelected())
					drawNumber();

				NetworkUtils.ANIMATED_ONCE = true;

				updateUI();
			}
		});

		animationNextButton.setOnAction(e -> {
			if (!Main.nextIndex()) {
				animationNextButton.setDisable(true);
			} else {
				animationPrevButton.setDisable(false);

				if (numberDrawable.isSelected())
					drawNumber();

				NetworkUtils.ANIMATED_ONCE = true;

				updateUI();
			}
		});
	}

	@FXML
	public void setTheme(ActionEvent e) {
		if (e.getSource().equals(themeChoosableDark))
			owner.setTheme(Themes.DARK);
		else if (e.getSource().equals(themeChoosableLight))
			owner.setTheme(Themes.LIGHT);
	}

	/**
	 * Defines all the possible themes for the application.
	 * 
	 * @author Dillon
	 *
	 */
	public enum Themes {
		LIGHT("light.css"), DARK("dark.css");

		private String fileName;

		private Themes(String fileName) {
			this.fileName = fileName;
		}

		public String getFileName() {
			return fileName;
		}

		public URL getFilePath() {
			return getClass().getResource(fileName);
		}

		public String getExternalFilePath() {
			return getFilePath().toExternalForm();
		}
	}

	@Override
	public void setOwner(Main owner) {
		this.owner = owner;
	}

	@Override
	public Main getOwner() {
		return owner;
	}
}
