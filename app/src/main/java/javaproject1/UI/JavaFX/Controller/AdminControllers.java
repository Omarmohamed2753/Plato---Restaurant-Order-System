package javaproject1.UI.JavaFX.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import java.util.List;
import java.util.stream.Collectors;

public class AdminControllers {

    private static OrderServiceImpl orderService = new OrderServiceImpl();
    private static MenuServiceImpl menuService = new MenuServiceImpl();
    private static RestaurantServiceImpl restaurantService = new RestaurantServiceImpl();

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

            TableColumn<Order, Void> actionCol = new TableColumn<>("Action");
            actionCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Next Status");
                {
                    btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                    btn.setOnAction(event -> {
                        Order order = getTableView().getItems().get(getIndex());
                        advanceOrderStatus(order);
                        
                        // REFRESH DATA
                        loadOrdersData(table, admin);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            });

            table.getColumns().addAll(idCol, userCol, totalCol, statusCol, actionCol);
            loadOrdersData(table, admin);

            VBox.setVgrow(table, Priority.ALWAYS);
            layout.getChildren().add(table);
            stage.getScene().setRoot(layout);
        }

        private static void loadOrdersData(TableView<Order> table, Admin admin) {
            if (admin.getRestaurant() != null) {
                List<Order> orders = orderService.getAllOrders().stream()
                    .filter(o -> o.getRestaurant() != null && 
                            o.getRestaurant().getRestaurantId().equals(admin.getRestaurant().getRestaurantId()))
                    .collect(Collectors.toList());
                table.setItems(FXCollections.observableArrayList(orders));
                table.refresh();
            }
        }

