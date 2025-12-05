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
import javaproject1.DAL.Repo.Implementation.RestaurantRepoImpl;
import javaproject1.DAL.Repo.Implementation.ReviewRepoImpl;

import java.util.List;

public class ReviewController {
    private static ReviewServiceImpl reviewService = new ReviewServiceImpl();
    private static RestaurantRepoImpl restaurantRepo = new RestaurantRepoImpl();
    private static ReviewRepoImpl reviewRepo = new ReviewRepoImpl();

    // Dark Theme Colors
    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String CARD_BACKGROUND = "#374151";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String ITEM_BACKGROUND = "#2b3543";
    private static final String ERROR_COLOR = "#ef4444";
    private static final String SUCCESS_COLOR = "#10b981";
    
    private static final String BUTTON_STYLE_BASE = 
        "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 20; -fx-cursor: hand; -fx-font-size: 15px;";

    public static void show(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(50));
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setMaxWidth(1100);

        Label titleLabel = new Label("⭐ My Reviews");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        titleLabel.setTextFill(Color.web(ACCENT_GOLD));

        // Reviews section
        VBox reviewsSection = createReviewsSection(stage, user);
        
        // Add new review section
        VBox addReviewSection = createAddReviewSection(stage, user);

        contentBox.getChildren().addAll(titleLabel, reviewsSection, addReviewSection);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);
        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
    }

    private static VBox createReviewsSection(Stage stage, User user) {
        VBox section = new VBox(20);
        section.setAlignment(Pos.TOP_CENTER);

        Label sectionTitle = new Label("Your Reviews");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        sectionTitle.setTextFill(Color.web(TEXT_COLOR_LIGHT));

        VBox reviewsList = new VBox(15);
        reviewsList.setAlignment(Pos.TOP_CENTER);

        // Get all reviews and filter for current user
        List<Review> allReviews = reviewService.getAllReviews();
        List<Review> userReviews = allReviews.stream()
            .filter(r -> r.getUser() != null && 
                        r.getUser().getId() != null && 
                        r.getUser().getId().equals(user.getId()))
            .toList();

        System.out.println("DEBUG ReviewController: Found " + userReviews.size() + " reviews for user " + user.getName());

        if (!userReviews.isEmpty()) {
            for (Review review : userReviews) {
                reviewsList.getChildren().add(createReviewCard(stage, user, review));
            }
        } else {
            Label emptyLabel = new Label("You haven't written any reviews yet.\nShare your dining experience!");
            emptyLabel.setFont(Font.font("System", 18));
            emptyLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
            emptyLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            emptyLabel.setWrapText(true);
            reviewsList.getChildren().add(emptyLabel);
        }

        section.getChildren().addAll(sectionTitle, reviewsList);
        return section;
    }

    private static HBox createReviewCard(Stage stage, User user, Review review) {
        HBox card = new HBox(30);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(900);
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);"
        );

        // Restaurant icon/info
        VBox iconBox = new VBox(5);
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setPrefWidth(120);

        Label restaurantIcon = new Label("🏪");
        restaurantIcon.setFont(Font.font("System", 50));

        // Get fresh restaurant data from database
        String restaurantName = "Plato Restaurant";
        if (review.getRestaurant() != null) {
            String restaurantId = review.getRestaurant().getRestaurantId();
            if (restaurantId != null) {
                try {
                    Restaurant freshRestaurant = restaurantRepo.getRestaurantById(Integer.parseInt(restaurantId));
                    if (freshRestaurant != null && freshRestaurant.getName() != null) {
                        restaurantName = freshRestaurant.getName();
                    }
                } catch (Exception e) {
                    System.err.println("Error loading restaurant: " + e.getMessage());
                }
            }
        }
        
        Label restaurantLabel = new Label(restaurantName);
        restaurantLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        restaurantLabel.setTextFill(Color.web(TEXT_COLOR_LIGHT));
        restaurantLabel.setWrapText(true);
        restaurantLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        restaurantLabel.setMaxWidth(120);

        iconBox.getChildren().addAll(restaurantIcon, restaurantLabel);

        // Review content
        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(contentBox, Priority.ALWAYS);
        contentBox.setPrefWidth(500);

        // Rating display
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < review.getRating() ? "⭐" : "☆");
            star.setFont(Font.font("System", 20));
            star.setTextFill(Color.web(i < review.getRating() ? ACCENT_GOLD : TEXT_COLOR_SECONDARY));
            ratingBox.getChildren().add(star);
        }
        Label ratingText = new Label(" " + review.getRating() + "/5");
        ratingText.setFont(Font.font("System", FontWeight.BOLD, 18));
        ratingText.setTextFill(Color.web(ACCENT_GOLD));
        ratingBox.getChildren().add(ratingText);

        // Comment
        Label commentLabel = new Label(review.getComment() != null ? review.getComment() : "No comment provided");
        commentLabel.setFont(Font.font("System", 16));
        commentLabel.setTextFill(Color.web(TEXT_COLOR_LIGHT));
        commentLabel.setWrapText(true);
        commentLabel.setMaxWidth(500);

        contentBox.getChildren().addAll(ratingBox, commentLabel);

        // Action buttons
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPrefWidth(140);

        Button editButton = new Button("Edit");
        editButton.setPrefWidth(120);
        editButton.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            BUTTON_STYLE_BASE.replace("15px", "14px")
        );
        editButton.setOnMouseEntered(e -> editButton.setStyle(editButton.getStyle() + "-fx-background-color: #047857;"));
        editButton.setOnMouseExited(e -> editButton.setStyle(editButton.getStyle().replace("-fx-background-color: #047857;", "-fx-background-color: " + PRIMARY_COLOR + ";")));

        editButton.setOnAction(e -> showEditReviewDialog(stage, user, review));

        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(120);
        deleteButton.setStyle(
            "-fx-background-color: " + ERROR_COLOR + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            BUTTON_STYLE_BASE.replace("15px", "14px")
        );
        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(deleteButton.getStyle() + "-fx-background-color: #dc2626;"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle(deleteButton.getStyle().replace("-fx-background-color: #dc2626;", "-fx-background-color: " + ERROR_COLOR + ";")));

        deleteButton.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete this review?");
            DialogPane dialogPane = confirmAlert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
            dialogPane.lookup(".label.content").setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-font-size: 14px;");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        reviewService.deleteReview(review, review.getRestaurant());
                        
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Review deleted successfully!");
                        DialogPane successPane = successAlert.getDialogPane();
                        successPane.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
                        successPane.lookup(".label.content").setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-font-size: 14px;");
                        successAlert.showAndWait();
                        
                        show(stage, user); // Refresh
                    } catch (Exception ex) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR, 
                            "Error deleting review: " + ex.getMessage());
                        errorAlert.showAndWait();
                        ex.printStackTrace();
                    }
                }
            });
        });

        actionBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(iconBox, contentBox, actionBox);
        return card;
    }

    private static void showEditReviewDialog(Stage stage, User user, Review review) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Review");
        
        // Get fresh restaurant name
        String restaurantName = "Plato Restaurant";
        if (review.getRestaurant() != null && review.getRestaurant().getRestaurantId() != null) {
            try {
                Restaurant freshRestaurant = restaurantRepo.getRestaurantById(
                    Integer.parseInt(review.getRestaurant().getRestaurantId())
                );
                if (freshRestaurant != null && freshRestaurant.getName() != null) {
                    restaurantName = freshRestaurant.getName();
                }
            } catch (Exception e) {
                System.err.println("Error loading restaurant: " + e.getMessage());
            }
        }
        
        dialog.setHeaderText("Update your review for " + restaurantName);
        dialog.getDialogPane().setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        Label ratingLabel = createDialogLabel("Rating (1-5):");
        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, review.getRating());
        ratingSpinner.setPrefWidth(100);
        ratingSpinner.getEditor().setStyle(
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-background-color: " + ITEM_BACKGROUND + "; " +
            "-fx-font-size: 16px;"
        );

        Label commentLabel = createDialogLabel("Comment:");
        TextArea commentArea = new TextArea(review.getComment());
        commentArea.setPrefRowCount(5);
        commentArea.setPrefColumnCount(40);
        commentArea.setWrapText(true);
        commentArea.setStyle(
            "-fx-control-inner-background: " + ITEM_BACKGROUND + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-font-size: 14px; " +
            "-fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + ";"
        );

        grid.add(ratingLabel, 0, 0);
        grid.add(ratingSpinner, 1, 0);
        grid.add(commentLabel, 0, 1);
        grid.add(commentArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButtonType) {
                try {
                    int newRating = ratingSpinner.getValue();
                    String newComment = commentArea.getText().trim();

                    if (newComment.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Please provide a comment!");
                        alert.showAndWait();
                        return;
                    }

                    reviewService.editReview(review, newRating, newComment);

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Review updated successfully!");
                    DialogPane successPane = successAlert.getDialogPane();
                    successPane.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
                    successPane.lookup(".label.content").setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-font-size: 14px;");
                    successAlert.showAndWait();

                    show(stage, user); // Refresh
                } catch (Exception ex) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, 
                        "Error updating review: " + ex.getMessage());
                    errorAlert.showAndWait();
                    ex.printStackTrace();
                }
            }
        });
    }

    private static VBox createAddReviewSection(Stage stage, User user) {
        VBox section = new VBox(25);
        section.setPadding(new Insets(30, 0, 0, 0));
        section.setAlignment(Pos.TOP_CENTER);
        section.setMaxWidth(900);
        section.setStyle(
            "-fx-border-color: " + PRIMARY_COLOR + "; " +
            "-fx-border-width: 2 0 0 0; " +
            "-fx-padding: 30 0 0 0;"
        );

        Label sectionTitle = new Label("Write a New Review");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        sectionTitle.setTextFill(Color.web(ACCENT_GOLD));

        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(25));
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);"
        );

        // Get the first (and only) restaurant
        List<Restaurant> restaurants = restaurantRepo.getAllRestaurants();
        if (restaurants.isEmpty()) {
            Label noRestaurant = new Label("No restaurant available for reviews.");
            noRestaurant.setFont(Font.font("System", 18));
            noRestaurant.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
            section.getChildren().addAll(sectionTitle, noRestaurant);
            return section;
        }
        
        Restaurant restaurant = restaurants.get(0); // Get the single restaurant
        
        // Display restaurant name
        Label restaurantInfoLabel = new Label("📍 Restaurant: " + restaurant.getName());
        restaurantInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        restaurantInfoLabel.setTextFill(Color.web(ACCENT_GOLD));

        // Rating selection
        Label ratingLabel = createFormLabel("Rating:");
        HBox ratingBox = new HBox(15);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        
        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 5);
        ratingSpinner.setPrefWidth(100);
        ratingSpinner.getEditor().setStyle(
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-background-color: " + ITEM_BACKGROUND + "; " +
            "-fx-font-size: 18px;"
        );
        
        Label ratingDisplay = new Label("⭐ 5/5");
        ratingDisplay.setFont(Font.font("System", FontWeight.BOLD, 20));
        ratingDisplay.setTextFill(Color.web(ACCENT_GOLD));
        
        ratingSpinner.valueProperty().addListener((obs, old, newVal) -> {
            String stars = "⭐".repeat(newVal);
            ratingDisplay.setText(stars + " " + newVal + "/5");
        });
        
        ratingBox.getChildren().addAll(ratingSpinner, ratingDisplay);

        // Comment
        Label commentLabel = createFormLabel("Your Review:");
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Share your dining experience...");
        commentArea.setPrefRowCount(6);
        commentArea.setPrefColumnCount(60);
        commentArea.setWrapText(true);
        commentArea.setStyle(
            "-fx-control-inner-background: " + ITEM_BACKGROUND + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            "-fx-font-size: 15px; " +
            "-fx-prompt-text-fill: " + TEXT_COLOR_SECONDARY + ";"
        );

        // Submit button
        Button submitButton = new Button("Submit Review ✓");
        submitButton.setPrefWidth(200);
        submitButton.setStyle(
            "-fx-background-color: " + SUCCESS_COLOR + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            BUTTON_STYLE_BASE
        );
        submitButton.setOnMouseEntered(e -> submitButton.setStyle(submitButton.getStyle() + "-fx-background-color: #059669;"));
        submitButton.setOnMouseExited(e -> submitButton.setStyle(submitButton.getStyle().replace("-fx-background-color: #059669;", "-fx-background-color: " + SUCCESS_COLOR + ";")));

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 16));

        submitButton.setOnAction(e -> {
            try {
                int rating = ratingSpinner.getValue();
                String comment = commentArea.getText().trim();

                if (comment.isEmpty()) {
                    showErrorMessage(messageLabel, "Please write a review comment!");
                    return;
                }

                // Create new review
                Review newReview = new Review();
                newReview.setReviewId("REV" + System.currentTimeMillis());
                newReview.setUser(user);
                newReview.setRestaurant(restaurant);
                newReview.setRating(rating);
                newReview.setComment(comment);

                reviewService.submitReview(newReview, restaurant);

                showSuccessMessage(messageLabel, "Review submitted successfully!");

                // Clear form
                ratingSpinner.getValueFactory().setValue(5);
                commentArea.clear();

                // Refresh after delay
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(() -> show(stage, user));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();

            } catch (Exception ex) {
                showErrorMessage(messageLabel, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.getChildren().addAll(submitButton, messageLabel);

        formBox.getChildren().addAll(
            restaurantInfoLabel,
            ratingLabel, ratingBox,
            commentLabel, commentArea,
            buttonBox
        );

        section.getChildren().addAll(sectionTitle, formBox);
        return section;
    }

    private static Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setTextFill(Color.web(TEXT_COLOR_LIGHT));
        return label;
    }

    private static Label createDialogLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.web(TEXT_COLOR_LIGHT));
        return label;
    }

    private static void showErrorMessage(Label label, String message) {
        label.setText("❌ " + message);
        label.setTextFill(Color.web(ERROR_COLOR));
    }

    private static void showSuccessMessage(Label label, String message) {
        label.setText("✓ " + message);
        label.setTextFill(Color.web(SUCCESS_COLOR));
    }
}