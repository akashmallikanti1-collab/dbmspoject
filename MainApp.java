import javafx.application.Application;
import javafx.stage.Stage;
import login.LoginScreen;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Automobile Platform - DBMS Project");
            stage.setWidth(900);
            stage.setHeight(600);
            new LoginScreen().show(stage);
            stage.show();
        } catch (Exception e) {
            System.out.println("Application Failed to Start");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
