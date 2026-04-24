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

import java.sql.*;

import util.DBConnection;

public class SalesAnalysisScreen {

    public void show(Stage stage) {

        // TITLE
        Label title = new Label("SALES ANALYSIS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        // CHART AXIS
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Car Models");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Units Sold");

        // BAR CHART
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Sales Overview");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales");

        try {
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT c.model_name, COUNT(b.booking_id) AS total_sales " +
                "FROM car_data c LEFT JOIN booking_data b ON c.car_id=b.car_id " +
                "GROUP BY c.model_name ORDER BY total_sales DESC"
            );

            while (rs.next()) {
                String model = rs.getString("model_name");
                int sales = rs.getInt("total_sales");

                XYChart.Data<String, Number> data = new XYChart.Data<>(model, sales);
                series.getData().add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        chart.getData().add(series);

        // COLOR ALL BARS GREEN (SALES)
        chart.lookupAll(".default-color0.chart-bar").forEach(n ->
            n.setStyle("-fx-bar-fill: #4CAF50;")
        );

        // BACK BUTTON
        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");

        back.setOnAction(e -> new DealerDashboard().show(stage));

        VBox root = new VBox(15, title, chart, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("Sales Analysis");
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