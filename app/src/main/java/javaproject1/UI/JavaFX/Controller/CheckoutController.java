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
import javafx.scene.control.Alert;

public class CheckoutController {
    public static void show(Stage stage, User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, 
            "Checkout feature coming soon!");
        alert.showAndWait();
    }
}