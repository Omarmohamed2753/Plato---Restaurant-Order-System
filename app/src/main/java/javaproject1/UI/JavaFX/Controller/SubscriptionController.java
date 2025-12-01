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
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javaproject1.BLL.Service.implementation.*;
import javaproject1.DAL.Entity.*;
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