package ch.elste.hdr.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import ch.elste.hdr.IO.exceptions.FileNotSpecifiedException;

/**
 * Used to load and save data from the network to an external XML file.
 * 
 * @author Dillon Elste
 *
 */
public class IODataLoader {
	private static final String INTERNAL_FILE_LOCATION = "./src/ch/elste/hdr/internalFiles/internalStorage.xml";
	private static final String INPUT_LAYER = "inputLayer";
	private static final String HIDDEN_LAYER = "hiddenLayer";
	private static final String OUTPUT_LAYER = "outputLayer";
	private static final String WEIGHTS = "weights";
	private static final String ERROR_RATE = "errorRate";
	private static final String LEARNING_RATE = "learningRate";
	private static final String TOTAL_LEARNING_ITERATIONS = "totalLearningIterations";
	private static final String GENERAL_INFORMATION = "generalInformation";

	private SAXBuilder builder;
	private XMLOutputter outputter;

	private Document internalStorageDocument;
	private Document loadedDocument;
	private Document saveFileDocument;

	/**
	 * The location that files are loaded from.
	 * 
	 * @see #setFileLocation(String)
	 */
	private String fileLocation;

	/**
	 * The location that files are saved to. If not initialized it is equal to
	 * {@link #fileLocation}.
	 * 
	 * @see #setSaveLocation(String)
	 */
	private String saveLocation;

	/**
	 * Create a {@link IODataLoader} object.
	 * 
	 * @param fileLocation
	 *            the location the file should be loaded from
	 * @param saveLocation
	 *            the location the file should be saved to
	 * @throws JDOMException
	 * @throws IOException
	 */
	public IODataLoader(String fileLocation, String saveLocation) throws JDOMException, IOException {
		builder = new SAXBuilder();
		outputter = new XMLOutputter(Format.getPrettyFormat());

		internalStorageDocument = builder.build(new File(INTERNAL_FILE_LOCATION));

		if (fileLocation.equals(INTERNAL_FILE_LOCATION)) {
			this.fileLocation = internalStorageDocument.getRootElement().getAttributeValue("lastSaveLocation");
			this.saveLocation = this.fileLocation;
		} else {
			this.fileLocation = fileLocation;
			this.saveLocation = saveLocation;
		}

		loadedDocument = builder.build(new File(this.fileLocation));
	}

	/**
	 * Create a {@link IODataLoader} object.
	 * 
	 * @param fileLocation
	 *            the location the file should be loaded from
	 * @throws JDOMException
	 * @throws IOException
	 */
	public IODataLoader(String fileLocation) throws JDOMException, IOException {
		this(fileLocation, "");
	}

	/**
	 * Create a {@link IODataLoader} object.
	 * 
	 * @throws JDOMException
	 * @throws IOException
	 */
	public IODataLoader() throws JDOMException, IOException {
		this(INTERNAL_FILE_LOCATION);
	}

	/**
	 * Return the error rate of the network stored in internal storage.
	 * 
	 * @return the {@link ch.elste.NeuralNetwork.SupervisedNeuralNetwork#errorRate
	 *         error rate}
	 * @throws DataConversionException
	 *             - if the error rate wasn't stored as double
	 */
	public double getErrorRate() throws DataConversionException {
		return getGeneralInformation().getAttribute(ERROR_RATE).getDoubleValue();
	}

	/**
	 * Get the learning rate of the network
	 * 
	 * @return the
	 *         {@link ch.elste.NeuralNetwork.SupervisedNeuralNetwork#learningRate
	 *         learning rate}
	 * 
	 * @throws DataConversionException
	 *             - if learning rate wasn't stored as double
	 */
	public double getLearningRate() throws DataConversionException {
		return getGeneralInformation().getAttribute(LEARNING_RATE).getDoubleValue();
	}

	/**
	 * Get the total amount of iterations the network went through.
	 * 
	 * @return the
	 *         {@link ch.elste.NeuralNetwork.SupervisedNeuralNetwork#totalLearningIterations
	 *         learning iterations}
	 * @throws DataConversionException
	 *             - if learning iterations weren't stored as Integer
	 */
	public int getTotalLearningIterations() throws DataConversionException {
		return getGeneralInformation().getAttribute(TOTAL_LEARNING_ITERATIONS).getIntValue();
	}

	/**
	 * Helper method.
	 * 
	 * @return the general information element in user's storage file
	 */
	private Element getGeneralInformation() {
		return loadedDocument.getRootElement().getChild(GENERAL_INFORMATION);
	}

