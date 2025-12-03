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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javaproject1.BLL.Service.implementation.*;
import javaproject1.DAL.Entity.*;

public class MenuController {
    private static MenuServiceImpl menuService = new MenuServiceImpl();
    private static CartServiceImpl cartService = new CartServiceImpl();
    private static RestaurantServiceImpl restaurantService = new RestaurantServiceImpl();

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
        titleLabel.setTextFill(Color.web("#1a1a1a"));

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

        // Refresh restaurant data to get latest menu items
        try {
            RestaurantServiceImpl restaurantService = new RestaurantServiceImpl();
            Restaurant freshRestaurant = restaurantService.getRestaurantById(
                Integer.parseInt(restaurant.getRestaurantId())
            );
            
            if (freshRestaurant != null) {
                restaurant = freshRestaurant; // Use fresh data
            }
        } catch (Exception e) {
            System.err.println("Error refreshing restaurant data: " + e.getMessage());
        }

        javaproject1.DAL.Entity.Menu menu = restaurant.getMenu();
        if (menu != null && menu.getItems() != null && !menu.getItems().isEmpty()) {
            System.out.println("Loading " + menu.getItems().size() + " menu items");
            for (javaproject1.DAL.Entity.MenuItem item : menu.getItems()) {
                System.out.println("Item: " + item.getName() + " - $" + item.getPrice());
                itemsBox.getChildren().add(createMenuItemCard(user, item));
            }
        } else {
            Label noItems = new Label("No menu items available yet.");
            noItems.setFont(Font.font("System", 16));
            noItems.setTextFill(Color.web("#636e72"));
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
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);" +
            "-fx-border-color:rgb(27, 168, 203); -fx-border-radius: 12;"
        );
        card.setMaxWidth(Double.MAX_VALUE);

        // IMAGE SECTION
        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setMinWidth(100);
        imageBox.setMinHeight(100);
        imageBox.setMaxWidth(100);
        imageBox.setMaxHeight(100);
        imageBox.setStyle(
            "-fx-background-color:rgb(37, 93, 177); " +
            "-fx-background-radius: 8;"
        );

        try {
            if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                ImageView imageView = new ImageView(new Image(item.getImagePath(), 100, 100, true, true));
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(100, 100);
                clip.setArcWidth(16);
                clip.setArcHeight(16);
                imageView.setClip(clip);
                
                imageBox.getChildren().add(imageView);
            } else {
                Label placeholder = new Label("🍔");
                placeholder.setFont(Font.font("System", 36));
                imageBox.getChildren().add(placeholder);
            }
        } catch (Exception e) {
            Label placeholder = new Label("🍔");
            placeholder.setFont(Font.font("System", 36));
            imageBox.getChildren().add(placeholder);
        }

        // INFO SECTION - ALL TEXT BLACK/DARK
        VBox infoBox = new VBox(5);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(item.getName() != null ? item.getName() : "Unnamed Item");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.web("#000000"));
        
        Label descLabel = new Label(item.getDescription() != null && !item.getDescription().isEmpty() ? item.getDescription() : "No description available");
        descLabel.setFont(Font.font("System", 12));
        descLabel.setTextFill(Color.web("#2d3436"));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(400);
        
        Label categoryLabel = new Label(item.getCategory() != null && !item.getCategory().isEmpty() ? item.getCategory() : "Uncategorized");
        categoryLabel.setFont(Font.font("System", 11));
        categoryLabel.setTextFill(Color.web("#636e72"));
        
        Label priceLabel = new Label("$" + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        priceLabel.setTextFill(Color.web("#27ae60"));

        infoBox.getChildren().addAll(nameLabel, categoryLabel, descLabel, priceLabel);

        // ACTION SECTION
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
        
        addButton.setOnAction(e -> {
            CartItem cartItem = new CartItem(item, quantitySpinner.getValue());
            cartService.addItem(user.getCart(), cartItem);
            showAlert("Added to cart!", Alert.AlertType.INFORMATION);
        });

        actionBox.getChildren().addAll(quantitySpinner, addButton);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(imageBox, infoBox, spacer, actionBox);
        return card;
    }

    private static void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}