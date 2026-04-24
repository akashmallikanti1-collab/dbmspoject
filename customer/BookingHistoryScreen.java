package customer;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.sql.*;

import util.DBConnection;
import util.Session;

public class BookingHistoryScreen {

    public void show(Stage stage){

        // TITLE
        Label title = new Label("BOOKING HISTORY");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        VBox list = new VBox(10);

        try{
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT c.model_name, b.booking_date " +
                "FROM booking_data b " +
                "JOIN car_data c ON b.car_id = c.car_id " +
                "WHERE b.customer_id = ? " +
                "ORDER BY b.booking_date DESC"
            );

            ps.setInt(1, Session.currentUserId);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            while(rs.next()){
                hasData = true;

                String model = rs.getString("model_name");
                Date date = rs.getDate("booking_date");

                Label item = new Label(
                    "Car: " + model + " | Date: " + date
                );
                item.setStyle("-fx-text-fill:white;");

                HBox row = new HBox(item);
                row.setStyle("-fx-padding:10; -fx-border-color:#333;");

                list.getChildren().add(row);
            }

            if(!hasData){
                Label empty = new Label("No bookings yet");
                empty.setStyle("-fx-text-fill:gray;");
                list.getChildren().add(empty);
            }

        }catch(Exception e){
            Label err = new Label("Failed to load booking history");
            err.setStyle("-fx-text-fill:red;");
            list.getChildren().add(err);
            e.printStackTrace();
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);

        // BACK BUTTON
        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");

        back.setOnAction(e -> new CustomerDashboard().show(stage));

        VBox root = new VBox(15, title, scroll, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 500, 400));
        stage.setTitle("Booking History");
    }

    private void styleButton(Button btn, String color){
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:20;"
        );
        btn.setPrefWidth(120);
    }
}