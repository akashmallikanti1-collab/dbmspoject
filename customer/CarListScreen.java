package customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import util.DBConnection;
import util.ImageLoader;
import util.Session;

public class CarListScreen {

    public void show(Stage stage) {

        Label title = new Label("AVAILABLE CARS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        TextField search = new TextField();
        search.setPromptText("Search Car...");
        search.setStyle("-fx-background-color:#1e1e1e; -fx-text-fill:white; -fx-prompt-text-fill:gray;");

        VBox list = new VBox(10);

        loadCars(list, "", stage);

        search.textProperty().addListener((obs, oldV, newV) -> {
            list.getChildren().clear();
            loadCars(list, newV, stage);
        });

        Button back = new Button("Back");
        back.setStyle("-fx-background-color:#9E9E9E;-fx-text-fill:white;-fx-background-radius:20;");
        back.setOnAction(e -> new CustomerDashboard().show(stage));

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background:#121212;");

        VBox root = new VBox(10, title, search, scrollPane, back);
        root.setStyle("-fx-background-color:#121212; -fx-padding:15;");

        stage.setScene(new Scene(root, 650, 550));
        stage.setTitle("Car List");
        stage.show();
    }

    private void loadCars(VBox container, String keyword, Stage stage) {

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT car_id, model_name, price_amount, image_url FROM car_data " +
                "WHERE LOWER(model_name) LIKE ?"
            );

            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int    id       = rs.getInt("car_id");
                String name     = rs.getString("model_name");
                int    price    = rs.getInt("price_amount");
                String fileName = rs.getString("image_url");

                // FIX: use ImageLoader instead of hardcoded path
                ImageView iv = new ImageView(ImageLoader.load(fileName));
                iv.setFitHeight(120);
                iv.setFitWidth(200);
                iv.setPreserveRatio(true);

                Label label = new Label(name + "  ₹" + price);
                label.setStyle("-fx-text-fill:white; -fx-font-weight:bold;");

                Label status = new Label();

                Button cart = new Button("Add to Cart");
                cart.setStyle("-fx-background-color:#4CAF50;-fx-text-fill:white;-fx-background-radius:15;");

                cart.setOnAction(e -> {
                    try {
                        Connection c = DBConnection.getConnection();
                        PreparedStatement p = c.prepareStatement(
                            "INSERT INTO cart_data VALUES (cart_seq.NEXTVAL,?,?)"
                        );
                        p.setInt(1, Session.currentUserId);
                        p.setInt(2, id);
                        p.executeUpdate();
                        status.setText("✔ Added to Cart");
                        status.setStyle("-fx-text-fill:lightgreen;");
                    } catch (Exception ex) {
                        status.setText("Failed");
                        status.setStyle("-fx-text-fill:red;");
                        ex.printStackTrace();
                    }
                });

                Button view = new Button("View Details");
                view.setStyle("-fx-background-color:#2196F3;-fx-text-fill:white;-fx-background-radius:15;");
                view.setOnAction(e -> new CarDetailsScreen().show(stage, id));

                VBox card = new VBox(8, label, iv, cart, view, status);
                card.setAlignment(Pos.CENTER);
                card.setStyle("-fx-padding:10; -fx-background-color:#1e1e1e; -fx-border-color:#333; -fx-border-radius:5;");

                container.getChildren().add(card);
            }

        } catch (Exception e) {
            Label err = new Label("Failed to load cars");
            err.setStyle("-fx-text-fill:red;");
            container.getChildren().add(err);
            e.printStackTrace();
        }
    }
}
