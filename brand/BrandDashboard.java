package brand;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import login.LoginScreen;
import util.DBConnection;
import util.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BrandDashboard {

    public void show(Stage stage) {

        Label title = new Label("BRAND DASHBOARD");
        title.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:white;");

        int brandId = Session.currentUserId - 2;
        String brandName = "Brand";
        int modelCount = 0;
        int bookingCount = 0;
        long revenue = 0;

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement brandStmt = con.prepareStatement(
                "SELECT brand_name FROM brand_data WHERE brand_id = ?"
            );
            brandStmt.setInt(1, brandId);
            ResultSet brandRs = brandStmt.executeQuery();
            if (brandRs.next()) {
                brandName = brandRs.getString(1);
            }

            PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(DISTINCT c.car_id) AS count_models, " +
                "COUNT(bk.booking_id) AS bookings, " +
                "NVL(SUM(c.price_amount),0) AS revenue " +
                "FROM car_data c " +
                "LEFT JOIN booking_data bk ON c.car_id=bk.car_id " +
                "WHERE c.brand_id = ?"
            );
            ps.setInt(1, brandId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                modelCount = rs.getInt("count_models");
                bookingCount = rs.getInt("bookings");
                revenue = rs.getLong("revenue");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Label brandInfo = new Label(brandName + " — " + modelCount + " models listed");
        brandInfo.setStyle("-fx-text-fill:#cbd5e1; -fx-font-size:12px;");

        HBox stats = new HBox(12,
            statBox("Models", String.valueOf(modelCount), "#4CAF50"),
            statBox("Bookings", String.valueOf(bookingCount), "#2196F3"),
            statBox("Revenue", "₹" + revenue, "#FFB300")
        );
        stats.setAlignment(Pos.CENTER);

        Button sales      = new Button("Sales Analysis");
        Button profit     = new Button("Profit Analysis");
        Button production = new Button("Production Overview");
        Button map        = new Button("Showroom Map");
        Button logout     = new Button("Logout");

        styleButton(sales,      "#4CAF50");
        styleButton(profit,     "#f44336");
        styleButton(production, "#2196F3");
        styleButton(map,        "#FF9800");
        styleButton(logout,     "#9E9E9E");

        sales.setOnAction(e      -> new BrandSalesScreen().show(stage));
        profit.setOnAction(e     -> new BrandProfitScreen().show(stage));
        production.setOnAction(e -> new BrandProductionScreen().show(stage));
        map.setOnAction(e        -> new BrandMapScreen().show(stage));
        logout.setOnAction(e     -> new LoginScreen().show(stage));

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        grid.add(sales, 0, 0);
        grid.add(profit, 1, 0);
        grid.add(production, 0, 1);
        grid.add(map, 1, 1);
        grid.setAlignment(Pos.CENTER);

        VBox root = new VBox(18, title, brandInfo, stats, grid, logout);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color:#0f172a; -fx-background-radius:18; -fx-border-radius:18;");

        stage.setScene(new Scene(root, 520, 470));
        stage.setTitle("Brand Dashboard");
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
