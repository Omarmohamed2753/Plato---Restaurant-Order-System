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

    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String CARD_BACKGROUND = "#374151";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String HOVER_COLOR = "#4b5563";
    
    private static final String BUTTON_STYLE_BASE = 
        "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 20; -fx-cursor: hand; -fx-font-size: 15px;";

    public static void show(Stage stage, User user) {
        currentUser = user;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        HBox navbar = createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(40));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label welcomeLabel = new Label("Welcome, " + user.getName() + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        welcomeLabel.setTextFill(Color.web(TEXT_COLOR_LIGHT));
        
        Label restaurantsTitle = new Label("Explore Our Plato Restaurant ");
        restaurantsTitle.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 24));
        restaurantsTitle.setTextFill(Color.web(ACCENT_GOLD));

        ScrollPane restaurantScroll = createRestaurantList(stage, user);
        VBox.setVgrow(restaurantScroll, Priority.ALWAYS);

        contentBox.getChildren().addAll(welcomeLabel, restaurantsTitle, restaurantScroll);
        root.setCenter(contentBox);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
    }

    public static HBox createNavBar(Stage stage, User user) {
        HBox navbar = new HBox(30);
        navbar.setPadding(new Insets(18, 50, 18, 50));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label brandLabel = new Label("Plato Restaurant");
        brandLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 24));
        brandLabel.setTextFill(Color.web(ACCENT_GOLD));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button homeButton = createNavButton("Home", () -> show(stage, user));
        Button ordersButton = createNavButton("My Orders", () -> OrdersController.show(stage, user));
        Button cartButton = createNavButton("Cart", () -> CartController.show(stage, user));
        Button  reviewsButton = createNavButton("Reviews", () -> ReviewController.show(stage, user));
        Button profileButton = createNavButton("Profile", () -> ProfileController.show(stage, user));
        Button logoutButton = createNavButton("Logout", () -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
            dialogPane.lookup(".label.content").setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-font-size: 14px;");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    WelcomeController.show(stage);
                }
            });
        });
        
        logoutButton.setStyle(logoutButton.getStyle() + " -fx-background-color: #ef4444; -fx-text-fill: " + TEXT_COLOR_LIGHT + ";");
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(logoutButton.getStyle() + "-fx-background-color: #dc2626;"));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(logoutButton.getStyle().replace("-fx-background-color: #dc2626;", "-fx-background-color: #ef4444;")));


        navbar.getChildren().addAll(brandLabel, spacer, homeButton, ordersButton, cartButton,reviewsButton, profileButton, logoutButton);
        return navbar;
    }

    private static Button createNavButton(String text, Runnable action) {
        Button button = new Button(text);
        
        String baseStyle = 
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
            "-fx-font-size: 15px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 10 18 10 18;";
            
        String hoverStyle = 
            "-fx-background-color: " + HOVER_COLOR + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 10 18 10 18; " +
            "-fx-background-radius: 8;";

        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        button.setOnAction(e -> action.run());
        return button;
    }

    private static ScrollPane createRestaurantList(Stage stage, User user) {
        VBox restaurantBox = new VBox(25);
        restaurantBox.setPadding(new Insets(20, 0, 20, 0));
        restaurantBox.setAlignment(Pos.TOP_CENTER);
        
        RestaurantRepoImpl restaurantRepo = new RestaurantRepoImpl();
        List<Restaurant> restaurants = restaurantRepo.getAllRestaurants();
        
        System.out.println("=== RESTAURANT LIST DEBUG ===");
        System.out.println("Total restaurants: " + restaurants.size());

        if (restaurants.isEmpty()) {
            Label noRestaurants = new Label("No gourmet restaurants available at the moment.");
            noRestaurants.setFont(Font.font("System", 18));
            noRestaurants.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
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
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-background-radius: 18; " + 
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 6);"
        );
        card.setMaxWidth(900);

        HBox contentBox = new HBox(30);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPrefSize(180, 180);
        imageBox.setStyle("-fx-background-color: #2b3543; -fx-background-radius: 15;");
        imageBox.setMinSize(180, 180);
        imageBox.setMaxSize(180, 180);

        ImageView imageView = loadRestaurantImage(restaurant.getImagePath());

        if (imageView != null) {
            imageView.setFitWidth(180);
            imageView.setFitHeight(180);
            // imageView.setPreserveRatio(false); // Fill the square
            imageView.setSmooth(true);

            // Add rounded corners to the image to match the container
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(180, 180);
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            imageView.setClip(clip);

            imageBox.getChildren().add(imageView);
        } else {
            // Fallback: If no image is found, show the label
            Label imagePlaceholder = new Label("📷");
            imagePlaceholder.setFont(Font.font(80));
            imagePlaceholder.setStyle("-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";");
            imageBox.getChildren().add(imagePlaceholder);
        }

        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.setPrefWidth(500);

        String restaurantName = restaurant.getName() != null ? restaurant.getName() : "Luxury Restaurant";
        Label nameLabel = new Label(restaurantName);
        nameLabel.setStyle(
            "-fx-font-size: 30px; " +
            "-fx-font-weight: extra-bold; " +
            "-fx-text-fill: " + ACCENT_GOLD + ";"
        );
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(500);
        
        String address = restaurant.getAddress() != null ? restaurant.getAddress() : "Exclusive Location";
        Label addressLabel = new Label("📍 " + address);
        addressLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";"
        );
        addressLabel.setWrapText(true);
        addressLabel.setMaxWidth(500);
        
        String hours = restaurant.getOpeningHours() != null ? restaurant.getOpeningHours() : "24/7";
        Label hoursLabel = new Label("🕐 " + hours);
        hoursLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";"
        );
        hoursLabel.setWrapText(true);
        hoursLabel.setMaxWidth(500);
        
        Label ratingLabel = new Label("⭐ " + String.format("%.1f", restaurant.getRating()) + " / 5.0");
        ratingLabel.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-padding: 6 15; " +
            "-fx-background-radius: 20; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold;"
        );

        infoBox.getChildren().addAll(nameLabel, addressLabel, hoursLabel, ratingLabel);

        Button viewMenuButton = new Button("View Menu");
        viewMenuButton.setPrefWidth(180);
        viewMenuButton.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            BUTTON_STYLE_BASE
        );
        viewMenuButton.setOnMouseEntered(e -> viewMenuButton.setStyle(viewMenuButton.getStyle() + "-fx-background-color: #047857;"));
        viewMenuButton.setOnMouseExited(e -> viewMenuButton.setStyle(viewMenuButton.getStyle().replace("-fx-background-color: #047857;", "-fx-background-color: " + PRIMARY_COLOR + ";")));
        viewMenuButton.setOnAction(e -> MenuController.show(stage, user, restaurant));

        VBox buttonContainer = new VBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(viewMenuButton);


        contentBox.getChildren().addAll(imageBox, infoBox, buttonContainer);
        card.getChildren().add(contentBox);

        return card;
    }
    private static ImageView loadRestaurantImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }

        try {
            // 1. Clean the path to get just the filename (e.g., "1.jpg")
            String filename = new java.io.File(imagePath).getName();

            // 2. Look in resources (Standard for built apps)
            String resourcePath = "/images/menu/" + filename; 

            java.net.URL imageUrl = ClientMainController.class.getResource(resourcePath);

            if (imageUrl != null) {
                return new ImageView(new Image(imageUrl.toExternalForm()));
            } 

            // 3. Optional: Fallback for local files (good for testing before moving files)
            java.io.File file = new java.io.File(imagePath);
            if (file.exists()) {
                return new ImageView(new Image(file.toURI().toString()));
            }

        } catch (Exception e) {
            System.err.println("Could not load image: " + imagePath);
        }
        return null; 
    }
}
