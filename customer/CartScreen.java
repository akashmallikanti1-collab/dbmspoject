package customer;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.sql.*;

import util.DBConnection;
import util.ImageLoader;
import util.Session;

public class CartScreen {

    public void show(Stage stage) {

        Label title = new Label("YOUR CART");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        VBox list = new VBox(10);

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT c.car_id, c.model_name, c.price_amount, c.image_url, ct.cart_id " +
                "FROM cart_data ct JOIN car_data c ON ct.car_id = c.car_id " +
                "WHERE ct.customer_id=?"
            );
            ps.setInt(1, Session.currentUserId);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;

                int    carId    = rs.getInt("car_id");
                int    cartId   = rs.getInt("cart_id");
                String name     = rs.getString("model_name");
                int    price    = rs.getInt("price_amount");
                String fileName = rs.getString("image_url");

                // FIX: use ImageLoader instead of broken file:resources/ path
                ImageView iv = new ImageView(ImageLoader.load(fileName));
                iv.setFitWidth(200);
                iv.setFitHeight(120);
                iv.setPreserveRatio(true);

                Label info = new Label(name + "  ₹" + price);
                info.setStyle("-fx-text-fill:white; -fx-font-weight:bold;");

                Label msg = new Label();

                Button remove = new Button("Remove");
                styleButton(remove, "#f44336");

                remove.setOnAction(e -> {
                    try {
                        Connection c = DBConnection.getConnection();
                        PreparedStatement del = c.prepareStatement(
                            "DELETE FROM cart_data WHERE cart_id=?"
                        );
                        del.setInt(1, cartId);
                        del.executeUpdate();
                        show(stage); // refresh
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                Button book = new Button("Book Now");
                styleButton(book, "#4CAF50");

                book.setOnAction(e -> {
                    try {
                        Connection c = DBConnection.getConnection();
                        PreparedStatement p = c.prepareStatement(
                            "INSERT INTO booking_data VALUES (booking_seq.NEXTVAL,?,?,SYSDATE)"
                        );
                        p.setInt(1, Session.currentUserId);
                        p.setInt(2, carId);
                        p.executeUpdate();
                        msg.setText("✔ Booked Successfully");
                        msg.setStyle("-fx-text-fill:lightgreen;");
                    } catch (Exception ex) {
                        msg.setText("Booking Failed");
                        msg.setStyle("-fx-text-fill:red;");
                        ex.printStackTrace();
                    }
                });

                VBox box = new VBox(8, info, iv, remove, book, msg);
                box.setAlignment(Pos.CENTER);
                box.setStyle("-fx-background-color:#1e1e1e; -fx-padding:10; -fx-border-color:#333; -fx-border-radius:5;");
                list.getChildren().add(box);
            }

            if (!hasData) {
                Label empty = new Label("Cart is empty");
                empty.setStyle("-fx-text-fill:gray;");
                list.getChildren().add(empty);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ScrollPane sp = new ScrollPane(list);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#121212;");

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new CustomerDashboard().show(stage));

        VBox root = new VBox(15, title, sp, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 600, 500));
        stage.setTitle("Cart");
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:20;"
        );
        btn.setPrefWidth(180);
    }
}
