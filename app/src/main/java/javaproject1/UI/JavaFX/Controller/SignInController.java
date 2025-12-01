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
    
    // Thread references for management
    private static Thread clientThread;
    private static Thread adminThread;

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

            // Disable button during login process
            signInButton.setDisable(true);
            messageLabel.setText("Logging in...");
            messageLabel.setTextFill(Color.web("#3498db"));

            try {
                boolean success = userService.login(email, password);
                if (success) {
                    User user = userService.getUserByEmail(email);
                    showSuccess(messageLabel, "Login successful! Loading...");

                    clientThread = new Thread(() -> {
                        try {
                            System.out.println(" Client Thread Started: " + Thread.currentThread().getName());
                            System.out.println("   User: " + user.getName() + " (ID: " + user.getId() + ")");
                            
                            // Simulate loading/initialization
                            Thread.sleep(500);
                            
                            // Update UI on JavaFX Application Thread
                            javafx.application.Platform.runLater(() -> {
                                System.out.println("   Navigating to Client Main Screen...");
                                ClientMainController.show(stage, user);
                                System.out.println(" Client Thread: UI Loaded Successfully");
                            });
                            
                        } catch (InterruptedException ex) {
                            System.err.println(" Client Thread Interrupted: " + ex.getMessage());
                            ex.printStackTrace();
                            
                            // Handle interruption on UI thread
                            javafx.application.Platform.runLater(() -> {
                                showError(messageLabel, "Login interrupted. Please try again.");
                                signInButton.setDisable(false);
                            });
                        } catch (Exception ex) {
                            System.err.println(" Client Thread Error: " + ex.getMessage());
                            ex.printStackTrace();
                            
                            // Handle errors on UI thread
                            javafx.application.Platform.runLater(() -> {
                                showError(messageLabel, "Error loading dashboard: " + ex.getMessage());
                                signInButton.setDisable(false);
                            });
                        }
                    }, "ClientThread-" + user.getId()); // Unique thread name
                    
                    // Set as daemon thread (will stop when app closes)
                    clientThread.setDaemon(true);
                    
                    // Start the thread
                    clientThread.start();
                    
                } else {
                    showError(messageLabel, "Invalid email or password!");
                    signInButton.setDisable(false);
                }
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
                signInButton.setDisable(false);
                ex.printStackTrace();
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

            // Disable button during login process
            signInButton.setDisable(true);
            messageLabel.setText("Authenticating admin...");
            messageLabel.setTextFill(Color.web("#3498db"));

            try {
                // Get admin by ID (you may need to add getAdminByEmail method)
                // For now, trying to get first admin
                Admin admin = adminService.getAdminById(1);
                
                if (admin != null && adminService.loginAdmin(admin, email, password)) {
                    showSuccess(messageLabel, "Admin login successful! Loading dashboard...");
                    
                    adminThread = new Thread(() -> {
                        try {
                            System.out.println(" Admin Thread Started: " + Thread.currentThread().getName());
                            System.out.println("   Admin: " + admin.getName() + " (ID: " + admin.getId() + ")");
                            if (admin.getRestaurant() != null) {
                                System.out.println("   Restaurant: " + admin.getRestaurant().getName());
                            }
                            
                            // Simulate loading/initialization
                            Thread.sleep(500);
                            
                            // Update UI on JavaFX Application Thread
                            javafx.application.Platform.runLater(() -> {
                                System.out.println("   Navigating to Admin Dashboard...");
                                AdminDashboardController.show(stage, admin);
                                System.out.println(" Admin Thread: Dashboard Loaded Successfully");
                            });
                            
                        } catch (InterruptedException ex) {
                            System.err.println("Admin Thread Interrupted: " + ex.getMessage());
                            ex.printStackTrace();
                            
                            // Handle interruption on UI thread
                            javafx.application.Platform.runLater(() -> {
                                showError(messageLabel, "Login interrupted. Please try again.");
                                signInButton.setDisable(false);
                            });
                        } catch (Exception ex) {
                            System.err.println("Admin Thread Error: " + ex.getMessage());
                            ex.printStackTrace();
                            
                            // Handle errors on UI thread
                            javafx.application.Platform.runLater(() -> {
                                showError(messageLabel, "Error loading dashboard: " + ex.getMessage());
                                signInButton.setDisable(false);
                            });
                        }
                    }, "AdminThread-" + admin.getId()); // Unique thread name
                    
                    // Set as daemon thread (will stop when app closes)
                    adminThread.setDaemon(true);
                    
                    // Start the thread
                    adminThread.start();
                    
                } else {
                    showError(messageLabel, "Invalid admin credentials!");
                    signInButton.setDisable(false);
                }
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
                signInButton.setDisable(false);
                ex.printStackTrace();
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
        backButton.setOnAction(e -> {
            // Clean up threads if navigating back
            if (clientThread != null && clientThread.isAlive()) {
                clientThread.interrupt();
            }
            if (adminThread != null && adminThread.isAlive()) {
                adminThread.interrupt();
            }
            WelcomeController.show(stage);
        });

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
    
    // Get the current client thread (for monitoring/debugging)
    public static Thread getClientThread() {
        return clientThread;
    }
    
    // Get the current admin thread (for monitoring/debugging)
    public static Thread getAdminThread() {
        return adminThread;
    }
    
    // Check if client thread is active
    public static boolean isClientThreadActive() {
        return clientThread != null && clientThread.isAlive();
    }
    
   // Check if admin thread is active
    public static boolean isAdminThreadActive() {
        return adminThread != null && adminThread.isAlive();
    }
}