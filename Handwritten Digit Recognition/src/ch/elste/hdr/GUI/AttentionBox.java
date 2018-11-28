package ch.elste.hdr.GUI;

import ch.elste.hdr.Main;
import javafx.scene.control.Button;
import javafx.stage.Modality;

public abstract class AttentionBox {
	protected static String theme = Main.CURRENT_THEME;
	protected Button[] buttons;
	protected int toReturn;
	protected String message, title;

	/**
	 * Creates a new AttentionBox.
	 * 
	 * @param message
	 * @param title
	 */
	public AttentionBox(String message, String title) {
		this.message = message;
		this.title = title;
	}

	/**
	 * Open a message box of some sort.
	 * 
	 * @param message
	 *            the message to be shown
	 * @param title
	 *            the title of the window
	 * @param modality
	 *            the modality of the window
	 * @param buttonTexts
	 *            the texts displayed on the buttons beneath the message
	 * @return an integer showing which button was pressed
	 */
	public abstract int display(String message, String title, Modality modality, String... buttonTexts);

	/**
	 * Open a message box of some sort.
	 * 
	 * @param title
	 *            the title of the window
	 * @param modality
	 *            the modality of the window
	 * @return a string
	 */
	public abstract String display(String title, Modality modality);

	/**
	 * Open a message box of some sort.
	 * 
	 * @param modality
	 *            the modality of the window
	 * @param strings
	 *            the texts displayed on the buttons beneath the message
	 * @return an integer showing which button was pressed
	 */
	public int display(Modality modality, String... strings) {
		return display(message, title, modality, strings);
	}

	/**
	 * Set the message of the window.
	 * 
	 * @param message
	 *            of the window
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Set the title of the window.
	 * 
	 * @param title
	 *            of the window
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
