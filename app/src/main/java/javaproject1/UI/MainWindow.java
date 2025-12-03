// package javaproject1.UI;

// import javaproject1.BLL.Service.implementation.UserServiceImpl;
// import javaproject1.DAL.Entity.User;

// import javax.swing.*;
// import java.awt.*;

// /**
//  * Simple GUI for Plato Restaurant Order System.
//  * Uses BLL services (e.g. UserServiceImpl) and DAL below them.
//  */
// public class MainWindow extends JFrame {

//     private final UserServiceImpl userService;

//     private JTextField emailField;
//     private JPasswordField passwordField;
//     private JTextArea outputArea;

//     public MainWindow() {
//         this.userService = new UserServiceImpl();
//         initUI();
//     }

//     private void initUI() {
//         setTitle("Plato - Restaurant Order System");
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setSize(600, 400);
//         setLocationRelativeTo(null); // center on screen

//         // Base colors: soft background + accent buttons
//         Color bg = new Color(245, 247, 250);
//         Color accent = new Color(33, 150, 243);

//         JPanel root = new JPanel(new BorderLayout(10, 10));
//         root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//         root.setBackground(bg);
//         setContentPane(root);

//         // Header
//         JLabel title = new JLabel("Plato Restaurant Order System", SwingConstants.CENTER);
//         title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
//         title.setForeground(new Color(45, 52, 54));
//         root.add(title, BorderLayout.NORTH);

//         // Center: simple login panel
//         JPanel center = new JPanel();
//         center.setBackground(bg);
//         center.setLayout(new GridBagLayout());
//         root.add(center, BorderLayout.CENTER);

//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(5, 5, 5, 5);
//         gbc.fill = GridBagConstraints.HORIZONTAL;

//         JLabel emailLabel = new JLabel("Email:");
//         emailField = new JTextField(25);
//         JLabel passLabel = new JLabel("Password:");
//         passwordField = new JPasswordField(25);

//         JButton loginButton = new JButton("Login");
//         loginButton.setBackground(accent);
//         loginButton.setForeground(Color.WHITE);
//         loginButton.setFocusPainted(false);

//         // Layout fields
//         gbc.gridx = 0; gbc.gridy = 0;
//         center.add(emailLabel, gbc);
//         gbc.gridx = 1; gbc.gridy = 0;
//         center.add(emailField, gbc);

//         gbc.gridx = 0; gbc.gridy = 1;
//         center.add(passLabel, gbc);
//         gbc.gridx = 1; gbc.gridy = 1;
//         center.add(passwordField, gbc);

//         gbc.gridx = 1; gbc.gridy = 2;
//         gbc.anchor = GridBagConstraints.EAST;
//         center.add(loginButton, gbc);

//         // Output area
//         outputArea = new JTextArea(6, 40);
//         outputArea.setEditable(false);
//         outputArea.setLineWrap(true);
//         outputArea.setWrapStyleWord(true);
//         JScrollPane scroll = new JScrollPane(outputArea);
//         root.add(scroll, BorderLayout.SOUTH);

//         // Events
//         loginButton.addActionListener(e -> handleLogin());
//     }

//     private void handleLogin() {
//         String email = emailField.getText().trim();
//         String password = new String(passwordField.getPassword());

//         if (email.isEmpty() || password.isEmpty()) {
//             JOptionPane.showMessageDialog(this,
//                     "Please enter email and password.",
//                     "Validation",
//                     JOptionPane.WARNING_MESSAGE);
//             return;
//         }

//         try {
//             boolean ok = userService.login(email, password);
//             if (ok) {
//                 User user = userService.getUserByEmail(email);
//                 appendOutput("Login successful. Welcome, " + (user != null ? user.getName() : email) + "!");
//             } else {
//                 appendOutput("Login failed. Please check your email or password.");
//             }
//         } catch (Exception ex) {
//             appendOutput("Error during login: " + ex.getMessage());
//         }
//     }

//     private void appendOutput(String text) {
//         if (outputArea.getText().isEmpty()) {
//             outputArea.setText(text);
//         } else {
//             outputArea.append("\n" + text);
//         }
//     }
// }


