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

import java.util.Date;
import java.util.Calendar;

public class ProfileController {

    private static UserServiceImpl userService = new UserServiceImpl();
    private static SubscriptionRepoImpl subscriptionRepo = new SubscriptionRepoImpl();
    private static AddressServiceImpl addressService = new AddressServiceImpl();

    public static void show(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(40));
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setMaxWidth(800);

        Label titleLabel = new Label("My Profile");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#667eea"));

        VBox profileCard = createProfileCard(stage, user);
        VBox subscriptionCard = createSubscriptionCard(stage, user);
        VBox addressesCard = createAddressesCard(stage, user);

        contentBox.getChildren().addAll(titleLabel, profileCard, subscriptionCard, addressesCard);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    private static VBox createProfileCard(Stage stage, User user) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label titleLabel = new Label("Personal Information");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#1a1a1a"));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        TextField nameField = createTextField(user.getName());
        TextField emailField = createTextField(user.getEmail());
        emailField.setDisable(true);
        emailField.setStyle("-fx-opacity: 0.7; -fx-padding: 10; -fx-font-size: 14px;");
        TextField phoneField = createTextField(user.getPhoneNumber());
        TextField ageField = createTextField(String.valueOf(user.getAge()));

        int row = 0;
        addFormRow(grid, row++, "Name:", nameField);
        addFormRow(grid, row++, "Email:", emailField);
        addFormRow(grid, row++, "Phone:", phoneField);
        addFormRow(grid, row++, "Age:", ageField);

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 14));

        Button updateButton = new Button("Update Profile");
        updateButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand;"
        );

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
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(updateButton);

        card.getChildren().addAll(titleLabel, grid, buttonBox, messageLabel);
        return card;
    }

    private static VBox createSubscriptionCard(Stage stage, User user) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label titleLabel = new Label("Elite Subscription");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#667eea"));

        VBox statusBox = new VBox(15);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        if (user.getSubscription() != null && user.getSubscription().isActive()) {
            // ACTIVE SUBSCRIPTION - Display subscription data
            Label statusLabel = new Label("✓ Active Elite Member");
            statusLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            statusLabel.setTextFill(Color.web("#27ae60"));

            // Subscription Details Box
            VBox detailsBox = new VBox(8);
            detailsBox.setPadding(new Insets(15));
            detailsBox.setStyle(
                "-fx-background-color: #f8f9fa; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #27ae60; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10;"
            );

            Label costLabel = new Label("Cost: $100/month");
            costLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            costLabel.setTextFill(Color.web("#000000"));

            Label discountLabel = new Label("Discount Rate: 10%");
            discountLabel.setFont(Font.font("System", 14));
            discountLabel.setTextFill(Color.web("#000000"));

            Label startLabel = new Label("Start Date: " + 
                (user.getSubscription().getStartDate() != null ? 
                    new java.text.SimpleDateFormat("MMM dd, yyyy").format(user.getSubscription().getStartDate()) : "N/A"));
            startLabel.setFont(Font.font("System", 14));
            startLabel.setTextFill(Color.web("#000000"));

            Label endLabel = new Label("Expires: " + 
                (user.getSubscription().getEndDate() != null ? 
                    new java.text.SimpleDateFormat("MMM dd, yyyy").format(user.getSubscription().getEndDate()) : "N/A"));
            endLabel.setFont(Font.font("System", 14));
            endLabel.setTextFill(Color.web("#000000"));

            detailsBox.getChildren().addAll(costLabel, discountLabel, startLabel, endLabel);

            Label benefitsLabel = new Label("Benefits: 10% discount on all orders • Priority delivery");
            benefitsLabel.setFont(Font.font("System", 14));
            benefitsLabel.setTextFill(Color.web("#4a5568"));

            Button cancelButton = new Button("Cancel Subscription");
            cancelButton.setStyle(
                "-fx-background-color: #e74c3c; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8 16; " +
                "-fx-background-radius: 15; " +
                "-fx-cursor: hand;"
            );

            Label messageLabel = new Label();
            messageLabel.setFont(Font.font("System", 14));

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

            statusBox.getChildren().addAll(statusLabel, detailsBox, benefitsLabel, cancelButton, messageLabel);
        } else {
            // NO ACTIVE SUBSCRIPTION - Show subscribe option
            Label statusLabel = new Label("No Active Subscription");
            statusLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            statusLabel.setTextFill(Color.web("#718096"));

            Label benefitsLabel = new Label("Subscribe to Elite and get:\n" +
                "• 10% discount on all orders\n• Priority delivery\n• Exclusive offers");
            benefitsLabel.setFont(Font.font("System", 14));
            benefitsLabel.setTextFill(Color.web("#4a5568"));

            Label priceLabel = new Label("Only $100/month");
            priceLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            priceLabel.setTextFill(Color.web("#f39c12"));

            Button subscribeButton = new Button("Subscribe to Elite");
            subscribeButton.setStyle(
                "-fx-background-color: #f39c12; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand;"
            );

            Label messageLabel = new Label();
            messageLabel.setFont(Font.font("System", 14));

            subscribeButton.setOnAction(e -> {
                showCreditCardDialog(stage, user, messageLabel);
            });

            statusBox.getChildren().addAll(statusLabel, benefitsLabel, priceLabel, subscribeButton, messageLabel);
        }

        card.getChildren().addAll(titleLabel, statusBox);
        return card;
    }

    private static void showCreditCardDialog(Stage stage, User user, Label messageLabel) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Elite Subscription Payment");
        dialog.setHeaderText("Enter your credit card information\nSubscription Cost: $100/month");

        ButtonType subscribeButtonType = new ButtonType("Subscribe", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(subscribeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("1234 5678 9012 3456");
        TextField cardHolderField = new TextField();
        cardHolderField.setPromptText("John Doe");
        TextField expiryField = new TextField();
        expiryField.setPromptText("MM/YY");
        TextField cvvField = new TextField();
        cvvField.setPromptText("123");

        grid.add(new Label("Card Number:"), 0, 0);
        grid.add(cardNumberField, 1, 0);
        grid.add(new Label("Card Holder:"), 0, 1);
        grid.add(cardHolderField, 1, 1);
        grid.add(new Label("Expiry Date:"), 0, 2);
        grid.add(expiryField, 1, 2);
        grid.add(new Label("CVV:"), 0, 3);
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
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label titleLabel = new Label("My Addresses");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#667eea"));

        VBox addressList = new VBox(10);
        
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            for (Address address : user.getAddresses()) {
                HBox addressRow = new HBox(15);
                addressRow.setAlignment(Pos.CENTER_LEFT);
                addressRow.setPadding(new Insets(10));
                addressRow.setStyle(
                    "-fx-background-color: #f8f9fa; " +
                    "-fx-background-radius: 8;"
                );

                Label addressLabel = new Label("📍 " + address.toString());
                addressLabel.setFont(Font.font("System", 14));
                addressLabel.setTextFill(Color.web("#000000")); // FIXED: Changed to black
                HBox.setHgrow(addressLabel, Priority.ALWAYS);

                Button deleteButton = new Button("Remove");
                deleteButton.setStyle(
                    "-fx-background-color: #e74c3c; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 5 10; " +
                    "-fx-background-radius: 12; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-size: 12px;"
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
            Label noAddress = new Label("No addresses added yet.");
            noAddress.setFont(Font.font("System", 14));
            noAddress.setTextFill(Color.web("#718096"));
            addressList.getChildren().add(noAddress);
        }

        VBox addAddressBox = new VBox(10);
        addAddressBox.setPadding(new Insets(15, 0, 0, 0));
        addAddressBox.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");

        Label addTitle = new Label("Add New Address");
        addTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        addTitle.setTextFill(Color.web("#1a1a1a"));

        GridPane addGrid = new GridPane();
        addGrid.setHgap(10);
        addGrid.setVgap(10);

        TextField streetField = createTextField("Street");
        TextField cityField = createTextField("City");
        TextField buildingField = createTextField("Building Number");

        addGrid.add(new Label("Street:"), 0, 0);
        addGrid.add(streetField, 1, 0);
        addGrid.add(new Label("City:"), 0, 1);
        addGrid.add(cityField, 1, 1);
        addGrid.add(new Label("Building:"), 0, 2);
        addGrid.add(buildingField, 1, 2);

        Button addButton = new Button("Add Address");
        addButton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 16; " +
            "-fx-background-radius: 15; " +
            "-fx-cursor: hand;"
        );

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 14));

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

        card.getChildren().addAll(titleLabel, addressList, addAddressBox);
        return card;
    }

    private static TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(300);
        field.setStyle("-fx-padding: 10; -fx-font-size: 14px;");
        return field;
    }

    private static void addFormRow(GridPane grid, int row, String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.BOLD, 14));
        label.setTextFill(Color.web("#1a1a1a"));
        grid.add(label, 0, row);
        grid.add(field, 1, row);
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