package dealer;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.sql.*;

import util.DBConnection;

public class TestDriveManageScreen {

    public void show(Stage stage) {

        Label title = new Label("TEST DRIVE REQUESTS");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:red;");

        VBox list = new VBox(10);
        loadRequests(list, stage);

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#121212;");

        Button back = new Button("Back");
        styleButton(back, "#9E9E9E");
        back.setOnAction(e -> new DealerDashboard().show(stage));

        VBox root = new VBox(15, title, scroll, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#121212; -fx-padding:20;");

        stage.setScene(new Scene(root, 700, 400));
        stage.setTitle("Test Drive Management");
    }

    private void loadRequests(VBox list, Stage stage) {
        list.getChildren().clear();

        try {
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT t.test_id, u.user_name, c.model_name, t.status " +
                "FROM testdrive_data t " +
                "JOIN user_data u ON t.customer_id = u.user_id " +
                "JOIN car_data c ON t.car_id = c.car_id"
            );

            boolean found = false;

            while (rs.next()) {
                found = true;

                int    testId = rs.getInt("test_id");
                String user   = rs.getString("user_name");
                String car    = rs.getString("model_name");
                String status = rs.getString("status");

                Label info = new Label(
                    "Customer: " + user +
                    " | Car: "    + car  +
                    " | Status: " + status.toUpperCase()
                );
                info.setStyle("-fx-text-fill:white;");

                Button accept = new Button("Approve");
                Button reject = new Button("Reject");

                styleButton(accept, "#4CAF50");
                styleButton(reject, "#f44336");

                // FIX: schema CHECK constraint requires lowercase: 'pending','approved','rejected'
                accept.setOnAction(e -> {
                    updateStatus(testId, "approved");
                    new TestDriveManageScreen().show(stage); // refresh
                });
                reject.setOnAction(e -> {
                    updateStatus(testId, "rejected");
                    new TestDriveManageScreen().show(stage); // refresh
                });

                // Disable buttons if already decided
                if (status.equalsIgnoreCase("approved") || status.equalsIgnoreCase("rejected")) {
                    accept.setDisable(true);
                    reject.setDisable(true);
                    info.setStyle("-fx-text-fill:gray;");
                }

                HBox row = new HBox(10, info, accept, reject);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding:10; -fx-border-color:#333; -fx-background-color:#1e1e1e;");

                list.getChildren().add(row);
            }

            if (!found) {
                Label empty = new Label("No test drive requests yet");
                empty.setStyle("-fx-text-fill:gray;");
                list.getChildren().add(empty);
            }

        } catch (Exception e) {
            Label err = new Label("Error loading requests");
            err.setStyle("-fx-text-fill:red;");
            list.getChildren().add(err);
            e.printStackTrace();
        }
    }

    private void updateStatus(int testId, String newStatus) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE testdrive_data SET status=? WHERE test_id=?"
            );
            ps.setString(1, newStatus);
            ps.setInt(2, testId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:20;"
        );
        btn.setPrefWidth(80);
    }
}
