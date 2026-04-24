package login;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.sql.*;

import util.AlertUtil;
import util.DBConnection;

public class SignupScreen {

    public void show(Stage stage) {

        Label title = new Label("CREATE ACCOUNT");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: red; -fx-font-weight: bold;");

        TextField name = new TextField();
        name.setPromptText("Username");

        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");

        ChoiceBox<String> role = new ChoiceBox<>();
        role.getItems().addAll("customer", "dealer", "brand");
        role.setStyle("-fx-background-color:#1e1e1e; -fx-text-fill:white;");

        Button signup = new Button("Create Account");
        Button back   = new Button("Back");

        styleButton(signup, "#27ae60");
        styleButton(back,   "#7f8c8d");

        signup.setOnAction(e -> {

            if (name.getText().isEmpty() || pass.getText().isEmpty() || role.getValue() == null) {
                AlertUtil.warning("Please fill all fields");
                return;
            }

            try {
                Connection con = DBConnection.getConnection();

                if (con == null) {
                    AlertUtil.error("Database connection failed. Check Oracle JDBC driver, DB URL, and credentials.");
                    return;
                }

                // FIX: schema column is user_name not user_email
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO user_data (user_id, user_name, user_pass, user_role) " +
                    "VALUES (user_seq.NEXTVAL, ?, ?, ?)"
                );

                ps.setString(1, name.getText());
                ps.setString(2, pass.getText());
                ps.setString(3, role.getValue());

                ps.executeUpdate();

                AlertUtil.show("Account Created Successfully! Please login.");
                new LoginScreen().show(stage);

            } catch (Exception ex) {
                AlertUtil.error("Error creating account: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        back.setOnAction(e -> new LoginScreen().show(stage));

        VBox root = new VBox(12, title, name, pass, role, signup, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #121212; -fx-padding: 20;");

        stage.setScene(new Scene(root, 400, 350));
        stage.setTitle("Create Account");
        stage.show();
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-radius: 20;" +
            "-fx-text-fill: white;" +
            "-fx-background-color: " + color + ";" +
            "-fx-padding: 8 15;"
        );
        btn.setPrefWidth(200);
    }
}
