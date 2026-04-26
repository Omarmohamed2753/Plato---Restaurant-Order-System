package javaproject1.UI.JavaFX;

import javafx.application.Application;
import javafx.stage.Stage;
import javaproject1.UI.JavaFX.Controller.WelcomeController;

public class PlatoApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Plato Restaurant Order System");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
        primaryStage.maximizedProperty().addListener((obs, wasMaximized, isMaximized) -> {
        if (!isMaximized) primaryStage.setMaximized(true);
        });
        WelcomeController.show(primaryStage);
        
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}