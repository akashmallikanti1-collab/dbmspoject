package dealer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import login.LoginScreen;
import util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;

public class DealerDashboard {

    public void show(Stage stage) {

        Label title = new Label("DEALER DASHBOARD");
        title.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:white;");

        Label user = new Label("Hyderabad Retail Center — Performance Summary");
        user.setStyle("-fx-text-fill:#cbd5e1; -fx-font-size:12px;");

        int totalCars = 0;
        int totalBookings = 0;
        int pendingTests = 0;

        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM car_data");
            if (rs.next()) totalCars = rs.getInt(1);

            rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM booking_data");
            if (rs.next()) totalBookings = rs.getInt(1);

            rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM testdrive_data WHERE status='pending'");
            if (rs.next()) pendingTests = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HBox stats = new HBox(12,
            statBox("Inventory", String.valueOf(totalCars), "#4CAF50"),
            statBox("Bookings", String.valueOf(totalBookings), "#2196F3"),
            statBox("Pending Tests", String.valueOf(pendingTests), "#FF9800")
        );
        stats.setAlignment(Pos.CENTER);

        Button inventory = new Button("Inventory");
        Button sales     = new Button("Brand-wise Sales");
        Button profit    = new Button("Brand-wise Profit");
        Button testdrive = new Button("Test Drive Requests");
        Button logout    = new Button("Logout");

        styleButton(inventory, "#2196F3");
        styleButton(sales,     "#4CAF50");
        styleButton(profit,    "#f44336");
        styleButton(testdrive, "#FF9800");
        styleButton(logout,    "#9E9E9E");

        inventory.setOnAction(e -> new InventoryScreen().show(stage));
        sales.setOnAction(e     -> new DealerSalesScreen().show(stage));
        profit.setOnAction(e    -> new DealerProfitScreen().show(stage));
        testdrive.setOnAction(e -> new TestDriveManageScreen().show(stage));
        logout.setOnAction(e    -> new LoginScreen().show(stage));

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        grid.add(inventory, 0, 0);
        grid.add(sales, 1, 0);
        grid.add(profit, 0, 1);
        grid.add(testdrive, 1, 1);
        grid.setAlignment(Pos.CENTER);

        VBox root = new VBox(18, title, user, stats, grid, logout);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color:#0f172a; -fx-background-radius:18; -fx-border-radius:18;");

        stage.setScene(new Scene(root, 520, 480));
        stage.setTitle("Dealer Dashboard");
    }

    private VBox statBox(String label, String value, String color) {
        Label title = new Label(value);
        title.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:" + color + ";");
        Label subtitle = new Label(label);
        subtitle.setStyle("-fx-text-fill:#cbd5e1; -fx-font-size:11px;");
        VBox box = new VBox(6, title, subtitle);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color:#111827; -fx-background-radius:16; -fx-border-color:#334155; -fx-border-radius:16;");
        box.setPrefWidth(150);
        return box;
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:18;" +
            "-fx-font-weight:bold;" +
            "-fx-padding:12 18;"
        );
        btn.setPrefWidth(220);
    }
}
