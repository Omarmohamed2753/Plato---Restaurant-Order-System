package javaproject1.UI.JavaFX.Controller;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javaproject1.DAL.Entity.User;

public class SubscriptionController {
    public static void show(Stage stage, User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, 
            "Elite Subscription:\n\n" +
            "• 10% discount on all orders\n" +
            "• Priority delivery\n" +
            "• Exclusive offers\n\n" +
            "Cost: $100/month");
        alert.setTitle("Elite Subscription");
        alert.showAndWait();
    }
}