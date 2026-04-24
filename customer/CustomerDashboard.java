package customer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import login.LoginScreen;

public class CustomerDashboard {

    public void show(Stage stage) {

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Main action buttons with clean design
        Button browseBtn = createButton("Browse Cars", "#667eea");
        Button searchBtn = createButton("Search & Filter", "#764ba2");
        Button compareBtn = createButton("Compare Cars", "#f093fb");
        Button cartBtn = createButton("My Cart", "#f5576c");
        Button bookBtn = createButton("Book Car", "#4facfe");
        Button historyBtn = createButton("Booking History", "#00f2fe");
        Button reviewBtn = createButton("Write Review", "#43e97b");
        Button testBtn = createButton("Test Drive", "#38f9d7");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff6b6b; -fx-font-size: 12px;");

        // Grid layout for buttons
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.add(browseBtn, 0, 0);
        grid.add(searchBtn, 1, 0);
        grid.add(compareBtn, 0, 1);
        grid.add(cartBtn, 1, 1);
        grid.add(bookBtn, 0, 2);
        grid.add(historyBtn, 1, 2);
        grid.add(reviewBtn, 0, 3);
        grid.add(testBtn, 1, 3);

        // Event handlers
        browseBtn.setOnAction(e -> new CarListScreen().show(stage));
        searchBtn.setOnAction(e -> new FilterScreen().show(stage));
        compareBtn.setOnAction(e -> new CompareScreen().show(stage));
        cartBtn.setOnAction(e -> new CartScreen().show(stage));
        bookBtn.setOnAction(e -> new BookingScreen().show(stage));
        historyBtn.setOnAction(e -> new BookingHistoryScreen().show(stage));
        reviewBtn.setOnAction(e -> new ReviewScreen().show(stage));
        testBtn.setOnAction(e -> new TestDriveScreen().show(stage));
        logoutBtn.setOnAction(e -> new LoginScreen().show(stage));

        VBox root = new VBox(30, title, grid, logoutBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Customer Dashboard");
        stage.show();
    }

    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; " +
                     "-fx-background-radius: 12; -fx-padding: 15 25; -fx-font-size: 14px; " +
                     "-fx-pref-width: 150; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);");
        return btn;
    }
}
