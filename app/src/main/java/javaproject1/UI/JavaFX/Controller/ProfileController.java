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
import javaproject1.BLL.Service.implementation.*;
import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Repo.Implementation.AddressRepoImpl;
import javaproject1.DAL.Repo.Implementation.SubscriptionRepoImpl;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.Date;

public class ProfileController {

    private static final UserServiceImpl         userService      = new UserServiceImpl();
    private static final SubscriptionRepoImpl    subscriptionRepo = new SubscriptionRepoImpl();
    private static final AddressServiceImpl      addressService   = new AddressServiceImpl();
    private static final AddressRepoImpl         addressRepo      = new AddressRepoImpl();

    private static final String BACKGROUND_DARK     = "#1f2937";
    private static final String PRIMARY_COLOR       = "#059669";
    private static final String ACCENT_GOLD         = "#fcd34d";
    private static final String CARD_BACKGROUND     = "#374151";
    private static final String TEXT_COLOR_LIGHT    = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String SUCCESS_COLOR       = "#10b981";
    private static final String ERROR_COLOR         = "#ef4444";

    private static final String BUTTON_STYLE_BASE =
            "-fx-font-weight: bold; -fx-padding: 12 25; -fx-background-radius: 30; -fx-cursor: hand; " +
            "-fx-font-size: 16px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.2, 0, 2);";

