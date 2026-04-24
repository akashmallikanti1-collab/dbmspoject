package brand;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import javafx.application.Platform;

import java.sql.*;

import util.DBConnection;

public class ProfitAnalysisScreen {

    public void show(Stage stage) {

        // TITLE
        Label title = new Label("BRAND PROFIT ANALYSIS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        // AXIS
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Brands");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Profit / Loss (₹)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Brand Performance");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        try {
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT b.brand_name, COUNT(bk.booking_id) AS total_sales " +
                "FROM brand_data b " +
                "JOIN car_data c ON b.brand_id=c.brand_id " +
                "LEFT JOIN booking_data bk ON c.car_id=bk.car_id " +
                "GROUP BY b.brand_name"
            );

            while (rs.next()) {

                String brand = rs.getString("brand_name");
                int sales = rs.getInt("total_sales");

                // PROFIT / LOSS LOGIC
                int value;
                if (sales == 0) {
                    value = -100000; // LOSS if no sales
                } else {
                    value = sales * 60000; // PROFIT
                }

                series.getData().add(new XYChart.Data<>(brand, value));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        chart.getData().add(series);

        // COLOR BASED ON PROFIT / LOSS
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {

                Node node = data.getNode();

                if (data.getYValue().intValue() >= 0) {
                    node.setStyle("-fx-bar-fill: #4CAF50;"); // GREEN
                } else {
                    node.setStyle("-fx-bar-fill: #f44336;"); // RED
                }
            }
        });

        // BACK BUTTON
        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");

        back.setOnAction(e -> new BrandDashboard().show(stage));

        VBox root = new VBox(15, title, chart, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 750, 500));
        stage.setTitle("Profit Analysis");
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:20;"
        );
        btn.setPrefWidth(120);
    }
}