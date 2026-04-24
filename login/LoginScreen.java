package login;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.sql.*;

import customer.CustomerDashboard;
import dealer.DealerDashboard;
import brand.BrandDashboard;

import util.AlertUtil;
import util.DBConnection;
import util.Session;

public class LoginScreen {

    public void show(Stage stage) {

        Label title = new Label("AUTOMOBILE PLATFORM");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Smart showroom analytics, maps and vehicle insights");
        subtitle.setStyle("-fx-text-fill: #b0bec5; -fx-font-size: 12px;");

        TextField email = new TextField();
        email.setPromptText("Enter Username");
        email.setStyle("-fx-background-color:#1e1e1e; -fx-text-fill:white; -fx-prompt-text-fill:#7f8c8d; -fx-background-radius:12; -fx-border-radius:12; -fx-border-color:#2c3e50; -fx-border-width:1;");

        PasswordField pass = new PasswordField();
        pass.setPromptText("Enter Password");
        pass.setStyle("-fx-background-color:#1e1e1e; -fx-text-fill:white; -fx-prompt-text-fill:#7f8c8d; -fx-background-radius:12; -fx-border-radius:12; -fx-border-color:#2c3e50; -fx-border-width:1;");

        Button loginBtn = new Button("Login");
        Button signupBtn = new Button("Create Account");
        Button exitBtn = new Button("Exit");

        styleButton(loginBtn, "#27ae60");
        styleButton(signupBtn, "#2980b9");
        styleButton(exitBtn, "#7f8c8d");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill:#e74c3c; -fx-font-size:12px;");

        loginBtn.setOnAction(e -> {
            msg.setText("");
            if (email.getText().isEmpty() || pass.getText().isEmpty()) {
                msg.setText("Please enter both username and password.");
                return;
            }

            try {
                Connection con = DBConnection.getConnection();

                if (con == null) {
                    AlertUtil.error("Database connection failed. Check Oracle JDBC driver, DB URL, and credentials.");
                    return;
                }

                PreparedStatement ps = con.prepareStatement(
                    "SELECT user_id, user_role FROM user_data WHERE user_name=? AND user_pass=?"
                );

                ps.setString(1, email.getText());
                ps.setString(2, pass.getText());

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    Session.currentUserId   = rs.getInt("user_id");
                    Session.currentUserRole = rs.getString("user_role");

                    String role = Session.currentUserRole.toLowerCase();
                    if (role.equals("customer")) {
                        new CustomerDashboard().show(stage);
                    } else if (role.equals("dealer")) {
                        new DealerDashboard().show(stage);
                    } else if (role.equals("brand")) {
                        new BrandDashboard().show(stage);
                    }
                } else {
                    msg.setText("Invalid username or password.");
                }

            } catch (Exception ex) {
                AlertUtil.error("Login failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        signupBtn.setOnAction(e -> new SignupScreen().show(stage));
        exitBtn.setOnAction(e -> stage.close());

        VBox controls = new VBox(12, email, pass, loginBtn, signupBtn, exitBtn);
        controls.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, subtitle, controls, msg);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #111827, #1f2937); -fx-padding: 30; -fx-border-radius:20; -fx-background-radius:20;");

        Scene scene = new Scene(root, 420, 520);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-radius: 999;" +
            "-fx-text-fill: white;" +
            "-fx-background-color: " + color + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;"
        );
        btn.setPrefWidth(220);
    }
}