    public static void show(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(50);
        contentBox.setPadding(new Insets(60, 40, 60, 40));
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setMaxWidth(1000);

        Label titleLabel = new Label(" My Profile");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 48));
        titleLabel.setTextFill(Color.web(ACCENT_GOLD));

        contentBox.getChildren().addAll(
                titleLabel,
                createProfileCard(stage, user),
                createSubscriptionCard(stage, user),
                createAddressesCard(stage, user));

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);
        stage.setScene(new Scene(root, 
            stage.getWidth() > 0 ? stage.getWidth() : 1200,
            stage.getHeight() > 0 ? stage.getHeight() : 750));
    }

    // ── Profile card ──────────────────────────────────────────────────────────

    private static VBox createProfileCard(Stage stage, User user) {
        VBox card = createCard("Personal Information", PRIMARY_COLOR);
        GridPane grid = new GridPane();
        grid.setHgap(40); grid.setVgap(25); grid.setAlignment(Pos.CENTER_LEFT);

        TextField nameField  = createStyledTextField(user.getName());
        TextField emailField = createStyledTextField(user.getEmail());
        emailField.setDisable(true);
        emailField.setStyle(emailField.getStyle() + " -fx-opacity: 0.7; -fx-background-color: #2b3543;");
        TextField phoneField = createStyledTextField(user.getPhoneNumber());
        TextField ageField   = createStyledTextField(String.valueOf(user.getAge()));

        int row = 0;
        addFormRow(grid, row++, "Name:", nameField);
        addFormRow(grid, row++, "Email:", emailField);
        addFormRow(grid, row++, "Phone:", phoneField);
        addFormRow(grid, row++, "Age:", ageField);

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 16));

        Button updateButton = new Button("Update Profile");
        updateButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: "
                + TEXT_COLOR_LIGHT + "; " + BUTTON_STYLE_BASE);

        updateButton.setOnAction(e -> {
            try {
                String name  = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                int age      = Integer.parseInt(ageField.getText().trim());

                if (name.isEmpty() || phone.isEmpty()) {
                    showError(messageLabel, "Name and phone cannot be empty!"); return;
                }
                userService.updateProfile(user, name, age, phone,
                        user.getEmail(), user.getPassword(), user.isElite(), user.getSubscription());
                showSuccess(messageLabel, "Profile updated successfully!");
            } catch (NumberFormatException ex) {
                showError(messageLabel, "Age must be a number!");
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
            }
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.getChildren().add(updateButton);
        card.getChildren().addAll(grid, buttonBox, messageLabel);
        return card;
    }

    // ── Subscription card ─────────────────────────────────────────────────────

    private static VBox createSubscriptionCard(Stage stage, User user) {
        VBox card = createCard("Elite Subscription", ACCENT_GOLD);
        VBox statusBox = new VBox(25);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        if (user.getSubscription() != null && user.getSubscription().isActive()) {
            Label statusLabel = new Label("Active Elite Member");
            statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            statusLabel.setTextFill(Color.web(SUCCESS_COLOR));

            VBox detailsBox = new VBox(12);
            detailsBox.setPadding(new Insets(20));
            detailsBox.setStyle("-fx-background-color: #2b3543; -fx-background-radius: 15; " +
                    "-fx-border-color: " + ACCENT_GOLD + "; -fx-border-width: 2; -fx-border-radius: 15;");

            detailsBox.getChildren().addAll(
                    createDetailLabel("Cost: $100/month", FontWeight.BOLD),
                    createDetailLabel("Discount Rate: 10%", FontWeight.NORMAL),
                    createDetailLabel("Expires: " + (user.getSubscription().getEndDate() != null
                            ? new java.text.SimpleDateFormat("MMM dd, yyyy")
                                    .format(user.getSubscription().getEndDate()) : "N/A"),
                            FontWeight.BOLD));

            Label messageLabel = new Label();
            Button cancelButton = new Button("Cancel Subscription");
            cancelButton.setStyle("-fx-background-color: " + ERROR_COLOR + "; -fx-text-fill: white; "
                    + BUTTON_STYLE_BASE.replace("16px", "14px"));

            cancelButton.setOnAction(e -> {
                new Alert(Alert.AlertType.CONFIRMATION,
                        "Cancel your Elite subscription?\nYou will lose all benefits.")
                        .showAndWait()
                        .ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    user.getSubscription().setActive(false);
                                    subscriptionRepo.updateSubscription(user.getSubscription());
                                    user.setElite(false);
                                    userService.updateUser(user);
                                    showSuccess(messageLabel, "Subscription cancelled.");
                                    scheduleRefresh(stage, user);
                                } catch (Exception ex) {
                                    showError(messageLabel, "Error: " + ex.getMessage());
                                }
                            }
                        });
            });

            statusBox.getChildren().addAll(statusLabel, detailsBox,
                    rowOf(cancelButton, messageLabel));

        } else {
            Label statusLabel = new Label("No Active Elite Subscription");
            statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            statusLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));

            Label priceLabel = new Label("Only $100/month");
            priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            priceLabel.setTextFill(Color.web(ACCENT_GOLD));

            Button subscribeButton = new Button("Subscribe Now");
            subscribeButton.setStyle("-fx-background-color: " + ACCENT_GOLD + "; -fx-text-fill: "
                    + BACKGROUND_DARK + "; " + BUTTON_STYLE_BASE);

            Label messageLabel = new Label();

            subscribeButton.setOnAction(e -> showCreditCardDialog(stage, user, messageLabel));

            statusBox.getChildren().addAll(statusLabel, priceLabel,
                    rowOf(subscribeButton, messageLabel));
        }

        card.getChildren().add(statusBox);
        return card;
    }

    private static void showCreditCardDialog(Stage stage, User user, Label messageLabel) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Elite Subscription Payment");
        dialog.setHeaderText("Enter your credit card information\nSubscription Cost: $100/month");
        dialog.getDialogPane().setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        ButtonType okType = new ButtonType("Subscribe", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(15); grid.setPadding(new Insets(20));

        TextField cardNum  = createStyledTextField("1234 5678 9012 3456");
        TextField holder   = createStyledTextField("John Doe");
        TextField expiry   = createStyledTextField("MM/YY");
        TextField cvv      = createStyledTextField("123");

        grid.add(createLabel("Card Number:"), 0, 0); grid.add(cardNum, 1, 0);
        grid.add(createLabel("Card Holder:"),  0, 1); grid.add(holder, 1, 1);
        grid.add(createLabel("Expiry Date:"),  0, 2); grid.add(expiry, 1, 2);
        grid.add(createLabel("CVV:"),          0, 3); grid.add(cvv,   1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response != okType) return;

            String cardNumber = cardNum.getText().trim().replaceAll("\\s+", "");
            if (cardNum.getText().trim().isEmpty() || holder.getText().trim().isEmpty()
                    || expiry.getText().trim().isEmpty() || cvv.getText().trim().isEmpty()) {
                showError(messageLabel, "All credit card fields are required!"); return;
            }
            if (!cardNumber.matches("\\d{16}")) {
                showError(messageLabel, "Card number must be 16 digits!"); return;
            }
            if (!cvv.getText().trim().matches("\\d{3,4}")) {
                showError(messageLabel, "CVV must be 3 or 4 digits!"); return;
            }

            try {
                Calendar cal = Calendar.getInstance();
                Date startDate = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                Date endDate = cal.getTime();

                Subscription newSub = new Subscription(startDate, endDate);
                newSub.setActive(true);
                subscriptionRepo.addSubscription(newSub);

                // Link subscription to user via JPA (no raw SQL)
                linkSubscriptionToUser(newSub.getId(), Integer.parseInt(user.getId()));

                user.setElite(true);
                user.setSubscription(newSub);
                userService.updateUser(user);

                showSuccess(messageLabel, "Subscription activated! Payment processed.");
                scheduleRefresh(stage, user);
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    /**
     * Sets user_id on the subscription row via JPA — no raw SQL.
     */
    private static void linkSubscriptionToUser(int subscriptionId, int userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            javaproject1.plato.Subscriptions s =
                    em.find(javaproject1.plato.Subscriptions.class, subscriptionId);
            javaproject1.plato.Users u =
                    em.find(javaproject1.plato.Users.class, userId);
            if (s != null && u != null) {
                s.setUserId(u);
                em.merge(s);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Error linking subscription: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // ── Addresses card ────────────────────────────────────────────────────────

    private static VBox createAddressesCard(Stage stage, User user) {
        VBox card = createCard("My Delivery Addresses", PRIMARY_COLOR);
        VBox addressList = new VBox(15);

        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            for (Address address : user.getAddresses()) {
                HBox row = new HBox(20);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(15));
                row.setStyle("-fx-background-color: #2b3543; -fx-background-radius: 12;");

                Label lbl = new Label(address.toString());
                lbl.setFont(Font.font("System", 16));
                lbl.setTextFill(Color.web(TEXT_COLOR_LIGHT));
                HBox.setHgrow(lbl, Priority.ALWAYS);

                Button deleteBtn = new Button("Remove");
                deleteBtn.setStyle("-fx-background-color: " + ERROR_COLOR + "; -fx-text-fill: white; " +
                        "-fx-padding: 8 15; -fx-background-radius: 15; -fx-cursor: hand; -fx-font-size: 14px;");

                deleteBtn.setOnAction(e ->
                        new Alert(Alert.AlertType.CONFIRMATION, "Remove this address?")
                                .showAndWait()
                                .ifPresent(r -> {
                                    if (r == ButtonType.OK) {
                                        try {
                                            userService.removeAddress(user, address);
                                            addressService.deleteAddress(
                                                    Integer.parseInt(address.getId()));
                                            show(stage, user);
                                        } catch (Exception ex) {
                                            new Alert(Alert.AlertType.ERROR,
                                                    "Error: " + ex.getMessage()).showAndWait();
                                        }
                                    }
                                }));

                row.getChildren().addAll(lbl, deleteBtn);
                addressList.getChildren().add(row);
            }
        } else {
            Label none = new Label("No addresses yet. Add your first delivery location!");
            none.setFont(Font.font("System", 18));
            none.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
            addressList.getChildren().add(none);
        }

        // Add address form
        VBox addBox = new VBox(20);
        addBox.setPadding(new Insets(25, 0, 0, 0));
        addBox.setStyle("-fx-border-color: #4b5563; -fx-border-width: 1 0 0 0;");

        Label addTitle = new Label("Add New Address");
        addTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        addTitle.setTextFill(Color.web(ACCENT_GOLD));

        GridPane addGrid = new GridPane();
        addGrid.setHgap(20); addGrid.setVgap(20);

        TextField streetField   = createStyledTextField("Street Name");
        TextField cityField     = createStyledTextField("City Name");
        TextField buildingField = createStyledTextField("Building Number");

        addGrid.add(createLabel("Street:"),   0, 0); addGrid.add(streetField,   1, 0);
        addGrid.add(createLabel("City:"),     0, 1); addGrid.add(cityField,     1, 1);
        addGrid.add(createLabel("Building:"), 0, 2); addGrid.add(buildingField, 1, 2);

        Button addButton = new Button("Save Address");
        addButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; "
                + BUTTON_STYLE_BASE.replace("16px", "14px"));

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 16));

        addButton.setOnAction(e -> {
            try {
                String street = streetField.getText().trim();
                String city   = cityField.getText().trim();
                String bText  = buildingField.getText().trim();

                if (street.isEmpty() || city.isEmpty() || bText.isEmpty()) {
                    showError(messageLabel, "All fields are required!"); return;
                }

                int building = Integer.parseInt(bText);
                Address newAddress = new Address(street, city, building);
                addressService.addAddress(newAddress);           // persists & sets id

                // Link address to user via JPA — no raw SQL
                linkAddressToUser(Integer.parseInt(newAddress.getId()),
                        Integer.parseInt(user.getId()));

                userService.addAddress(user, newAddress);

                streetField.clear(); cityField.clear(); buildingField.clear();
                showSuccess(messageLabel, "Address added successfully!");
                scheduleRefresh(stage, user);
            } catch (NumberFormatException ex) {
                showError(messageLabel, "Building number must be a number!");
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        addBox.getChildren().addAll(addTitle, addGrid, addButton, messageLabel);
        card.getChildren().addAll(addressList, addBox);
        return card;
    }

    /**
     * Inserts a row into user_addresses via JPA — no raw SQL.
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
                // The ManyToMany is owned by Users.addressSet — add and merge
                if (u.getAddressSet() == null) {
                    u.setAddressSet(new java.util.HashSet<>());
                }
                u.getAddressSet().add(a);
                em.merge(u);
            }

            em.getTransaction().commit();
            System.out.println("Linked address " + addressId + " to user " + userId);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Error linking address: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    private static void scheduleRefresh(Stage stage, User user) {
        new Thread(() -> {
            try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> show(stage, user));
        }).start();
    }

    private static HBox rowOf(javafx.scene.Node... nodes) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(nodes);
        return row;
    }

    private static VBox createCard(String title, String titleColor) {
        VBox card = new VBox(30);
        card.setPadding(new Insets(40));
        card.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-background-radius: 20; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 20, 0, 0, 8);");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web(titleColor));

        Region sep = new Region();
        sep.setMinHeight(4); sep.setPrefWidth(100);
        sep.setStyle("-fx-background-color: " + ACCENT_GOLD + "; -fx-background-radius: 2;");

        VBox header = new VBox(10);
        header.getChildren().addAll(titleLabel, sep);
        card.getChildren().add(header);
        return card;
    }

    private static TextField createStyledTextField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefWidth(350);
        f.setStyle("-fx-padding: 12; -fx-font-size: 15px; -fx-background-color: #4b5563; " +
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                "-fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
                "-fx-border-color: #6b7280; -fx-border-radius: 10; -fx-background-radius: 10;");
        return f;
    }

    private static Label createLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        l.setTextFill(Color.web(TEXT_COLOR_LIGHT));
        return l;
    }

    private static Label createDetailLabel(String text, FontWeight w) {
        Label l = new Label(text);
        l.setFont(Font.font("System", w, 16));
        l.setTextFill(Color.web(TEXT_COLOR_LIGHT));
        return l;
    }

    private static void addFormRow(GridPane grid, int row, String labelText, TextField field) {
        grid.add(createLabel(labelText), 0, row);
        grid.add(field, 1, row);
    }

    private static void showError(Label label, String msg) {
        label.setText(msg);
        label.setTextFill(Color.web(ERROR_COLOR));
    }

    private static void showSuccess(Label label, String msg) {
        label.setText(msg);
        label.setTextFill(Color.web(SUCCESS_COLOR));
    }
}