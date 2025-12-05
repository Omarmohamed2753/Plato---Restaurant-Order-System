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
    
    private static Thread clientThread;
    private static Thread adminThread;

    // Dark Theme Colors
    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String CARD_BACKGROUND = "#374151";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String ITEM_BACKGROUND = "#2b3543";
    private static final String BUTTON_BLUE = "#3b82f6";
    private static final String BUTTON_RED = "#ef4444";

    public static void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        // Header
        HBox header = createHeader(stage);
        root.setTop(header);

        // Form
        VBox formBox = new VBox(30);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(60));
        formBox.setMaxWidth(550);

        Label titleLabel = new Label("Sign In");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        titleLabel.setTextFill(Color.web(ACCENT_GOLD));

        // Tab pane for Client/Admin
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-border-color: transparent;"
        );

        // Client Tab
        Tab clientTab = new Tab("Client Login");
        clientTab.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        VBox clientBox = createClientLoginForm(stage);
        clientTab.setContent(clientBox);

        // Admin Tab
        Tab adminTab = new Tab("Admin Login");
        adminTab.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        VBox adminBox = createAdminLoginForm(stage);
        adminTab.setContent(adminBox);

        tabPane.getTabs().addAll(clientTab, adminTab);

        formBox.getChildren().addAll(titleLabel, tabPane);

        StackPane centerPane = new StackPane(formBox);
        centerPane.setAlignment(Pos.CENTER);
        root.setCenter(centerPane);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    private static VBox createClientLoginForm(Stage stage) {
        VBox box = new VBox(25);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-background-radius: 15;");

        TextField emailField = createTextField("Email");
        PasswordField passwordField = createPasswordField("Password");

        Button signInButton = new Button("Sign In →");
        signInButton.setPrefWidth(300);
        signInButton.setPrefHeight(50);
        signInButton.setStyle(
            "-fx-background-color: " + BUTTON_BLUE + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );
        
        signInButton.setOnMouseEntered(e -> signInButton.setStyle(signInButton.getStyle() + "-fx-background-color: #2563eb;"));
        signInButton.setOnMouseExited(e -> signInButton.setStyle(signInButton.getStyle().replace("-fx-background-color: #2563eb;", "-fx-background-color: " + BUTTON_BLUE + ";")));
        
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 15));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        signInButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showError(messageLabel, "Please enter email and password!");
                return;
            }

            signInButton.setDisable(true);
            messageLabel.setText("Logging in...");
            messageLabel.setTextFill(Color.web(PRIMARY_COLOR));

            try {
                boolean success = userService.login(email, password);
                if (success) {
                    User user = userService.getUserByEmail(email);
                    showSuccess(messageLabel, "Login successful! Loading...");

                    clientThread = new Thread(() -> {
                        try {
                            System.out.println("✓ Client Thread Started: " + Thread.currentThread().getName());
                            System.out.println("  User: " + user.getName() + " (ID: " + user.getId() + ")");
                            
                            Thread.sleep(500);
                            
                            javafx.application.Platform.runLater(() -> {
                                System.out.println("  Navigating to Client Main Screen...");
                                ClientMainController.show(stage, user);
                                System.out.println("✓ Client Thread: UI Loaded Successfully");
                            });
                            
                        } catch (InterruptedException ex) {
                            System.err.println("✗ Client Thread Interrupted: " + ex.getMessage());
                            ex.printStackTrace();
                            
                            javafx.application.Platform.runLater(() -> {
                                showError(messageLabel, "Login interrupted. Please try again.");
                                signInButton.setDisable(false);
                            });
                        } catch (Exception ex) {
                            System.err.println("✗ Client Thread Error: " + ex.getMessage());
                            ex.printStackTrace();
                            
                            javafx.application.Platform.runLater(() -> {
                                showError(messageLabel, "Error loading dashboard: " + ex.getMessage());
                                signInButton.setDisable(false);
                            });
                        }
                    }, "ClientThread-" + user.getId());
                    
                    clientThread.setDaemon(true);
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
        signUpLink.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 15px; -fx-underline: true;");
        signUpLink.setOnAction(e -> SignUpController.show(stage));

        box.getChildren().addAll(emailField, passwordField, signInButton, messageLabel, signUpLink);
        return box;
    }

    private static VBox createAdminLoginForm(Stage stage) {
        VBox box = new VBox(25);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-background-radius: 15;");

        TextField emailField = createTextField("Admin Email");
        PasswordField passwordField = createPasswordField("Admin Password");

        Button signInButton = new Button("Admin Sign In →");
        signInButton.setPrefWidth(300);
        signInButton.setPrefHeight(50);
        signInButton.setStyle(
            "-fx-background-color: " + BUTTON_RED + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );

        signInButton.setOnMouseEntered(e -> signInButton.setStyle(signInButton.getStyle() + "-fx-background-color: #dc2626;"));
        signInButton.setOnMouseExited(e -> signInButton.setStyle(signInButton.getStyle().replace("-fx-background-color: #dc2626;", "-fx-background-color: " + BUTTON_RED + ";")));

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 15));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        signInButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showError(messageLabel, "Please enter email and password!");
                return;
            }

            signInButton.setDisable(true);
            messageLabel.setText("Authenticating admin...");
            messageLabel.setTextFill(Color.web(PRIMARY_COLOR));

            try {
                Admin admin = adminService.getAdminById(1);
                
                if (admin != null && adminService.loginAdmin(admin, email, password)) {
                    showSuccess(messageLabel, "Admin login successful! Loading dashboard...");
                    
                    adminThread = new Thread(() -> {
                        try {
                            System.out.println("✓ Admin Thread Started: " + Thread.currentThread().getName());
                            System.out.println("  Admin: " + admin.getName() + " (ID: " + admin.getId() + ")");
                            if (admin.getRestaurant() != null) {
                                System.out.println("  Restaurant: " + admin.getRestaurant().getName());
                            }
                            
                            Thread.sleep(500);
                            
                            javafx.application.Platform.runLater(() -> {
                                System.out.println("  Navigating to Admin Dashboard...");
                                AdminDashboardController.show(stage, admin);
                                System.out.println("✓ Admin Thread: Dashboard Loaded Successfully");
                            });
                            
                        } catch (InterruptedException ex) {
                            System.err.println("✗ Admin Thread Interrupted: " + ex.getMessage());
                            ex.printStackTrace();
                            
                            javafx.application.Platform.runLater(() -> {
                                showError(messageLabel, "Login interrupted. Please try again.");
                                signInButton.setDisable(false);
                            });
                        } catch (Exception ex) {
                            System.err.println("✗ Admin Thread Error: " + ex.getMessage());
                            ex.printStackTrace();
                            
                            javafx.application.Platform.runLater(() -> {
                                showError(messageLabel, "Error loading dashboard: " + ex.getMessage());
                                signInButton.setDisable(false);
                            });
                        }
                    }, "AdminThread-" + admin.getId());
                    
                    adminThread.setDaemon(true);
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
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 3);");

        Button backButton = new Button("← Back");
        backButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + PRIMARY_COLOR + "; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-cursor: hand;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(backButton.getStyle() + "-fx-text-fill: " + ACCENT_GOLD + ";"));
        backButton.setOnMouseExited(e -> backButton.setStyle(backButton.getStyle().replace("-fx-text-fill: " + ACCENT_GOLD + ";", "-fx-text-fill: " + PRIMARY_COLOR + ";")));
        
        backButton.setOnAction(e -> {
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
        field.setPrefWidth(350);
        field.setStyle(
            "-fx-padding: 15; " +
            "-fx-font-size: 16px; " +
            "-fx-background-color: " + ITEM_BACKGROUND + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
            "-fx-background-radius: 10;"
        );
        return field;
    }

    private static PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefWidth(350);
        field.setStyle(
            "-fx-padding: 15; " +
            "-fx-font-size: 16px; " +
            "-fx-background-color: " + ITEM_BACKGROUND + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
            "-fx-background-radius: 10;"
        );
        return field;
    }

    private static void showError(Label label, String message) {
        label.setText("❌ " + message);
        label.setTextFill(Color.web(BUTTON_RED));
    }

    private static void showSuccess(Label label, String message) {
        label.setText("✓ " + message);
        label.setTextFill(Color.web(PRIMARY_COLOR));
    }
    
    public static Thread getClientThread() {
        return clientThread;
    }
    
    public static Thread getAdminThread() {
        return adminThread;
    }
    
    public static boolean isClientThreadActive() {
        return clientThread != null && clientThread.isAlive();
    }
    
    public static boolean isAdminThreadActive() {
        return adminThread != null && adminThread.isAlive();
    }
}