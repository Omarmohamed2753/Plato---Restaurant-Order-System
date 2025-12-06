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

    // Theme Colors
    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String CARD_BACKGROUND = "#374151";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String ITEM_BACKGROUND = "#2b3543";
    private static final String ERROR_COLOR = "#ef4444";
    private static final String SUCCESS_COLOR = "#10b981";
    private static final String BUTTON_BLUE = "#3b82f6";

    // ORDERS CONTROLLER
    public static class AdminOrdersController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Manage Orders");

            TableView<Order> table = new TableView<>();
            table.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-control-inner-background: " + CARD_BACKGROUND + ";"
            );
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            TableColumn<Order, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getOrderId()));
            styleTableColumn(idCol);

            TableColumn<Order, String> userCol = new TableColumn<>("User");
            userCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getUser() != null ? d.getValue().getUser().getName() : "Unknown"));
            styleTableColumn(userCol);

            TableColumn<Order, String> totalCol = new TableColumn<>("Total");
            totalCol.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getTotalAmount())));
            styleTableColumn(totalCol);

            TableColumn<Order, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().toString()));
            styleTableColumn(statusCol);

            TableColumn<Order, String> deliveryCol = new TableColumn<>("Delivery Person");
            deliveryCol.setCellValueFactory(d -> {
                Order order = d.getValue();
                if (order.getDelivery() != null && order.getDelivery().getDeliveryPerson() != null) {
                    return new SimpleStringProperty(order.getDelivery().getDeliveryPerson().getName());
                }
                return new SimpleStringProperty("Not Assigned");
            });
            styleTableColumn(deliveryCol);

            TableColumn<Order, Void> assignCol = new TableColumn<>("Assign Delivery");
            assignCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Assign");
                {
                    btn.setStyle(
                        "-fx-background-color: " + PRIMARY_COLOR + "; " +
                        "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-cursor: hand;"
                    );
                    btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-background-color: #047857;"));
                    btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: #047857;", "-fx-background-color: " + PRIMARY_COLOR + ";")));
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
                        if (order.getStatus() != OrderStatus.DELIVERED && 
                            order.getStatus() != OrderStatus.CANCELLED) {
                            setGraphic(btn);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            });
            styleTableColumn(assignCol);

            TableColumn<Order, Void> actionCol = new TableColumn<>("Action");
            actionCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Next Status");
                {
                    btn.setStyle(
                        "-fx-background-color: " + BUTTON_BLUE + "; " +
                        "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-cursor: hand;"
                    );
                    btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-background-color: #2563eb;"));
                    btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: #2563eb;", "-fx-background-color: " + BUTTON_BLUE + ";")));
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
            styleTableColumn(actionCol);

            table.getColumns().addAll(idCol, userCol, totalCol, statusCol, deliveryCol, assignCol, actionCol);
            loadOrdersData(table, admin);

            VBox.setVgrow(table, Priority.ALWAYS);
            layout.getChildren().add(table);
            stage.getScene().setRoot(layout);
        }

        private static void loadOrdersData(TableView<Order> table, Admin admin) {
            if (admin.getRestaurant() != null) {
                System.out.println("DEBUG: Loading orders for restaurant " + admin.getRestaurant().getRestaurantId());
                
                List<Order> allOrders = orderService.getAllOrders();
                System.out.println("DEBUG: Total orders in system: " + allOrders.size());
                
                List<Order> orders = allOrders.stream()
                    .filter(o -> o.getRestaurant() != null && 
                            o.getRestaurant().getRestaurantId().equals(admin.getRestaurant().getRestaurantId()))
                    .collect(Collectors.toList());
                
                System.out.println("DEBUG: Orders for this restaurant: " + orders.size());
                
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
                        showStyledAlert("Please assign a delivery person first!", Alert.AlertType.WARNING);
                        yield order.getStatus();
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
            dialog.getDialogPane().setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

            ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

            // Style buttons
            dialog.getDialogPane().lookupButton(assignButtonType).setStyle(
                "-fx-background-color: " + PRIMARY_COLOR + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 15;"
            );
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
                "-fx-background-color: " + ERROR_COLOR + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 15;"
            );

            List<Employee> allEmployees = employeeService.getAllEmployees();
            List<Order> activeOrders = orderService.getAllOrders();
            
            List<Employee> deliveryPersons = allEmployees.stream()
                .filter(emp -> emp.getRole() != null && emp.getRole().equalsIgnoreCase("Delivery"))
                .collect(Collectors.toList());

            java.util.Set<String> occupiedDeliveryPersonIds = activeOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED)
                .filter(o -> o.getDelivery() != null && o.getDelivery().getDeliveryPerson() != null)
                .map(o -> o.getDelivery().getDeliveryPerson().getId())
                .collect(Collectors.toSet());

            if (deliveryPersons.isEmpty()) {
                showStyledAlert("No delivery persons available in this restaurant!", Alert.AlertType.WARNING);
                return;
            }

            List<Employee> availablePersons = new ArrayList<>();
            List<Employee> busyPersons = new ArrayList<>();
            
            for (Employee emp : deliveryPersons) {
                if (occupiedDeliveryPersonIds.contains(emp.getId())) {
                    busyPersons.add(emp);
                } else {
                    availablePersons.add(emp);
                }
            }
            
            if (availablePersons.isEmpty()) {
                showStyledAlert("No delivery persons available right now!\nAll delivery staff are currently busy with other orders.", 
                         Alert.AlertType.WARNING);
                return;
            }

            VBox content = new VBox(20);
            content.setPadding(new Insets(20));
            content.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

            Label statsLabel = new Label(String.format("Available: %d | Busy: %d", 
                availablePersons.size(), busyPersons.size()));
            statsLabel.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " + SUCCESS_COLOR + ";"
            );

            ComboBox<Employee> deliveryCombo = new ComboBox<>();
            deliveryCombo.setPrefWidth(350);
            deliveryCombo.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + ";"
            );
            
            for (Employee emp : availablePersons) {
                deliveryCombo.getItems().add(emp);
            }

            deliveryCombo.setCellFactory(lv -> new ListCell<Employee>() {
                @Override
                protected void updateItem(Employee emp, boolean empty) {
                    super.updateItem(emp, empty);
                    if (empty || emp == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(emp.getName() + " - " + emp.getPhoneNumber() + " ✓ Available");
                        setStyle(
                            "-fx-text-fill: " + SUCCESS_COLOR + "; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-color: " + CARD_BACKGROUND + ";"
                        );
                    }
                }
            });

            deliveryCombo.setButtonCell(new ListCell<Employee>() {
                @Override
                protected void updateItem(Employee emp, boolean empty) {
                    super.updateItem(emp, empty);
                    if (empty || emp == null) {
                        setText("Select Available Delivery Person");
                        setStyle("-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";");
                    } else {
                        setText(emp.getName() + " ✓");
                        setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + ";");
                    }
                }
            });

            Label promptLabel = new Label("Select Delivery Person:");
            promptLabel.setStyle(
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold;"
            );

            Label infoLabel = new Label(busyPersons.isEmpty() ? 
                "All delivery staff are available!" : 
                String.format("Note: %d delivery person(s) are currently busy with other orders", busyPersons.size()));
            infoLabel.setStyle(
                "-fx-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
                "-fx-font-size: 12px;"
            );

            content.getChildren().addAll(statsLabel, promptLabel, deliveryCombo, infoLabel);
            dialog.getDialogPane().setContent(content);

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
                List<Order> currentOrders = orderService.getAllOrders();
                boolean isNowOccupied = currentOrders.stream()
                    .filter(o -> o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED)
                    .filter(o -> o.getDelivery() != null && o.getDelivery().getDeliveryPerson() != null)
                    .anyMatch(o -> o.getDelivery().getDeliveryPerson().getId().equals(selectedEmployee.getId()));
                
                if (isNowOccupied) {
                    showStyledAlert("Sorry! This delivery person was just assigned to another order.\nPlease select a different person.", 
                             Alert.AlertType.ERROR);
                    return;
                }

                try {
                    System.out.println("=== ASSIGNING DELIVERY PERSON ===");
                    System.out.println("Order ID: " + order.getOrderId());
                    System.out.println("Employee: " + selectedEmployee.getName() + " (ID: " + selectedEmployee.getId() + ")");
                    
                    Delivery delivery = order.getDelivery();
                    if (delivery == null) {
                        delivery = new Delivery();
                        delivery.setDeliveryId("DEL" + System.currentTimeMillis());
                        delivery.setStatus("Pending Assignment");
                        deliveryService.addDelivery(delivery);
                        System.out.println("Created new delivery: " + delivery.getDeliveryId());
                    } else {
                        System.out.println("Using existing delivery: " + delivery.getDeliveryId());
                    }

                    deliveryService.assignDeliveryPerson(delivery, selectedEmployee, order);
                    System.out.println("Service assigned delivery person");
                    
                    try (java.sql.Connection conn = javaproject1.DAL.DataBase.DBConnection.getConnection()) {
                        String updateDeliverySql = "UPDATE delivery SET delivery_person_id = ?, status = ? WHERE delivery_id = ?";
                        try (java.sql.PreparedStatement stmt = conn.prepareStatement(updateDeliverySql)) {
                            stmt.setInt(1, Integer.parseInt(selectedEmployee.getId()));
                            stmt.setString(2, "Assigned");
                            stmt.setString(3, delivery.getDeliveryId());
                            int rows = stmt.executeUpdate();
                            System.out.println("Updated delivery table: " + rows + " rows");
                        }
                        
                        String updateOrderSql = "UPDATE orders SET delivery_id = ? WHERE order_id = ?";
                        try (java.sql.PreparedStatement stmt = conn.prepareStatement(updateOrderSql)) {
                            stmt.setString(1, delivery.getDeliveryId());
                            stmt.setString(2, order.getOrderId());
                            int rows = stmt.executeUpdate();
                            System.out.println("Linked order to delivery: " + rows + " rows");
                        }
                    }
                    
                    order.setDelivery(delivery);
                    delivery.setDeliveryPerson(selectedEmployee);
                    
                    System.out.println("=== ASSIGNMENT COMPLETE ===");
                    
                    showStyledAlert("Delivery person assigned successfully!\n" +
                             "Order #" + order.getOrderId() + " → " + selectedEmployee.getName(), 
                             Alert.AlertType.INFORMATION);
                    
                    System.out.println("Refreshing table data...");
                    loadOrdersData(table, admin);
                    
                } catch (Exception ex) {
                    showStyledAlert("Error assigning delivery person: " + ex.getMessage(), Alert.AlertType.ERROR);
                    System.err.println("ERROR in delivery assignment:");
                    ex.printStackTrace();
                }
            });
        }
    }

    // MENU CONTROLLER 
    public static class AdminMenuController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Manage Menu");

            HBox form = new HBox(15);
            form.setAlignment(Pos.CENTER_LEFT);
            form.setPadding(new Insets(20));
            form.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-background-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);"
            );
            
            TextField nameField = createStyledTextField("Name");
            nameField.setPrefWidth(180);
            
            TextField priceField = createStyledTextField("Price");
            priceField.setPrefWidth(120);
            
            TextField descField = createStyledTextField("Description");
            descField.setPrefWidth(220);
            
            TextField catField = createStyledTextField("Category");
            catField.setPrefWidth(140);
            
            Button addBtn = new Button("Add Item");
            addBtn.setStyle(
                "-fx-background-color: " + SUCCESS_COLOR + "; " +
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 15; " +
                "-fx-cursor: hand;"
            );
            addBtn.setOnMouseEntered(e -> addBtn.setStyle(addBtn.getStyle() + "-fx-background-color: #059669;"));
            addBtn.setOnMouseExited(e -> addBtn.setStyle(addBtn.getStyle().replace("-fx-background-color: #059669;", "-fx-background-color: " + SUCCESS_COLOR + ";")));

            TableView<MenuItem> table = new TableView<>();
            table.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-control-inner-background: " + CARD_BACKGROUND + ";"
            );
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            TableColumn<MenuItem, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
            styleTableColumn(nameCol);

            TableColumn<MenuItem, String> priceCol = new TableColumn<>("Price");
            priceCol.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getPrice())));
            styleTableColumn(priceCol);

            TableColumn<MenuItem, String> catCol = new TableColumn<>("Category");
            catCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));
            styleTableColumn(catCol);
            
            TableColumn<MenuItem, String> descCol = new TableColumn<>("Description");
            descCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
            styleTableColumn(descCol);

            TableColumn<MenuItem, Void> delCol = new TableColumn<>("Delete");
            delCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Remove");
                {
                    btn.setStyle(
                        "-fx-background-color: " + ERROR_COLOR + "; " +
                        "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-cursor: hand;"
                    );
                    btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-background-color: #dc2626;"));
                    btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: #dc2626;", "-fx-background-color: " + ERROR_COLOR + ";")));
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
            styleTableColumn(delCol);

            table.getColumns().addAll(nameCol, priceCol, catCol, descCol, delCol);
            loadMenuData(table, admin);

            addBtn.setOnAction(e -> {
                try {
                    String name = nameField.getText().trim();
                    String priceText = priceField.getText().trim();
                    String description = descField.getText().trim();
                    String category = catField.getText().trim();
                    
                    if (name.isEmpty() || priceText.isEmpty()) {
                        showStyledAlert("Name and Price are required!", Alert.AlertType.WARNING);
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
                        
                        nameField.clear(); 
                        priceField.clear(); 
                        descField.clear();
                        catField.clear();
                        
                        loadMenuData(table, admin);
                        showStyledAlert("Menu item added successfully!", Alert.AlertType.INFORMATION);
                    }
                } catch (NumberFormatException ex) {
                    showStyledAlert("Price must be a valid number!", Alert.AlertType.ERROR);
                } catch (Exception ex) {
                    showStyledAlert("Error: " + ex.getMessage(), Alert.AlertType.ERROR);
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
                    Restaurant freshRestaurant = restaurantService.getRestaurantById(
                        Integer.parseInt(admin.getRestaurant().getRestaurantId())
                    );
                    
                    if (freshRestaurant != null) {
                        admin.setRestaurant(freshRestaurant);
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

    // EMPLOYEES CONTROLLER
    public static class AdminEmployeesController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Manage Employees");

            HBox form = new HBox(15);
            form.setAlignment(Pos.CENTER_LEFT);
            form.setPadding(new Insets(20));
            form.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-background-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);"
            );
            
            TextField nameField = createStyledTextField("Name");
            nameField.setPrefWidth(180);
            
            TextField roleField = createStyledTextField("Role");
            roleField.setPrefWidth(180);
            
            TextField phoneField = createStyledTextField("Phone");
            phoneField.setPrefWidth(160);
            
            Button hireBtn = new Button("Hire Employee");
            hireBtn.setStyle(
                "-fx-background-color: " + SUCCESS_COLOR + "; " +
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 15; " +
                "-fx-cursor: hand;"
            );
            hireBtn.setOnMouseEntered(e -> hireBtn.setStyle(hireBtn.getStyle() + "-fx-background-color: #059669;"));
            hireBtn.setOnMouseExited(e -> hireBtn.setStyle(hireBtn.getStyle().replace("-fx-background-color: #059669;", "-fx-background-color: " + SUCCESS_COLOR + ";")));

            TableView<Employee> table = new TableView<>();
            table.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-control-inner-background: " + CARD_BACKGROUND + ";"
            );
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
            styleTableColumn(nameCol);
            
            TableColumn<Employee, String> roleCol = new TableColumn<>("Role");
            roleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole()));
            styleTableColumn(roleCol);
            
            TableColumn<Employee, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhoneNumber()));
            styleTableColumn(phoneCol);

            TableColumn<Employee, Void> actionCol = new TableColumn<>("Action");
            actionCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Fire");
            
                {
                    btn.setStyle(
                        "-fx-background-color: " + ERROR_COLOR + "; " +
                        "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-cursor: hand;"
                    );
                    btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-background-color: #dc2626;"));
                    btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: #dc2626;", "-fx-background-color: " + ERROR_COLOR + ";")));
            
                    btn.setOnAction(event -> {
                        Employee emp = getTableView().getItems().get(getIndex());
            
                        if (admin.getRestaurant() != null) {
                            restaurantService.fireEmployee(admin.getRestaurant(), emp);
            
                            String empId = emp.getId();
                            String numericPart = empId.replaceAll("\\D", "");
            
                            try {
                                int intId = Integer.parseInt(numericPart);
                                employeeService.deleteEmployee(intId);
                            } catch (NumberFormatException ex) {
                                System.err.println("Cannot delete employee, ID numeric part invalid: " + empId);
                            }
            
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
            styleTableColumn(actionCol);

            table.getColumns().addAll(nameCol, roleCol, phoneCol, actionCol);
            loadEmployeeData(table, admin);

            hireBtn.setOnAction(e -> {
                String name = nameField.getText().trim();
                String role = roleField.getText().trim();
                String phone = phoneField.getText().trim();
                
                if(name.isEmpty() || role.isEmpty()) {
                    showStyledAlert("Name and Role are required!", Alert.AlertType.WARNING);
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
                    showStyledAlert("Employee hired successfully!", Alert.AlertType.INFORMATION);
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

    // REVIEWS CONTROLLER
    public static class AdminReviewsController {
        private static ReviewServiceImpl reviewService = new ReviewServiceImpl();
        
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Restaurant Reviews");
            
            TableView<Review> table = new TableView<>();
            table.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-control-inner-background: " + CARD_BACKGROUND + ";"
            );
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            TableColumn<Review, String> userCol = new TableColumn<>("User");
            userCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getUser() != null ? d.getValue().getUser().getName() : "Anonymous"
            ));
            styleTableColumn(userCol);
            
            TableColumn<Review, String> ratingCol = new TableColumn<>("Rating");
            ratingCol.setCellValueFactory(d -> new SimpleStringProperty("⭐ " + d.getValue().getRating()));
            styleTableColumn(ratingCol);
            
            TableColumn<Review, String> commentCol = new TableColumn<>("Comment");
            commentCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getComment()));
            styleTableColumn(commentCol);

            table.getColumns().addAll(userCol, ratingCol, commentCol);

            if (admin.getRestaurant() != null) {
                String restaurantId = admin.getRestaurant().getRestaurantId();
                
                List<Review> allReviews = reviewService.getAllReviews();
                
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
                    noReviews.setFont(Font.font("System", 18));
                    noReviews.setStyle("-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";");
                    VBox.setMargin(noReviews, new Insets(40, 0, 0, 0));
                    layout.getChildren().add(noReviews);
                }
            } else {
                Label noRestaurant = new Label("No restaurant assigned.");
                noRestaurant.setFont(Font.font("System", 18));
                noRestaurant.setStyle("-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";");
                VBox.setMargin(noRestaurant, new Insets(40, 0, 0, 0));
                layout.getChildren().add(noRestaurant);
            }
            
            stage.getScene().setRoot(layout);
        }
    }

    // USERS CONTROLLER 
    public static class AdminUsersController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Users Information");
            
            TableView<User> table = new TableView<>();
            table.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-control-inner-background: " + CARD_BACKGROUND + ";"
            );
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            TableColumn<User, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getId()));
            styleTableColumn(idCol);
            
            TableColumn<User, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
            styleTableColumn(nameCol);
            
            TableColumn<User, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
            styleTableColumn(emailCol);
            
            TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhoneNumber()));
            styleTableColumn(phoneCol);
            
            TableColumn<User, String> eliteCol = new TableColumn<>("Elite");
            eliteCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isElite() ? "Yes" : "No"));
            styleTableColumn(eliteCol);
            
            TableColumn<User, String> ordersCol = new TableColumn<>("Orders");
            ordersCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrders() != null ? String.valueOf(d.getValue().getOrders().size()) : "0"
            ));
            styleTableColumn(ordersCol);

            table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, eliteCol, ordersCol);
            
            UserServiceImpl userService = new UserServiceImpl();
            List<User> allUsers = userService.getAllUsers();
            table.setItems(FXCollections.observableArrayList(allUsers));
            
            VBox.setVgrow(table, Priority.ALWAYS);
            layout.getChildren().add(table);
            stage.getScene().setRoot(layout);
        }
    }

    // HELPER METHODS 
    private static VBox createBaseLayout(Stage stage, Admin admin, String title) {
        VBox layout = new VBox(25);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");
        
        HBox header = new HBox(25);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Button backBtn = new Button("← Back");
        backBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + PRIMARY_COLOR + "; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-cursor: hand;"
        );
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(backBtn.getStyle() + "-fx-text-fill: " + ACCENT_GOLD + ";"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(backBtn.getStyle().replace("-fx-text-fill: " + ACCENT_GOLD + ";", "-fx-text-fill: " + PRIMARY_COLOR + ";")));
        backBtn.setOnAction(e -> AdminDashboardController.show(stage, admin));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 36));
        titleLabel.setStyle("-fx-text-fill: " + ACCENT_GOLD + ";");
        
        header.getChildren().addAll(backBtn, titleLabel);
        layout.getChildren().add(header);
        
        return layout;
    }
    
    private static TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(
            "-fx-padding: 12; " +
            "-fx-font-size: 14px; " +
            "-fx-background-color: " + ITEM_BACKGROUND + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
            "-fx-background-radius: 10;"
        );
        return field;
    }
    
    private static void styleTableColumn(TableColumn<?, ?> column) {
        column.setStyle(
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-alignment: CENTER-LEFT; " +
            "-fx-font-size: 14px;"
        );
    }
    
    private static void showStyledAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
        
        // Style content
        javafx.scene.Node content = dialogPane.lookup(".label.content");
        if (content != null) {
            content.setStyle(
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                "-fx-font-size: 14px;"
            );
        }
        
        // Style header
        javafx.scene.Node header = dialogPane.lookup(".header-panel");
        if (header != null) {
            header.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
        }
        
        javafx.scene.Node headerText = dialogPane.lookup(".header-panel .label");
        if (headerText != null) {
            headerText.setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + ";");
        }
        
        alert.showAndWait();
    }
}