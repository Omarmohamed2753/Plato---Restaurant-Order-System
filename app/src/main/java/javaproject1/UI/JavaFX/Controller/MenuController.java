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
import javaproject1.DAL.Entity.Menu;
import javaproject1.DAL.Entity.MenuItem;

public class MenuController {
    private static MenuServiceImpl menuService = new MenuServiceImpl();
    private static CartServiceImpl cartService = new CartServiceImpl();
    private static RestaurantServiceImpl restaurantService = new RestaurantServiceImpl();

    public static void show(Stage stage, User user, Restaurant restaurant) {
        System.out.println("=== MENU CONTROLLER DEBUG ===");
        System.out.println("Restaurant ID: " + restaurant.getRestaurantId());
        System.out.println("Restaurant Name: " + restaurant.getName());
        
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

        // Refresh restaurant data
        try {
            System.out.println("Refreshing restaurant data...");
            Restaurant freshRestaurant = restaurantService.getRestaurantById(
                Integer.parseInt(restaurant.getRestaurantId())
            );
            
            if (freshRestaurant != null) {
                restaurant = freshRestaurant;
                System.out.println("Fresh restaurant loaded: " + restaurant.getName());
            }
        } catch (Exception e) {
            System.err.println("Error refreshing: " + e.getMessage());
            e.printStackTrace();
        }

        Menu menu = restaurant.getMenu();
        
        if (menu != null && menu.getItems() != null && !menu.getItems().isEmpty()) {
            System.out.println("=== LOADING " + menu.getItems().size() + " MENU ITEMS ===");
            
            for (MenuItem item : menu.getItems()) {
                System.out.println("\n--- Menu Item ---");
                System.out.println("ID: " + item.getItemId());
                System.out.println("Name: " + item.getName());
                System.out.println("Price: $" + item.getPrice());
                System.out.println("Category: " + item.getCategory());
                System.out.println("Description: " + item.getDescription());
                
                itemsBox.getChildren().add(createMenuItemCard(user, item));
            }
        } else {
            System.out.println("=== NO MENU ITEMS FOUND ===");
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

    private static HBox createMenuItemCard(User user, MenuItem item) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);" +
            "-fx-border-color: rgb(27, 168, 203); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 12;"
        );

        // IMAGE
        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPrefSize(100, 100);
        imageBox.setStyle("-fx-background-color: rgb(37, 93, 177); -fx-background-radius: 8;");

        Label placeholder = new Label("🍔");
        placeholder.setFont(Font.font("System", 36));
        placeholder.setStyle("-fx-text-fill: white;");
        imageBox.getChildren().add(placeholder);

        // INFO BOX - SIMPLIFIED with explicit inline styles
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.setPrefWidth(400);

        // Name Label - LARGE and BOLD
        Label nameLabel = new Label(item.getName() != null ? item.getName() : "Unnamed Item");
        nameLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #000000;"
        );
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(400);
        
        // Category Label
        Label categoryLabel = new Label(item.getCategory() != null ? item.getCategory() : "General");
        categoryLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-text-fill: #666666; " +
            "-fx-font-style: italic;"
        );
        
        // Description Label
        Label descLabel = new Label(item.getDescription() != null ? item.getDescription() : "No description");
        descLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #333333;"
        );
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(400);
        
        // Price Label - GREEN
        Label priceLabel = new Label("$" + String.format("%.2f", item.getPrice()));
        priceLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #27ae60;"
        );

        infoBox.getChildren().addAll(nameLabel, categoryLabel, descLabel, priceLabel);

        // ACTION BOX
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPrefWidth(150);

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1);
        quantitySpinner.setPrefWidth(80);

        Button addButton = new Button("Add to Cart");
        addButton.setPrefWidth(120);
        addButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 16; " +
            "-fx-background-radius: 15; " +
            "-fx-cursor: hand;"
        );
        
        addButton.setOnAction(e -> {
            CartItem cartItem = new CartItem(item, quantitySpinner.getValue());
            cartService.addItem(user.getCart(), cartItem);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Added " + item.getName() + " to cart!");
            alert.showAndWait();
        });

        actionBox.getChildren().addAll(quantitySpinner, addButton);

        card.getChildren().addAll(imageBox, infoBox, actionBox);
        return card;
    }
}