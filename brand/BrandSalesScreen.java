package brand;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import util.DBConnection;
import util.Session;

public class BrandSalesScreen {

    public void show(Stage stage) {

        Label title = new Label("SALES ANALYSIS - YOUR MODELS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        // ── FETCH BRAND NAME ──────────────────────────────────────
        String brandName = "Your Brand";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT brand_name FROM brand_data WHERE brand_id = " +
                "(SELECT brand_id FROM brand_data WHERE brand_id = ?)"
            );
            // brand user_id maps to brand_data by position (user 3=brand 1, 4=brand 2 etc.)
            int brandId = Session.currentUserId - 2; // user_id 3 -> brand_id 1
            ResultSet r = con.createStatement().executeQuery(
                "SELECT brand_name FROM brand_data WHERE brand_id=" + brandId
            );
            if (r.next()) brandName = r.getString(1);
        } catch (Exception ignored) {}

        Label sub = new Label("Brand: " + brandName);
        sub.setStyle("-fx-text-fill:gray; -fx-font-size:12px;");

        // ── FETCH OWN MODEL SALES FROM DB ────────────────────────
        List<String> models = new ArrayList<>();
        List<Integer> sales  = new ArrayList<>();
        int brandId = Session.currentUserId - 2;

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT c.model_name, COUNT(b.booking_id) AS total " +
                "FROM car_data c LEFT JOIN booking_data b ON c.car_id = b.car_id " +
                "WHERE c.brand_id = ? " +
                "GROUP BY c.model_name ORDER BY total DESC"
            );
            ps.setInt(1, brandId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                models.add(rs.getString("model_name"));
                sales.add(rs.getInt("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ── LINE CHART ───────────────────────────────────────────
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Model");
        yAxis.setLabel("Bookings");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Model-wise Sales (Live)");
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.setPrefHeight(350);
        chart.setPrefWidth(700);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bookings");

        for (int i = 0; i < models.size(); i++) {
            series.getData().add(new XYChart.Data<>(models.get(i), sales.get(i)));
        }
        chart.getData().add(series);

        // Green = rise, Red = drop per point
        Platform.runLater(() -> {
            Node line = chart.lookup(".chart-series-line");
            if (line != null) line.setStyle("-fx-stroke:#4CAF50; -fx-stroke-width:2px;");

            var data = series.getData();
            for (int i = 0; i < data.size(); i++) {
                int cur  = data.get(i).getYValue().intValue();
                int prev = i > 0 ? data.get(i-1).getYValue().intValue() : cur;
                String color = cur >= prev ? "#4CAF50" : "#f44336";
                Node sym = data.get(i).getNode();
                if (sym != null) {
                    sym.setStyle(
                        "-fx-background-color:" + color + ",white;" +
                        "-fx-background-radius:6px;-fx-padding:6px;"
                    );
                    Tooltip.install(sym, new Tooltip(
                        models.get(i) + "\nBookings: " + cur +
                        (i > 0 ? (cur >= prev ? "  ▲" : "  ▼") : "")
                    ));
                }
            }
        });

        // ── STATS ────────────────────────────────────────────────
        int total = sales.stream().mapToInt(Integer::intValue).sum();
        int max   = sales.stream().mapToInt(Integer::intValue).max().orElse(0);

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(10));
        stats.setStyle("-fx-background-color:#1e1e1e; -fx-border-color:#333;");
        stats.getChildren().addAll(
            statBox("Total Sales",   String.valueOf(total), "#4CAF50"),
            statBox("Best Model",    String.valueOf(max) + " bookings", "#2196F3"),
            statBox("Models Listed", String.valueOf(models.size()), "#FF9800")
        );

        Button refresh = new Button("Refresh");
        styleButton(refresh, "#2196F3");
        refresh.setOnAction(e -> new BrandSalesScreen().show(stage));

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new BrandDashboard().show(stage));

        HBox btns = new HBox(15, refresh, back);
        btns.setAlignment(Pos.CENTER);

        VBox root = new VBox(12, title, sub, chart, stats, btns);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#121212;");

        stage.setScene(new Scene(root, 780, 600));
        stage.setTitle("Brand Sales");
    }

    private VBox statBox(String lbl, String val, String color) {
        Label v = new Label(val);
        v.setStyle("-fx-text-fill:" + color + ";-fx-font-size:20px;-fx-font-weight:bold;");
        Label l = new Label(lbl);
        l.setStyle("-fx-text-fill:gray;-fx-font-size:11px;");
        VBox b = new VBox(4, v, l);
        b.setAlignment(Pos.CENTER);
        return b;
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color:" + color + ";-fx-text-fill:white;" +
            "-fx-background-radius:20;-fx-font-weight:bold;"
        );
        btn.setPrefWidth(130);
    }
}
