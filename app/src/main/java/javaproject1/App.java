package javaproject1;

import javax.swing.SwingUtilities;

import javaproject1.UI.MainWindow;

// import javaproject1.DAL.Enums.PaymentM;
// import javaproject1.DAL.Entity.*;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.List;
// import java.util.concurrent.TimeUnit;
public class App {
    public static void main(String[] args) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });

    
}
}
