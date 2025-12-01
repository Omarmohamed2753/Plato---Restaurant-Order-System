package javaproject1.UI.JavaFX.Controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class WelcomeController {

    public static void show(Stage stage) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        // Title
        Label titleLabel = new Label("Welcome to Plato");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Restaurant Order System");
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 24));
        subtitleLabel.setTextFill(Color.web("#e0e0e0"));

        // Buttons container
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(300);

        Button signUpButton = createStyledButton("Sign Up", "#4CAF50");
        Button signInButton = createStyledButton("Sign In", "#2196F3");

        signUpButton.setOnAction(e -> SignUpController.show(stage));
        signInButton.setOnAction(e -> SignInController.show(stage));

        buttonBox.getChildren().addAll(signUpButton, signInButton);

        root.getChildren().addAll(titleLabel, subtitleLabel, buttonBox);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
    }

    private static Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setPrefHeight(50);
        button.setFont(Font.font("System", FontWeight.BOLD, 16));
        button.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);",
            color
        ));

        button.setOnMouseEntered(e -> button.setStyle(String.format(
            "-fx-background-color: derive(%s, -10%%); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 7);",
            color
        )));

        button.setOnMouseExited(e -> button.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);",
            color
        )));

        return button;
    }
}