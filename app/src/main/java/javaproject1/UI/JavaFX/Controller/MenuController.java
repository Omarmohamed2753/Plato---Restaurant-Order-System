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
import javaproject1.DAL.Enums.OrderStatus;

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

        Label titleLabel = new Label(restaurant.getName() + " Menu");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));

        ScrollPane menuScroll = createMenuItems(user, restaurant);
        VBox.setVgrow(menuScroll, Priority.ALWAYS);

        contentBox.getChildren().addAll(titleLabel, menuScroll);
        root.setCenter(contentBox);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    private static ScrollPane createMenuItems(User user, Restaurant restaurant) {
        VBox itemsBox = new VBox(15);
        itemsBox.setPadding(new Insets(20));

        Menu menu = restaurant.getMenu();
        if (menu != null && menu.getItems() != null) {
            for (MenuItem item : menu.getItems()) {
                itemsBox.getChildren().add(createMenuItemCard(user, item));
            }
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
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        VBox infoBox = new VBox(5);
        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        Label descLabel = new Label(item.getDescription());
        descLabel.setFont(Font.font("System", 12));
        descLabel.setTextFill(Color.web("#7f8c8d"));
        
        Label priceLabel = new Label("$" + item.getPrice());
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        priceLabel.setTextFill(Color.web("#27ae60"));

        infoBox.getChildren().addAll(nameLabel, descLabel, priceLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1);
        quantitySpinner.setPrefWidth(80);

        Button addButton = new Button("Add to Cart");
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

        card.getChildren().addAll(infoBox, spacer, quantitySpinner, addButton);
        return card;
    }

    private static void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}