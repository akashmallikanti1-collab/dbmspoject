package customer;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

import util.DBConnection;
import util.ImageLoader;

public class CarDetailsScreen {

    public void show(Stage stage, int carId) {

        Label title = new Label("CAR DETAILS");
        title.setStyle("-fx-font-size:18px; -fx-text-fill:red; -fx-font-weight:bold;");

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM car_data WHERE car_id=?"
            );
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String name     = rs.getString("model_name");
                int    price    = rs.getInt("price_amount");
                String engine   = rs.getString("engine_info");
                String mileage  = rs.getString("mileage_info");
                String trans    = rs.getString("transmission_type");
                int    seats    = rs.getInt("seating_capacity");
                int    safety   = rs.getInt("safety_rating");
                String fileName = rs.getString("image_url");

                // FIX: use ImageLoader instead of hardcoded absolute path
                ImageView iv = new ImageView(ImageLoader.load(fileName));
                iv.setFitWidth(300);
                iv.setFitHeight(180);
                iv.setPreserveRatio(true);

                int safetyVal = Math.min(Math.max(safety, 0), 5);
                String stars  = "★".repeat(safetyVal) + "☆".repeat(5 - safetyVal);

                Label n = new Label("Model: " + name);
                Label p = new Label("Price: ₹" + price);
                Label d = new Label(
                    "Engine: "       + engine   + "\n" +
                    "Mileage: "      + mileage  + "\n" +
                    "Transmission: " + trans    + "\n" +
                    "Seats: "        + seats
                );
                Label r = new Label("Safety: " + stars);

                n.setStyle("-fx-text-fill:white; -fx-font-weight:bold; -fx-font-size:14px;");
                p.setStyle("-fx-text-fill:lightgreen; -fx-font-size:14px;");
                d.setStyle("-fx-text-fill:white;");
                r.setStyle("-fx-text-fill:gold;");

                content.getChildren().addAll(iv, n, p, d, r);

            } else {
                Label notFound = new Label("Car not found");
                notFound.setStyle("-fx-text-fill:gray;");
                content.getChildren().add(notFound);
            }

        } catch (Exception e) {
            Label err = new Label("Error loading details");
            err.setStyle("-fx-text-fill:red;");
            content.getChildren().add(err);
            e.printStackTrace();
        }

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#121212;");

        Button back = new Button("Back");
        back.setStyle("-fx-background-color:#9E9E9E;-fx-text-fill:white;-fx-background-radius:20;");
        back.setOnAction(e -> new CarListScreen().show(stage));

        VBox root = new VBox(15, title, sp, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 500, 550));
        stage.setTitle("Car Details");
        stage.show();
    }
}
