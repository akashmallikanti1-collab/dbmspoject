package customer;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.sql.*;
import java.util.HashMap;

import util.DBConnection;
import util.Session;

public class BookingScreen {

    public void show(Stage stage){

        // TITLE
        Label title = new Label("BOOK A CAR");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        // DROPDOWN FOR CARS
        ComboBox<String> carBox = new ComboBox<>();
        carBox.setPromptText("Select Car");

        // MAP TO STORE NAME -> ID
        HashMap<String, Integer> carMap = new HashMap<>();

        try{
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT car_id, model_name FROM car_data"
            );

            while(rs.next()){
                String name = rs.getString("model_name");
                int id = rs.getInt("car_id");

                carBox.getItems().add(name);
                carMap.put(name, id);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        // MESSAGE LABEL
        Label msg = new Label();

        // BUTTONS
        Button book = new Button("Confirm Booking");
        Button back = new Button("Back");
        Button cancel = new Button("Cancel");

        styleButton(book, "#4CAF50");   // green
        styleButton(back, "#9E9E9E");   // grey
        styleButton(cancel, "#f44336"); // red

        // BOOK ACTION
        book.setOnAction(e->{
            try{
                if(carBox.getValue() == null){
                    msg.setText("Please select a car");
                    msg.setStyle("-fx-text-fill:red;");
                    return;
                }

                int carId = carMap.get(carBox.getValue());

                Connection con = DBConnection.getConnection();

                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO booking_data VALUES (booking_seq.NEXTVAL,?,?,SYSDATE)"
                );

                ps.setInt(1, Session.currentUserId);
                ps.setInt(2, carId);

                ps.executeUpdate();

                msg.setText("Booking Successful!");
                msg.setStyle("-fx-text-fill:green;");

            }catch(Exception ex){
                msg.setText("Booking Failed");
                msg.setStyle("-fx-text-fill:red;");
                ex.printStackTrace();
            }
        });

        // NAVIGATION
        back.setOnAction(e -> new CustomerDashboard().show(stage));
        cancel.setOnAction(e -> carBox.setValue(null));

        VBox root = new VBox(12, title, carBox, book, cancel, back, msg);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 400, 350));
        stage.setTitle("Book Car");
    }

    private void styleButton(Button btn, String color){
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:20;"
        );
        btn.setPrefWidth(200);
    }
}