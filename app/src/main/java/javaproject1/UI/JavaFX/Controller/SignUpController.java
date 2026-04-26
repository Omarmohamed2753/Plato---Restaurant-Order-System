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
import javaproject1.BLL.Service.implementation.AddressServiceImpl;
import javaproject1.BLL.Service.implementation.UserServiceImpl;
import javaproject1.DAL.Entity.Address;
import javaproject1.DAL.Entity.User;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.util.Set;

public class SignUpController {

    private static final UserServiceImpl    userService    = new UserServiceImpl();
    private static final AddressServiceImpl addressService = new AddressServiceImpl();

    private static final String BACKGROUND_DARK      = "#1f2937";
    private static final String PRIMARY_COLOR        = "#059669";
    private static final String ACCENT_GOLD          = "#fcd34d";
    private static final String CARD_BACKGROUND      = "#374151";
    private static final String TEXT_COLOR_LIGHT     = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String ITEM_BACKGROUND      = "#2b3543";
    private static final String ERROR_COLOR          = "#ef4444";
    private static final String SUCCESS_COLOR        = "#10b981";

    public static void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");
        root.setTop(createHeader(stage));

        VBox formBox = new VBox(25);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setMaxWidth(650);

        Label titleLabel = new Label("Create Account");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        titleLabel.setTextFill(Color.web(ACCENT_GOLD));

