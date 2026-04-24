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

public class FilterScreen {

    public void show(Stage stage) {

        Label title = new Label("FILTER CARS");
        title.setStyle("-fx-font-size:18px; -fx-text-fill:red; -fx-font-weight:bold;");

        TextField min = new TextField();
        min.setPromptText("Min Price");
        min.setStyle("-fx-background-color:#1e1e1e; -fx-text-fill:white; -fx-prompt-text-fill:gray;");

        TextField max = new TextField();
        max.setPromptText("Max Price");
        max.setStyle("-fx-background-color:#1e1e1e; -fx-text-fill:white; -fx-prompt-text-fill:gray;");

        Button search = new Button("Apply Filters");
        styleButton(search, "#4CAF50");

        VBox results = new VBox(10);
        Label msg = new Label();

        search.setOnAction(e -> {
            results.getChildren().clear();
            msg.setText("");

            try {
                if (min.getText().isEmpty() || max.getText().isEmpty()) {
                    msg.setText("Enter both values");
                    msg.setStyle("-fx-text-fill:red;");
                    return;
                }

                int minVal = Integer.parseInt(min.getText().trim());
                int maxVal = Integer.parseInt(max.getText().trim());

                if (minVal > maxVal) {
                    msg.setText("Min price must be less than Max price");
                    msg.setStyle("-fx-text-fill:red;");
                    return;
                }

                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(
                    "SELECT model_name, price_amount, image_url FROM car_data " +
                    "WHERE price_amount BETWEEN ? AND ? ORDER BY price_amount"
                );
                ps.setInt(1, minVal);
                ps.setInt(2, maxVal);

                ResultSet rs = ps.executeQuery();
                boolean found = false;

                while (rs.next()) {
                    found = true;

                    String name     = rs.getString("model_name");
                    int    price    = rs.getInt("price_amount");
                    String fileName = rs.getString("image_url");

                    // FIX: use ImageLoader instead of broken image URL
                    ImageView iv = new ImageView(ImageLoader.load(fileName));
                    iv.setFitWidth(200);
                    iv.setFitHeight(120);
                    iv.setPreserveRatio(true);

                    Label l = new Label(name + "  ₹" + price);
                    l.setStyle("-fx-text-fill:white; -fx-font-weight:bold;");

                    VBox card = new VBox(5, l, iv);
                    card.setAlignment(Pos.CENTER);
                    card.setStyle("-fx-background-color:#1e1e1e; -fx-padding:10; -fx-border-color:#333; -fx-border-radius:5;");

                    results.getChildren().add(card);
                }

                if (!found) {
                    msg.setText("No cars found in this range");
                    msg.setStyle("-fx-text-fill:gray;");
                }

            } catch (NumberFormatException ex) {
                msg.setText("Enter valid numbers");
                msg.setStyle("-fx-text-fill:red;");
            } catch (Exception ex) {
                msg.setText("Error loading data");
                msg.setStyle("-fx-text-fill:red;");
                ex.printStackTrace();
            }
        });

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new CustomerDashboard().show(stage));

        VBox root = new VBox(12, title, min, max, search, msg, results, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#121212;");

        stage.setScene(new Scene(sp, 600, 500));
        stage.setTitle("Filter Cars");
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:20;"
        );
        btn.setPrefWidth(200);
    }
}
