package javaproject1.UI.JavaFX.Controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class WelcomeController {

    // Theme Colors
    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String BUTTON_BLUE = "#3b82f6";
    
    public static void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        VBox contentBox = new VBox(40);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(80));

        
        Label titleLabel = new Label("Welcome to Plato");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 56));
        titleLabel.setTextFill(Color.web(ACCENT_GOLD));

        Label subtitleLabel = new Label("Elite Restaurant Order System");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        subtitleLabel.setTextFill(Color.web(TEXT_COLOR_LIGHT));

        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(350);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));

        Button signUpButton = createStyledButton("Create Account", PRIMARY_COLOR);
        Button signInButton = createStyledButton("Sign In", BUTTON_BLUE);

        signUpButton.setOnAction(e -> SignUpController.show(stage));
        signInButton.setOnAction(e -> SignInController.show(stage));

        buttonBox.getChildren().addAll(signUpButton, signInButton);

        contentBox.getChildren().addAll( titleLabel, subtitleLabel, buttonBox);
        root.setCenter(contentBox);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    private static Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(350);
        button.setPrefHeight(60);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 30; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);",
            color
        ));

        button.setOnMouseEntered(e -> button.setStyle(String.format(
            "-fx-background-color: derive(%s, -15%%); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 30; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 20, 0, 0, 8); " +
            "-fx-scale-x: 1.02; -fx-scale-y: 1.02;",
            color
        )));

        button.setOnMouseExited(e -> button.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 30; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);",
            color
        )));

        return button;
    }
}