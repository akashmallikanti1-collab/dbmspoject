package util;

import javafx.scene.control.Alert;

public class AlertUtil {

    public static void show(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Automobile Platform");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void warning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Warning");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}