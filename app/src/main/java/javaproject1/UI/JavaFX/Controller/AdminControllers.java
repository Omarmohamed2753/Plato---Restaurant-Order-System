package javaproject1.UI.JavaFX.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javaproject1.BLL.Service.implementation.*;
import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Entity.Menu;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminControllers {

    private static OrderServiceImpl orderService = new OrderServiceImpl();
    private static MenuServiceImpl menuService = new MenuServiceImpl();
    private static RestaurantServiceImpl restaurantService = new RestaurantServiceImpl();
    private static EmployeeServiceImpl employeeService = new EmployeeServiceImpl();
    private static DeliveryServiceImpl deliveryService = new DeliveryServiceImpl();

    // ================== ORDERS CONTROLLER ==================
    public static class AdminOrdersController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Manage Orders");

            TableView<Order> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            TableColumn<Order, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getOrderId()));

            TableColumn<Order, String> userCol = new TableColumn<>("User");
            userCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getUser() != null ? d.getValue().getUser().getName() : "Unknown"));

            TableColumn<Order, String> totalCol = new TableColumn<>("Total");
            totalCol.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getTotalAmount())));

            TableColumn<Order, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().toString()));

            TableColumn<Order, String> deliveryCol = new TableColumn<>("Delivery Person");
            deliveryCol.setCellValueFactory(d -> {
                Order order = d.getValue();
                if (order.getDelivery() != null && order.getDelivery().getDeliveryPerson() != null) {
                    return new SimpleStringProperty(order.getDelivery().getDeliveryPerson().getName());
                }
                return new SimpleStringProperty("Not Assigned");
            });

            TableColumn<Order, Void> assignCol = new TableColumn<>("Assign Delivery");
            assignCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Assign");
                {
                    btn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10;");
                    btn.setOnAction(event -> {
                        Order order = getTableView().getItems().get(getIndex());
                        showAssignDeliveryDialog(stage, admin, order, table);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Order order = getTableView().getItems().get(getIndex());
                        // Show button for all orders except DELIVERED and CANCELLED
                        if (order.getStatus() != OrderStatus.DELIVERED && 
                            order.getStatus() != OrderStatus.CANCELLED) {
                            setGraphic(btn);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            });

            TableColumn<Order, Void> actionCol = new TableColumn<>("Action");
            actionCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Next Status");
                {
                    btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10;");
                    btn.setOnAction(event -> {
                        Order order = getTableView().getItems().get(getIndex());
                        advanceOrderStatus(order);
                        loadOrdersData(table, admin);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            });

            table.getColumns().addAll(idCol, userCol, totalCol, statusCol, deliveryCol, assignCol, actionCol);
            loadOrdersData(table, admin);

            VBox.setVgrow(table, Priority.ALWAYS);
            layout.getChildren().add(table);
            stage.getScene().setRoot(layout);
        }

        private static void loadOrdersData(TableView<Order> table, Admin admin) {
            if (admin.getRestaurant() != null) {
                System.out.println("DEBUG: Loading orders for restaurant " + admin.getRestaurant().getRestaurantId());
                
                // Get fresh orders from database with all related data
                List<Order> allOrders = orderService.getAllOrders();
                System.out.println("DEBUG: Total orders in system: " + allOrders.size());
                
                List<Order> orders = allOrders.stream()
                    .filter(o -> o.getRestaurant() != null && 
                            o.getRestaurant().getRestaurantId().equals(admin.getRestaurant().getRestaurantId()))
                    .collect(Collectors.toList());
                
                System.out.println("DEBUG: Orders for this restaurant: " + orders.size());
                
                // Debug each order's delivery info
                for (Order order : orders) {
                    System.out.println("  Order #" + order.getOrderId() + 
                        " - Delivery: " + (order.getDelivery() != null ? order.getDelivery().getDeliveryId() : "null") +
                        " - Person: " + (order.getDelivery() != null && order.getDelivery().getDeliveryPerson() != null ? 
                                        order.getDelivery().getDeliveryPerson().getName() : "Not Assigned"));
                }
                
                table.setItems(FXCollections.observableArrayList(orders));
                table.refresh();
            }
        }

        private static void advanceOrderStatus(Order order) {
            OrderStatus nextStatus = switch (order.getStatus()) {
                case PENDING -> OrderStatus.CONFIRMED;
                case CONFIRMED -> OrderStatus.PREPARING;
                case PREPARING -> OrderStatus.READY_FOR_DELIVERY;
                case READY_FOR_DELIVERY -> {
                    if (order.getDelivery() != null && order.getDelivery().getDeliveryPerson() != null) {
                        yield OrderStatus.OUT_FOR_DELIVERY;
                    } else {
                        showAlert("Please assign a delivery person first!", Alert.AlertType.WARNING);
                        yield order.getStatus(); // Don't advance
                    }
                }
                case OUT_FOR_DELIVERY -> OrderStatus.DELIVERED;
                default -> order.getStatus();
            };
            
            if (nextStatus != order.getStatus()) {
                orderService.updateStatus(order, nextStatus);
            }
        }

        private static void showAssignDeliveryDialog(Stage stage, Admin admin, Order order, TableView<Order> table) {
            Dialog<Employee> dialog = new Dialog<>();
            dialog.setTitle("Assign Delivery Person");
            dialog.setHeaderText("Select a delivery person for Order #" + order.getOrderId());

            ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

            // Get all delivery persons who are available
            List<Employee> allEmployees = employeeService.getAllEmployees();
            List<Order> activeOrders = orderService.getAllOrders();
            
            // Filter for delivery persons only in this restaurant
            List<Employee> deliveryPersons = allEmployees.stream()
                .filter(emp -> emp.getRole() != null && emp.getRole().equalsIgnoreCase("Delivery"))
                .collect(Collectors.toList());

            // CRITICAL FIX: Find ALL occupied delivery persons (not just OUT_FOR_DELIVERY)
            // A delivery person is busy if they're assigned to ANY order that's NOT delivered or cancelled
            java.util.Set<String> occupiedDeliveryPersonIds = activeOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED)
                .filter(o -> o.getDelivery() != null && o.getDelivery().getDeliveryPerson() != null)
                .map(o -> o.getDelivery().getDeliveryPerson().getId())
                .collect(Collectors.toSet());

            if (deliveryPersons.isEmpty()) {
                showAlert("No delivery persons available in this restaurant!", Alert.AlertType.WARNING);
                return;
            }

            // CRITICAL: Separate available and busy delivery persons
            List<Employee> availablePersons = new ArrayList<>();
            List<Employee> busyPersons = new ArrayList<>();
            
            for (Employee emp : deliveryPersons) {
                if (occupiedDeliveryPersonIds.contains(emp.getId())) {
                    busyPersons.add(emp);
                } else {
                    availablePersons.add(emp);
                }
            }
            
            // Check if any available persons exist
            if (availablePersons.isEmpty()) {
                showAlert("No delivery persons available right now!\nAll delivery staff are currently busy with other orders.", 
                         Alert.AlertType.WARNING);
                return;
            }

            VBox content = new VBox(15);
            content.setPadding(new Insets(20));

            // Show availability stats
            Label statsLabel = new Label(String.format("Available: %d | Busy: %d", 
                availablePersons.size(), busyPersons.size()));
            statsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #059669;");

            ComboBox<Employee> deliveryCombo = new ComboBox<>();
            deliveryCombo.setPrefWidth(300);
            
            // Add ONLY available employees to the combo box
            for (Employee emp : availablePersons) {
                deliveryCombo.getItems().add(emp);
            }

            // Custom cell factory - now only shows available persons
            deliveryCombo.setCellFactory(lv -> new ListCell<Employee>() {
                @Override
                protected void updateItem(Employee emp, boolean empty) {
                    super.updateItem(emp, empty);
                    if (empty || emp == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        // All persons in the combo are available
                        setText(emp.getName() + " - " + emp.getPhoneNumber() + " ✓ Available");
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    }
                }
            });

            // Button cell should show selected person
            deliveryCombo.setButtonCell(new ListCell<Employee>() {
                @Override
                protected void updateItem(Employee emp, boolean empty) {
                    super.updateItem(emp, empty);
                    if (empty || emp == null) {
                        setText("Select Available Delivery Person");
                    } else {
                        setText(emp.getName() + " ✓");
                    }
                }
            });

            Label infoLabel = new Label(busyPersons.isEmpty() ? 
                "All delivery staff are available!" : 
                String.format("Note: %d delivery person(s) are currently busy with other orders", busyPersons.size()));
            infoLabel.setStyle("-fx-text-fill: #636e72; -fx-font-size: 12px;");

            content.getChildren().addAll(
                statsLabel,
                new Label("Select Delivery Person:"),
                deliveryCombo,
                infoLabel
            );

            dialog.getDialogPane().setContent(content);

            // Disable assign button if no selection
            Node assignButton = dialog.getDialogPane().lookupButton(assignButtonType);
            assignButton.setDisable(true);
            deliveryCombo.valueProperty().addListener((obs, old, newVal) -> {
                assignButton.setDisable(newVal == null);
            });

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == assignButtonType) {
                    return deliveryCombo.getValue();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(selectedEmployee -> {
                // CRITICAL: Double-check that employee is still available (race condition protection)
                // Reload orders to check current status
                List<Order> currentOrders = orderService.getAllOrders();
                boolean isNowOccupied = currentOrders.stream()
                    .filter(o -> o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED)
                    .filter(o -> o.getDelivery() != null && o.getDelivery().getDeliveryPerson() != null)
                    .anyMatch(o -> o.getDelivery().getDeliveryPerson().getId().equals(selectedEmployee.getId()));
                
                if (isNowOccupied) {
                    showAlert("Sorry! This delivery person was just assigned to another order.\nPlease select a different person.", 
                             Alert.AlertType.ERROR);
                    return;
                }

                try {
                    System.out.println("=== ASSIGNING DELIVERY PERSON ===");
                    System.out.println("Order ID: " + order.getOrderId());
                    System.out.println("Employee: " + selectedEmployee.getName() + " (ID: " + selectedEmployee.getId() + ")");
                    
                    // CRITICAL FIX: Get or create delivery record
                    Delivery delivery = order.getDelivery();
                    if (delivery == null) {
                        // Create new delivery
                        delivery = new Delivery();
                        delivery.setDeliveryId("DEL" + System.currentTimeMillis());
                        delivery.setStatus("Pending Assignment");
                        deliveryService.addDelivery(delivery);
                        System.out.println("Created new delivery: " + delivery.getDeliveryId());
                    } else {
                        System.out.println("Using existing delivery: " + delivery.getDeliveryId());
                    }

                    // Assign delivery person using the service
                    deliveryService.assignDeliveryPerson(delivery, selectedEmployee, order);
                    System.out.println("Service assigned delivery person");
                    
                    // CRITICAL FIX: Update delivery in database with delivery person
                    try (java.sql.Connection conn = javaproject1.DAL.DataBase.DBConnection.getConnection()) {
                        // Update delivery table with person
                        String updateDeliverySql = "UPDATE delivery SET delivery_person_id = ?, status = ? WHERE delivery_id = ?";
                        try (java.sql.PreparedStatement stmt = conn.prepareStatement(updateDeliverySql)) {
                            stmt.setInt(1, Integer.parseInt(selectedEmployee.getId()));
                            stmt.setString(2, "Assigned");
                            stmt.setString(3, delivery.getDeliveryId());
                            int rows = stmt.executeUpdate();
                            System.out.println("Updated delivery table: " + rows + " rows");
                        }
                        
                        // CRITICAL FIX: Link delivery to order if not already linked
                        String updateOrderSql = "UPDATE orders SET delivery_id = ? WHERE order_id = ?";
                        try (java.sql.PreparedStatement stmt = conn.prepareStatement(updateOrderSql)) {
                            stmt.setString(1, delivery.getDeliveryId());
                            stmt.setString(2, order.getOrderId());
                            int rows = stmt.executeUpdate();
                            System.out.println("Linked order to delivery: " + rows + " rows");
                        }
                    }
                    
                    // CRITICAL FIX: Update the in-memory order object
                    order.setDelivery(delivery);
                    delivery.setDeliveryPerson(selectedEmployee);
                    
                    System.out.println("=== ASSIGNMENT COMPLETE ===");
                    
                    showAlert("Delivery person assigned successfully!\n" +
                             "Order #" + order.getOrderId() + " → " + selectedEmployee.getName(), 
                             Alert.AlertType.INFORMATION);
                    
                    // CRITICAL FIX: Reload data from database to refresh the table
                    System.out.println("Refreshing table data...");
                    loadOrdersData(table, admin);
                    
                } catch (Exception ex) {
                    showAlert("Error assigning delivery person: " + ex.getMessage(), Alert.AlertType.ERROR);
                    System.err.println("ERROR in delivery assignment:");
                    ex.printStackTrace();
                }
            });
        }
    }

    // ================== MENU CONTROLLER ==================
    public static class AdminMenuController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Manage Menu");

            HBox form = new HBox(10);
            form.setAlignment(Pos.CENTER_LEFT);
            TextField nameField = new TextField(); 
            nameField.setPromptText("Name");
            nameField.setPrefWidth(150);
            
            TextField priceField = new TextField(); 
            priceField.setPromptText("Price");
            priceField.setPrefWidth(100);
            
            TextField descField = new TextField(); 
            descField.setPromptText("Description");
            descField.setPrefWidth(200);
            
            TextField catField = new TextField(); 
            catField.setPromptText("Category");
            catField.setPrefWidth(120);
            
            Button addBtn = new Button("Add Item");
            addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15;");

            TableView<MenuItem> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            TableColumn<MenuItem, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

            TableColumn<MenuItem, String> priceCol = new TableColumn<>("Price");
            priceCol.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getPrice())));

            TableColumn<MenuItem, String> catCol = new TableColumn<>("Category");
            catCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));
            
            TableColumn<MenuItem, String> descCol = new TableColumn<>("Description");
            descCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));

            TableColumn<MenuItem, Void> delCol = new TableColumn<>("Delete");
            delCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("X");
                {
                    btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10;");
                    btn.setOnAction(event -> {
                        MenuItem item = getTableView().getItems().get(getIndex());
                        if (admin.getRestaurant() != null) {
                            menuService.removeItem(Integer.parseInt(admin.getRestaurant().getRestaurantId()), item);
                            loadMenuData(table, admin);
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            });

            table.getColumns().addAll(nameCol, priceCol, catCol, descCol, delCol);
            loadMenuData(table, admin);

            addBtn.setOnAction(e -> {
                try {
                    String name = nameField.getText().trim();
                    String priceText = priceField.getText().trim();
                    String description = descField.getText().trim();
                    String category = catField.getText().trim();
                    
                    if (name.isEmpty() || priceText.isEmpty()) {
                        showAlert("Name and Price are required!", Alert.AlertType.WARNING);
                        return;
                    }

                    double price = Double.parseDouble(priceText);
                    
                    MenuItem newItem = new MenuItem();
                    newItem.setName(name);
                    newItem.setPrice(price);
                    newItem.setDescription(description.isEmpty() ? "No description" : description);
                    newItem.setCategory(category.isEmpty() ? "General" : category);
                    
                    if (admin.getRestaurant() != null) {
                        int rId = Integer.parseInt(admin.getRestaurant().getRestaurantId());
                        menuService.addItem(rId, newItem);
                        
                        // Clear fields
                        nameField.clear(); 
                        priceField.clear(); 
                        descField.clear();
                        catField.clear();
                        
                        // Refresh table
                        loadMenuData(table, admin);
                        showAlert("Menu item added successfully!", Alert.AlertType.INFORMATION);
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Price must be a valid number!", Alert.AlertType.ERROR);
                } catch (Exception ex) {
                    showAlert("Error: " + ex.getMessage(), Alert.AlertType.ERROR);
                    ex.printStackTrace();
                }
            });

            form.getChildren().addAll(nameField, priceField, descField, catField, addBtn);
            VBox.setVgrow(table, Priority.ALWAYS);
            layout.getChildren().addAll(form, table);
            stage.getScene().setRoot(layout);
        }

        private static void loadMenuData(TableView<MenuItem> table, Admin admin) {
            if (admin.getRestaurant() != null) {
                try {
                    // Re-fetch the restaurant to get fresh data
                    Restaurant freshRestaurant = restaurantService.getRestaurantById(
                        Integer.parseInt(admin.getRestaurant().getRestaurantId())
                    );
                    
                    if (freshRestaurant != null) {
                        admin.setRestaurant(freshRestaurant); // Update admin's reference
                        Menu menu = freshRestaurant.getMenu();
                        
                        if (menu != null && menu.getItems() != null) {
                            table.setItems(FXCollections.observableArrayList(menu.getItems()));
                        } else {
                            table.setItems(FXCollections.observableArrayList());
                        }
                        table.refresh();
                    }
                } catch (Exception e) {
                    System.err.println("Error loading menu data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // ================== EMPLOYEES CONTROLLER ==================
    public static class AdminEmployeesController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Manage Employees");

            HBox form = new HBox(10);
            form.setAlignment(Pos.CENTER_LEFT);
            
            TextField nameField = new TextField(); 
            nameField.setPromptText("Name");
            nameField.setPrefWidth(150);
            
            TextField roleField = new TextField(); 
            roleField.setPromptText("Role");
            roleField.setPrefWidth(150);
            
            TextField phoneField = new TextField();
            phoneField.setPromptText("Phone");
            phoneField.setPrefWidth(130);
            
            Button hireBtn = new Button("Hire");
            hireBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 8 15;");

            TableView<Employee> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
            
            TableColumn<Employee, String> roleCol = new TableColumn<>("Role");
            roleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole()));
            
            TableColumn<Employee, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhoneNumber()));

            TableColumn<Employee, Void> actionCol = new TableColumn<>("Action");
            actionCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Fire");
            
                {
                    btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10;");
            
                    btn.setOnAction(event -> {
                        Employee emp = getTableView().getItems().get(getIndex());
            
                        if (admin.getRestaurant() != null) {
                            // Fire employee in the restaurant
                            restaurantService.fireEmployee(admin.getRestaurant(), emp);
            
                            String empId = emp.getId(); // could be "EMP2" or a large number
            
                            // Extract digits only
                            String numericPart = empId.replaceAll("\\D", ""); // removes non-digits
            
                            try {
                                // Parse numeric part as int safely
                                int intId = Integer.parseInt(numericPart);
                                employeeService.deleteEmployee(intId);
                            } catch (NumberFormatException ex) {
                                // If numeric part is empty or too large, handle gracefully
                                System.err.println("Cannot delete employee, ID numeric part invalid: " + empId);
                                // Optionally show an alert to the user
                            }
            
                            // Reload table data
                            loadEmployeeData(table, admin);
                        }
                    });
                }
            
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            });

            table.getColumns().addAll(nameCol, roleCol, phoneCol, actionCol);
            loadEmployeeData(table, admin);

            hireBtn.setOnAction(e -> {
                String name = nameField.getText().trim();
                String role = roleField.getText().trim();
                String phone = phoneField.getText().trim();
                
                if(name.isEmpty() || role.isEmpty()) {
                    showAlert("Name and Role are required!", Alert.AlertType.WARNING);
                    return;
                }
            
                Employee newEmp = new Employee();
                newEmp.setName(name);
                newEmp.setRole(role);
                newEmp.setAge(25);
                newEmp.setPhoneNumber(phone.isEmpty() ? "N/A" : phone); 
                newEmp.setExperiencesYear(0);
                
                if (admin.getRestaurant() != null) {
                    newEmp.setRestaurant(admin.getRestaurant());
                    employeeService.addEmployee(newEmp);
                    restaurantService.hireEmployee(admin.getRestaurant(), newEmp);
                    table.getItems().add(newEmp);
                    nameField.clear(); 
                    roleField.clear();
                    phoneField.clear();
                    showAlert("Employee hired successfully!", Alert.AlertType.INFORMATION);
                }
            });

            form.getChildren().addAll(nameField, roleField, phoneField, hireBtn);
            VBox.setVgrow(table, Priority.ALWAYS);
            layout.getChildren().addAll(form, table);
            stage.getScene().setRoot(layout);
        }

        private static void loadEmployeeData(TableView<Employee> table, Admin admin) {
            if (admin.getRestaurant() != null) {
                try {
                    // Refresh restaurant data
                    Restaurant r = restaurantService.getRestaurantById(Integer.parseInt(admin.getRestaurant().getRestaurantId()));
                    admin.setRestaurant(r);
                    
                    if(r != null && r.getEmployees() != null) {
                        table.setItems(FXCollections.observableArrayList(r.getEmployees()));
                    } else {
                        table.setItems(FXCollections.observableArrayList());
                    }
                    table.refresh();
                } catch (Exception e) {
                    System.err.println("Error loading employee data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // ================== REVIEWS CONTROLLER ==================
    public static class AdminReviewsController {
        private static ReviewServiceImpl reviewService = new ReviewServiceImpl();
        
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Restaurant Reviews");
            
            TableView<Review> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            TableColumn<Review, String> userCol = new TableColumn<>("User");
            userCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getUser() != null ? d.getValue().getUser().getName() : "Anonymous"
            ));
            
            TableColumn<Review, String> ratingCol = new TableColumn<>("Rating");
            ratingCol.setCellValueFactory(d -> new SimpleStringProperty("⭐ " + d.getValue().getRating()));
            
            TableColumn<Review, String> commentCol = new TableColumn<>("Comment");
            commentCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getComment()));

            table.getColumns().addAll(userCol, ratingCol, commentCol);

            if (admin.getRestaurant() != null) {
                String restaurantId = admin.getRestaurant().getRestaurantId();
                
                // Load ALL reviews from database (with full user details)
                List<Review> allReviews = reviewService.getAllReviews();
                
                // Filter for this restaurant only
                List<Review> restaurantReviews = allReviews.stream()
                    .filter(review -> review.getRestaurant() != null && 
                                     review.getRestaurant().getRestaurantId() != null &&
                                     review.getRestaurant().getRestaurantId().equals(restaurantId))
                    .collect(Collectors.toList());
                
                System.out.println("DEBUG AdminReviewsController: Found " + restaurantReviews.size() + 
                                   " reviews for restaurant " + restaurantId);
                
                if (!restaurantReviews.isEmpty()) {
                    table.setItems(FXCollections.observableArrayList(restaurantReviews));
                    VBox.setVgrow(table, Priority.ALWAYS);
                    layout.getChildren().add(table);
                } else {
                    Label noReviews = new Label("No reviews yet.");
                    noReviews.setFont(Font.font("System", 16));
                    noReviews.setTextFill(Color.web("#636e72"));
                    layout.getChildren().add(noReviews);
                }
            } else {
                Label noRestaurant = new Label("No restaurant assigned.");
                noRestaurant.setFont(Font.font("System", 16));
                noRestaurant.setTextFill(Color.web("#636e72"));
                layout.getChildren().add(noRestaurant);
            }
            
            stage.getScene().setRoot(layout);
        }
    }

    // ================== USERS CONTROLLER ==================
    public static class AdminUsersController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Users Information");
            
            TableView<User> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            TableColumn<User, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getId()));
            
            TableColumn<User, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
            
            TableColumn<User, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
            
            TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhoneNumber()));
            
            TableColumn<User, String> eliteCol = new TableColumn<>("Elite");
            eliteCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isElite() ? "Yes" : "No"));
            
            TableColumn<User, String> ordersCol = new TableColumn<>("Orders");
            ordersCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrders() != null ? String.valueOf(d.getValue().getOrders().size()) : "0"
            ));

            table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, eliteCol, ordersCol);
            
            // Load all users from database
            UserServiceImpl userService = new UserServiceImpl();
            List<User> allUsers = userService.getAllUsers();
            table.setItems(FXCollections.observableArrayList(allUsers));
            
            VBox.setVgrow(table, Priority.ALWAYS);
            layout.getChildren().add(table);
            stage.getScene().setRoot(layout);
        }
    }

    // ================== HELPER METHODS ==================
    private static VBox createBaseLayout(Stage stage, Admin admin, String title) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #f5f7fa;");
        
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #667eea; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setOnAction(e -> AdminDashboardController.show(stage, admin));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#1a1a1a"));
        
        header.getChildren().addAll(backBtn, titleLabel);
        layout.getChildren().add(header);
        
        return layout;
    }
    
    private static void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}