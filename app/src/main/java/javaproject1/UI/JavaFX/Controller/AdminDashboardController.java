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

public class AdminDashboardController {

    private static AdminServiceImpl adminService = new AdminServiceImpl();
    private static OrderServiceImpl orderService = new OrderServiceImpl();
    private static UserServiceImpl userService = new UserServiceImpl();
    private static EmployeeServiceImpl employeeService = new EmployeeServiceImpl();

    // Dark Theme Colors - matching client side
    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String CARD_BACKGROUND = "#374151";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String HOVER_COLOR = "#4b5563";
    private static final String ERROR_COLOR = "#ef4444";

    public static void show(Stage stage, Admin admin) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        HBox navbar = createAdminNavBar(stage, admin);
        root.setTop(navbar);

        VBox sidebar = createSidebar(stage, admin);
        root.setLeft(sidebar);

        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(40));

        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 38));
        titleLabel.setTextFill(Color.web(ACCENT_GOLD));

        Label restaurantLabel = new Label("🏪 Restaurant: " + 
            (admin.getRestaurant() != null ? admin.getRestaurant().getName() : "N/A"));
        restaurantLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        restaurantLabel.setTextFill(Color.web(TEXT_COLOR_LIGHT));

        HBox statsBox = createStatsCards(admin);
        VBox ordersSection = createOrdersSection(admin);

        contentBox.getChildren().addAll(titleLabel, restaurantLabel, statsBox, ordersSection);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 750);
        stage.setScene(scene);
    }

    private static HBox createAdminNavBar(Stage stage, Admin admin) {
        HBox navbar = new HBox(30);
        navbar.setPadding(new Insets(18, 50, 18, 50));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        Label brandLabel = new Label("🔐 Admin Portal");
        brandLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 24));
        brandLabel.setTextFill(Color.web(ACCENT_GOLD));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label adminNameLabel = new Label("Admin: " + admin.getName());
        adminNameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
        adminNameLabel.setTextFill(Color.web(TEXT_COLOR_LIGHT));

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
            "-fx-background-color: " + ERROR_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand;"
        );
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(logoutButton.getStyle() + "-fx-background-color: #dc2626;"));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(logoutButton.getStyle().replace("-fx-background-color: #dc2626;", "-fx-background-color: " + ERROR_COLOR + ";")));
        
        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Logout from admin panel?");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
            dialogPane.lookup(".label.content").setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-font-size: 14px;");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    WelcomeController.show(stage);
                }
            });
        });

        navbar.getChildren().addAll(brandLabel, spacer, adminNameLabel, logoutButton);
        return navbar;
    }

    private static VBox createSidebar(Stage stage, Admin admin) {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 0);");

        Label menuLabel = new Label("MENU");
        menuLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 13));
        menuLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
        menuLabel.setPadding(new Insets(0, 0, 10, 5));

        Button dashboardBtn = createSidebarButton("📊 Dashboard", true);
        dashboardBtn.setOnAction(e -> show(stage, admin));

        Button ordersBtn = createSidebarButton("📦 Orders", false);
        ordersBtn.setOnAction(e -> AdminControllers.AdminOrdersController.show(stage, admin));

        Button menuBtn = createSidebarButton("🍽️ Menu", false);
        menuBtn.setOnAction(e -> AdminControllers.AdminMenuController.show(stage, admin));

        Button employeesBtn = createSidebarButton("👥 Employees", false);
        employeesBtn.setOnAction(e -> AdminControllers.AdminEmployeesController.show(stage, admin));

        Button usersBtn = createSidebarButton("👤 Users", false);
        usersBtn.setOnAction(e -> AdminControllers.AdminUsersController.show(stage, admin));

        Button reviewsBtn = createSidebarButton("⭐ Reviews", false);
        reviewsBtn.setOnAction(e -> AdminControllers.AdminReviewsController.show(stage, admin));

        sidebar.getChildren().addAll(
            menuLabel,
            dashboardBtn,
            ordersBtn,
            menuBtn,
            employeesBtn,
            usersBtn,
            reviewsBtn
        );

        return sidebar;
    }

    private static Button createSidebarButton(String text, boolean active) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
        
        if (active) {
            button.setStyle(
                "-fx-background-color: " + PRIMARY_COLOR + "; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 12 15; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-font-weight: bold;"
            );
        } else {
            button.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                "-fx-padding: 12 15; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            );
            button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + HOVER_COLOR + "; " +
                "-fx-text-fill: " + ACCENT_GOLD + "; " +
                "-fx-padding: 12 15; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            ));
            button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                "-fx-padding: 12 15; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            ));
        }
        
        return button;
    }

    private static HBox createStatsCards(Admin admin) {
        HBox statsBox = new HBox(25);
        statsBox.setAlignment(Pos.CENTER);
    
        if (admin.getRestaurant() == null || admin.getRestaurant().getRestaurantId() == null) {
            VBox ordersCard = createStatCard("📦", "Total Orders", "0", PRIMARY_COLOR);
            VBox usersCard = createStatCard("👥", "Total Users", "0", "#9b59b6");
            VBox employeesCard = createStatCard("👔", "Employees", "0", "#e67e22");
            VBox pendingCard = createStatCard("⏳", "Pending", "0", ERROR_COLOR);
    
            statsBox.getChildren().addAll(ordersCard, usersCard, employeesCard, pendingCard);
            return statsBox;
        }
    
        String adminRestId = admin.getRestaurant().getRestaurantId();
        List<Order> allOrders = orderService.getAllOrders();
    
        int totalOrders = (int) allOrders.stream()
                .filter(o -> o.getRestaurant() != null &&
                             o.getRestaurant().getRestaurantId() != null &&
                             o.getRestaurant().getRestaurantId().equals(adminRestId))
                .count();
    
        VBox ordersCard = createStatCard("📦", "Total Orders", String.valueOf(totalOrders), PRIMARY_COLOR);
        
        int totalUsers = userService.getAllUsers().size();
        VBox usersCard = createStatCard("👥", "Total Users", String.valueOf(totalUsers), "#9b59b6");
    
        int totalEmployees = (int) employeeService.getAllEmployees().stream()
                
                .count();
    
        VBox employeesCard = createStatCard("👔", "Employees", String.valueOf(totalEmployees), "#e67e22");
    
        int pendingOrders = (int) allOrders.stream()
                .filter(o -> o.getRestaurant() != null &&
                             o.getRestaurant().getRestaurantId() != null &&
                             o.getRestaurant().getRestaurantId().equals(adminRestId) &&
                             (o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.CONFIRMED))
                .count();
    
        VBox pendingCard = createStatCard("⏳", "Pending", String.valueOf(pendingOrders), ERROR_COLOR);
    
        statsBox.getChildren().addAll(ordersCard, usersCard, employeesCard, pendingCard);
        return statsBox;
    }

    private static VBox createStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(220);
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-background-radius: 18; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 42));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
        titleLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 32));
        valueLabel.setTextFill(Color.web(color));

        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return card;
    }

    private static VBox createOrdersSection(Admin admin) {
        VBox section = new VBox(20);
        section.setPadding(new Insets(30));
        section.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-background-radius: 18; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );
    
        Label titleLabel = new Label("Recent Orders");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(ACCENT_GOLD));
    
        TableView<Order> table = new TableView<>();
        table.setPrefHeight(350);
        table.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-control-inner-background: #2b3543; " +
            "-fx-table-cell-border-color: #4b5563;"
        );
    
        TableColumn<Order, String> idCol = new TableColumn<>("Order ID");
        idCol.setPrefWidth(120);
        idCol.setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + ";");
        idCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getOrderId()));
    
        TableColumn<Order, String> userCol = new TableColumn<>("User");
        userCol.setPrefWidth(180);
        userCol.setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + ";");
        userCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getUser() != null
                                ? data.getValue().getUser().getName()
                                : "N/A"
                ));
    
        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setPrefWidth(120);
        totalCol.setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + ";");
        totalCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        "$" + String.format("%.2f", data.getValue().getTotalAmount())
                ));
    
        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(180);
        statusCol.setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + ";");
        statusCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatus().toString()
                ));
    
        table.getColumns().addAll(idCol, userCol, totalCol, statusCol);
    
        if (admin.getRestaurant() == null || admin.getRestaurant().getRestaurantId() == null) {
            Label noRestaurantLabel = new Label("No restaurant assigned to this admin.");
            noRestaurantLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noRestaurantLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
    
            section.getChildren().addAll(titleLabel, noRestaurantLabel, table);
            return section;
        }
    
        String adminRestId = admin.getRestaurant().getRestaurantId();
    
        List<Order> recentOrders = orderService.getAllOrders().stream()
                .filter(o ->
                        o.getRestaurant() != null &&
                        o.getRestaurant().getRestaurantId() != null &&
                        o.getRestaurant().getRestaurantId().equals(adminRestId)
                )
                .limit(10)
                .toList();
    
        table.getItems().addAll(recentOrders);
    
        section.getChildren().addAll(titleLabel, table);
        return section;
    }
}