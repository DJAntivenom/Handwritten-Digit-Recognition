package ch.elste.hdr.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewFileBox extends AttentionBox {
	String toReturn;
	
	public NewFileBox(String message, String title) {
		super(message, title);
	}

	@Deprecated
	@Override
	public int display(String message, String title, Modality modality, String... buttonTexts) {
		return 0;
	}
	
	@Override
	public String display(String title, Modality modality) {
		Stage stage = new Stage();
		Scene scene;
		
		Label learningLabel = new Label("learning rate");
		TextField learningField = new TextField();
		learningField.setPromptText("X.X");
		
		Label hiddenLabel = new Label("hidden layers");
		TextField hiddenField = new TextField();
		hiddenField.setPromptText("XX, XX, ...");
		hiddenField.textProperty().addListener((changeListener, oldValue, newValue)->{
			if(!newValue.matches("(\\d+,*\\s*)*")) {
				hiddenField.setText(oldValue);
			}
		});

		Button accept = new Button("submit");
		accept.setOnAction(e->{
			toReturn = learningField.getText() + "!" + hiddenField.getText();
			stage.close();
		});
		
		HBox learningBox = new HBox(20);
		learningBox.setAlignment(Pos.CENTER);
		learningBox.getChildren().addAll(learningLabel, learningField);
		
		HBox hiddenBox = new HBox(20);
		hiddenBox.setAlignment(Pos.CENTER);
		hiddenBox.getChildren().addAll(hiddenLabel, hiddenField);
		
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));
		root.getChildren().addAll(learningBox, hiddenBox, accept);
		
		scene = new Scene(root);
		scene.getStylesheets().clear();
		scene.getStylesheets().add(theme);
		
		stage.initModality(modality);
		stage.setTitle(title);
		stage.setResizable(true);
		stage.setWidth(300);
		stage.setScene(scene);
		stage.showAndWait();
		
		stage.setOnCloseRequest(e->{
			e.consume();
			toReturn = "";
			stage.close();
		});
		
		return toReturn;
	}

}
