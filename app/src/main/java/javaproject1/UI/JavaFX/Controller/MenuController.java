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

    // Theme Colors ---
    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String CARD_BACKGROUND = "#374151";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String ITEM_BACKGROUND = "#2b3543"; 
    private static final String SUCCESS_GREEN = "#10b981";


    public static void show(Stage stage, User user, Restaurant restaurant) {
        System.out.println("=== MENU CONTROLLER DEBUG ===");
        System.out.println("Restaurant ID: " + restaurant.getRestaurantId());
        System.out.println("Restaurant Name: " + restaurant.getName());
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(50));
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setMaxWidth(1000);

        // Restaurant header with back button
        HBox headerBox = new HBox(30);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back to Restaurants");
        backButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-cursor: hand;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(backButton.getStyle() + "-fx-text-fill: " + ACCENT_GOLD + ";"));
        backButton.setOnMouseExited(e -> backButton.setStyle(backButton.getStyle().replace("-fx-text-fill: " + ACCENT_GOLD + ";", "-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";")));
        backButton.setOnAction(e -> ClientMainController.show(stage, user));

        Label titleLabel = new Label("🍽️" + restaurant.getName() + " Menu");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        titleLabel.setTextFill(Color.web(TEXT_COLOR_LIGHT));

        headerBox.getChildren().addAll(backButton, titleLabel);

        ScrollPane menuScroll = createMenuItems(user, restaurant);
        VBox.setVgrow(menuScroll, Priority.ALWAYS);

        contentBox.getChildren().addAll(titleLabel, menuScroll);
        root.setCenter(contentBox);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
    }

    private static ScrollPane createMenuItems(User user, Restaurant restaurant) {
        VBox itemsBox = new VBox(20);
        itemsBox.setPadding(new Insets(10, 0, 10, 0));
        itemsBox.setAlignment(Pos.TOP_CENTER);

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
                System.out.println("Item Name: " + item.getName() + ", Price: $" + item.getPrice());
                itemsBox.getChildren().add(createMenuItemCard(user, item));
            }
        } else {
            System.out.println("=== NO MENU ITEMS FOUND ===");
            Label noItems = new Label("No menu items available yet for this restaurant.");
            noItems.setFont(Font.font("System", 18));
            noItems.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
            itemsBox.getChildren().add(noItems);
        }

        ScrollPane scrollPane = new ScrollPane(itemsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scrollPane;
    }

    private static HBox createMenuItemCard(User user, MenuItem item) {
        HBox card = new HBox(30);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(800);
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);"
        );

        
        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPrefSize(120, 120);
        imageBox.setMinSize(120, 120);
        imageBox.setMaxSize(120, 120);
        imageBox.setStyle("-fx-background-color: " + ITEM_BACKGROUND + "; -fx-background-radius: 10;");

        ImageView imageView = loadMenuItemImage(item.getImagePath());
        if (imageView != null) {
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
            
            
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(120, 120);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            imageView.setClip(clip);
            
            imageBox.getChildren().add(imageView);
        } else {
            Label placeholder = new Label("🍽️");
            placeholder.setFont(Font.font("System", 60));
            placeholder.setStyle("-fx-text-fill: " + ACCENT_GOLD + ";");
            imageBox.getChildren().add(placeholder);
        }

        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.setPrefWidth(450);

        Label nameLabel = new Label(item.getName() != null ? item.getName() : "Unnamed Item");
        nameLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: extra-bold; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + ";"
        );
        nameLabel.setWrapText(true);
        
        Label categoryLabel = new Label("Category: " + (item.getCategory() != null ? item.getCategory() : "General"));
        categoryLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: " + PRIMARY_COLOR + "; " +
            "-fx-font-weight: bold;"
        );
        
        Label descLabel = new Label(item.getDescription() != null ? item.getDescription() : "A delicious item from our menu.");
        descLabel.setStyle(
            "-fx-font-size: 15px; " +
            "-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";"
        );
        descLabel.setWrapText(true);
        
        Label priceLabel = new Label("$" + String.format("%.2f", item.getPrice()));
        priceLabel.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + SUCCESS_GREEN + ";"
        );

        infoBox.getChildren().addAll(nameLabel, categoryLabel, descLabel, new Region(), priceLabel);
        VBox.setVgrow(infoBox.getChildren().get(3), Priority.ALWAYS);

           
        VBox actionBox = new VBox(15);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPrefWidth(150);

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1);
        quantitySpinner.setPrefWidth(100);
        quantitySpinner.getEditor().setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-background-color: " + ITEM_BACKGROUND + "; -fx-font-size: 16px;");

        Button addButton = new Button("Add to Cart ➕");
        addButton.setPrefWidth(140);
        addButton.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 18; " +
            "-fx-background-radius: 18; " +
            "-fx-cursor: hand;"
        );
        
        addButton.setOnMouseEntered(e -> addButton.setStyle(addButton.getStyle() + "-fx-background-color: #047857;"));
        addButton.setOnMouseExited(e -> addButton.setStyle(addButton.getStyle().replace("-fx-background-color: #047857;", "-fx-background-color: " + PRIMARY_COLOR + ";")));
        
        addButton.setOnAction(e -> {
            CartItem cartItem = new CartItem(item, quantitySpinner.getValue());
            cartService.addItem(user.getCart(), cartItem);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Added " + item.getName() + " to cart!", ButtonType.OK);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
            dialogPane.lookup(".label.content").setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-font-size: 16px;");
            
            alert.showAndWait();
        });

        actionBox.getChildren().addAll(quantitySpinner, addButton);

        card.getChildren().addAll(imageBox, infoBox, actionBox);
        return card;
    }

    private static ImageView loadMenuItemImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null; 
        }

        try {
            //  Extract just the filename (e.g., "1.jpg") from any path (e.g., "C:/Users/.../1.jpg")
            // This fixes the issue even if you don't update the database immediately!
            String filename = new java.io.File(imagePath).getName();

            // Define the path where images live in your resources
            String resourcePath = "/images/menu/" + filename;

            // Try to load from the classpath
            java.net.URL imageUrl = MenuController.class.getResource(resourcePath);

            if (imageUrl != null) {
                System.out.println("✓ Loaded image: " + resourcePath);
                return new ImageView(new Image(imageUrl.toExternalForm()));
            } else {
                System.err.println("⚠ Image not found in resources: " + resourcePath);

                // This helps if you haven't moved the files to src/main/resources yet
                java.io.File file = new java.io.File(imagePath);
                if (file.exists()) {
                     System.out.println("✓ Loaded from local disk: " + imagePath);
                     return new ImageView(new Image(file.toURI().toString()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
        return null;
    }
}