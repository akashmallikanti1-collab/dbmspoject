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

public class BrandProductionScreen {

    public void show(Stage stage) {

        Label title = new Label("PRODUCTION OVERVIEW - YOUR MODELS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        int brandId = Session.currentUserId - 2;

        List<String>  models     = new ArrayList<>();
        List<Integer> prices     = new ArrayList<>();
        List<Integer> salesList  = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT c.model_name, c.price_amount, c.seating_capacity, " +
                "c.safety_rating, COUNT(b.booking_id) AS sales " +
                "FROM car_data c LEFT JOIN booking_data b ON c.car_id = b.car_id " +
                "WHERE c.brand_id = ? " +
                "GROUP BY c.model_name, c.price_amount, c.seating_capacity, c.safety_rating " +
                "ORDER BY c.price_amount ASC"
            );
            ps.setInt(1, brandId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                models.add(rs.getString("model_name"));
                prices.add(rs.getInt("price_amount"));
                salesList.add(rs.getInt("sales"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ── PRICE LINE CHART (production overview = price range) ─
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Model");
        yAxis.setLabel("Price (₹)");

        LineChart<String, Number> priceChart = new LineChart<>(xAxis, yAxis);
        priceChart.setTitle("Model Price Range (Live)");
        priceChart.setCreateSymbols(true);
        priceChart.setAnimated(false);
        priceChart.setPrefHeight(280);
        priceChart.setPrefWidth(700);

        XYChart.Series<String, Number> priceSeries = new XYChart.Series<>();
        priceSeries.setName("Price (₹)");
        for (int i = 0; i < models.size(); i++) {
            priceSeries.getData().add(new XYChart.Data<>(models.get(i), prices.get(i)));
        }
        priceChart.getData().add(priceSeries);

        // ── SALES LINE CHART ─────────────────────────────────────
        CategoryAxis xAxis2 = new CategoryAxis();
        NumberAxis   yAxis2 = new NumberAxis();
        xAxis2.setLabel("Model");
        yAxis2.setLabel("Bookings");

        LineChart<String, Number> salesChart = new LineChart<>(xAxis2, yAxis2);
        salesChart.setTitle("Model Bookings (Live)");
        salesChart.setCreateSymbols(true);
        salesChart.setAnimated(false);
        salesChart.setPrefHeight(280);
        salesChart.setPrefWidth(700);

        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Bookings");
        for (int i = 0; i < models.size(); i++) {
            salesSeries.getData().add(new XYChart.Data<>(models.get(i), salesList.get(i)));
        }
        salesChart.getData().add(salesSeries);

        // Color both charts: green rise, red drop
        Platform.runLater(() -> {
            colorLine(priceChart, priceSeries, models, prices.stream().map(p -> (Number) p).toList());
            colorLine(salesChart, salesSeries, models, salesList.stream().map(s -> (Number) s).toList());
        });

        // ── STATS ─────────────────────────────────────────────────
        int totalModels = models.size();
        int avgPrice    = (int) prices.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        int totalSales  = salesList.stream().mapToInt(Integer::intValue).sum();

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(10));
        stats.setStyle("-fx-background-color:#1e1e1e; -fx-border-color:#333;");
        stats.getChildren().addAll(
            statBox("Models",       String.valueOf(totalModels), "#2196F3"),
            statBox("Avg Price",    "₹" + avgPrice,             "#FF9800"),
            statBox("Total Booked", String.valueOf(totalSales),  "#4CAF50")
        );

        Button refresh = new Button("Refresh");
        styleButton(refresh, "#2196F3");
        refresh.setOnAction(e -> new BrandProductionScreen().show(stage));

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new BrandDashboard().show(stage));

        HBox btns = new HBox(15, refresh, back);
        btns.setAlignment(Pos.CENTER);

        VBox root = new VBox(12, title, priceChart, salesChart, stats, btns);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color:#121212;");

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#121212;-fx-background-color:#121212;");

        stage.setScene(new Scene(sp, 780, 700));
        stage.setTitle("Brand Production");
    }

    private void colorLine(LineChart<String, Number> chart,
                            XYChart.Series<String, Number> series,
                            List<String> models, List<Number> vals) {
        Node line = chart.lookup(".chart-series-line");
        if (line != null) line.setStyle("-fx-stroke:#4CAF50;-fx-stroke-width:2px;");

        var data = series.getData();
        for (int i = 0; i < data.size(); i++) {
            double cur  = data.get(i).getYValue().doubleValue();
            double prev = i > 0 ? data.get(i-1).getYValue().doubleValue() : cur;
            String color = cur >= prev ? "#4CAF50" : "#f44336";
            Node sym = data.get(i).getNode();
            if (sym != null) {
                sym.setStyle(
                    "-fx-background-color:" + color + ",white;" +
                    "-fx-background-radius:6px;-fx-padding:5px;"
                );
                Tooltip.install(sym, new Tooltip(models.get(i) + "\nValue: " + vals.get(i)));
            }
        }
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
