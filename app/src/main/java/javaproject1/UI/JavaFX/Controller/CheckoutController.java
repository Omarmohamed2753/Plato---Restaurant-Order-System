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
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.BLL.Service.implementation.*;
import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Enums.OrderStatus;
import javaproject1.DAL.Enums.PaymentM;

import java.util.*;
import javafx.scene.control.Alert;
import javaproject1.DAL.Repo.Implementation.RestaurantRepoImpl;

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

        Label titleLabel = new Label("Checkout");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#1a1a1a"));

        // Order Summary
        VBox summaryBox = new VBox(15);
        summaryBox.setPadding(new Insets(20));
        summaryBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label summaryTitle = new Label("Order Summary");
        summaryTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        summaryTitle.setTextFill(Color.web("#1a1a1a"));

        VBox itemsBox = new VBox(10);
        double subtotal = 0.0;
        for (CartItem item : cart.getItems()) {
            HBox itemRow = new HBox(10);
            Label itemName = new Label(item.getMenuItem().getName() + " x" + item.getQuantity());
            itemName.setFont(Font.font("System", 14));
            itemName.setTextFill(Color.web("#1a1a1a"));
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label itemPrice = new Label("$" + String.format("%.2f", item.getSubPrice()));
            itemPrice.setFont(Font.font("System", FontWeight.BOLD, 14));
            itemPrice.setTextFill(Color.web("#1a1a1a"));
            itemRow.getChildren().addAll(itemName, spacer, itemPrice);
            itemsBox.getChildren().add(itemRow);
            subtotal += item.getSubPrice();
        }

        double tax = subtotal * 0.1;
        double deliveryFee = 30.0;
        double discount = 0.0;
        if (user.getSubscription() != null && user.getSubscription().isActive()) {
            discount = (subtotal + tax + deliveryFee) * 0.1;
        }
        double total = subtotal + tax + deliveryFee - discount;

        Label subtotalLabel = createSummaryRow("Subtotal:", "$" + String.format("%.2f", subtotal));
        Label taxLabel = createSummaryRow("Tax (10%):", "$" + String.format("%.2f", tax));
        Label deliveryLabel = createSummaryRow("Delivery Fee:", "$" + String.format("%.2f", deliveryFee));
        if (discount > 0) {
            Label discountLabel = createSummaryRow("Discount (10%):", "-$" + String.format("%.2f", discount));
            discountLabel.setTextFill(Color.web("#27ae60"));
            summaryBox.getChildren().add(discountLabel);
        }
        Label totalLabel = new Label("Total: $" + String.format("%.2f", total));
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        totalLabel.setTextFill(Color.web("#667eea"));

        summaryBox.getChildren().addAll(summaryTitle, itemsBox, subtotalLabel, taxLabel, deliveryLabel, totalLabel);

        // Address Selection
        VBox addressBox = new VBox(15);
        addressBox.setPadding(new Insets(20));
        addressBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label addressTitle = new Label("Delivery Address");
        addressTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        addressTitle.setTextFill(Color.web("#1a1a1a"));

        ComboBox<Address> addressCombo = new ComboBox<>();
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            addressCombo.getItems().addAll(user.getAddresses());
            addressCombo.setValue(user.getAddresses().get(0));
        } else {
            Label noAddress = new Label("No addresses available. Please add an address in your profile.");
            noAddress.setFont(Font.font("System", 14));
            noAddress.setTextFill(Color.web("#e74c3c"));
            addressBox.getChildren().addAll(addressTitle, noAddress);
        }

        if (!addressCombo.getItems().isEmpty()) {
            addressCombo.setPrefWidth(400);
            addressBox.getChildren().addAll(addressTitle, addressCombo);
        }

        // Payment Method
        VBox paymentBox = new VBox(15);
        paymentBox.setPadding(new Insets(20));
        paymentBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label paymentTitle = new Label("Payment Method");
        paymentTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        paymentTitle.setTextFill(Color.web("#1a1a1a"));

        ToggleGroup paymentGroup = new ToggleGroup();
        RadioButton cashRadio = new RadioButton("Cash on Delivery");
        cashRadio.setToggleGroup(paymentGroup);
        cashRadio.setSelected(true);
        cashRadio.setTextFill(Color.web("#1a1a1a"));
        RadioButton cardRadio = new RadioButton("Credit Card");
        cardRadio.setToggleGroup(paymentGroup);
        cardRadio.setTextFill(Color.web("#1a1a1a"));

        paymentBox.getChildren().addAll(paymentTitle, cashRadio, cardRadio);

        // Place Order Button
        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.setPrefWidth(300);
        placeOrderButton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 30 15 30; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand;"
        );

        placeOrderButton.setOnAction(e -> {
            if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please add a delivery address first!");
                alert.showAndWait();
                return;
            }

            Address selectedAddress = addressCombo.getValue();
            if (selectedAddress == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a delivery address!");
                alert.showAndWait();
                return;
            }

            // Get restaurant from first menu item (simplified - assumes all items from same restaurant)
            Restaurant restaurant = null;
            if (!cart.getItems().isEmpty()) {
                // For now, get first restaurant - in real app, you'd need to group by restaurant
                MenuItem firstItem = cart.getItems().get(0).getMenuItem();
                // We need to find which restaurant this menu item belongs to
                // This is simplified - you'd need to query menu_items -> menu -> restaurant
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

            // Create order (order_id will be auto-generated by database)
            Order order = new Order();
            order.setUser(user);
            order.setRestaurant(restaurant);
            order.setItems(new ArrayList<>(cart.getItems()));
            order.setDeliveryAddress(selectedAddress);
            order.setTotalAmount(total);
            order.setStatus(OrderStatus.PENDING);
            order.setOrderDate(new Date());

            // Create payment
            Payment payment = new Payment();
            payment.setPaymentId("PAY" + System.currentTimeMillis());
            payment.setAmount(total);
            payment.setPaymentMethod(cashRadio.isSelected() ? PaymentM.Cash : PaymentM.CreditCard);
            payment.setStatus("Pending");
            order.setPayment(payment);

            try {
                orderService.addOrder(order);
                // Update payment with order ID after order is created
                if (order.getOrderId() != null) {
                    payment.setOrderId(order.getOrderId());
                }
                cartService.clearCart(cart);
                
                Alert success = new Alert(Alert.AlertType.INFORMATION, 
                    "Order placed successfully!\nOrder ID: " + order.getOrderId() + "\nTotal: $" + String.format("%.2f", total));
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

    private static Label createSummaryRow(String label, String value) {
        HBox row = new HBox(10);
        Label labelLbl = new Label(label);
        labelLbl.setFont(Font.font("System", 14));
        labelLbl.setTextFill(Color.web("#4a5568"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label valueLbl = new Label(value);
        valueLbl.setFont(Font.font("System", 14));
        valueLbl.setTextFill(Color.web("#1a1a1a"));
        row.getChildren().addAll(labelLbl, spacer, valueLbl);
        
        Label result = new Label();
        result.setGraphic(row);
        return result;
    }
}