        private static void advanceOrderStatus(Order order) {
            OrderStatus nextStatus = switch (order.getStatus()) {
                case PENDING -> OrderStatus.CONFIRMED;
                case CONFIRMED -> OrderStatus.PREPARING;
                case PREPARING -> OrderStatus.READY_FOR_DELIVERY;
                case READY_FOR_DELIVERY -> OrderStatus.OUT_FOR_DELIVERY;
                case OUT_FOR_DELIVERY -> OrderStatus.DELIVERED;
                default -> order.getStatus();
            };
            orderService.updateStatus(order, nextStatus);
        }
    }

    // ================== MENU CONTROLLER ==================
    public static class AdminMenuController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Manage Menu");

            HBox form = new HBox(10);
            form.setAlignment(Pos.CENTER_LEFT);
            TextField nameField = new TextField(); nameField.setPromptText("Name");
            TextField priceField = new TextField(); priceField.setPromptText("Price");
            TextField catField = new TextField(); catField.setPromptText("Category");
            Button addBtn = new Button("Add Item");
            addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");

            TableView<MenuItem> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            TableColumn<MenuItem, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

            TableColumn<MenuItem, String> priceCol = new TableColumn<>("Price");
            priceCol.setCellValueFactory(d -> new SimpleStringProperty("$" + d.getValue().getPrice()));

            TableColumn<MenuItem, String> catCol = new TableColumn<>("Category");
            catCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));

            TableColumn<MenuItem, Void> delCol = new TableColumn<>("Delete");
            delCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("X");
                {
                    btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                    btn.setOnAction(event -> {
                        MenuItem item = getTableView().getItems().get(getIndex());
                        if (admin.getRestaurant() != null) {
                            menuService.removeItem(Integer.parseInt(admin.getRestaurant().getRestaurantId()), item);
                            loadMenuData(table, admin); // REFRESH
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            });

            table.getColumns().addAll(nameCol, priceCol, catCol, delCol);
            loadMenuData(table, admin);

            addBtn.setOnAction(e -> {
                try {
                    String name = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    String category = catField.getText();
                    
                    if (name.isEmpty()) return;

                    MenuItem newItem = new MenuItem(null, name, "Desc", price, category);
                    if (admin.getRestaurant() != null) {
                        // Ensure we have a valid int ID for restaurant logic
                        int rId = Integer.parseInt(admin.getRestaurant().getRestaurantId());
                        menuService.addItem(rId, newItem);
                        
                        // Clear & Refresh
                        nameField.clear(); priceField.clear(); catField.clear();
                        loadMenuData(table, admin); 
                    }
                } catch (Exception ex) {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Invalid Input: " + ex.getMessage()); 
                    a.show();
                }
            });

            form.getChildren().addAll(nameField, priceField, catField, addBtn);
            VBox.setVgrow(table, Priority.ALWAYS);
            layout.getChildren().addAll(form, table);
            stage.getScene().setRoot(layout);
        }

        private static void loadMenuData(TableView<MenuItem> table, Admin admin) {
            if (admin.getRestaurant() != null) {
                // Must re-fetch the menu to see new items
                Menu menu = menuService.getMenuByRestaurant(Integer.parseInt(admin.getRestaurant().getRestaurantId()));
                if (menu != null && menu.getItems() != null) {
                    table.setItems(FXCollections.observableArrayList(menu.getItems()));
                    table.refresh();
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
            TextField nameField = new TextField(); nameField.setPromptText("Name");
            TextField roleField = new TextField(); roleField.setPromptText("Role");
            Button hireBtn = new Button("Hire");
            hireBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");

            TableView<Employee> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
            
            TableColumn<Employee, String> roleCol = new TableColumn<>("Role");
            roleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole()));

            TableColumn<Employee, Void> actionCol = new TableColumn<>("Action");
            actionCol.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Fire");
                {
                    btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                    btn.setOnAction(event -> {
                        Employee emp = getTableView().getItems().get(getIndex());
                        if (admin.getRestaurant() != null) {
                            restaurantService.fireEmployee(admin.getRestaurant(), emp);
                            loadEmployeeData(table, admin); // REFRESH
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            });

            table.getColumns().addAll(nameCol, roleCol, actionCol);
            loadEmployeeData(table, admin);

            hireBtn.setOnAction(e -> {
                String name = nameField.getText();
                String role = roleField.getText();
                if(name.isEmpty() || role.isEmpty()) return;

                Employee newEmp = new Employee();
                newEmp.setId("EMP" + System.currentTimeMillis());
                newEmp.setName(name);
                newEmp.setRole(role);
                newEmp.setAge(25); newEmp.setPhoneNumber("000"); 
                
                if (admin.getRestaurant() != null) {
                    restaurantService.hireEmployee(admin.getRestaurant(), newEmp);
                    loadEmployeeData(table, admin); // REFRESH
                    nameField.clear(); roleField.clear();
                }
            });

            form.getChildren().addAll(nameField, roleField, hireBtn);
            VBox.setVgrow(table, Priority.ALWAYS);
            layout.getChildren().addAll(form, table);
            stage.getScene().setRoot(layout);
        }

        private static void loadEmployeeData(TableView<Employee> table, Admin admin) {
            if (admin.getRestaurant() != null) {
                // Refresh restaurant object to get latest list from DB
                Restaurant r = restaurantService.getRestaurantById(Integer.parseInt(admin.getRestaurant().getRestaurantId()));
                // Update the admin's reference
                admin.setRestaurant(r);
                
                if(r != null && r.getEmployees() != null) {
                    table.setItems(FXCollections.observableArrayList(r.getEmployees()));
                    table.refresh();
                }
            }
        }
    }

    // ================== REVIEWS CONTROLLER ==================
    public static class AdminReviewsController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Restaurant Reviews");
            
            ListView<String> list = new ListView<>();
            VBox.setVgrow(list, Priority.ALWAYS);

            if (admin.getRestaurant() != null) {
                Restaurant r = restaurantService.getRestaurantById(Integer.parseInt(admin.getRestaurant().getRestaurantId()));
                if (r != null && r.getReviews() != null) {
                    for (Review review : r.getReviews()) {
                        list.getItems().add(
                            "⭐ " + review.getRating() + " | " + 
                            (review.getUser() != null ? review.getUser().getName() : "Anonymous") + 
                            ": " + review.getComment()
                        );
                    }
                }
            }
            if(list.getItems().isEmpty()) list.getItems().add("No reviews yet.");
            
            layout.getChildren().add(list);
            stage.getScene().setRoot(layout);
        }
    }

    // ================== USERS CONTROLLER ==================
    public static class AdminUsersController {
        public static void show(Stage stage, Admin admin) {
            VBox layout = createBaseLayout(stage, admin, "Users Information");
            Label label = new Label("User management is handled via Orders section.");
            label.setFont(Font.font("System", 16));
            label.setTextFill(Color.BLACK);
            layout.getChildren().add(label);
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
}