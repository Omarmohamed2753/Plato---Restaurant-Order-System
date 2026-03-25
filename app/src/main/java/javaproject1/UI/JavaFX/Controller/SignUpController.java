package javaproject1.UI.JavaFX.Controller;

import javafx.geometry.Insets;
import java.util.Set;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javaproject1.BLL.Service.implementation.AddressServiceImpl;
import javaproject1.BLL.Service.implementation.UserServiceImpl;
import javaproject1.DAL.Entity.Address;
import javaproject1.DAL.Entity.User;

public class SignUpController {

    private static UserServiceImpl userService = new UserServiceImpl();
    private static AddressServiceImpl addressService = new AddressServiceImpl();

    // Theme Colors
    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String CARD_BACKGROUND = "#374151";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String ITEM_BACKGROUND = "#2b3543";
    private static final String ERROR_COLOR = "#ef4444";
    private static final String SUCCESS_COLOR = "#10b981";

    public static void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        HBox header = createHeader(stage);
        root.setTop(header);

        VBox formBox = new VBox(25);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setMaxWidth(650);

        Label titleLabel = new Label("Create Account");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        titleLabel.setTextFill(Color.web(ACCENT_GOLD));

        VBox cardBox = new VBox(25);
        cardBox.setPadding(new Insets(40));
        cardBox.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-background-radius: 20; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        TextField nameField     = createTextField("Full Name");
        TextField emailField    = createTextField("Email (e.g. user@example.com)");
        PasswordField passwordField        = createPasswordField("Password (min 6 characters)");
        PasswordField confirmPasswordField = createPasswordField("Confirm Password");
        TextField phoneField    = createTextField("Phone (010/011/012/015 + 8 digits)");
        TextField ageField      = createTextField("Age");
        TextField streetField   = createTextField("Street");
        TextField cityField     = createTextField("City");
        TextField buildingField = createTextField("Building Number");

        // Inline validation hint labels
        Label emailValidLabel = createInlineValidLabel();
        Label phoneValidLabel = createInlineValidLabel();