        VBox cardBox = new VBox(25);
        cardBox.setPadding(new Insets(40));
        cardBox.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-background-radius: 20; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);");

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20); grid.setAlignment(Pos.CENTER);

        TextField     nameField            = createTextField("Full Name");
        TextField     emailField           = createTextField("Email (e.g. user@example.com)");
        PasswordField passwordField        = createPasswordField("Password (min 6 characters)");
        PasswordField confirmPasswordField = createPasswordField("Confirm Password");
        TextField     phoneField           = createTextField("Phone (010/011/012/015 + 8 digits)");
        TextField     ageField             = createTextField("Age");
        TextField     streetField          = createTextField("Street");
        TextField     cityField            = createTextField("City");
        TextField     buildingField        = createTextField("Building Number");

        Label emailValidLabel = createInlineValidLabel();
        Label phoneValidLabel = createInlineValidLabel();

        emailField.textProperty().addListener((obs, o, n) -> {
            if (n.isEmpty()) { emailValidLabel.setText(""); return; }
            if (isValidEmail(n)) {
                emailValidLabel.setText("Valid email");
                emailValidLabel.setTextFill(Color.web(SUCCESS_COLOR));
            } else {
                emailValidLabel.setText("Enter a valid email (e.g. user@example.com)");
                emailValidLabel.setTextFill(Color.web(ERROR_COLOR));
            }
        });

        phoneField.textProperty().addListener((obs, o, n) -> {
            String digits = n.replaceAll("[^\\d]", "");
            if (!digits.equals(n)) { phoneField.setText(digits); return; }
            if (n.length() > 11) { phoneField.setText(n.substring(0, 11)); return; }
            if (n.isEmpty()) { phoneValidLabel.setText(""); return; }
            if (isValidEgyptianPhone(n)) {
                phoneValidLabel.setText("Valid phone number");
                phoneValidLabel.setTextFill(Color.web(SUCCESS_COLOR));
            } else if (n.length() < 11) {
                phoneValidLabel.setText("Enter 11 digits starting with 010, 011, 012, or 015");
                phoneValidLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
            } else {
                phoneValidLabel.setText("Must start with 010, 011, 012, or 015");
                phoneValidLabel.setTextFill(Color.web(ERROR_COLOR));
            }
        });

        int row = 0;
        grid.add(createFormLabel("Name:"),    0, row); grid.add(nameField, 1, row++);
        grid.add(createFormLabel("Email:"),   0, row); grid.add(new VBox(4, emailField, emailValidLabel), 1, row++);
        grid.add(createFormLabel("Password:"),0, row); grid.add(passwordField, 1, row++);
        grid.add(createFormLabel("Confirm:"), 0, row); grid.add(confirmPasswordField, 1, row++);
        grid.add(createFormLabel("Phone:"),   0, row); grid.add(new VBox(4, phoneField, phoneValidLabel), 1, row++);
        grid.add(createFormLabel("Age:"),     0, row); grid.add(ageField, 1, row++);

        Label addressSep = new Label("Delivery Address");
        addressSep.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        addressSep.setTextFill(Color.web(PRIMARY_COLOR));
        grid.add(addressSep, 0, row++, 2, 1);

        grid.add(createFormLabel("Street:"),   0, row); grid.add(streetField,   1, row++);
        grid.add(createFormLabel("City:"),     0, row); grid.add(cityField,     1, row++);
        grid.add(createFormLabel("Building:"), 0, row); grid.add(buildingField, 1, row++);

        Button signUpButton = new Button("Create Account");
        signUpButton.setPrefWidth(250); signUpButton.setPrefHeight(50);
        signUpButton.setStyle("-fx-background-color: " + SUCCESS_COLOR + "; -fx-text-fill: white; " +
                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 25; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);");

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 15));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(500);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        signUpButton.setOnAction(e -> {
            try {
                String name     = nameField.getText().trim();
                String email    = emailField.getText().trim();
                String password = passwordField.getText();
                String confirmP = confirmPasswordField.getText();
                String phone    = phoneField.getText().trim();
                String ageText  = ageField.getText().trim();
                String street   = streetField.getText().trim();
                String city     = cityField.getText().trim();
                String bText    = buildingField.getText().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                        phone.isEmpty() || ageText.isEmpty() || street.isEmpty() ||
                        city.isEmpty() || bText.isEmpty()) {
                    showError(messageLabel, "All fields are required!"); return;
                }
                if (!isValidEmail(email)) {
                    showError(messageLabel, "Please enter a valid email address."); return;
                }
                if (!isValidEgyptianPhone(phone)) {
                    showError(messageLabel,
                            "Phone must be 11 digits starting with 010, 011, 012, or 015"); return;
                }
                if (!password.equals(confirmP)) {
                    showError(messageLabel, "Passwords do not match!"); return;
                }
                if (password.length() < 6) {
                    showError(messageLabel, "Password must be at least 6 characters!"); return;
                }

                int age      = Integer.parseInt(ageText);
                int building = Integer.parseInt(bText);

                if (age < 1 || age > 120) {
                    showError(messageLabel, "Please enter a valid age (1-120)!"); return;
                }

                // Persist address
                Address address = new Address(street, city, building);
                addressService.addAddress(address);

                // Create user (also creates cart row via UserRepoImpl.addUser)
                User user = new User(null, name, age, phone, email, password, null);
                boolean success = userService.createAccount(user);

                if (success) {
                    // Link address to user via JPA — no raw SQL
                    linkAddressToUser(Integer.parseInt(address.getId()),
                            Integer.parseInt(user.getId()));

                    showSuccess(messageLabel, "Account created! Redirecting to sign in...");
                    new Thread(() -> {
                        try { Thread.sleep(1500); } catch (InterruptedException ex2) { /* ignore */ }
                        javafx.application.Platform.runLater(() -> SignInController.show(stage));
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
        scrollPane.setStyle("-fx-background: " + BACKGROUND_DARK + "; -fx-background-color: transparent;");

        root.setCenter(scrollPane);
        stage.setScene(new Scene(root, 
            stage.getWidth() > 0 ? stage.getWidth() : 1200,
            stage.getHeight() > 0 ? stage.getHeight() : 750));
    }

    // ── JPA helper — replaces raw "INSERT INTO user_addresses" ────────────────

    /**
     * Adds the address to the user's address set via JPA (ManyToMany).
     * This writes the user_addresses junction row without any raw SQL.
     */
    private static void linkAddressToUser(int addressId, int userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            javaproject1.plato.Users u =
                    em.find(javaproject1.plato.Users.class, userId);
            javaproject1.plato.Address a =
                    em.find(javaproject1.plato.Address.class, addressId);

            if (u != null && a != null) {
                if (u.getAddressSet() == null) u.setAddressSet(new java.util.HashSet<>());
                u.getAddressSet().add(a);
                em.merge(u);
                System.out.println("Linked address " + addressId + " to user " + userId);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Error linking address to user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private static final Set<String> ALLOWED_DOMAINS = Set.of(
            "gmail.com", "yahoo.com", "outlook.com",
            "hotmail.com", "live.com", "icloud.com");

    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+\\.com$")) return false;
        String domain = email.substring(email.indexOf("@") + 1);
        return ALLOWED_DOMAINS.contains(domain);
    }

    private static boolean isValidEgyptianPhone(String phone) {
        if (phone == null) return false;
        return phone.matches("^(010|011|012|015)\\d{8}$");
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private static HBox createHeader(Stage stage) {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");

        Button back = new Button("Back");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_COLOR + "; " +
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        back.setOnAction(e -> WelcomeController.show(stage));
        header.getChildren().add(back);
        return header;
    }

    private static TextField createTextField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt); f.setPrefWidth(350);
        f.setStyle("-fx-padding: 12; -fx-font-size: 15px; -fx-background-color: " + ITEM_BACKGROUND + "; " +
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
                "-fx-background-radius: 8;");
        return f;
    }

    private static PasswordField createPasswordField(String prompt) {
        PasswordField f = new PasswordField();
        f.setPromptText(prompt); f.setPrefWidth(350);
        f.setStyle("-fx-padding: 12; -fx-font-size: 15px; -fx-background-color: " + ITEM_BACKGROUND + "; " +
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
                "-fx-background-radius: 8;");
        return f;
    }

    private static Label createFormLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        l.setTextFill(Color.web(TEXT_COLOR_LIGHT));
        return l;
    }

    private static Label createInlineValidLabel() {
        Label l = new Label();
        l.setFont(Font.font("System", 13));
        l.setWrapText(true); l.setMaxWidth(350);
        return l;
    }

    private static void showError(Label l, String msg) {
        l.setText(msg); l.setTextFill(Color.web(ERROR_COLOR));
    }

    private static void showSuccess(Label l, String msg) {
        l.setText(msg); l.setTextFill(Color.web(SUCCESS_COLOR));
    }
}