	/**
	 * Helper method.
	 * 
	 * @return the weights element in user's storage file.
	 */
	private Element getWeights(String layerName) {
		return loadedDocument.getRootElement().getChild(WEIGHTS).getChild(layerName);
	}

	/**
	 * Set the location of the file.
	 * 
	 * @param fileLocation
	 *            the location of the file
	 * @throws IOException
	 *             if the given fileLocation is invalid
	 * @throws JDOMException
	 *             multiple possible causes
	 */
	public void setFileLocation(String fileLocation) throws JDOMException, IOException {
		this.fileLocation = fileLocation;
		loadedDocument = builder.build(new File(this.fileLocation));
	}

	/**
	 * Set the location the {@link IODataLoader} should save to.
	 * 
	 * @param saveLocation
	 *            the location of the file
	 */
	public void setSaveLocation(String saveLocation) {
		this.saveLocation = saveLocation;
	}

	/**
	 * Get the weights between the input and the hidden layer.
	 * 
	 * @return the weights as a double array
	 */
	public double[] getInputWeights() {
		double[] temp = new double[getWeights(INPUT_LAYER).getChildren().size()];

		java.util.List<Element> children = getWeights(INPUT_LAYER).getChildren();
		for (int i = 0; i < temp.length - 1; i++) {
			temp[i] = Double.parseDouble(children.get(i).getContent(0).getValue());
		}

		return temp;
	}

	/**
	 * Get the input layer size
	 * 
	 * @return the size of the input layer
	 * @throws DataConversionException
	 */
	public int getInputLayerSize() throws DataConversionException {
		return getWeights(INPUT_LAYER).getAttribute("size").getIntValue();
	}

	/**
	 * Get the hidden layers.
	 * 
	 * @return the weights as an <code>ArrayList&ltdouble[]&gt</code>
	 */
	public ArrayList<double[]> getHiddenLayers() {
		ArrayList<double[]> temp = new ArrayList<>();
		double[] tempDouble;

		java.util.List<Element> layers = getWeights(HIDDEN_LAYER).getChildren();
		for (int i = 0; i < layers.size(); i++) {
			tempDouble = new double[layers.get(i).getChildren().size()];
			for (int j = 0; j < tempDouble.length; j++) {
				tempDouble[j] = Double.parseDouble(layers.get(i).getChildren().get(j).getContent(0).getValue());
			}
			temp.add(tempDouble);
		}

		return temp;
	}

	/**
	 * Get the sizes of the hidden layers.
	 * 
	 * @return the sizes of the hidden layers as an integer-array
	 * @throws DataConversionException
	 */
	public int[] getHiddenLayerSizes() throws DataConversionException {
		int[] temp = new int[getWeights(HIDDEN_LAYER).getChildren().size()];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = getWeights(HIDDEN_LAYER).getChildren().get(i).getAttribute("size").getIntValue();
		}

