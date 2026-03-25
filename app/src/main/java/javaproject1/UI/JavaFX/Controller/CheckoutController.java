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
import javaproject1.DAL.Repo.Implementation.DeliveryRepoImpl;

import java.util.*;

public class CheckoutController {
    private static OrderServiceImpl    orderService    = new OrderServiceImpl();
    private static CartServiceImpl     cartService     = new CartServiceImpl();
    private static PaymentServiceImpl  paymentService  = new PaymentServiceImpl();
    private static RestaurantRepoImpl  restaurantRepo  = new RestaurantRepoImpl();
    private static DeliveryServiceImpl deliveryService = new DeliveryServiceImpl();
    private static DeliveryRepoImpl    deliveryRepo    = new DeliveryRepoImpl();

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
        titleLabel.setStyle(
            "-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        // ── Calculate totals ──────────────────────────────────────────────────
        double subtotal = 0.0;
        for (CartItem item : cart.getItems()) {
            subtotal += item.getSubPrice();
        }
        double tax         = subtotal * 0.1;
        double deliveryFee = 30.0;
        double discount    = 0.0;
        if (user.getSubscription() != null && user.getSubscription().isActive()) {
            discount = (subtotal + tax + deliveryFee) * 0.1;
        }
        double total = subtotal + tax + deliveryFee - discount;

        VBox summaryBox  = createSummaryBox(cart, subtotal, tax, deliveryFee, discount, total);
        VBox addressBox  = createAddressBox(user);
        ComboBox<Address> addressCombo =
            (ComboBox<Address>) addressBox.lookup(".combo-box");

        VBox paymentBox  = createPaymentBox();
        ToggleGroup paymentGroup = (ToggleGroup) paymentBox.getUserData();
        VBox cardDetailsBox =
            (VBox) paymentBox.getChildren().get(paymentBox.getChildren().size() - 1);

        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.setPrefWidth(300);
        placeOrderButton.setStyle(
            "-fx-background-color: #27ae60; -fx-text-fill: white; " +
            "-fx-font-size: 18px; -fx-font-weight: bold; " +
            "-fx-padding: 15 30; -fx-background-radius: 25; -fx-cursor: hand;");

        Label orderMessageLabel = new Label();
        orderMessageLabel.setFont(Font.font("System", 14));
        orderMessageLabel.setWrapText(true);

        placeOrderButton.setOnAction(e -> {
            // ── Address check ─────────────────────────────────────────────────
            if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
                new Alert(Alert.AlertType.ERROR,
                    "Please add a delivery address in your profile first!")
                    .showAndWait();
                return;
            }

            Address selectedAddress = addressCombo != null ? addressCombo.getValue() : null;
            if (selectedAddress == null) {
                new Alert(Alert.AlertType.ERROR, "Please select a delivery address!")
                    .showAndWait();
                return;
            }

            // ── Payment method check ──────────────────────────────────────────
            RadioButton selectedPayment =
                (RadioButton) paymentGroup.getSelectedToggle();
            if (selectedPayment == null) {
                new Alert(Alert.AlertType.ERROR, "Please select a payment method!")
                    .showAndWait();
                return;
            }

            boolean isCreditCard = selectedPayment.getText().contains("Credit Card");

            if (isCreditCard) {
                TextField cardNumberField =
                    (TextField) cardDetailsBox.lookup("#cardNumber");
                TextField cardHolderField =
                    (TextField) cardDetailsBox.lookup("#cardHolder");
                TextField expiryField =
                    (TextField) cardDetailsBox.lookup("#expiry");
                TextField cvvField =
                    (TextField) cardDetailsBox.lookup("#cvv");

                if (cardNumberField.getText().trim().isEmpty() ||
                        cardHolderField.getText().trim().isEmpty() ||
                        expiryField.getText().trim().isEmpty() ||
                        cvvField.getText().trim().isEmpty()) {
                    new Alert(Alert.AlertType.ERROR,
                        "Please fill in all credit card details!").showAndWait();
                    return;
                }

                String cardNumber =
                    cardNumberField.getText().trim().replaceAll("\\s+", "");
                String cvv = cvvField.getText().trim();

                if (!cardNumber.matches("\\d{16}")) {
                    new Alert(Alert.AlertType.ERROR,
                        "Card number must be 16 digits!").showAndWait();
                    return;
                }
                if (!cvv.matches("\\d{3,4}")) {
                    new Alert(Alert.AlertType.ERROR,
                        "CVV must be 3 or 4 digits!").showAndWait();
                    return;
                }
            }

            // ── Determine restaurant from cart items ──────────────────────────
            Restaurant restaurant = null;
            if (!cart.getItems().isEmpty()) {
                MenuItem firstItem = cart.getItems().get(0).getMenuItem();
                List<Restaurant> restaurants = restaurantRepo.getAllRestaurants();
                outer:
                for (Restaurant r : restaurants) {
                    if (r.getMenu() != null && r.getMenu().getItems() != null) {
                        for (MenuItem mi : r.getMenu().getItems()) {
                            if (mi.getItemId().equals(firstItem.getItemId())) {
                                restaurant = r;
                                break outer;
                            }
                        }
                    }
                }
            }

            if (restaurant == null) {
                new Alert(Alert.AlertType.ERROR,
                    "Could not determine restaurant. Please try again.").showAndWait();
                return;
            }

            placeOrderButton.setDisable(true);
            orderMessageLabel.setText("Processing your order...");
            orderMessageLabel.setTextFill(Color.web("#3498db"));

            try {
                System.out.println("=== STARTING ORDER CREATION ===");

                // ── 1. Create Delivery record ─────────────────────────────────
                Delivery delivery = new Delivery();
                delivery.setDeliveryId("DEL" + System.currentTimeMillis());
                delivery.setStatus("Pending Assignment");
                delivery.setEstimatedDeliveryTime(null);
                delivery.setDeliveryPerson(null);
                deliveryRepo.addDelivery(delivery);
                System.out.println("✓ Delivery created: " + delivery.getDeliveryId());

                // ── 2. Process & persist Payment ──────────────────────────────
                Payment payment = new Payment();
                payment.setPaymentId("PAY" + System.currentTimeMillis());
                payment.setAmount(total);
                payment.setPaymentMethod(isCreditCard ? PaymentM.CreditCard : PaymentM.Cash);
                payment.setStatus("Pending");
                payment.setTransactionDate(new Date());

                boolean paymentSuccess = paymentService.processPayment(payment);
                if (!paymentSuccess) {
                    throw new Exception("Payment processing failed!");
                }
                paymentService.addPayment(payment);
                System.out.println("✓ Payment created: " + payment.getPaymentId());

                // ── 3. Create Order ───────────────────────────────────────────
                // Snapshot the cart items before clearing
                List<CartItem> orderedItems = new ArrayList<>(cart.getItems());

                Order order = new Order();
                order.setUser(user);
                order.setRestaurant(restaurant);
                order.setItems(orderedItems);
                order.setDeliveryAddress(selectedAddress);
                order.setTotalAmount(total);
                order.setStatus(OrderStatus.PENDING);
                order.setOrderDate(new Date());
                order.setPayment(payment);
                order.setDelivery(delivery);

                orderService.addOrder(order);
                System.out.println("✓ Order created: " + order.getOrderId());

                if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
                    throw new Exception(
                        "Failed to create order — no order ID generated!");
                }

                // Update payment with the real order ID
                payment.setOrderId(order.getOrderId());
                paymentService.updatePayment(payment);
                System.out.println("✓ Payment updated with order ID");

                // ── 4. Link cart items → order_items, then DELETE them from cart ──
                try (java.sql.Connection conn = javaproject1.DAL.DataBase.DBConnection
                        .getConnection()) {

                    // 4a. Insert into order_items
                    String insertSql =
                        "INSERT INTO order_items (order_id, cart_item_id) VALUES (?, ?)";
                    try (java.sql.PreparedStatement stmt =
                                 conn.prepareStatement(insertSql)) {
                        for (CartItem item : orderedItems) {
                            if (item.getCartItemID() > 0) {
                                stmt.setString(1, order.getOrderId());
                                stmt.setInt(2, item.getCartItemID());
                                stmt.addBatch();
                            }
                        }
                        int[] results = stmt.executeBatch();
                        System.out.println("✓ Linked " + results.length
                                + " cart items to order");
                    }

                    // 4b. CRITICAL FIX: physically delete cart_item rows from DB
                    //     so they never reappear when the user logs back in.
                    String deleteSql =
                        "DELETE FROM cart_item WHERE cart_id = ?";
                    try (java.sql.PreparedStatement stmt =
                                 conn.prepareStatement(deleteSql)) {
                        stmt.setInt(1, cart.getCartId());
                        int deleted = stmt.executeUpdate();
                        System.out.println("✓ Deleted " + deleted
                                + " cart_item rows from DB for cart "
                                + cart.getCartId());
                    }
                }

                // ── 5. Clear the in-memory cart list ──────────────────────────
                cart.getItems().clear();
                System.out.println("✓ In-memory cart cleared");
                System.out.println("=== ORDER CREATION COMPLETE ===");

                // ── 6. Success alert & navigate home ─────────────────────────
                Alert success = new Alert(Alert.AlertType.INFORMATION,
                    "Order placed successfully!\n\n" +
                    "Order ID: "    + order.getOrderId() + "\n" +
                    "Restaurant: "  + restaurant.getName() + "\n" +
                    "Delivery to: " + selectedAddress.getStreet()
                                    + ", " + selectedAddress.getCity() + "\n" +
                    "Total: $"      + String.format("%.2f", total) + "\n" +
                    "Payment: "     + (isCreditCard ? "Credit Card" : "Cash on Delivery")
                                    + "\n\n" +
                    "You can track your order in 'My Orders'.");
                success.setTitle("Order Confirmed");
                success.showAndWait();

                ClientMainController.show(stage, user);

            } catch (Exception ex) {
                placeOrderButton.setDisable(false);
                orderMessageLabel.setText("Error: " + ex.getMessage());
                orderMessageLabel.setTextFill(Color.web("#e74c3c"));

                new Alert(Alert.AlertType.ERROR,
                    "Error placing order: " + ex.getMessage()).showAndWait();
                ex.printStackTrace();
            }
        });

        contentBox.getChildren().addAll(
            titleLabel, summaryBox, addressBox, paymentBox,
            placeOrderButton, orderMessageLabel);
        contentBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    // ── Summary box ───────────────────────────────────────────────────────────

    private static VBox createSummaryBox(Cart cart, double subtotal, double tax,
                                         double deliveryFee, double discount, double total) {
        VBox summaryBox = new VBox(15);
        summaryBox.setPadding(new Insets(20));
        summaryBox.setStyle(
            "-fx-background-color: white; -fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        Label summaryTitle = new Label("Order Summary");
        summaryTitle.setStyle(
            "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        VBox itemsBox = new VBox(10);
        for (CartItem item : cart.getItems()) {
            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);

            Label itemName = new Label(item.getMenuItem().getName() + " x" + item.getQuantity());
            itemName.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label itemPrice = new Label("$" + String.format("%.2f", item.getSubPrice()));
            itemPrice.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000;");

            itemRow.getChildren().addAll(itemName, spacer, itemPrice);
            itemsBox.getChildren().add(itemRow);
        }

        HBox subtotalRow  = createSummaryRow("Subtotal:", "$" + String.format("%.2f", subtotal));
        HBox taxRow       = createSummaryRow("Tax (10%):", "$" + String.format("%.2f", tax));
        HBox deliveryRow  = createSummaryRow("Delivery Fee:", "$" + String.format("%.2f", deliveryFee));

        VBox allRows = new VBox(5);
        allRows.getChildren().addAll(subtotalRow, taxRow, deliveryRow);

        if (discount > 0) {
            HBox discountRow = createSummaryRow(
                "Elite Discount (10%):", "-$" + String.format("%.2f", discount));
            Label discountLabel = (Label) discountRow.getChildren().get(2);
            discountLabel.setStyle(
                "-fx-font-size: 14px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
            allRows.getChildren().add(discountRow);
        }

        Label totalLabel = new Label("Total: $" + String.format("%.2f", total));
        totalLabel.setStyle(
            "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

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

    // ── Address box ───────────────────────────────────────────────────────────

    private static VBox createAddressBox(User user) {
        VBox addressBox = new VBox(15);
        addressBox.setPadding(new Insets(20));
        addressBox.setStyle(
            "-fx-background-color: white; -fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        Label addressTitle = new Label("Delivery Address");
        addressTitle.setStyle(
            "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        ComboBox<Address> addressCombo = new ComboBox<>();
        addressCombo.getStyleClass().add("combo-box");

        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            addressCombo.getItems().addAll(user.getAddresses());
            addressCombo.setValue(user.getAddresses().get(0));
            addressCombo.setPrefWidth(500);
            addressCombo.setStyle("-fx-font-size: 14px;");
            addressBox.getChildren().addAll(addressTitle, addressCombo);
        } else {
            Label noAddress = new Label(
                "No addresses available. Please add an address in your profile.");
            noAddress.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");
            addressBox.getChildren().addAll(addressTitle, noAddress);
        }

        return addressBox;
    }

    // ── Payment box ───────────────────────────────────────────────────────────

    private static VBox createPaymentBox() {
        VBox paymentBox = new VBox(15);
        paymentBox.setPadding(new Insets(20));
        paymentBox.setStyle(
            "-fx-background-color: white; -fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        Label paymentTitle = new Label("Payment Method");
        paymentTitle.setStyle(
            "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        ToggleGroup paymentGroup = new ToggleGroup();

        RadioButton cashRadio = new RadioButton("Cash on Delivery");
        cashRadio.setToggleGroup(paymentGroup);
        cashRadio.setSelected(true);
        cashRadio.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");

        RadioButton cardRadio = new RadioButton("Credit Card");
        cardRadio.setToggleGroup(paymentGroup);
        cardRadio.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");

        VBox cardDetailsBox = new VBox(10);
        cardDetailsBox.setPadding(new Insets(15, 0, 0, 30));
        cardDetailsBox.setVisible(false);
        cardDetailsBox.setManaged(false);
        cardDetailsBox.setStyle(
            "-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 8;");

        GridPane cardGrid = new GridPane();
        cardGrid.setHgap(10);
        cardGrid.setVgap(10);

        TextField cardNumberField = new TextField();
        cardNumberField.setId("cardNumber");
        cardNumberField.setPromptText("1234 5678 9012 3456");
        cardNumberField.setPrefWidth(300);

        TextField cardHolderField = new TextField();
        cardHolderField.setId("cardHolder");
        cardHolderField.setPromptText("John Doe");
        cardHolderField.setPrefWidth(300);

        TextField expiryField = new TextField();
        expiryField.setId("expiry");
        expiryField.setPromptText("MM/YY");
        expiryField.setPrefWidth(100);

        TextField cvvField = new TextField();
        cvvField.setId("cvv");
        cvvField.setPromptText("123");
        cvvField.setPrefWidth(80);

        cardGrid.add(new Label("Card Number:"), 0, 0);
        cardGrid.add(cardNumberField, 1, 0);
        cardGrid.add(new Label("Card Holder:"), 0, 1);
        cardGrid.add(cardHolderField, 1, 1);

        HBox expiryRow = new HBox(10);
        expiryRow.getChildren().addAll(
            new Label("Expiry:"), expiryField, new Label("CVV:"), cvvField);
        cardGrid.add(expiryRow, 1, 2);

        Label secureLabel = new Label(
            "🔒 Your payment information is secure and encrypted");
        secureLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60;");

        cardDetailsBox.getChildren().addAll(cardGrid, secureLabel);

        paymentGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal == cardRadio) {
                cardDetailsBox.setVisible(true);
                cardDetailsBox.setManaged(true);
            } else {
                cardDetailsBox.setVisible(false);
                cardDetailsBox.setManaged(false);
            }
        });

        paymentBox.setUserData(paymentGroup);
        paymentBox.getChildren().addAll(paymentTitle, cashRadio, cardRadio, cardDetailsBox);
        return paymentBox;
    }
}