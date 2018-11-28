package ch.elste.hdr.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox extends AttentionBox {
	/**
	 * Creates a new AlertBox
	 * 
	 * @param message
	 * @param title
	 */
	public AlertBox(String message, String title) {
		super(message, title);
	}

	@Override
	public int display(String message, String title, Modality modality, String... buttonTexts) {
		Stage stage = new Stage();
		Scene scene;
		Label messageLabel = new Label(message.replaceAll("(.*(\\?|\\.|!))\\s+(.*)", "$1\n$3"));
		buttons = new Button[buttonTexts.length];
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new Button(buttonTexts[i]);
			final int j = i;
			buttons[i].setOnAction(e -> {
				toReturn = j;
				stage.close();
			});
		}

		messageLabel.setTextAlignment(TextAlignment.CENTER);

		stage.initModality(modality);
		stage.setTitle(title);
		stage.setResizable(true);
		stage.setWidth(300);

		HBox buttonsBox = new HBox(20);
		buttonsBox.setAlignment(Pos.CENTER);
		buttonsBox.getChildren().addAll(buttons);

		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));
		vbox.getChildren().addAll(messageLabel, buttonsBox);
		vbox.setAlignment(Pos.CENTER);

		scene = new Scene(vbox);
		scene.getStylesheets().clear();
		scene.getStylesheets().setAll(theme);

		stage.setScene(scene);
		stage.showAndWait();

		return toReturn;
	}
	
	@Deprecated
	@Override
	public String display(String title, Modality modality) {
		return null;
	}
}
