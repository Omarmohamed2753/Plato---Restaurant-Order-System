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
import javaproject1.DAL.Repo.Implementation.RestaurantRepoImpl;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class ClientMainController {

    private static UserServiceImpl userService = new UserServiceImpl();
    private static RestaurantServiceImpl restaurantService = new RestaurantServiceImpl();
    private static User currentUser;

    public static void show(Stage stage, User user) {
        currentUser = user;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        // Navigation Bar
        HBox navbar = createNavBar(stage, user);
        root.setTop(navbar);

        // Main Content
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(30));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label welcomeLabel = new Label("Welcome, " + user.getName() + "!");
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        welcomeLabel.setTextFill(Color.web("#1a1a1a"));

        // Restaurant List
        Label restaurantLabel = new Label("Available Restaurants");
        restaurantLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        restaurantLabel.setTextFill(Color.web("#1a1a1a"));

        ScrollPane restaurantScroll = createRestaurantList(stage, user);
        VBox.setVgrow(restaurantScroll, Priority.ALWAYS);

        contentBox.getChildren().addAll(welcomeLabel, restaurantLabel, restaurantScroll);
        root.setCenter(contentBox);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    public static HBox createNavBar(Stage stage, User user) {
        HBox navbar = new HBox(20);
        navbar.setPadding(new Insets(15, 30, 15, 30));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label brandLabel = new Label("🍽️ Plato");
        brandLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        brandLabel.setTextFill(Color.web("#667eea"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button homeButton = createNavButton("Home", () -> show(stage, user));
        Button ordersButton = createNavButton("My Orders", () -> OrdersController.show(stage, user));
        Button cartButton = createNavButton("Cart", () -> CartController.show(stage, user));
        Button profileButton = createNavButton("Profile", () -> ProfileController.show(stage, user));
        Button logoutButton = createNavButton("Logout", () -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    WelcomeController.show(stage);
                }
            });
        });

        navbar.getChildren().addAll(brandLabel, spacer, homeButton, ordersButton, cartButton, profileButton, logoutButton);
        return navbar;
    }

    private static Button createNavButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #2d3436; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 15 8 15;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #f0f0f0; " +
            "-fx-text-fill: #667eea; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-background-radius: 5;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #2d3436; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 15 8 15;"
        ));
        button.setOnAction(e -> action.run());
        return button;
    }

    private static ScrollPane createRestaurantList(Stage stage, User user) {
        VBox restaurantBox = new VBox(15);
        restaurantBox.setPadding(new Insets(20));
        
        // Get all restaurants
        RestaurantRepoImpl restaurantRepo = new RestaurantRepoImpl();
        List<Restaurant> restaurants = restaurantRepo.getAllRestaurants();

        if (restaurants.isEmpty()) {
            Label noRestaurants = new Label("No restaurants available at the moment.");
            noRestaurants.setFont(Font.font("System", 16));
            noRestaurants.setTextFill(Color.web("#636e72"));
            restaurantBox.getChildren().add(noRestaurants);
        } else {
            for (Restaurant restaurant : restaurants) {
                restaurantBox.getChildren().add(createRestaurantCard(stage, user, restaurant));
            }
        }

        ScrollPane scrollPane = new ScrollPane(restaurantBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scrollPane;
    }

    private static VBox createRestaurantCard(Stage stage, User user, Restaurant restaurant) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );
        card.setPrefWidth(800);

        HBox contentBox = new HBox(20);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        // ===== IMAGE SECTION - NEW! =====
        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPrefWidth(150);
        imageBox.setPrefHeight(150);
        imageBox.setStyle(
            "-fx-background-color: #f5f7fa; " +
            "-fx-background-radius: 8;"
        );

        try {
            if (restaurant.getImagePath() != null && !restaurant.getImagePath().isEmpty()) {
                ImageView imageView = new ImageView();
                Image image = new Image(
                    restaurant.getImagePath(), 
                    150, 150, 
                    true, true, 
                    true  // Load in background
                );
                
                imageView.setImage(image);
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(false);
                
                // Clip to rounded rectangle
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(150, 150);
                clip.setArcWidth(16);
                clip.setArcHeight(16);
                imageView.setClip(clip);
                
                // Add placeholder while loading or on error
                if (image.isError()) {
                    Label placeholder = new Label("🍽️");
                    placeholder.setFont(Font.font("System", 48));
                    imageBox.getChildren().add(placeholder);
                } else {
                    imageBox.getChildren().add(imageView);
                }
            } else {
                // No image path - show placeholder
                Label placeholder = new Label("🍽️");
                placeholder.setFont(Font.font("System", 48));
                imageBox.getChildren().add(placeholder);
            }
        } catch (Exception e) {
            // If image fails to load, show placeholder
            Label placeholder = new Label("🍽️");
            placeholder.setFont(Font.font("System", 48));
            imageBox.getChildren().add(placeholder);
            System.err.println("Error loading image for " + restaurant.getName() + ": " + e.getMessage());
        }

        // ===== INFO SECTION =====
        VBox infoBox = new VBox(8);
        VBox.setVgrow(infoBox, Priority.ALWAYS);

        Label nameLabel = new Label(restaurant.getName() != null ? restaurant.getName() : "Unnamed Restaurant");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        nameLabel.setTextFill(Color.web("#1a1a1a"));
        
        Label addressLabel = new Label("📍 " + (restaurant.getAddress() != null && !restaurant.getAddress().isEmpty() ? restaurant.getAddress() : "Address not available"));
        addressLabel.setFont(Font.font("System", 14));
        addressLabel.setTextFill(Color.web("#4a5568"));
        
        Label hoursLabel = new Label("🕐 " + (restaurant.getOpeningHours() != null && !restaurant.getOpeningHours().isEmpty() ? restaurant.getOpeningHours() : "Hours not available"));
        hoursLabel.setFont(Font.font("System", 14));
        hoursLabel.setTextFill(Color.web("#4a5568"));
        
        Label ratingLabel = new Label("⭐ " + String.format("%.1f", restaurant.getRating()));
        ratingLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        ratingLabel.setTextFill(Color.web("#f39c12"));

        infoBox.getChildren().addAll(nameLabel, addressLabel, hoursLabel, ratingLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ===== BUTTON SECTION =====
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button viewMenuButton = new Button("View Menu");
        viewMenuButton.setPrefWidth(120);
        viewMenuButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand;"
        );
        viewMenuButton.setOnMouseEntered(e -> viewMenuButton.setStyle(
            "-fx-background-color: #5568d3; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand;"
        ));
        viewMenuButton.setOnMouseExited(e -> viewMenuButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand;"
        ));
        viewMenuButton.setOnAction(e -> MenuController.show(stage, user, restaurant));

        buttonBox.getChildren().add(viewMenuButton);

        contentBox.getChildren().addAll(imageBox, infoBox, spacer, buttonBox);
        card.getChildren().add(contentBox);

        return card;
    }
}