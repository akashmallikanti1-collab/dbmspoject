package dealer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

import util.DBConnection;

public class DealerProfitScreen {

    public void show(Stage stage) {

        Label title = new Label("BRAND-WISE REVENUE & ESTIMATED PROFIT");
        title.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:white;");

        List<String>  brands   = new ArrayList<>();
        List<Long>    revenue  = new ArrayList<>();
        List<Long>    profit   = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT bd.brand_name, NVL(SUM(c.price_amount),0) AS revenue, COUNT(bk.booking_id) AS sales " +
                "FROM brand_data bd " +
                "JOIN car_data c ON bd.brand_id = c.brand_id " +
                "LEFT JOIN booking_data bk ON c.car_id = bk.car_id " +
                "GROUP BY bd.brand_name ORDER BY revenue DESC"
            );
            while (rs.next()) {
                String brand = rs.getString("brand_name");
                long   rev   = rs.getLong("revenue");
                long   est   = rev > 0 ? Math.round(rev * 0.15) : -100000L;
                brands.add(brand);
                revenue.add(rev);
                profit.add(est);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Brand");
        yAxis.setLabel("Revenue (₹)");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Revenue Trend by Brand");
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.setPrefHeight(380);
        chart.setPrefWidth(720);

        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Revenue");

        for (int i = 0; i < brands.size(); i++) {
            revenueSeries.getData().add(new XYChart.Data<>(brands.get(i), revenue.get(i)));
        }
        chart.getData().add(revenueSeries);

        Platform.runLater(() -> {
            Node line = chart.lookup(".chart-series-line");
            if (line != null) line.setStyle("-fx-stroke:#4CAF50;-fx-stroke-width:2.5px;");

            for (int i = 0; i < revenueSeries.getData().size(); i++) {
                long value = revenueSeries.getData().get(i).getYValue().longValue();
                Node sym = revenueSeries.getData().get(i).getNode();

                if (sym != null) {
                    String color = value > 0 ? "#4CAF50" : "#f44336";
                    sym.setStyle(
                        "-fx-background-color:" + color + ",white;" +
                        "-fx-background-radius:7px;-fx-padding:7px;"
                    );
                    String tooltipText = brands.get(i) + "\nRevenue: ₹" + value +
                        "\nEst. Profit: ₹" + (profit.get(i) > 0 ? profit.get(i) : 0);
                    Tooltip.install(sym, new Tooltip(tooltipText));
                }
            }
        });

        long totalRevenue = revenue.stream().mapToLong(Long::longValue).sum();
        long totalProfit  = profit.stream().filter(p -> p > 0).mapToLong(Long::longValue).sum();
        long noSales      = revenue.stream().filter(r -> r == 0).count();
        String topBrand   = brands.isEmpty() ? "-" : brands.get(0);

        HBox stats = new HBox(22);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(12));
        stats.setStyle("-fx-background-color:#1f2937; -fx-border-color:#334155; -fx-border-radius:14; -fx-background-radius:14;");
        stats.getChildren().addAll(
            statBox("Total Revenue", "₹" + totalRevenue, "#4CAF50"),
            statBox("Est. Profit", "₹" + totalProfit, "#FFB300"),
            statBox("Top Brand", topBrand, "#2196F3"),
            statBox("No Sales", noSales + " brands", "#f44336")
        );

        VBox table = new VBox(10);
        table.setPadding(new Insets(12));
        table.setStyle("-fx-background-color:#111827; -fx-border-color:#334155; -fx-border-radius:14; -fx-background-radius:14;");
        Label tableTitle = new Label("Revenue Breakdown");
        tableTitle.setStyle("-fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:bold;");
        table.getChildren().add(tableTitle);

        long maxRevenue = revenue.stream().mapToLong(Long::longValue).max().orElse(1);
        if (maxRevenue == 0) maxRevenue = 1;

        for (int i = 0; i < brands.size(); i++) {
            long rev = revenue.get(i);
            double ratio = (double) rev / maxRevenue;
            String color = rev == 0 ? "#f44336" : ratio >= 0.66 ? "#4CAF50" : "#FF9800";

            Label nameLbl = new Label(brands.get(i));
            nameLbl.setStyle("-fx-text-fill:white; -fx-font-size:12px;");
            nameLbl.setMinWidth(140);

            Rectangle bar = new Rectangle(Math.max(4, ratio * 280), 12);
            bar.setFill(javafx.scene.paint.Color.web(color));
            bar.setArcWidth(8);
            bar.setArcHeight(8);

            Label valLbl = new Label(rev == 0 ? "No Sales" : "₹" + rev);
            valLbl.setStyle("-fx-text-fill:" + color + "; -fx-font-size:12px; -fx-font-weight:bold;");

            HBox row = new HBox(10, nameLbl, bar, valLbl);
            row.setAlignment(Pos.CENTER_LEFT);
            table.getChildren().add(row);
        }

        Button refresh = new Button("Refresh");
        styleButton(refresh, "#2196F3");
        refresh.setOnAction(e -> new DealerProfitScreen().show(stage));

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new DealerDashboard().show(stage));

        HBox btns = new HBox(14, refresh, back);
        btns.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, chart, stats, table, btns);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#0b1120;");

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#0b1120; -fx-background-color:#0b1120;");

        stage.setScene(new Scene(sp, 820, 760));
        stage.setTitle("Brand-wise Revenue & Profit");
    }

    private VBox statBox(String lbl, String val, String color) {
        Label valueLabel = new Label(val);
        valueLabel.setStyle("-fx-text-fill:" + color + "; -fx-font-size:18px; -fx-font-weight:bold;");
        Label textLabel = new Label(lbl);
        textLabel.setStyle("-fx-text-fill:#cbd5e1; -fx-font-size:11px;");
        VBox box = new VBox(4, valueLabel, textLabel);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color:" + color + "; -fx-text-fill:white; -fx-background-radius:20; -fx-font-weight:bold;"
        );
        btn.setPrefWidth(140);
    }
}
