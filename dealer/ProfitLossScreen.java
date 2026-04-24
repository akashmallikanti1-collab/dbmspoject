package dealer;

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

public class ProfitLossScreen {

    public void show(Stage stage) {

        // TITLE
        Label title = new Label("PROFIT / LOSS ANALYSIS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        // AXIS
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Car Models");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Profit / Loss (₹)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Dealer Performance");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        try {
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT c.model_name, COUNT(b.booking_id) AS total_sales " +
                "FROM car_data c LEFT JOIN booking_data b ON c.car_id=b.car_id " +
                "GROUP BY c.model_name"
            );

            while (rs.next()) {

                String model = rs.getString("model_name");
                int sales = rs.getInt("total_sales");

                // PROFIT LOGIC
                int value;
                if (sales == 0) {
                    value = -50000; // LOSS if no sales
                } else {
                    value = sales * 50000; // PROFIT
                }

                XYChart.Data<String, Number> data = new XYChart.Data<>(model, value);
                series.getData().add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        chart.getData().add(series);

        // COLOR LOGIC AFTER RENDER
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

        back.setOnAction(e -> new DealerDashboard().show(stage));

        VBox root = new VBox(15, title, chart, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 750, 500));
        stage.setTitle("Profit / Loss");
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