		return temp;
	}

	/**
	 * Get the weights between the last hidden and the output layer.
	 * 
	 * @return the weights as a double array
	 */
	public double[] getOutputWeights() {
		double[] temp = new double[getWeights(OUTPUT_LAYER).getChildren().size()];

		java.util.List<Element> children = getWeights(OUTPUT_LAYER).getChildren();
		for (int i = 0; i < temp.length - 1; i++) {
			temp[i] = Double.parseDouble(children.get(i).getContent(0).getValue());
		}

		return temp;
	}

	/**
	 * Get the size of the output layer.
	 * 
	 * @return the size of the output layer
	 * @throws DataConversionException
	 */
	public int getOutputLayerSize() throws DataConversionException {
		return getWeights(OUTPUT_LAYER).getAttribute("size").getIntValue();
	}

	/**
	 * Save the data to an external XML file at {@link #saveLocation the previously
	 * specified location}.
	 * 
	 * @param inputWeights
	 *            the weights
	 * @param hiddenWeights
	 *            the weights of the network, may be <code>null</code>.
	 * @param outputWeights
	 *            the weights
	 * @param errorRate
	 *            the error rate of the network
	 * @param learningRate
	 *            the learning rate of the network
	 * @param totalLearningIterations
	 *            the total amount of iterations the network went through
	 * @throws IOException
	 * @throws FileNotSpecifiedException
	 *             - if the location to save to wasn't specified.
	 * 
	 * @see {@link #setFileLocation(String)}
	 */
	public void save(double[] inputWeights, int inputSize, ArrayList<double[]> hiddenWeights, int[] hiddenSizes,
			double[] outputWeights, int outputSize, double errorRate, double learningRate, int totalLearningIterations)
			throws IOException {
		save(saveLocation, inputWeights, inputSize, hiddenWeights, hiddenSizes, outputWeights, outputSize, errorRate,
				learningRate, totalLearningIterations);
	}

	/**
	 * Save the data to an external XML file.
	 * 
	 * @param fileLocation
	 *            where the file should be saved to, in form of a string
	 * @param inputWeights
	 *            the weights of the network
	 * @param hiddenWeights
	 *            the weights of the network, may be <code>null</code>.
	 * @param outputWeights
	 *            the weights of the network
	 * @param errorRate
	 *            the error rate of the network
	 * @param learningRate
	 *            the learning rate of the network
	 * @param totalLearningIterations
	 *            the total amount of iterations the network went through
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws FileNotSpecifiedException
	 *             if the location to save to wasn't specified.
	 */
	public void save(String fileLocation, double[] inputWeights, int inputSize, ArrayList<double[]> hiddenWeights,
			int[] hiddenSizes, double[] outputWeights, int outputSize, double errorRate, double learningRate,
			int totalLearningIterations) throws IOException {
		saveFileDocument = new Document();
		saveFileDocument.setRootElement(new Element("root"));
		saveFileDocument.getRootElement().addContent(new Element(GENERAL_INFORMATION));
		saveFileDocument.getRootElement().addContent(new Element(WEIGHTS));
		saveFileDocument.getRootElement().getChild(WEIGHTS).addContent(java.util.Arrays.asList(new Element(INPUT_LAYER),
				new Element(HIDDEN_LAYER), new Element(OUTPUT_LAYER)));

		saveFileDocument.getRootElement().getChild(WEIGHTS).getChild(OUTPUT_LAYER).setAttribute("size",
				outputSize + "");
		saveFileDocument.getRootElement().getChild(WEIGHTS).getChild(INPUT_LAYER).setAttribute("size", inputSize + "");

		saveGeneralInformation(saveFileDocument, errorRate, learningRate, totalLearningIterations);
		saveInputWeights(saveFileDocument, inputWeights);
		saveHiddenWeights(saveFileDocument, hiddenWeights, hiddenSizes);
		saveOutputWeights(saveFileDocument, outputWeights);

		if (fileLocation.equals("")) {
			throw new FileNotSpecifiedException();
		}

		try {
			outputter.output(saveFileDocument, new FileOutputStream(fileLocation));
			outputter.output(internalStorageDocument, new FileOutputStream(INTERNAL_FILE_LOCATION));
		} catch (FileNotFoundException e) {
			System.err.println(String.format("The save location %s may not have been initialized yet.", fileLocation));
			throw e;
		}
	}

	private void saveGeneralInformation(Document document, double errorRate, double learningRate,
			int totalLearningIterations) {
		document.getRootElement().getChild(GENERAL_INFORMATION).setAttribute(ERROR_RATE, errorRate + "");
		document.getRootElement().getChild(GENERAL_INFORMATION).setAttribute(LEARNING_RATE, learningRate + "");
		document.getRootElement().getChild(GENERAL_INFORMATION).setAttribute(TOTAL_LEARNING_ITERATIONS,
				totalLearningIterations + "");
	}

	private void saveInputWeights(Document document, double[] weights) {
		Element e;
		for (int i = 0; i < weights.length; i++) {
			e = new Element("weight");
			e.addContent(weights[i] + "");
			document.getRootElement().getChild(WEIGHTS).getChild(INPUT_LAYER).addContent(e);
		}
	}

	private void saveHiddenWeights(Document document, ArrayList<double[]> weights, int[] hiddenSizes) {
		Element e;
		for (int i = 0; i < hiddenSizes.length; i++) {
			e = new Element(HIDDEN_LAYER + i);
			e.setAttribute("size", hiddenSizes[i] + "");
			document.getRootElement().getChild(WEIGHTS).getChild(HIDDEN_LAYER).addContent(e);
			if (weights != null) {
				for (int j = 0; j < weights.get(i).length; j++) {
					e = new Element("weight");
					e.addContent(weights.get(i)[j] + "");
					document.getRootElement().getChild(WEIGHTS).getChild(HIDDEN_LAYER).getChild(HIDDEN_LAYER + i)
							.addContent(e);
				}
			}
		}
	}

	private void saveOutputWeights(Document document, double[] weights) {
		Element e;
		for (int i = 0; i < weights.length; i++) {
			e = new Element("weight");
			e.addContent(weights[i] + "");
			document.getRootElement().getChild(WEIGHTS).getChild(OUTPUT_LAYER).addContent(e);
		}
	}
}
