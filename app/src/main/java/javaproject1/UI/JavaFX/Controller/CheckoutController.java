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
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.util.*;

public class CheckoutController {

    private static final OrderServiceImpl    orderService    = new OrderServiceImpl();
    private static final CartServiceImpl     cartService     = new CartServiceImpl();
    private static final PaymentServiceImpl  paymentService  = new PaymentServiceImpl();
    private static final RestaurantRepoImpl  restaurantRepo  = new RestaurantRepoImpl();
    private static final DeliveryServiceImpl deliveryService = new DeliveryServiceImpl();
    private static final DeliveryRepoImpl    deliveryRepo    = new DeliveryRepoImpl();

    public static void show(Stage stage, User user) {
        Cart cart = user.getCart();

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Your cart is empty!").showAndWait();
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
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        // ── Calculate totals ──────────────────────────────────────────────────
        double subtotal = cart.getItems().stream()
                .mapToDouble(CartItem::getSubPrice).sum();
        double tax         = subtotal * 0.1;
        double deliveryFee = 30.0;
        double discount    = 0.0;
        if (user.getSubscription() != null && user.getSubscription().isActive()) {
            discount = (subtotal + tax + deliveryFee) * 0.1;
        }
        double total = subtotal + tax + deliveryFee - discount;

        VBox summaryBox  = createSummaryBox(cart, subtotal, tax, deliveryFee, discount, total);
        VBox addressBox  = createAddressBox(user);
        ComboBox<Address> addressCombo = (ComboBox<Address>) addressBox.lookup(".combo-box");

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
            // ── Validate address ──────────────────────────────────────────────
            if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
                new Alert(Alert.AlertType.ERROR,
                        "Please add a delivery address in your profile first!").showAndWait();
                return;
            }

            Address selectedAddress = addressCombo != null ? addressCombo.getValue() : null;
            if (selectedAddress == null) {
                new Alert(Alert.AlertType.ERROR,
                        "Please select a delivery address!").showAndWait();
                return;
            }

            // ── Validate payment ──────────────────────────────────────────────
            RadioButton selectedPayment = (RadioButton) paymentGroup.getSelectedToggle();
            if (selectedPayment == null) {
                new Alert(Alert.AlertType.ERROR,
                        "Please select a payment method!").showAndWait();
                return;
            }

            boolean isCreditCard = selectedPayment.getText().contains("Credit Card");

            if (isCreditCard) {
                TextField cardNumberField = (TextField) cardDetailsBox.lookup("#cardNumber");
                TextField cardHolderField = (TextField) cardDetailsBox.lookup("#cardHolder");
                TextField expiryField     = (TextField) cardDetailsBox.lookup("#expiry");
                TextField cvvField        = (TextField) cardDetailsBox.lookup("#cvv");

                if (cardNumberField.getText().trim().isEmpty() ||
                        cardHolderField.getText().trim().isEmpty() ||
                        expiryField.getText().trim().isEmpty() ||
                        cvvField.getText().trim().isEmpty()) {
                    new Alert(Alert.AlertType.ERROR,
                            "Please fill in all credit card details!").showAndWait();
                    return;
                }

                String cardNumber = cardNumberField.getText().trim().replaceAll("\\s+", "");
                String cvv = cvvField.getText().trim();

                if (!cardNumber.matches("\\d{16}")) {
                    new Alert(Alert.AlertType.ERROR, "Card number must be 16 digits!").showAndWait();
                    return;
                }
                if (!cvv.matches("\\d{3,4}")) {
                    new Alert(Alert.AlertType.ERROR, "CVV must be 3 or 4 digits!").showAndWait();
                    return;
                }
            }

            // ── Resolve restaurant from cart ──────────────────────────────────
            Restaurant restaurant = resolveRestaurant(cart);
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

                // 1. Create Delivery record
                Delivery delivery = new Delivery();
                delivery.setDeliveryId("DEL" + System.currentTimeMillis());
                delivery.setStatus("Pending Assignment");
                deliveryRepo.addDelivery(delivery);
                System.out.println("Delivery created: " + delivery.getDeliveryId());

                // 2. Process Payment
                Payment payment = new Payment();
                payment.setPaymentId("PAY" + System.currentTimeMillis());
                payment.setAmount(total);
                payment.setPaymentMethod(isCreditCard ? PaymentM.CreditCard : PaymentM.Cash);
                payment.setStatus("Pending");
                payment.setTransactionDate(new Date());

