package customer;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.sql.*;
import java.util.HashMap;

import util.DBConnection;
import util.Session;

public class TestDriveScreen {

    public void show(Stage stage) {

        Label title = new Label("TEST DRIVE");
        title.setStyle("-fx-font-size:20px; -fx-text-fill:red; -fx-font-weight:bold;");

        // CAR SELECTION
        Label carLabel = new Label("Select Car:");
        carLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold;");

        ComboBox<String> carBox = new ComboBox<>();
        carBox.setPromptText("Choose a car...");
        carBox.setMaxWidth(300);

        HashMap<String, Integer> map = new HashMap<>();

        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT car_id, model_name FROM car_data ORDER BY model_name"
            );
            while (rs.next()) {
                String name = rs.getString("model_name");
                int    id   = rs.getInt("car_id");
                carBox.getItems().add(name);
                map.put(name, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // DATE PICKER
        Label dateLabel = new Label("Preferred Date:");
        dateLabel.setStyle("-fx-text-fill:white; -fx-font-weight:bold;");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Pick a date");
        datePicker.setMaxWidth(300);

        // STATUS MESSAGE
        Label msg = new Label();
        msg.setWrapText(true);

        // REQUEST BUTTON
        Button request = new Button("Request Test Drive");
        styleButton(request, "#4CAF50");

        // YOUR REQUESTS LIST
        Label reqTitle = new Label("Your Requests:");
        reqTitle.setStyle("-fx-text-fill:white; -fx-font-weight:bold; -fx-font-size:14px;");

        VBox list = new VBox(8);
        loadRequests(list);

        request.setOnAction(e -> {
            if (carBox.getValue() == null) {
                msg.setText("Please select a car.");
                msg.setStyle("-fx-text-fill:orange;");
                return;
            }
            if (datePicker.getValue() == null) {
                msg.setText("Please select a preferred date.");
                msg.setStyle("-fx-text-fill:orange;");
                return;
            }

            try {
                Connection con = DBConnection.getConnection();

                // Check for duplicate pending request for same car
                PreparedStatement check = con.prepareStatement(
                    "SELECT COUNT(*) FROM testdrive_data " +
                    "WHERE customer_id=? AND car_id=? AND status='pending'"
                );
                check.setInt(1, Session.currentUserId);
                check.setInt(2, map.get(carBox.getValue()));
                ResultSet rs = check.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    msg.setText("You already have a pending request for this car.");
                    msg.setStyle("-fx-text-fill:orange;");
                    return;
                }

                // Insert new request
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO testdrive_data VALUES (test_seq.NEXTVAL, ?, ?, 'pending')"
                );
                ps.setInt(1, Session.currentUserId);
                ps.setInt(2, map.get(carBox.getValue()));
                ps.executeUpdate();

                msg.setText("Request submitted for " + carBox.getValue() +
                            " on " + datePicker.getValue() + ". Status: PENDING");
                msg.setStyle("-fx-text-fill:lightgreen;");

                carBox.setValue(null);
                datePicker.setValue(null);
                loadRequests(list);

            } catch (Exception ex) {
                msg.setText("Request failed: " + ex.getMessage());
                msg.setStyle("-fx-text-fill:red;");
                ex.printStackTrace();
            }
        });

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new CustomerDashboard().show(stage));

        VBox form = new VBox(10, carLabel, carBox, dateLabel, datePicker, request, msg);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setStyle("-fx-background-color:#1e1e1e; -fx-padding:15; -fx-border-color:#333;");
        form.setMaxWidth(400);

        VBox root = new VBox(15, title, form, reqTitle, list, back);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#121212;");

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#121212; -fx-background-color:#121212;");

        stage.setScene(new Scene(sp, 600, 560));
        stage.setTitle("Test Drive");
    }

    private void loadRequests(VBox box) {
        box.getChildren().clear();

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT c.model_name, t.status " +
                "FROM testdrive_data t JOIN car_data c ON t.car_id = c.car_id " +
                "WHERE t.customer_id = ? ORDER BY t.test_id DESC"
            );
            ps.setInt(1, Session.currentUserId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                String name   = rs.getString("model_name");
                String status = rs.getString("status");

                String color;
                String icon;
                switch (status.toLowerCase()) {
                    case "approved": color = "#4CAF50"; icon = "[APPROVED]"; break;
                    case "rejected": color = "#f44336"; icon = "[REJECTED]"; break;
                    default:         color = "orange";  icon = "[PENDING]";  break;
                }

                Label l = new Label(name + "  ->  " + icon);
                l.setStyle(
                    "-fx-text-fill:" + color + ";" +
                    "-fx-font-weight:bold;" +
                    "-fx-background-color:#1e1e1e;" +
                    "-fx-padding:8;" +
                    "-fx-border-color:#333;"
                );
                l.setMaxWidth(Double.MAX_VALUE);
                box.getChildren().add(l);
            }

            if (!found) {
                Label empty = new Label("No test drive requests yet.");
                empty.setStyle("-fx-text-fill:gray;");
                box.getChildren().add(empty);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:20;" +
            "-fx-font-weight:bold;"
        );
        btn.setPrefWidth(250);
    }
}
