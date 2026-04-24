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

public class SalesComparisonScreen {

    public void show(Stage stage) {

        // TITLE
        Label title = new Label("SALES COMPARISON");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        // AXIS
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Brands");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Sales");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Brand Sales Comparison");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales");

        try {
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT b.brand_name, COUNT(bk.booking_id) AS total_sales " +
                "FROM brand_data b " +
                "JOIN car_data c ON b.brand_id=c.brand_id " +
                "LEFT JOIN booking_data bk ON c.car_id=bk.car_id " +
                "GROUP BY b.brand_name ORDER BY total_sales DESC"
            );

            while (rs.next()) {

                String brand = rs.getString("brand_name");
                int sales = rs.getInt("total_sales");

                series.getData().add(new XYChart.Data<>(brand, sales));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        chart.getData().add(series);

        // COLOR GREEN FOR SALES
        Platform.runLater(() -> {
            chart.lookupAll(".default-color0.chart-bar").forEach(n ->
                n.setStyle("-fx-bar-fill: #4CAF50;")
            );
        });

        // BACK BUTTON
        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");

        back.setOnAction(e -> new BrandDashboard().show(stage));

        VBox root = new VBox(15, title, chart, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("Sales Comparison");
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