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
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Enums.OrderStatus;
import javaproject1.DAL.Enums.PaymentM;
import javaproject1.DAL.Repo.Implementation.RestaurantRepoImpl;

import java.util.*;

public class CheckoutController {
    private static OrderServiceImpl orderService = new OrderServiceImpl();
    private static CartServiceImpl cartService = new CartServiceImpl();
    private static RestaurantRepoImpl restaurantRepo = new RestaurantRepoImpl();

    public static void show(Stage stage, User user) {
        Cart cart = user.getCart();
        
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Your cart is empty!");
            alert.showAndWait();
            CartController.show(stage, user);
            return;
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(30));
        contentBox.setMaxWidth(800);

        // TITLE
        Label titleLabel = new Label("Checkout");
        titleLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #000000;"
        );

        // Calculate totals
        double subtotal = 0.0;
        for (CartItem item : cart.getItems()) {
            subtotal += item.getSubPrice();
        }
        double tax = subtotal * 0.1;
        double deliveryFee = 30.0;
        double discount = 0.0;
        if (user.getSubscription() != null && user.getSubscription().isActive()) {
            discount = (subtotal + tax + deliveryFee) * 0.1;
        }
        double total = subtotal + tax + deliveryFee - discount;

        // ORDER SUMMARY BOX
        VBox summaryBox = createSummaryBox(cart, subtotal, tax, deliveryFee, discount, total);

        // ADDRESS SELECTION BOX
        VBox addressBox = createAddressBox(user);
        ComboBox<Address> addressCombo = (ComboBox<Address>) addressBox.lookup(".combo-box");

        // PAYMENT METHOD BOX
        VBox paymentBox = createPaymentBox();
        ToggleGroup paymentGroup = (ToggleGroup) paymentBox.getUserData();

        // PLACE ORDER BUTTON
        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.setPrefWidth(300);
        placeOrderButton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 30; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand;"
        );

        placeOrderButton.setOnAction(e -> {
            if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please add a delivery address first!");
                alert.showAndWait();
                return;
            }

            Address selectedAddress = addressCombo != null ? addressCombo.getValue() : null;
            if (selectedAddress == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a delivery address!");
                alert.showAndWait();
                return;
            }

            // Get restaurant
            Restaurant restaurant = null;
            if (!cart.getItems().isEmpty()) {
                MenuItem firstItem = cart.getItems().get(0).getMenuItem();
                List<Restaurant> restaurants = restaurantRepo.getAllRestaurants();
                for (Restaurant r : restaurants) {
                    if (r.getMenu() != null && r.getMenu().getItems() != null) {
                        for (MenuItem mi : r.getMenu().getItems()) {
                            if (mi.getItemId().equals(firstItem.getItemId())) {
                                restaurant = r;
                                break;
                            }
                        }
                        if (restaurant != null) break;
                    }
                }
            }

            if (restaurant == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not determine restaurant. Please try again.");
                alert.showAndWait();
                return;
            }

            // Create order
            Order order = new Order();
            order.setUser(user);
            order.setRestaurant(restaurant);
            order.setItems(new ArrayList<>(cart.getItems()));
            order.setDeliveryAddress(selectedAddress);
            order.setTotalAmount(total);
            order.setStatus(OrderStatus.PENDING);
            order.setOrderDate(new Date());

            // Create payment
            RadioButton cashRadio = (RadioButton) paymentGroup.getSelectedToggle();
            Payment payment = new Payment();
            payment.setPaymentId("PAY" + System.currentTimeMillis());
            payment.setAmount(total);
            payment.setPaymentMethod(cashRadio != null && cashRadio.getText().contains("Cash") 
                ? PaymentM.Cash : PaymentM.CreditCard);
            payment.setStatus("Pending");
            order.setPayment(payment);

            try {
                orderService.addOrder(order);
                if (order.getOrderId() != null) {
                    payment.setOrderId(order.getOrderId());
                }
                cartService.clearCart(cart);
                
                Alert success = new Alert(Alert.AlertType.INFORMATION, 
                    "Order placed successfully!\nOrder ID: " + order.getOrderId() + 
                    "\nTotal: $" + String.format("%.2f", total));
                success.showAndWait();
                
                ClientMainController.show(stage, user);
            } catch (Exception ex) {
                Alert error = new Alert(Alert.AlertType.ERROR, "Error placing order: " + ex.getMessage());
                error.showAndWait();
            }
        });

        contentBox.getChildren().addAll(titleLabel, summaryBox, addressBox, paymentBox, placeOrderButton);
        contentBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    private static VBox createSummaryBox(Cart cart, double subtotal, double tax, 
                                         double deliveryFee, double discount, double total) {
        VBox summaryBox = new VBox(15);
        summaryBox.setPadding(new Insets(20));
        summaryBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label summaryTitle = new Label("Order Summary");
        summaryTitle.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #000000;"
        );

        VBox itemsBox = new VBox(10);
        for (CartItem item : cart.getItems()) {
            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            
            Label itemName = new Label(item.getMenuItem().getName() + " x" + item.getQuantity());
            itemName.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label itemPrice = new Label("$" + String.format("%.2f", item.getSubPrice()));
            itemPrice.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000;");
            
            itemRow.getChildren().addAll(itemName, spacer, itemPrice);
            itemsBox.getChildren().add(itemRow);
        }

        // Subtotal row
        HBox subtotalRow = createSummaryRow("Subtotal:", "$" + String.format("%.2f", subtotal));
        
        // Tax row
        HBox taxRow = createSummaryRow("Tax (10%):", "$" + String.format("%.2f", tax));
        
        // Delivery row
        HBox deliveryRow = createSummaryRow("Delivery Fee:", "$" + String.format("%.2f", deliveryFee));
        
        VBox allRows = new VBox(5);
        allRows.getChildren().addAll(subtotalRow, taxRow, deliveryRow);
        
        // Discount row (if applicable)
        if (discount > 0) {
            HBox discountRow = createSummaryRow("Discount (10%):", "-$" + String.format("%.2f", discount));
            Label discountLabel = (Label) discountRow.getChildren().get(2);
            discountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60;");
            allRows.getChildren().add(discountRow);
        }
        
        // Total
        Label totalLabel = new Label("Total: $" + String.format("%.2f", total));
        totalLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #667eea;"
        );

        summaryBox.getChildren().addAll(summaryTitle, itemsBox, allRows, totalLabel);
        return summaryBox;
    }

    private static HBox createSummaryRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #4a5568;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        
        row.getChildren().addAll(labelLbl, spacer, valueLbl);
        return row;
    }

    private static VBox createAddressBox(User user) {
        VBox addressBox = new VBox(15);
        addressBox.setPadding(new Insets(20));
        addressBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label addressTitle = new Label("Delivery Address");
        addressTitle.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #000000;"
        );

        ComboBox<Address> addressCombo = new ComboBox<>();
        addressCombo.getStyleClass().add("combo-box");
        
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            addressCombo.getItems().addAll(user.getAddresses());
            addressCombo.setValue(user.getAddresses().get(0));
            addressCombo.setPrefWidth(400);
            addressCombo.setStyle("-fx-font-size: 14px;");
            addressBox.getChildren().addAll(addressTitle, addressCombo);
        } else {
            Label noAddress = new Label("No addresses available. Please add an address in your profile.");
            noAddress.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");
            addressBox.getChildren().addAll(addressTitle, noAddress);
        }

        return addressBox;
    }

    private static VBox createPaymentBox() {
        VBox paymentBox = new VBox(15);
        paymentBox.setPadding(new Insets(20));
        paymentBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label paymentTitle = new Label("Payment Method");
        paymentTitle.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #000000;"
        );

        ToggleGroup paymentGroup = new ToggleGroup();
        
        RadioButton cashRadio = new RadioButton("Cash on Delivery");
        cashRadio.setToggleGroup(paymentGroup);
        cashRadio.setSelected(true);
        cashRadio.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");
        
        RadioButton cardRadio = new RadioButton("Credit Card");
        cardRadio.setToggleGroup(paymentGroup);
        cardRadio.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");

        paymentBox.setUserData(paymentGroup);
        paymentBox.getChildren().addAll(paymentTitle, cashRadio, cardRadio);
        return paymentBox;
    }
}