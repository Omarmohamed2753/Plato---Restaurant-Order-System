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
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javaproject1.BLL.Service.implementation.UserServiceImpl;
import javaproject1.DAL.Entity.Address;
import javaproject1.DAL.Entity.User;

public class SignUpController {

    private static UserServiceImpl userService = new UserServiceImpl();

    public static void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        // Header
        HBox header = createHeader(stage);
        root.setTop(header);

        // Form
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(500);

        Label titleLabel = new Label("Create Account");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#2d3436"));

        // Form fields
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        TextField nameField = createTextField("Full Name");
        TextField emailField = createTextField("Email");
        PasswordField passwordField = createPasswordField("Password");
        PasswordField confirmPasswordField = createPasswordField("Confirm Password");
        TextField phoneField = createTextField("Phone Number");
        TextField ageField = createTextField("Age");
        
        // Address fields
        TextField streetField = createTextField("Street");
        TextField cityField = createTextField("City");
        TextField buildingField = createTextField("Building Number");

        int row = 0;
        grid.add(new Label("Name:"), 0, row);
        grid.add(nameField, 1, row++);
        
        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);
        
        grid.add(new Label("Password:"), 0, row);
        grid.add(passwordField, 1, row++);
        
        grid.add(new Label("Confirm:"), 0, row);
        grid.add(confirmPasswordField, 1, row++);
        
        grid.add(new Label("Phone:"), 0, row);
        grid.add(phoneField, 1, row++);
        
        grid.add(new Label("Age:"), 0, row);
        grid.add(ageField, 1, row++);
        
        grid.add(new Label("Street:"), 0, row);
        grid.add(streetField, 1, row++);
        
        grid.add(new Label("City:"), 0, row);
        grid.add(cityField, 1, row++);
        
        grid.add(new Label("Building:"), 0, row);
        grid.add(buildingField, 1, row++);

        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefWidth(200);
        signUpButton.setPrefHeight(45);
        signUpButton.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 22.5; " +
            "-fx-cursor: hand;"
        );

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 14));

        signUpButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                String phone = phoneField.getText().trim();
                String ageText = ageField.getText().trim();
                String street = streetField.getText().trim();
                String city = cityField.getText().trim();
                String buildingText = buildingField.getText().trim();

                // Validation
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || 
                    phone.isEmpty() || ageText.isEmpty() || street.isEmpty() || 
                    city.isEmpty() || buildingText.isEmpty()) {
                    showError(messageLabel, "All fields are required!");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    showError(messageLabel, "Passwords do not match!");
                    return;
                }

                int age = Integer.parseInt(ageText);
                int building = Integer.parseInt(buildingText);

                // Create address
                Address address = new Address(street, city, building);

                // Create user
                User user = new User(null, name, age, phone, email, password, address);

                boolean success = userService.createAccount(user);
                if (success) {
                    showSuccess(messageLabel, "Account created successfully!");
                    // Wait a moment then go to sign in
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            javafx.application.Platform.runLater(() -> 
                                SignInController.show(stage)
                            );
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                } else {
                    showError(messageLabel, "Email already exists!");
                }
            } catch (NumberFormatException ex) {
                showError(messageLabel, "Age and Building must be numbers!");
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
            }
        });

        formBox.getChildren().addAll(titleLabel, grid, signUpButton, messageLabel);
        
        ScrollPane scrollPane = new ScrollPane(formBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f7fa; -fx-background-color: transparent;");
        
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
    }

    private static HBox createHeader(Stage stage) {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #667eea; -fx-font-size: 14px; -fx-cursor: hand;");
        backButton.setOnAction(e -> WelcomeController.show(stage));

        header.getChildren().add(backButton);
        return header;
    }

    private static TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(300);
        field.setStyle("-fx-padding: 10; -fx-font-size: 14px;");
        return field;
    }

    private static PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefWidth(300);
        field.setStyle("-fx-padding: 10; -fx-font-size: 14px;");
        return field;
    }

    private static void showError(Label label, String message) {
        label.setText(message);
        label.setTextFill(Color.web("#e74c3c"));
    }

    private static void showSuccess(Label label, String message) {
        label.setText(message);
        label.setTextFill(Color.web("#27ae60"));
    }
}