                if (!paymentService.processPayment(payment)) {
                    throw new Exception("Payment processing failed!");
                }
                paymentService.addPayment(payment);
                System.out.println("Payment created: " + payment.getPaymentId());

                // 3. Create Order (snapshot items before cart clear)
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
                System.out.println("Order created: " + order.getOrderId());

                if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
                    throw new Exception("Failed to create order — no ID generated!");
                }

                // Update payment with real order ID
                payment.setOrderId(order.getOrderId());
                paymentService.updatePayment(payment);

                // 4. Link cart items → order_items via JPA, then delete cart_item rows
                linkAndClearOrderItems(order, orderedItems, cart);

                // 5. Clear in-memory cart
                cart.getItems().clear();
                System.out.println("=== ORDER CREATION COMPLETE ===");

                // 6. Success
                new Alert(Alert.AlertType.INFORMATION,
                        "Order placed successfully!\n\n" +
                        "Order ID: "    + order.getOrderId() + "\n" +
                        "Restaurant: "  + restaurant.getName() + "\n" +
                        "Delivery to: " + selectedAddress.getStreet()
                                        + ", " + selectedAddress.getCity() + "\n" +
                        "Total: $"      + String.format("%.2f", total) + "\n" +
                        "Payment: "     + (isCreditCard ? "Credit Card" : "Cash on Delivery")
                                        + "\n\nYou can track your order in 'My Orders'.")
                        .showAndWait();

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
        
        stage.setScene(new Scene(root, 
            stage.getWidth() > 0 ? stage.getWidth() : 1200,
            stage.getHeight() > 0 ? stage.getHeight() : 750));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Finds the restaurant that owns the first menu item in the cart.
     * Uses JPA — no JDBC.
     */
    private static Restaurant resolveRestaurant(Cart cart) {
        if (cart.getItems().isEmpty()) return null;

        String firstItemId = cart.getItems().get(0).getMenuItem().getItemId();
        List<javaproject1.DAL.Entity.Restaurant> all = restaurantRepo.getAllRestaurants();

        for (javaproject1.DAL.Entity.Restaurant r : all) {
            if (r.getMenu() == null || r.getMenu().getItems() == null) continue;
            for (MenuItem mi : r.getMenu().getItems()) {
                if (mi.getItemId().equals(firstItemId)) return r;
            }
        }
        return null;
    }

    /**
     * Inserts order_items rows and removes the corresponding cart_item rows,
     * both in a single JPA transaction — no raw SQL, no PreparedStatement.
     */
    private static void linkAndClearOrderItems(Order order,
                                               List<CartItem> orderedItems,
                                               Cart cart) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            javaproject1.plato.Orders jpaOrder = em.find(
                    javaproject1.plato.Orders.class,
                    Integer.parseInt(order.getOrderId()));

            if (jpaOrder != null) {
                int linked = 0;
                for (CartItem item : orderedItems) {
                    if (item.getCartItemID() <= 0) continue;

                    javaproject1.plato.CartItem jpaItem = em.find(
                            javaproject1.plato.CartItem.class, item.getCartItemID());
                    if (jpaItem == null) continue;

                    javaproject1.plato.OrderItems oi = new javaproject1.plato.OrderItems();
                    oi.setOrderId(jpaOrder);
                    oi.setCartItemId(jpaItem);
                    em.persist(oi);
                    linked++;
                }
                System.out.println("Linked " + linked + " cart items to order");
            }

            // Delete all cart_item rows for this cart so they never reappear
            int deleted = em
                    .createQuery("DELETE FROM CartItem ci WHERE ci.cartId.cartId = :cartId")
                    .setParameter("cartId", cart.getCartId())
                    .executeUpdate();
            System.out.println("Deleted " + deleted + " cart_item rows for cart "
                    + cart.getCartId());

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Error linking order items: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ── UI builders (unchanged) ───────────────────────────────────────────────

    private static VBox createSummaryBox(Cart cart, double subtotal, double tax,
                                         double deliveryFee, double discount, double total) {
        VBox summaryBox = new VBox(15);
        summaryBox.setPadding(new Insets(20));
        summaryBox.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        Label summaryTitle = new Label("Order Summary");
        summaryTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        VBox itemsBox = new VBox(10);
        for (CartItem item : cart.getItems()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label name  = new Label(item.getMenuItem().getName() + " x" + item.getQuantity());
            name.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            Label price = new Label("$" + String.format("%.2f", item.getSubPrice()));
            price.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #000000;");
            row.getChildren().addAll(name, sp, price);
            itemsBox.getChildren().add(row);
        }

        HBox subtotalRow  = createSummaryRow("Subtotal:", "$" + String.format("%.2f", subtotal));
        HBox taxRow       = createSummaryRow("Tax (10%):", "$" + String.format("%.2f", tax));
        HBox deliveryRow  = createSummaryRow("Delivery Fee:", "$" + String.format("%.2f", deliveryFee));

        VBox allRows = new VBox(5);
        allRows.getChildren().addAll(subtotalRow, taxRow, deliveryRow);

        if (discount > 0) {
            HBox discountRow = createSummaryRow(
                    "Elite Discount (10%):", "-$" + String.format("%.2f", discount));
            ((Label) discountRow.getChildren().get(2))
                    .setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
            allRows.getChildren().add(discountRow);
        }

        Label totalLabel = new Label("Total: $" + String.format("%.2f", total));
        totalLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        summaryBox.getChildren().addAll(summaryTitle, itemsBox, allRows, totalLabel);
        return summaryBox;
    }

    private static HBox createSummaryRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label); l.setStyle("-fx-font-size: 14px; -fx-text-fill: #4a5568;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(value); v.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
        row.getChildren().addAll(l, sp, v);
        return row;
    }

    private static VBox createAddressBox(User user) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        Label title = new Label("Delivery Address");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        ComboBox<Address> combo = new ComboBox<>();
        combo.getStyleClass().add("combo-box");

        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            combo.getItems().addAll(user.getAddresses());
            combo.setValue(user.getAddresses().get(0));
            combo.setPrefWidth(500);
            combo.setStyle("-fx-font-size: 14px;");
            box.getChildren().addAll(title, combo);
        } else {
            Label none = new Label("No addresses available. Please add one in your profile.");
            none.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");
            box.getChildren().addAll(title, none);
        }
        return box;
    }

    private static VBox createPaymentBox() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        Label title = new Label("Payment Method");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        ToggleGroup group = new ToggleGroup();

        RadioButton cash = new RadioButton("Cash on Delivery");
        cash.setToggleGroup(group);
        cash.setSelected(true);
        cash.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");

        RadioButton card = new RadioButton("Credit Card");
        card.setToggleGroup(group);
        card.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px;");

        VBox cardDetails = new VBox(10);
        cardDetails.setPadding(new Insets(15, 0, 0, 30));
        cardDetails.setVisible(false);
        cardDetails.setManaged(false);
        cardDetails.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 8;");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField cardNum  = new TextField(); cardNum.setId("cardNumber");  cardNum.setPromptText("1234 5678 9012 3456"); cardNum.setPrefWidth(300);
        TextField holder   = new TextField(); holder.setId("cardHolder");   holder.setPromptText("John Doe");             holder.setPrefWidth(300);
        TextField expiry   = new TextField(); expiry.setId("expiry");       expiry.setPromptText("MM/YY");                expiry.setPrefWidth(100);
        TextField cvv      = new TextField(); cvv.setId("cvv");             cvv.setPromptText("123");                     cvv.setPrefWidth(80);

        grid.add(new Label("Card Number:"), 0, 0); grid.add(cardNum, 1, 0);
        grid.add(new Label("Card Holder:"), 0, 1); grid.add(holder, 1, 1);
        HBox expiryRow = new HBox(10);
        expiryRow.getChildren().addAll(new Label("Expiry:"), expiry, new Label("CVV:"), cvv);
        grid.add(expiryRow, 1, 2);

        Label secure = new Label("Your payment information is secure and encrypted");
        secure.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60;");

        cardDetails.getChildren().addAll(grid, secure);

        group.selectedToggleProperty().addListener((obs, old, nv) -> {
            boolean show = nv == card;
            cardDetails.setVisible(show);
            cardDetails.setManaged(show);
        });

        box.setUserData(group);
        box.getChildren().addAll(title, cash, card, cardDetails);
        return box;
    }
}