package dealer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

import util.DBConnection;

public class DealerSalesScreen {

    public void show(Stage stage) {

        Label title = new Label("BRAND-WISE SALES ANALYSIS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        // ── FETCH BRAND-WISE SALES FROM DB ───────────────────────
        List<String>  brands = new ArrayList<>();
        List<Integer> sales  = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT bd.brand_name, COUNT(bk.booking_id) AS total " +
                "FROM brand_data bd " +
                "JOIN car_data c ON bd.brand_id = c.brand_id " +
                "LEFT JOIN booking_data bk ON c.car_id = bk.car_id " +
                "GROUP BY bd.brand_name ORDER BY total DESC"
            );
            while (rs.next()) {
                brands.add(rs.getString("brand_name"));
                sales.add(rs.getInt("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ── LINE CHART ───────────────────────────────────────────
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Brand");
        yAxis.setLabel("Total Bookings");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Brand-wise Sales (Live from DB)");
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.setPrefHeight(380);
        chart.setPrefWidth(720);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales per Brand");

        for (int i = 0; i < brands.size(); i++) {
            series.getData().add(new XYChart.Data<>(brands.get(i), sales.get(i)));
        }
        chart.getData().add(series);

        Platform.runLater(() -> {
            Node line = chart.lookup(".chart-series-line");
            if (line != null) line.setStyle("-fx-stroke:#4CAF50;-fx-stroke-width:2.5px;");

            var data = series.getData();
            for (int i = 0; i < data.size(); i++) {
                int cur  = data.get(i).getYValue().intValue();
                int prev = i > 0 ? data.get(i-1).getYValue().intValue() : cur;
                String color = cur >= prev ? "#4CAF50" : "#f44336";
                Node sym = data.get(i).getNode();
                if (sym != null) {
                    sym.setStyle(
                        "-fx-background-color:" + color + ",white;" +
                        "-fx-background-radius:7px;-fx-padding:7px;"
                    );
                    Tooltip.install(sym, new Tooltip(
                        brands.get(i) + "\nTotal Sales: " + cur +
                        (i > 0 ? (cur >= prev ? "  ▲ Rise" : "  ▼ Drop") : "")
                    ));
                }
            }
        });

        // ── STATS TABLE ───────────────────────────────────────────
        int grandTotal = sales.stream().mapToInt(Integer::intValue).sum();
        int maxSales   = sales.stream().mapToInt(Integer::intValue).max().orElse(0);
        String topBrand = brands.isEmpty() ? "-" : brands.get(0);

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(12));
        stats.setStyle("-fx-background-color:#1e1e1e; -fx-border-color:#333;");
        stats.getChildren().addAll(
            statBox("Total Bookings", String.valueOf(grandTotal),   "#4CAF50"),
            statBox("Top Brand",      topBrand,                     "#2196F3"),
            statBox("Best Count",     String.valueOf(maxSales),      "#FF9800"),
            statBox("Brands",         String.valueOf(brands.size()), "#9C27B0")
        );

        // ── BRAND BREAKDOWN BAR LIST ──────────────────────────────
        VBox breakdown = new VBox(6);
        breakdown.setPadding(new Insets(10));
        breakdown.setStyle("-fx-background-color:#1e1e1e; -fx-border-color:#333;");
        breakdown.getChildren().add(new Label("Brand Breakdown:") {{
            setStyle("-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:13px;");
        }});

        int maxBar = sales.stream().mapToInt(Integer::intValue).max().orElse(1);
        if (maxBar == 0) maxBar = 1;

        for (int i = 0; i < brands.size(); i++) {
            int    cnt   = sales.get(i);
            double pct   = grandTotal > 0 ? cnt * 100.0 / grandTotal : 0;
            double ratio = (double) cnt / maxBar;
            String color = ratio >= 0.66 ? "#4CAF50" : ratio >= 0.33 ? "#FF9800" : "#f44336";

            Label nameLbl = new Label(String.format("%-14s", brands.get(i)));
            nameLbl.setStyle("-fx-text-fill:white;-fx-font-size:12px;");
            nameLbl.setMinWidth(130);

            javafx.scene.shape.Rectangle bar = new javafx.scene.shape.Rectangle(Math.max(4, ratio * 280), 14);
            bar.setFill(javafx.scene.paint.Color.web(color));
            bar.setArcWidth(6); bar.setArcHeight(6);

            Label cntLbl = new Label(cnt + " (" + String.format("%.1f", pct) + "%)");
            cntLbl.setStyle("-fx-text-fill:" + color + ";-fx-font-size:12px;-fx-font-weight:bold;");

            HBox row = new HBox(10, nameLbl, bar, cntLbl);
            row.setAlignment(Pos.CENTER_LEFT);
            breakdown.getChildren().add(row);
        }

        Button refresh = new Button("Refresh");
        styleButton(refresh, "#2196F3");
        refresh.setOnAction(e -> new DealerSalesScreen().show(stage));

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new DealerDashboard().show(stage));

        HBox btns = new HBox(15, refresh, back);
        btns.setAlignment(Pos.CENTER);

        VBox root = new VBox(12, title, chart, stats, breakdown, btns);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#121212;");

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#121212;-fx-background-color:#121212;");

        stage.setScene(new Scene(sp, 800, 750));
        stage.setTitle("Brand-wise Sales");
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
