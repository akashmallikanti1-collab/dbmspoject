package customer;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.sql.*;
import java.util.HashMap;

import util.DBConnection;
import util.ImageLoader;

public class CompareScreen {

    public void show(Stage stage) {

        Label title = new Label("COMPARE CARS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        ComboBox<String> car1 = new ComboBox<>();
        ComboBox<String> car2 = new ComboBox<>();
        car1.setPromptText("Select Car 1");
        car2.setPromptText("Select Car 2");

        HashMap<String, Integer> map = new HashMap<>();

        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT car_id, model_name FROM car_data"
            );
            while (rs.next()) {
                String name = rs.getString("model_name");
                int    id   = rs.getInt("car_id");
                car1.getItems().add(name);
                car2.getItems().add(name);
                map.put(name, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button compare = new Button("Compare");
        styleButton(compare, "#2196F3");

        Label msg = new Label();

        HBox resultBox = new HBox(20);
        resultBox.setAlignment(Pos.CENTER);

        compare.setOnAction(e -> {
            try {
                resultBox.getChildren().clear();

                if (car1.getValue() == null || car2.getValue() == null) {
                    msg.setText("Select both cars");
                    msg.setStyle("-fx-text-fill:red;");
                    return;
                }

                int id1 = map.get(car1.getValue());
                int id2 = map.get(car2.getValue());

                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM car_data WHERE car_id IN (?,?)"
                );
                ps.setInt(1, id1);
                ps.setInt(2, id2);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    String name     = rs.getString("model_name");
                    int    price    = rs.getInt("price_amount");
                    String engine   = rs.getString("engine_info");
                    String mileage  = rs.getString("mileage_info");
                    int    safety   = rs.getInt("safety_rating");
                    String fileName = rs.getString("image_url");

                    // FIX: use ImageLoader instead of broken image URL
                    ImageView iv = new ImageView(ImageLoader.load(fileName));
                    iv.setFitWidth(180);
                    iv.setFitHeight(110);
                    iv.setPreserveRatio(true);

                    int safetyVal = Math.min(Math.max(safety, 0), 5);
                    String stars  = "★".repeat(safetyVal) + "☆".repeat(5 - safetyVal);

                    Label l = new Label(name);
                    l.setStyle("-fx-text-fill:white; -fx-font-weight:bold;");

                    Label p = new Label("₹" + price);
                    p.setStyle("-fx-text-fill:lightgreen;");

                    Label d = new Label(
                        "Engine: "  + engine  + "\n" +
                        "Mileage: " + mileage + "\n" +
                        "Safety: "  + stars
                    );
                    d.setStyle("-fx-text-fill:white;");

                    VBox card = new VBox(8, l, iv, p, d);
                    card.setAlignment(Pos.CENTER);
                    card.setStyle("-fx-background-color:#1e1e1e; -fx-padding:10; -fx-border-color:#333; -fx-border-radius:5;");

                    resultBox.getChildren().add(card);
                }

                msg.setText("");

            } catch (Exception ex) {
                msg.setText("Comparison failed");
                msg.setStyle("-fx-text-fill:red;");
                ex.printStackTrace();
            }
        });

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new CustomerDashboard().show(stage));

        VBox root = new VBox(12, title, car1, car2, compare, msg, resultBox, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#121212;");

        stage.setScene(new Scene(sp, 800, 500));
        stage.setTitle("Compare Cars");
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
