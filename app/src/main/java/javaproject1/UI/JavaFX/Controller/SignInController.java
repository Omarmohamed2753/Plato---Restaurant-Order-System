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
import javaproject1.BLL.Service.implementation.AdminServiceImpl;
import javaproject1.BLL.Service.implementation.UserServiceImpl;
import javaproject1.DAL.Entity.Admin;
import javaproject1.DAL.Entity.User;

public class SignInController {

    private static UserServiceImpl userService = new UserServiceImpl();
    private static AdminServiceImpl adminService = new AdminServiceImpl();

    public static void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        // Header
        HBox header = createHeader(stage);
        root.setTop(header);

        // Form
        VBox formBox = new VBox(25);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(50));
        formBox.setMaxWidth(450);

        Label titleLabel = new Label("Sign In");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.web("#2d3436"));

        // Tab pane for Client/Admin
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white;");

        // Client Tab
        Tab clientTab = new Tab("Client");
        VBox clientBox = createClientLoginForm(stage);
        clientTab.setContent(clientBox);

        // Admin Tab
        Tab adminTab = new Tab("Admin");
        VBox adminBox = createAdminLoginForm(stage);
        adminTab.setContent(adminBox);

        tabPane.getTabs().addAll(clientTab, adminTab);

        formBox.getChildren().addAll(titleLabel, tabPane);

        StackPane centerPane = new StackPane(formBox);
        centerPane.setAlignment(Pos.CENTER);
        root.setCenter(centerPane);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
    }

    private static VBox createClientLoginForm(Stage stage) {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));

        TextField emailField = createTextField("Email");
        PasswordField passwordField = createPasswordField("Password");

        Button signInButton = new Button("Sign In");
        signInButton.setPrefWidth(250);
        signInButton.setPrefHeight(45);
        signInButton.setStyle(
            "-fx-background-color: #2196F3; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 22.5; " +
            "-fx-cursor: hand;"
        );

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 14));

        signInButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showError(messageLabel, "Please enter email and password!");
                return;
            }

            try {
                boolean success = userService.login(email, password);
                if (success) {
                    User user = userService.getUserByEmail(email);
                    showSuccess(messageLabel, "Login successful!");
                    
                    // Start client thread and show main screen
                    Thread clientThread = new Thread(() -> {
                        try {
                            Thread.sleep(500);
                            javafx.application.Platform.runLater(() -> 
                                ClientMainController.show(stage, user)
                            );
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }, "ClientThread");
                    clientThread.start();
                } else {
                    showError(messageLabel, "Invalid email or password!");
                }
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
            }
        });

        Hyperlink signUpLink = new Hyperlink("Don't have an account? Sign up");
        signUpLink.setOnAction(e -> SignUpController.show(stage));

        box.getChildren().addAll(emailField, passwordField, signInButton, messageLabel, signUpLink);
        return box;
    }

    private static VBox createAdminLoginForm(Stage stage) {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));

        TextField emailField = createTextField("Admin Email");
        PasswordField passwordField = createPasswordField("Admin Password");

        Button signInButton = new Button("Admin Sign In");
        signInButton.setPrefWidth(250);
        signInButton.setPrefHeight(45);
        signInButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 22.5; " +
            "-fx-cursor: hand;"
        );

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 14));

        signInButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showError(messageLabel, "Please enter email and password!");
                return;
            }

            try {
                // Get admin by ID (you may need to add getAdminByEmail method)
                // For now, trying to get first admin
                Admin admin = adminService.getAdminById(1);
                
                if (admin != null && adminService.loginAdmin(admin, email, password)) {
                    showSuccess(messageLabel, "Admin login successful!");
                    
                    // Start admin thread and show dashboard
                    Thread adminThread = new Thread(() -> {
                        try {
                            Thread.sleep(500);
                            javafx.application.Platform.runLater(() -> 
                                AdminDashboardController.show(stage, admin)
                            );
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }, "AdminThread");
                    adminThread.start();
                } else {
                    showError(messageLabel, "Invalid admin credentials!");
                }
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
            }
        });

        box.getChildren().addAll(emailField, passwordField, signInButton, messageLabel);
        return box;
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
        field.setStyle("-fx-padding: 12; -fx-font-size: 14px; -fx-background-radius: 5;");
        return field;
    }

    private static PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefWidth(300);
        field.setStyle("-fx-padding: 12; -fx-font-size: 14px; -fx-background-radius: 5;");
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