        // ── Real-time email validation ────────────────────────────────────────
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                emailValidLabel.setText("");
            } else if (isValidEmail(newVal)) {
                emailValidLabel.setText("✓ Valid email");
                emailValidLabel.setTextFill(Color.web(SUCCESS_COLOR));
            } else {
                emailValidLabel.setText("✗ Enter a valid email (e.g. user@example.com)");
                emailValidLabel.setTextFill(Color.web(ERROR_COLOR));
            }
        });

        // ── Real-time phone validation (digits only, max 11) ─────────────────
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Strip any non-digit characters immediately
            String digitsOnly = newVal.replaceAll("[^\\d]", "");
            if (!digitsOnly.equals(newVal)) {
                phoneField.setText(digitsOnly);
                return;
            }
            // Enforce max length of 11
            if (newVal.length() > 11) {
                phoneField.setText(newVal.substring(0, 11));
                return;
            }

            if (newVal.isEmpty()) {
                phoneValidLabel.setText("");
            } else if (isValidEgyptianPhone(newVal)) {
                phoneValidLabel.setText("✓ Valid phone number");
                phoneValidLabel.setTextFill(Color.web(SUCCESS_COLOR));
            } else if (newVal.length() < 11) {
                phoneValidLabel.setText("Enter 11 digits starting with 010, 011, 012, or 015");
                phoneValidLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
            } else {
                phoneValidLabel.setText("✗ Must start with 010, 011, 012, or 015");
                phoneValidLabel.setTextFill(Color.web(ERROR_COLOR));
            }
        });

        // ── Build grid rows ───────────────────────────────────────────────────
        int row = 0;

        grid.add(createFormLabel("Name:"), 0, row);
        grid.add(nameField, 1, row++);

        grid.add(createFormLabel("Email:"), 0, row);
        VBox emailBox = new VBox(4, emailField, emailValidLabel);
        grid.add(emailBox, 1, row++);

        grid.add(createFormLabel("Password:"), 0, row);
        grid.add(passwordField, 1, row++);

        grid.add(createFormLabel("Confirm:"), 0, row);
        grid.add(confirmPasswordField, 1, row++);

        grid.add(createFormLabel("Phone:"), 0, row);
        VBox phoneBox = new VBox(4, phoneField, phoneValidLabel);
        grid.add(phoneBox, 1, row++);

        grid.add(createFormLabel("Age:"), 0, row);
        grid.add(ageField, 1, row++);

        Label addressSeparator = new Label("📍 Delivery Address");
        addressSeparator.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        addressSeparator.setTextFill(Color.web(PRIMARY_COLOR));
        grid.add(addressSeparator, 0, row++, 2, 1);

        grid.add(createFormLabel("Street:"), 0, row);
        grid.add(streetField, 1, row++);

        grid.add(createFormLabel("City:"), 0, row);
        grid.add(cityField, 1, row++);

        grid.add(createFormLabel("Building:"), 0, row);
        grid.add(buildingField, 1, row++);

        // ── Sign-up button ────────────────────────────────────────────────────
        Button signUpButton = new Button("Create Account ✓");
        signUpButton.setPrefWidth(250);
        signUpButton.setPrefHeight(50);
        signUpButton.setStyle(
            "-fx-background-color: " + SUCCESS_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );
        signUpButton.setOnMouseEntered(e ->
            signUpButton.setStyle(signUpButton.getStyle() + "-fx-background-color: #059669;"));
        signUpButton.setOnMouseExited(e ->
            signUpButton.setStyle(signUpButton.getStyle()
                .replace("-fx-background-color: #059669;", "-fx-background-color: " + SUCCESS_COLOR + ";")));

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 15));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(500);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        signUpButton.setOnAction(e -> {
            try {
                String name         = nameField.getText().trim();
                String email        = emailField.getText().trim();
                String password     = passwordField.getText();
                String confirmPwd   = confirmPasswordField.getText();
                String phone        = phoneField.getText().trim();
                String ageText      = ageField.getText().trim();
                String street       = streetField.getText().trim();
                String city         = cityField.getText().trim();
                String buildingText = buildingField.getText().trim();

                // ── Blank check ───────────────────────────────────────────────
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                        phone.isEmpty() || ageText.isEmpty() || street.isEmpty() ||
                        city.isEmpty() || buildingText.isEmpty()) {
                    showError(messageLabel, "All fields are required!");
                    return;
                }

                // ── Email validation ──────────────────────────────────────────
                if (!isValidEmail(email)) {
                    showError(messageLabel,
                        "Please enter a valid email address (e.g. user@example.com)");
                    emailField.requestFocus();
                    return;
                }

                // ── Phone validation ──────────────────────────────────────────
                if (!isValidEgyptianPhone(phone)) {
                    showError(messageLabel,
                        "Phone must be 11 digits and start with 010, 011, 012, or 015");
                    phoneField.requestFocus();
                    return;
                }

                // ── Password checks ───────────────────────────────────────────
                if (!password.equals(confirmPwd)) {
                    showError(messageLabel, "Passwords do not match!");
                    return;
                }
                if (password.length() < 6) {
                    showError(messageLabel, "Password must be at least 6 characters!");
                    return;
                }

                int age = Integer.parseInt(ageText);
                if (age < 1 || age > 120) {
                    showError(messageLabel, "Please enter a valid age (1-120)!");
                    return;
                }

                int building = Integer.parseInt(buildingText);

                // ── Persist the address first so we get its generated ID ──────
                Address address = new Address(street, city, building);
                addressService.addAddress(address);   // sets address.getId()

                // ── Create the user account (also creates the cart row) ───────
                User user = new User(null, name, age, phone, email, password, null);
                boolean success = userService.createAccount(user);

                if (success) {
                    // ── Link address → user in user_addresses junction table ──
                    if (address.getId() != null && user.getId() != null) {
                        try (java.sql.Connection conn =
                                     javaproject1.DAL.DataBase.DBConnection.getConnection();
                             java.sql.PreparedStatement stmt = conn.prepareStatement(
                                     "INSERT INTO user_addresses (user_id, address_id) VALUES (?, ?)")) {
                            stmt.setInt(1, Integer.parseInt(user.getId()));
                            stmt.setString(2, address.getId());
                            stmt.executeUpdate();
                            System.out.println("✓ Address " + address.getId()
                                    + " linked to user " + user.getId());
                        } catch (Exception ex) {
                            System.err.println("Error linking address to user: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }

                    showSuccess(messageLabel,
                        "Account created successfully! Redirecting to sign in...");

                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            javafx.application.Platform.runLater(() ->
                                    SignInController.show(stage));
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();

                } else {
                    showError(messageLabel, "Email already exists!");
                }

            } catch (NumberFormatException ex) {
                showError(messageLabel, "Age and Building must be valid numbers!");
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(signUpButton, messageLabel);

        cardBox.getChildren().addAll(grid, buttonBox);
        formBox.getChildren().addAll(titleLabel, cardBox);

        ScrollPane scrollPane = new ScrollPane(formBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: " + BACKGROUND_DARK + "; -fx-background-color: transparent;");

        root.setCenter(scrollPane);
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    // ── Validation helpers ────────────────────────────────────────────────────

    /**
     * Validates email using a standard pattern:
     * local-part @ domain . TLD  (TLD >= 2 chars).
     */
    private static final Set<String> ALLOWED_DOMAINS = Set.of(
        "gmail.com",
        "yahoo.com",
        "outlook.com",
        "hotmail.com",
        "live.com",
        "icloud.com"
    );
    
    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
    
        // 1. Check format + .com
        boolean validFormat = email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+\\.com$");
        if (!validFormat) return false;
    
        // 2. Check domain is popular
        String domain = email.substring(email.indexOf("@") + 1);
        return ALLOWED_DOMAINS.contains(domain);
    }

    /**
     * Egyptian mobile numbers: exactly 11 digits, prefix 010 / 011 / 012 / 015.
     */
    private static boolean isValidEgyptianPhone(String phone) {
        if (phone == null) return false;
        return phone.matches("^(010|011|012|015)\\d{8}$");
    }

    // ── UI factory helpers ────────────────────────────────────────────────────

    private static Label createInlineValidLabel() {
        Label label = new Label();
        label.setFont(Font.font("System", 13));
        label.setWrapText(true);
        label.setMaxWidth(350);
        return label;
    }

    private static HBox createHeader(Stage stage) {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setStyle("-fx-background-color: " + CARD_BACKGROUND
                + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 3);");

        Button backButton = new Button("← Back");
        backButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + PRIMARY_COLOR + "; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-cursor: hand;"
        );
        backButton.setOnMouseEntered(e ->
            backButton.setStyle(backButton.getStyle() + "-fx-text-fill: " + ACCENT_GOLD + ";"));
        backButton.setOnMouseExited(e ->
            backButton.setStyle(backButton.getStyle()
                .replace("-fx-text-fill: " + ACCENT_GOLD + ";",
                         "-fx-text-fill: " + PRIMARY_COLOR + ";")));
        backButton.setOnAction(e -> WelcomeController.show(stage));

        header.getChildren().add(backButton);
        return header;
    }

    private static TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(350);
        field.setStyle(
            "-fx-padding: 12; " +
            "-fx-font-size: 15px; " +
            "-fx-background-color: " + ITEM_BACKGROUND + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
            "-fx-background-radius: 8;"
        );
        return field;
    }

    private static PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefWidth(350);
        field.setStyle(
            "-fx-padding: 12; " +
            "-fx-font-size: 15px; " +
            "-fx-background-color: " + ITEM_BACKGROUND + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
            "-fx-background-radius: 8;"
        );
        return field;
    }

    private static Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.web(TEXT_COLOR_LIGHT));
        return label;
    }

    private static void showError(Label label, String message) {
        label.setText("❌ " + message);
        label.setTextFill(Color.web(ERROR_COLOR));
    }

    private static void showSuccess(Label label, String message) {
        label.setText("✓ " + message);
        label.setTextFill(Color.web(SUCCESS_COLOR));
    }
}