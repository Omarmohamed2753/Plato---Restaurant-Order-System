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

        HBox navbar = createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(30));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label welcomeLabel = new Label("Welcome, " + user.getName() + "!");
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        welcomeLabel.setTextFill(Color.web("#1a1a1a"));

        ScrollPane restaurantScroll = createRestaurantList(stage, user);
        VBox.setVgrow(restaurantScroll, Priority.ALWAYS);

        contentBox.getChildren().addAll(welcomeLabel, restaurantScroll);
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
        
        RestaurantRepoImpl restaurantRepo = new RestaurantRepoImpl();
        List<Restaurant> restaurants = restaurantRepo.getAllRestaurants();
        
        System.out.println("=== RESTAURANT LIST DEBUG ===");
        System.out.println("Total restaurants: " + restaurants.size());

        if (restaurants.isEmpty()) {
            Label noRestaurants = new Label("No restaurants available at the moment.");
            noRestaurants.setFont(Font.font("System", 16));
            noRestaurants.setTextFill(Color.web("#636e72"));
            restaurantBox.getChildren().add(noRestaurants);
        } else {
            for (Restaurant restaurant : restaurants) {
                System.out.println("\n--- Restaurant " + restaurant.getRestaurantId() + " ---");
                System.out.println("Name: " + restaurant.getName());
                System.out.println("Address: " + restaurant.getAddress());
                System.out.println("Hours: " + restaurant.getOpeningHours());
                System.out.println("Rating: " + restaurant.getRating());
                
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
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5);" +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 15;"
        );
        card.setPrefWidth(800);

        HBox contentBox = new HBox(20);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        // IMAGE
        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPrefSize(150, 150);
        imageBox.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");

        Label imagePlaceholder = new Label("🍽️");
        imagePlaceholder.setFont(Font.font(50));
        imagePlaceholder.setStyle("-fx-text-fill: #95a5a6;");
        imageBox.getChildren().add(imagePlaceholder);

        // INFO BOX - SIMPLIFIED with inline styles
        VBox infoBox = new VBox(12);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.setPrefWidth(400);

        // Restaurant Name - BIG and BOLD
        String restaurantName = restaurant.getName() != null ? restaurant.getName() : "Restaurant";
        Label nameLabel = new Label(restaurantName);
        nameLabel.setStyle(
            "-fx-font-size: 26px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #000000;"
        );
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(400);
        
        // Address
        String address = restaurant.getAddress() != null ? restaurant.getAddress() : "Address not available";
        Label addressLabel = new Label("📍 " + address);
        addressLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #2d3436;"
        );
        addressLabel.setWrapText(true);
        addressLabel.setMaxWidth(400);
        
        // Hours
        String hours = restaurant.getOpeningHours() != null ? restaurant.getOpeningHours() : "Hours not available";
        Label hoursLabel = new Label("🕐 " + hours);
        hoursLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #2d3436;"
        );
        hoursLabel.setWrapText(true);
        hoursLabel.setMaxWidth(400);
        
        // Rating
        Label ratingLabel = new Label("⭐ " + String.format("%.1f", restaurant.getRating()));
        ratingLabel.setStyle(
            "-fx-background-color: #fff3cd; " +
            "-fx-text-fill: #856404; " +
            "-fx-padding: 5 10; " +
            "-fx-background-radius: 15; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold;"
        );

        infoBox.getChildren().addAll(nameLabel, addressLabel, hoursLabel, ratingLabel);

        // BUTTON
        Button viewMenuButton = new Button("View Menu");
        viewMenuButton.setPrefWidth(120);
        viewMenuButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand;"
        );
        viewMenuButton.setOnAction(e -> MenuController.show(stage, user, restaurant));

        contentBox.getChildren().addAll(imageBox, infoBox, viewMenuButton);
        card.getChildren().add(contentBox);

        return card;
    }
}