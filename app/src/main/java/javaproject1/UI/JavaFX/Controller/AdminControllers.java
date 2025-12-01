package javaproject1.UI.JavaFX.Controller;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javaproject1.DAL.Entity.Admin;

public class AdminControllers {
    public static class AdminOrdersController {
        public static void show(Stage stage, Admin admin) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Admin Orders Management - Coming Soon");
            alert.showAndWait();
        }
    }
    
    public static class AdminMenuController {
        public static void show(Stage stage, Admin admin) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Admin Menu Management - Coming Soon");
            alert.showAndWait();
        }
    }
    
    public static class AdminEmployeesController {
        public static void show(Stage stage, Admin admin) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Admin Employees Management - Coming Soon");
            alert.showAndWait();
        }
    }
    
    public static class AdminUsersController {
        public static void show(Stage stage, Admin admin) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Admin Users Management - Coming Soon");
            alert.showAndWait();
        }
    }
    
    public static class AdminReviewsController {
        public static void show(Stage stage, Admin admin) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Admin Reviews Management - Coming Soon");
            alert.showAndWait();
        }
    }
}
