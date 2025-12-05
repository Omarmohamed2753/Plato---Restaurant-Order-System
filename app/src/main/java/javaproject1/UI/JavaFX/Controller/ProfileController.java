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
import javaproject1.DAL.Repo.Implementation.SubscriptionRepoImpl;

import java.util.Date;
import java.util.Calendar;

public class ProfileController {

    private static UserServiceImpl userService = new UserServiceImpl();
    private static SubscriptionRepoImpl subscriptionRepo = new SubscriptionRepoImpl();
    private static AddressServiceImpl addressService = new AddressServiceImpl();

    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String CARD_BACKGROUND = "#374151";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String SUCCESS_COLOR = "#10b981";
    private static final String ERROR_COLOR = "#ef4444";
    
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

        VBox profileCard = createProfileCard(stage, user);
        VBox subscriptionCard = createSubscriptionCard(stage, user);
        VBox addressesCard = createAddressesCard(stage, user);

        contentBox.getChildren().addAll(titleLabel, profileCard, subscriptionCard, addressesCard);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
    }

    private static VBox createCard(String title, String titleColor) {
        VBox card = new VBox(30);
        card.setPadding(new Insets(40));
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-background-radius: 20; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 20, 0, 0, 8);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web(titleColor));
        
        Region separator = new Region();
        separator.setMinHeight(4);
        separator.setPrefWidth(100);
        separator.setStyle("-fx-background-color: " + ACCENT_GOLD + "; -fx-background-radius: 2;");
        
        VBox header = new VBox(10);
        header.getChildren().addAll(titleLabel, separator);

        card.getChildren().add(header);
        
        return card;
    }

    private static VBox createProfileCard(Stage stage, User user) {
        VBox card = createCard("Personal Information", PRIMARY_COLOR);

        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(25);
        grid.setAlignment(Pos.CENTER_LEFT);

        TextField nameField = createStyledTextField(user.getName());
        TextField emailField = createStyledTextField(user.getEmail());
        emailField.setDisable(true);
        emailField.setStyle(emailField.getStyle() + " -fx-opacity: 0.7; -fx-background-color: #2b3543;");
        TextField phoneField = createStyledTextField(user.getPhoneNumber());
        TextField ageField = createStyledTextField(String.valueOf(user.getAge()));

        int row = 0;
        addFormRow(grid, row++, "Name:", nameField);
        addFormRow(grid, row++, "Email:", emailField);
        addFormRow(grid, row++, "Phone:", phoneField);
        addFormRow(grid, row++, "Age:", ageField);

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 16));
        messageLabel.setPadding(new Insets(10, 0, 0, 0));

        Button updateButton = new Button("Update Profile");
        updateButton.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            BUTTON_STYLE_BASE
        );
        updateButton.setOnMouseEntered(e -> updateButton.setStyle(updateButton.getStyle() + "-fx-background-color: " + SUCCESS_COLOR + ";"));
        updateButton.setOnMouseExited(e -> updateButton.setStyle(updateButton.getStyle().replace("-fx-background-color: " + SUCCESS_COLOR + ";", "-fx-background-color: " + PRIMARY_COLOR + ";")));

        updateButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());

                if (name.isEmpty() || phone.isEmpty()) {
                    showError(messageLabel, "Name and phone cannot be empty!");
                    return;
                }

                userService.updateProfile(user, name, age, phone, 
                    user.getEmail(), user.getPassword(), user.isElite(), user.getSubscription());
                
                showSuccess(messageLabel, "Profile updated successfully!");
                
                User refreshedUser = userService.getUserById(Integer.parseInt(user.getId()));
                if (refreshedUser != null) {
                    user.setName(refreshedUser.getName());
                    user.setAge(refreshedUser.getAge());
                    user.setPhoneNumber(refreshedUser.getPhoneNumber());
                }
            } catch (NumberFormatException ex) {
                showError(messageLabel, "Age must be a number!");
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
            }
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().add(updateButton);

        card.getChildren().addAll(grid, buttonBox, messageLabel);
        return card;
    }

    private static VBox createSubscriptionCard(Stage stage, User user) {
        VBox card = createCard("👑 Elite Subscription", ACCENT_GOLD);

        VBox statusBox = new VBox(25);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        if (user.getSubscription() != null && user.getSubscription().isActive()) {
            Label statusLabel = new Label("✓ Active Elite Member");
            statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            statusLabel.setTextFill(Color.web(SUCCESS_COLOR));

            VBox detailsBox = new VBox(12);
            detailsBox.setPadding(new Insets(20));
            detailsBox.setStyle(
                "-fx-background-color: #2b3543; " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: " + ACCENT_GOLD + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 15;"
            );

            Label costLabel = createDetailLabel("Cost: $100/month", FontWeight.BOLD);
            Label discountLabel = createDetailLabel("Discount Rate: 10%", FontWeight.NORMAL);
            Label startLabel = createDetailLabel("Start Date: " + 
                (user.getSubscription().getStartDate() != null ? 
                    new java.text.SimpleDateFormat("MMM dd, yyyy").format(user.getSubscription().getStartDate()) : "N/A"), FontWeight.NORMAL);
            Label endLabel = createDetailLabel("Expires: " + 
                (user.getSubscription().getEndDate() != null ? 
                    new java.text.SimpleDateFormat("MMM dd, yyyy").format(user.getSubscription().getEndDate()) : "N/A"), FontWeight.BOLD);
            endLabel.setTextFill(Color.web(ERROR_COLOR));

            detailsBox.getChildren().addAll(costLabel, discountLabel, startLabel, endLabel);

            Label benefitsLabel = new Label("Benefits: 10% discount on all orders • Priority delivery • Exclusive offers");
            benefitsLabel.setFont(Font.font("System", 16));
            benefitsLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));

            Button cancelButton = new Button("Cancel Subscription");
            cancelButton.setStyle(
                "-fx-background-color: " + ERROR_COLOR + "; " +
                "-fx-text-fill: white; " +
                BUTTON_STYLE_BASE.replace("16px", "14px")
            );
            
            Label messageLabel = new Label();
            messageLabel.setFont(Font.font("System", 16));

            cancelButton.setOnAction(e -> {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
                    "Are you sure you want to cancel your Elite subscription?\n\nYou will lose:\n" +
                    "• 10% discount on all orders\n• Priority delivery\n• Exclusive offers");
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Subscription sub = user.getSubscription();
                            sub.setActive(false);
                            subscriptionRepo.updateSubscription(sub);
                            user.setElite(false);
                            userService.updateUser(user);
                            
                            showSuccess(messageLabel, "Subscription cancelled successfully!");
                            
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1500);
                                    javafx.application.Platform.runLater(() -> show(stage, user));
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        } catch (Exception ex) {
                            showError(messageLabel, "Error cancelling subscription: " + ex.getMessage());
                        }
                    }
                });
            });

            HBox buttonContainer = new HBox(cancelButton, messageLabel);
            buttonContainer.setSpacing(20);
            buttonContainer.setAlignment(Pos.CENTER_LEFT);

            statusBox.getChildren().addAll(statusLabel, detailsBox, benefitsLabel, buttonContainer);
        } else {
            Label statusLabel = new Label("No Active Elite Subscription");
            statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            statusLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));

            Label benefitsLabel = new Label("Unlock the Elite experience and enjoy:\n" +
                "• 10% discount on all orders\n• Priority delivery\n• Exclusive offers");
            benefitsLabel.setFont(Font.font("System", 16));
            benefitsLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));

            Label priceLabel = new Label("Only $100/month");
            priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            priceLabel.setTextFill(Color.web(ACCENT_GOLD));

            Button subscribeButton = new Button("Subscribe Now");
            subscribeButton.setStyle(
                "-fx-background-color: " + ACCENT_GOLD + "; " +
                "-fx-text-fill: " + BACKGROUND_DARK + "; " +
                BUTTON_STYLE_BASE
            );

            Label messageLabel = new Label();
            messageLabel.setFont(Font.font("System", 16));

            subscribeButton.setOnAction(e -> {
                showCreditCardDialog(stage, user, messageLabel);
            });

            HBox buttonContainer = new HBox(subscribeButton, messageLabel);
            buttonContainer.setSpacing(20);
            buttonContainer.setAlignment(Pos.CENTER_LEFT);

            statusBox.getChildren().addAll(statusLabel, benefitsLabel, priceLabel, buttonContainer);
        }

        card.getChildren().add(statusBox);
        return card;
    }

    private static void showCreditCardDialog(Stage stage, User user, Label messageLabel) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Elite Subscription Payment");
        dialog.setHeaderText("Enter your credit card information\nSubscription Cost: $100/month");
        dialog.getDialogPane().setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        ButtonType subscribeButtonType = new ButtonType("Subscribe", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(subscribeButtonType, ButtonType.CANCEL);
        
        dialog.getDialogPane().lookupButton(subscribeButtonType).setStyle("-fx-background-color: " + ACCENT_GOLD + "; -fx-text-fill: " + BACKGROUND_DARK + ";");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle("-fx-background-color: " + TEXT_COLOR_SECONDARY + "; -fx-text-fill: " + BACKGROUND_DARK + ";");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField cardNumberField = createStyledTextField("1234 5678 9012 3456");
        TextField cardHolderField = createStyledTextField("John Doe");
        TextField expiryField = createStyledTextField("MM/YY");
        TextField cvvField = createStyledTextField("123");

        grid.add(createLabel("Card Number:"), 0, 0);
        grid.add(cardNumberField, 1, 0);
        grid.add(createLabel("Card Holder:"), 0, 1);
        grid.add(cardHolderField, 1, 1);
        grid.add(createLabel("Expiry Date:"), 0, 2);
        grid.add(expiryField, 1, 2);
        grid.add(createLabel("CVV:"), 0, 3);
        grid.add(cvvField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == subscribeButtonType) {
                String cardNumber = cardNumberField.getText().trim().replaceAll("\\s+", "");
                String cardHolder = cardHolderField.getText().trim();
                String expiry = expiryField.getText().trim();
                String cvv = cvvField.getText().trim();

                if (cardNumber.isEmpty() || cardHolder.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                    showError(messageLabel, "All credit card fields are required!");
                    return;
                }

                if (!cardNumber.matches("\\d{16}")) {
                    showError(messageLabel, "Card number must be 16 digits!");
                    return;
                }

                if (!cvv.matches("\\d{3,4}")) {
                    showError(messageLabel, "CVV must be 3 or 4 digits!");
                    return;
                }

                try {
                    Calendar cal = Calendar.getInstance();
                    Date startDate = cal.getTime();
                    cal.add(Calendar.MONTH, 1);
                    Date endDate = cal.getTime();
                    
                    Subscription newSub = new Subscription(startDate, endDate);
                    newSub.setActive(true);
                    subscriptionRepo.addSubscription(newSub);
                    
                    user.setElite(true);
                    user.setSubscription(newSub);
                    userService.updateUser(user);
                    
                    try (java.sql.Connection conn = javaproject1.DAL.DataBase.DBConnection.getConnection();
                         java.sql.PreparedStatement stmt = conn.prepareStatement(
                             "UPDATE subscriptions SET user_id = ? WHERE subscription_id = ?")) {
                        stmt.setInt(1, Integer.parseInt(user.getId()));
                        stmt.setInt(2, newSub.getId());
                        stmt.executeUpdate();
                    }
                    
                    showSuccess(messageLabel, "Subscription activated successfully! Payment processed.");
                    
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            javafx.application.Platform.runLater(() -> show(stage, user));
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                } catch (Exception ex) {
                    showError(messageLabel, "Error activating subscription: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }

    private static VBox createAddressesCard(Stage stage, User user) {
        VBox card = createCard("My Delivery Addresses 📍", PRIMARY_COLOR);

        VBox addressList = new VBox(15);
        
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            for (Address address : user.getAddresses()) {
                HBox addressRow = new HBox(20);
                addressRow.setAlignment(Pos.CENTER_LEFT);
                addressRow.setPadding(new Insets(15));
                addressRow.setStyle(
                    "-fx-background-color: #2b3543; " +
                    "-fx-background-radius: 12;"
                );

                Label addressLabel = new Label("🏠 " + address.toString());
                addressLabel.setFont(Font.font("System", 16));
                addressLabel.setTextFill(Color.web(TEXT_COLOR_LIGHT)); 
                HBox.setHgrow(addressLabel, Priority.ALWAYS);

                Button deleteButton = new Button("Remove");
                deleteButton.setStyle(
                    "-fx-background-color: " + ERROR_COLOR + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 8 15; " +
                    "-fx-background-radius: 15; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-size: 14px;"
                );

                deleteButton.setOnAction(e -> {
                     Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
                        "Remove this address?");
                    confirmAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                userService.removeAddress(user, address);
                                addressService.deleteAddress(Integer.parseInt(address.getId()));
                                show(stage, user);
                            } catch (Exception ex) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR, 
                                    "Error removing address: " + ex.getMessage());
                                errorAlert.showAndWait();
                            }
                        }
                    });
                });

                addressRow.getChildren().addAll(addressLabel, deleteButton);
                addressList.getChildren().add(addressRow);
            }
        } else {
            Label noAddress = new Label("No addresses added yet. Add your first delivery location!");
            noAddress.setFont(Font.font("System", 18));
            noAddress.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
            addressList.getChildren().add(noAddress);
        }

        VBox addAddressBox = new VBox(20);
        addAddressBox.setPadding(new Insets(25, 0, 0, 0));
        addAddressBox.setStyle("-fx-border-color: #4b5563; -fx-border-width: 1 0 0 0;");

        Label addTitle = new Label("Add New Address");
        addTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        addTitle.setTextFill(Color.web(ACCENT_GOLD));

        GridPane addGrid = new GridPane();
        addGrid.setHgap(20);
        addGrid.setVgap(20);

        TextField streetField = createStyledTextField("Street Name");
        TextField cityField = createStyledTextField("City Name");
        TextField buildingField = createStyledTextField("Building Number");

        addGrid.add(createLabel("Street:"), 0, 0);
        addGrid.add(streetField, 1, 0);
        addGrid.add(createLabel("City:"), 0, 1);
        addGrid.add(cityField, 1, 1);
        addGrid.add(createLabel("Building:"), 0, 2);
        addGrid.add(buildingField, 1, 2);

        Button addButton = new Button("Save Address");
        addButton.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            BUTTON_STYLE_BASE.replace("16px", "14px")
        );

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 16));

        addButton.setOnAction(e -> {
            try {
                String street = streetField.getText().trim();
                String city = cityField.getText().trim();
                String buildingText = buildingField.getText().trim();

                if (street.isEmpty() || city.isEmpty() || buildingText.isEmpty()) {
                    showError(messageLabel, "All fields are required!");
                    return;
                }

                int building = Integer.parseInt(buildingText);
                Address newAddress = new Address(street, city, building);
                addressService.addAddress(newAddress);
                
                try (java.sql.Connection conn = javaproject1.DAL.DataBase.DBConnection.getConnection();
                     java.sql.PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO user_addresses (user_id, address_id) VALUES (?, ?)")) {
                    stmt.setInt(1, Integer.parseInt(user.getId()));
                    stmt.setString(2, newAddress.getId());
                    stmt.executeUpdate();
                }
                
                userService.addAddress(user, newAddress);
                
                streetField.clear();
                cityField.clear();
                buildingField.clear();
                
                showSuccess(messageLabel, "Address added successfully!");
                
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> show(stage, user));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } catch (NumberFormatException ex) {
                showError(messageLabel, "Building number must be a number!");
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        addAddressBox.getChildren().addAll(addTitle, addGrid, addButton, messageLabel);

        card.getChildren().addAll(addressList, addAddressBox);
        return card;
    }

    private static TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(350);
        field.setStyle(
            "-fx-padding: 12; " +
            "-fx-font-size: 15px; " +
            "-fx-background-color: #4b5563; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
            "-fx-border-color: #6b7280; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        return field;
    }

    private static Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setTextFill(Color.web(TEXT_COLOR_LIGHT));
        return label;
    }
    
    private static Label createDetailLabel(String text, FontWeight weight) {
        Label label = new Label(text);
        label.setFont(Font.font("System", weight, 16));
        label.setTextFill(Color.web(TEXT_COLOR_LIGHT)); 
        return label;
    }

    private static void addFormRow(GridPane grid, int row, String labelText, TextField field) {
        Label label = createLabel(labelText);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private static void showError(Label label, String message) {
        label.setText(message);
        label.setTextFill(Color.web(ERROR_COLOR));
    }

    private static void showSuccess(Label label, String message) {
        label.setText(message);
        label.setTextFill(Color.web(SUCCESS_COLOR));
    }
}
