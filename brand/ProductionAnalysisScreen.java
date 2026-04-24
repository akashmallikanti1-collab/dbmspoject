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

public class ProductionAnalysisScreen {

    public void show(Stage stage) {

        // TITLE
        Label title = new Label("PRODUCTION ANALYSIS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        // AXIS
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Brands");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Models");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Brand Production Overview");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Models");

        try {
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT b.brand_name, COUNT(c.car_id) AS total_models " +
                "FROM brand_data b LEFT JOIN car_data c ON b.brand_id=c.brand_id " +
                "GROUP BY b.brand_name ORDER BY total_models DESC"
            );

            while (rs.next()) {

                String brand = rs.getString("brand_name");
                int count = rs.getInt("total_models");

                series.getData().add(new XYChart.Data<>(brand, count));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        chart.getData().add(series);

        // COLOR (BLUE FOR PRODUCTION)
        Platform.runLater(() -> {
            chart.lookupAll(".default-color0.chart-bar").forEach(n ->
                n.setStyle("-fx-bar-fill: #2196F3;")
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
        stage.setTitle("Production Analysis");
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