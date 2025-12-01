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
import javaproject1.BLL.Service.implementation.UserServiceImpl;
import javaproject1.DAL.Entity.User;

public class ProfileController {

    private static UserServiceImpl userService = new UserServiceImpl();

    public static void show(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        // Navigation Bar
        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        // Profile Content
        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(40));
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setMaxWidth(700);

        Label titleLabel = new Label("My Profile");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#2d3436"));

        // Profile Card
        VBox profileCard = new VBox(20);
        profileCard.setPadding(new Insets(30));
        profileCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        // Profile Info
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        TextField nameField = createTextField(user.getName());
        TextField emailField = createTextField(user.getEmail());
        emailField.setDisable(true);
        TextField phoneField = createTextField(user.getPhoneNumber());
        TextField ageField = createTextField(String.valueOf(user.getAge()));

        CheckBox eliteCheckBox = new CheckBox("Elite Member");
        eliteCheckBox.setSelected(user.isElite());
        eliteCheckBox.setDisable(true);

        int row = 0;
        addFormRow(grid, row++, "Name:", nameField);
        addFormRow(grid, row++, "Email:", emailField);
        addFormRow(grid, row++, "Phone:", phoneField);
        addFormRow(grid, row++, "Age:", ageField);
        
        grid.add(eliteCheckBox, 1, row++);

        // Subscription Info
        if (user.getSubscription() != null && user.getSubscription().isActive()) {
            Label subLabel = new Label("✓ Active Elite Subscription");
            subLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            subLabel.setTextFill(Color.web("#27ae60"));
            grid.add(subLabel, 1, row++);
        } else {
            Button subscribeButton = new Button("Subscribe to Elite");
            subscribeButton.setStyle(
                "-fx-background-color: #f39c12; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8 16 8 16; " +
                "-fx-background-radius: 15; " +
                "-fx-cursor: hand;"
            );
            subscribeButton.setOnAction(e -> SubscriptionController.show(stage, user));
            grid.add(subscribeButton, 1, row++);
        }

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button updateButton = new Button("Update Profile");
        updateButton.setPrefWidth(150);
        updateButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand;"
        );

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 14));

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
            } catch (NumberFormatException ex) {
                showError(messageLabel, "Age must be a number!");
            } catch (Exception ex) {
                showError(messageLabel, "Error: " + ex.getMessage());
            }
        });

        buttonBox.getChildren().add(updateButton);

        profileCard.getChildren().addAll(grid, buttonBox, messageLabel);

        // Addresses Section
        VBox addressSection = createAddressSection(user);

        contentBox.getChildren().addAll(titleLabel, profileCard, addressSection);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    private static VBox createAddressSection(User user) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(30));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label titleLabel = new Label("My Addresses");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        VBox addressList = new VBox(10);
        
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            for (var address : user.getAddresses()) {
                Label addressLabel = new Label("📍 " + address.toString());
                addressLabel.setFont(Font.font("System", 14));
                addressList.getChildren().add(addressLabel);
            }
        } else {
            Label noAddress = new Label("No addresses added yet.");
            noAddress.setFont(Font.font("System", 14));
            noAddress.setTextFill(Color.web("#95a5a6"));
            addressList.getChildren().add(noAddress);
        }

        section.getChildren().addAll(titleLabel, addressList);
        return section;
    }

    private static TextField createTextField(String text) {
        TextField field = new TextField(text);
        field.setPrefWidth(300);
        field.setStyle("-fx-padding: 10; -fx-font-size: 14px;");
        return field;
    }

    private static void addFormRow(GridPane grid, int row, String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.BOLD, 14));
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