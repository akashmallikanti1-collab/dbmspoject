package dealer;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.sql.*;

import util.DBConnection;
import util.ImageLoader;

public class InventoryScreen {

    public void show(Stage stage) {

        Label title = new Label("SHOWROOM INVENTORY");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        VBox list = new VBox(10);

        try {
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT model_name, price_amount, image_url FROM car_data ORDER BY model_name"
            );

            while (rs.next()) {

                String name     = rs.getString("model_name");
                int    price    = rs.getInt("price_amount");
                String fileName = rs.getString("image_url");

                // FIX: use ImageLoader — no more broken file:resources/placeholder.png
                ImageView imgView = new ImageView(ImageLoader.load(fileName));
                imgView.setFitWidth(120);
                imgView.setFitHeight(80);
                imgView.setPreserveRatio(true);

                Label carName = new Label(name);
                carName.setStyle("-fx-text-fill:white; -fx-font-weight:bold;");

                Label carPrice = new Label("₹" + price);
                carPrice.setStyle("-fx-text-fill:#4CAF50; -fx-font-weight:bold;");

                VBox info = new VBox(5, carName, carPrice);
                info.setAlignment(Pos.CENTER_LEFT);

                HBox row = new HBox(15, imgView, info);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding:10; -fx-border-color:#333; -fx-background-color:#1e1e1e; -fx-border-radius:5;");

                list.getChildren().add(row);
            }

        } catch (Exception e) {
            Label error = new Label("Failed to load inventory");
            error.setStyle("-fx-text-fill:red;");
            list.getChildren().add(error);
            e.printStackTrace();
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#121212;");

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new DealerDashboard().show(stage));

        VBox root = new VBox(15, title, scroll, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 500, 450));
        stage.setTitle("Inventory");
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
