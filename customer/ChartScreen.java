package customer;

import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.application.Platform;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import util.DBConnection;

public class ChartScreen {

    public void show(Stage stage) {

        Label title = new Label("HYDERABAD SALES ANALYTICS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        // ── FETCH LIVE DATA FROM DB ──────────────────────────────
        List<String> models = new ArrayList<>();
        List<Integer> sales = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT c.model_name, COUNT(b.booking_id) AS total " +
                "FROM car_data c LEFT JOIN booking_data b ON c.car_id = b.car_id " +
                "GROUP BY c.model_name ORDER BY total DESC"
            );
            while (rs.next()) {
                models.add(rs.getString("model_name"));
                sales.add(rs.getInt("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ── SUMMARY STATS ────────────────────────────────────────
        int total = sales.stream().mapToInt(Integer::intValue).sum();
        int max   = sales.stream().mapToInt(Integer::intValue).max().orElse(0);
        int min   = sales.stream().mapToInt(Integer::intValue).min().orElse(0);

        // ── LINE CHART ───────────────────────────────────────────
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Car Model");
        yAxis.setLabel("Total Bookings");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Live Sales Trend (updates with each booking)");
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.setPrefHeight(380);
        chart.setPrefWidth(740);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bookings per Model");

        for (int i = 0; i < models.size(); i++) {
            series.getData().add(new XYChart.Data<>(models.get(i), sales.get(i)));
        }

        chart.getData().add(series);

        // ── COLOR EACH POINT: GREEN = rise/same, RED = drop ──────
        Platform.runLater(() -> {
            var data = series.getData();

            // Color the connecting line based on overall trend
            Node line = chart.lookup(".chart-series-line");
            if (line != null) {
                // Use green if overall trend is neutral/up, will recolor per segment below
                line.setStyle("-fx-stroke: #4CAF50; -fx-stroke-width: 2px;");
            }

            for (int i = 0; i < data.size(); i++) {
                int cur  = data.get(i).getYValue().intValue();
                int prev = (i > 0) ? data.get(i - 1).getYValue().intValue() : cur;

                String color = (cur >= prev) ? "#4CAF50" : "#f44336";

                Node symbol = data.get(i).getNode();
                if (symbol != null) {
                    symbol.setStyle(
                        "-fx-background-color: " + color + ", white;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-padding: 6px;"
                    );

                    // Tooltip showing exact value
                    Tooltip tp = new Tooltip(
                        models.get(i) + "\nBookings: " + cur +
                        (i > 0 ? (cur >= prev ? "  ▲ Rise" : "  ▼ Drop") : "")
                    );
                    Tooltip.install(symbol, tp);
                }
            }
        });

        // ── STATS BOX ────────────────────────────────────────────
        HBox stats = new HBox(40);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(12));
        stats.setStyle("-fx-background-color:#1e1e1e; -fx-border-color:#333; -fx-border-radius:5;");
        stats.getChildren().addAll(
            statBox("Total Bookings", String.valueOf(total),        "#2196F3"),
            statBox("Best Selling",   String.valueOf(max) + " sales", "#4CAF50"),
            statBox("Lowest Sales",   String.valueOf(min) + " sales", "#f44336"),
            statBox("Models Tracked", String.valueOf(models.size()), "#FF9800")
        );

        // ── LEGEND ───────────────────────────────────────────────
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);

        Circle g = new Circle(7, Color.web("#4CAF50"));
        Label gl = new Label("Rising / Same");
        gl.setStyle("-fx-text-fill:white;");

        Circle r = new Circle(7, Color.web("#f44336"));
        Label rl = new Label("Dropping");
        rl.setStyle("-fx-text-fill:white;");

        legend.getChildren().addAll(g, gl, r, rl);

        // ── REFRESH BUTTON ────────────────────────────────────────
        Button refresh = new Button("Refresh Data");
        refresh.setStyle(
            "-fx-background-color:#2196F3;-fx-text-fill:white;" +
            "-fx-background-radius:20;-fx-font-weight:bold;"
        );
        refresh.setPrefWidth(160);
        refresh.setOnAction(e -> new ChartScreen().show(stage));

        // ── BACK BUTTON ───────────────────────────────────────────
        Button back = new Button("Back");
        back.setStyle(
            "-fx-background-color:#9E9E9E;-fx-text-fill:white;" +
            "-fx-background-radius:20;"
        );
        back.setPrefWidth(120);
        back.setOnAction(e -> new CustomerDashboard().show(stage));

        HBox buttons = new HBox(15, refresh, back);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, chart, legend, stats, buttons);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#121212;");

        stage.setScene(new Scene(root, 820, 650));
        stage.setTitle("Sales Analytics");
    }

    private VBox statBox(String label, String value, String color) {
        Label val = new Label(value);
        val.setStyle("-fx-text-fill:" + color + "; -fx-font-size:20px; -fx-font-weight:bold;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:gray; -fx-font-size:11px;");
        VBox box = new VBox(4, val, lbl);
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
