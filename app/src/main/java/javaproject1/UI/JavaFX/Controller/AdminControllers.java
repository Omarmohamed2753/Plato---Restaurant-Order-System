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
    private static EmployeeServiceImpl employeeService = new EmployeeServiceImpl();

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
            
            // TableColumn<Employee, String> idCol = new TableColumn<>("ID");
            // idCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getId()));
            
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
                Restaurant r = restaurantService.getRestaurantById(Integer.parseInt(admin.getRestaurant().getRestaurantId()));
                if (r != null && r.getReviews() != null && !r.getReviews().isEmpty()) {
                    table.setItems(FXCollections.observableArrayList(r.getReviews()));
                } else {
                    Label noReviews = new Label("No reviews yet.");
                    noReviews.setFont(Font.font("System", 16));
                    noReviews.setTextFill(Color.web("#636e72"));
                    layout.getChildren().add(noReviews);
                }
            }
            
            VBox.setVgrow(table, Priority.ALWAYS);
            if (!table.getItems().isEmpty()) {
                layout.getChildren().add(table);
            }
            stage.getScene().setRoot(layout);
        }
    }

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