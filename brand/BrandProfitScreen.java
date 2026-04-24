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

public class BrandProfitScreen {

    public void show(Stage stage) {

        Label title = new Label("PROFIT ANALYSIS - YOUR MODELS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        int brandId = Session.currentUserId - 2;

        List<String>  models  = new ArrayList<>();
        List<Long>    profits = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT c.model_name, c.price_amount, COUNT(b.booking_id) AS sales " +
                "FROM car_data c LEFT JOIN booking_data b ON c.car_id = b.car_id " +
                "WHERE c.brand_id = ? " +
                "GROUP BY c.model_name, c.price_amount ORDER BY sales DESC"
            );
            ps.setInt(1, brandId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String model = rs.getString("model_name");
                long   price = rs.getLong("price_amount");
                int    sale  = rs.getInt("sales");
                // profit = sales * price, loss = -price if 0 sales
                long profit = sale > 0 ? sale * price : -price;
                models.add(model);
                profits.add(profit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ── LINE CHART ───────────────────────────────────────────
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Model");
        yAxis.setLabel("Profit / Loss (₹)");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Model-wise Profit / Loss (Live)");
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.setPrefHeight(350);
        chart.setPrefWidth(700);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        for (int i = 0; i < models.size(); i++) {
            series.getData().add(new XYChart.Data<>(models.get(i), profits.get(i)));
        }
        chart.getData().add(series);

        Platform.runLater(() -> {
            Node line = chart.lookup(".chart-series-line");
            if (line != null) line.setStyle("-fx-stroke:#4CAF50;-fx-stroke-width:2px;");

            var data = series.getData();
            for (int i = 0; i < data.size(); i++) {
                long cur  = data.get(i).getYValue().longValue();
                long prev = i > 0 ? data.get(i-1).getYValue().longValue() : cur;
                // green if profit & rising, red if loss or dropping
                String color = (cur >= 0 && cur >= prev) ? "#4CAF50" : "#f44336";
                Node sym = data.get(i).getNode();
                if (sym != null) {
                    sym.setStyle(
                        "-fx-background-color:" + color + ",white;" +
                        "-fx-background-radius:6px;-fx-padding:6px;"
                    );
                    String label = cur >= 0
                        ? "Profit: ₹" + cur
                        : "Loss: ₹" + Math.abs(cur);
                    Tooltip.install(sym, new Tooltip(models.get(i) + "\n" + label));
                }
            }
        });

        // ── STATS ────────────────────────────────────────────────
        long totalProfit  = profits.stream().filter(p -> p > 0).mapToLong(Long::longValue).sum();
        long totalLoss    = profits.stream().filter(p -> p < 0).mapToLong(Long::longValue).sum();
        long profitModels = profits.stream().filter(p -> p > 0).count();

        HBox stats = new HBox(25);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(10));
        stats.setStyle("-fx-background-color:#1e1e1e; -fx-border-color:#333;");
        stats.getChildren().addAll(
            statBox("Total Revenue",     "₹" + totalProfit,          "#4CAF50"),
            statBox("Total Loss",        "₹" + Math.abs(totalLoss),  "#f44336"),
            statBox("Profitable Models", String.valueOf(profitModels), "#2196F3")
        );

        Button refresh = new Button("Refresh");
        styleButton(refresh, "#2196F3");
        refresh.setOnAction(e -> new BrandProfitScreen().show(stage));

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new BrandDashboard().show(stage));

        HBox btns = new HBox(15, refresh, back);
        btns.setAlignment(Pos.CENTER);

        VBox root = new VBox(12, title, chart, stats, btns);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#121212;");

        stage.setScene(new Scene(root, 780, 600));
        stage.setTitle("Brand Profit");
    }

    private VBox statBox(String lbl, String val, String color) {
        Label v = new Label(val);
        v.setStyle("-fx-text-fill:" + color + ";-fx-font-size:18px;-fx-font-weight:bold;");
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
