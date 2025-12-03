package javaproject1.UI.JavaFX.Controller;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javaproject1.BLL.Service.implementation.*;
import javaproject1.DAL.Entity.*;

import java.util.List;

public class MenuController {
    private static MenuServiceImpl menuService = new MenuServiceImpl();
    private static CartServiceImpl cartService = new CartServiceImpl();

    public static void show(Stage stage, User user, Restaurant restaurant) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(30));

        // Restaurant header with back button
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back");
        backButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #667eea; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> ClientMainController.show(stage, user));

        Label titleLabel = new Label(restaurant.getName() + " Menu");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));

        headerBox.getChildren().addAll(backButton, titleLabel);

        ScrollPane menuScroll = createMenuItems(user, restaurant);
        VBox.setVgrow(menuScroll, Priority.ALWAYS);

        contentBox.getChildren().addAll(headerBox, menuScroll);
        root.setCenter(contentBox);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    private static ScrollPane createMenuItems(User user, Restaurant restaurant) {
        VBox itemsBox = new VBox(15);
        itemsBox.setPadding(new Insets(20));

        javaproject1.DAL.Entity.Menu menu = restaurant.getMenu();
        if (menu != null && menu.getItems() != null && !menu.getItems().isEmpty()) {
            for (javaproject1.DAL.Entity.MenuItem item : menu.getItems()) {
                itemsBox.getChildren().add(createMenuItemCard(user, item));
            }
        } else {
            Label noItems = new Label("No menu items available yet.");
            noItems.setFont(Font.font("System", 16));
            noItems.setTextFill(Color.web("#95a5a6"));
            itemsBox.getChildren().add(noItems);
        }

        ScrollPane scrollPane = new ScrollPane(itemsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scrollPane;
    }

    private static HBox createMenuItemCard(User user, javaproject1.DAL.Entity.MenuItem item) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        // ===== IMAGE SECTION - NEW! =====
        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPrefWidth(100);
        imageBox.setPrefHeight(100);
        imageBox.setStyle(
            "-fx-background-color: #f5f7fa; " +
            "-fx-background-radius: 8;"
        );

        try {
            if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                ImageView imageView = new ImageView();
                Image image = new Image(
                    item.getImagePath(), 
                    100, 100, 
                    true, true, 
                    true
                );
                
                imageView.setImage(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(false);
                
                // Clip to rounded rectangle
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(100, 100);
                clip.setArcWidth(16);
                clip.setArcHeight(16);
                imageView.setClip(clip);
                
                if (image.isError()) {
                    Label placeholder = new Label("🍽️");
                    placeholder.setFont(Font.font("System", 36));
                    imageBox.getChildren().add(placeholder);
                } else {
                    imageBox.getChildren().add(imageView);
                }
            } else {
                Label placeholder = new Label("🍽️");
                placeholder.setFont(Font.font("System", 36));
                imageBox.getChildren().add(placeholder);
            }
        } catch (Exception e) {
            Label placeholder = new Label("🍽️");
            placeholder.setFont(Font.font("System", 36));
            imageBox.getChildren().add(placeholder);
        }

        // ===== INFO SECTION =====
        VBox infoBox = new VBox(5);
        VBox.setVgrow(infoBox, Priority.ALWAYS);

        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        Label descLabel = new Label(item.getDescription());
        descLabel.setFont(Font.font("System", 12));
        descLabel.setTextFill(Color.web("#7f8c8d"));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(400);
        
        Label categoryLabel = new Label(item.getCategory());
        categoryLabel.setFont(Font.font("System", 11));
        categoryLabel.setTextFill(Color.web("#95a5a6"));
        
        Label priceLabel = new Label("$" + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        priceLabel.setTextFill(Color.web("#27ae60"));

        infoBox.getChildren().addAll(nameLabel, categoryLabel, descLabel, priceLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ===== ACTION SECTION =====
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(Pos.CENTER);

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1);
        quantitySpinner.setPrefWidth(80);
        quantitySpinner.setStyle("-fx-font-size: 14px;");

        Button addButton = new Button("Add to Cart");
        addButton.setPrefWidth(120);
        addButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 16 8 16; " +
            "-fx-background-radius: 15; " +
            "-fx-cursor: hand;"
        );
        addButton.setOnMouseEntered(e -> addButton.setStyle(
            "-fx-background-color: #5568d3; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 16 8 16; " +
            "-fx-background-radius: 15; " +
            "-fx-cursor: hand;"
        ));
        addButton.setOnMouseExited(e -> addButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 16 8 16; " +
            "-fx-background-radius: 15; " +
            "-fx-cursor: hand;"
        ));
        
        addButton.setOnAction(e -> {
            CartItem cartItem = new CartItem(item, quantitySpinner.getValue());
            cartService.addItem(user.getCart(), cartItem);
            showAlert("Added to cart!", Alert.AlertType.INFORMATION);
        });

        actionBox.getChildren().addAll(quantitySpinner, addButton);

        card.getChildren().addAll(imageBox, infoBox, spacer, actionBox);
        return card;
    }

    private static void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}