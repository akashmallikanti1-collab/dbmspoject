package brand;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import javafx.scene.chart.PieChart;

import java.sql.*;

import util.DBConnection;

public class AreaAnalyticsScreen {

    public void show(Stage stage) {

        // TITLE
        Label title = new Label("AREA ANALYTICS - HYDERABAD");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        PieChart chart = new PieChart();
        chart.setTitle("Bookings by Area");

        try {
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT COUNT(*) AS total FROM booking_data"
            );

            if (rs.next()) {

                int total = rs.getInt("total");

                // DISTRIBUTION (SIMULATED FOR PROJECT)
                int north = total * 25 / 100;
                int south = total * 25 / 100;
                int east  = total * 25 / 100;
                int west  = total - (north + south + east);

                chart.getData().add(new PieChart.Data("North Hyderabad", north));
                chart.getData().add(new PieChart.Data("South Hyderabad", south));
                chart.getData().add(new PieChart.Data("East Hyderabad", east));
                chart.getData().add(new PieChart.Data("West Hyderabad", west));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // BACK BUTTON
        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");

        back.setOnAction(e -> new BrandDashboard().show(stage));

        VBox root = new VBox(15, title, chart, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 600, 450));
        stage.setTitle("Area Analytics");
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