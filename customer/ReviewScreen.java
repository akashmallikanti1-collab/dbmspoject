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

public class ReviewScreen {

    public void show(Stage stage){

        Label title = new Label("CAR REVIEWS");
        title.setStyle("-fx-font-size:18px; -fx-text-fill:red; -fx-font-weight:bold;");

        ComboBox<String> carBox = new ComboBox<>();
        carBox.setPromptText("Select Car");

        HashMap<String, Integer> map = new HashMap<>();

        // LOAD CARS
        try{
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT car_id, model_name FROM car_data"
            );

            while(rs.next()){
                String name = rs.getString("model_name");
                int id = rs.getInt("car_id");

                carBox.getItems().add(name);
                map.put(name, id);
            }

        }catch(Exception e){ e.printStackTrace(); }

        ComboBox<Integer> ratingBox = new ComboBox<>();
        ratingBox.getItems().addAll(1,2,3,4,5);
        ratingBox.setPromptText("Rating");

        TextArea comment = new TextArea();
        comment.setPromptText("Write your review...");

        Label msg = new Label();

        VBox reviewList = new VBox(8);

        Button submit = new Button("Submit Review");
        styleButton(submit, "#4CAF50");

        submit.setOnAction(e->{
            try{
                if(carBox.getValue()==null || ratingBox.getValue()==null){
                    msg.setText("Select car and rating");
                    msg.setStyle("-fx-text-fill:red;");
                    return;
                }

                Connection con = DBConnection.getConnection();

                CallableStatement cs = con.prepareCall(
                    "{call add_review(?,?,?,?)}"
                );

                cs.setInt(1, Session.currentUserId);
                cs.setInt(2, map.get(carBox.getValue()));
                cs.setInt(3, ratingBox.getValue());
                cs.setString(4, comment.getText());

                cs.execute();

                msg.setText("Review Added Successfully");
                msg.setStyle("-fx-text-fill:green;");

                loadReviews(reviewList);

            }catch(Exception ex){
                msg.setText("Error adding review");
                msg.setStyle("-fx-text-fill:red;");
                ex.printStackTrace();
            }
        });

        loadReviews(reviewList);

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new CustomerDashboard().show(stage));

        VBox root = new VBox(12,
                title,
                carBox,
                ratingBox,
                comment,
                submit,
                msg,
                new Label("Reviews:"),
                reviewList,
                back
        );

        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);

        stage.setScene(new Scene(sp, 600, 550));
        stage.setTitle("Reviews");
    }

    private void loadReviews(VBox box){
        box.getChildren().clear();

        try{
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT c.model_name, r.rating, r.review_text " +
                "FROM review_data r JOIN car_data c ON r.car_id=c.car_id"
            );

            while(rs.next()){

                String name = rs.getString(1);
                int rating = rs.getInt(2);
                String text = rs.getString(3);

                String stars = "★".repeat(rating) + "☆".repeat(5-rating);

                Label l = new Label(
                    name + "\n" +
                    "Rating: " + stars + "\n" +
                    text
                );

                l.setStyle("-fx-text-fill:white; -fx-background-color:#1e1e1e; -fx-padding:8;");

                box.getChildren().add(l);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
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