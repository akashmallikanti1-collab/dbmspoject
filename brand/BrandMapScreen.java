package brand;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

import util.DBConnection;
import util.Session;

public class BrandMapScreen {

    // Fixed showroom coords for each brand in Hyderabad
    private static final Map<String, double[]> SHOWROOM_COORDS = new LinkedHashMap<>();
    private static final Map<String, String>   SHOWROOM_ADDR   = new LinkedHashMap<>();

    static {
        SHOWROOM_COORDS.put("Hyundai",     new double[]{17.4126, 78.4425}); // Banjara Hills
        SHOWROOM_COORDS.put("Honda",       new double[]{17.4497, 78.3799}); // Hitech City
        SHOWROOM_COORDS.put("Toyota",      new double[]{17.4399, 78.4983}); // Secunderabad
        SHOWROOM_COORDS.put("Tata Motors", new double[]{17.4239, 78.4072}); // Jubilee Hills
        SHOWROOM_COORDS.put("Mahindra",    new double[]{17.4401, 78.3489}); // Gachibowli

        SHOWROOM_ADDR.put("Hyundai",     "Road No.12, Banjara Hills, Hyderabad");
        SHOWROOM_ADDR.put("Honda",       "Hitech City Main Road, Madhapur, Hyderabad");
        SHOWROOM_ADDR.put("Toyota",      "Station Road, Secunderabad, Hyderabad");
        SHOWROOM_ADDR.put("Tata Motors", "Jubilee Hills Check Post, Hyderabad");
        SHOWROOM_ADDR.put("Mahindra",    "Gachibowli, Financial District, Hyderabad");
    }

    public void show(Stage stage) {

        Label title = new Label("SHOWROOM LOCATIONS - HYDERABAD");
        title.setStyle("-fx-font-size:16px; -fx-text-fill:red; -fx-font-weight:bold;");

        String brandName = "Your Brand";
        try {
            Connection con = DBConnection.getConnection();
            ResultSet r = con.createStatement().executeQuery(
                "SELECT bd.brand_name FROM user_data u " +
                "JOIN brand_data bd ON LOWER(u.user_name) = LOWER(bd.brand_name) " +
                "WHERE u.user_id = " + Session.currentUserId
            );
            if (r.next()) {
                brandName = r.getString(1);
            }
        } catch (Exception ignored) {
        }

        Label brandLbl = new Label("Showing showrooms for: " + brandName);
        brandLbl.setStyle("-fx-text-fill:#4CAF50; -fx-font-weight:bold; -fx-font-size:13px;");

        double lat = 17.3850;
        double lng = 78.4867;
        String addr = "Hyderabad, Telangana";
        for (Map.Entry<String, double[]> entry : SHOWROOM_COORDS.entrySet()) {
            if (brandName.toLowerCase().contains(entry.getKey().toLowerCase()) ||
                entry.getKey().toLowerCase().contains(brandName.toLowerCase())) {
                lat  = entry.getValue()[0];
                lng  = entry.getValue()[1];
                addr = SHOWROOM_ADDR.get(entry.getKey());
                break;
            }
        }

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        webView.setPrefSize(720, 420);

        StringBuilder markerJs = new StringBuilder();
        for (Map.Entry<String, double[]> entry : SHOWROOM_COORDS.entrySet()) {
            String bName = entry.getKey();
            double[] coords = entry.getValue();
            String address = SHOWROOM_ADDR.getOrDefault(bName, "Hyderabad, Telangana");
            boolean highlight = bName.equalsIgnoreCase(brandName);
            markerJs.append("L.marker([")
                    .append(coords[0]).append(", ").append(coords[1]).append(")")
                    .append(".addTo(map)")
                    .append(".bindPopup('<strong>")
                    .append(bName.replace("'", "\\'"))
                    .append("</strong><br>")
                    .append(address.replace("'", "\\'"))
                    .append("')")
                    .append(highlight ? ".openPopup()" : "")
                    .append(";\n");
        }

        String html = "<!DOCTYPE html><html><head>" +
            "<meta charset='utf-8'/>" +
            "<title>Brand Showroom Map</title>" +
            "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>" +
            "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>" +
            "<style>body{margin:0;background:#121212;color:#f8fafc;font-family:Segoe UI,Arial,sans-serif;}" +
            "#map{height:420px;width:100%;border-radius:14px;box-shadow:0 10px 30px rgba(0,0,0,0.35);}" +
            ".info{padding:14px;background:#111827;border-bottom:1px solid #1f2937;}" +
            ".tag{display:inline-block;background:#4CAF50;color:#0f172a;padding:4px 10px;border-radius:999px;margin:4px 4px 0 0;font-size:12px;}" +
            "</style></head><body>" +
            "<div class='info'><div style='font-size:16px;font-weight:700;color:#34d399;'>" + brandName + " Showroom</div>" +
            "<div style='margin-top:4px;color:#94a3b8;'>Location: " + addr + "</div>" +
            "<div style='margin-top:8px;'><span class='tag'>Hyderabad</span>" +
            "<span class='tag'>Brand-focused map</span></div></div>" +
            "<div id='map'></div>" +
            "<script>var map = L.map('map').setView([" + lat + ", " + lng + "], 12);" +
            "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
            "maxZoom:19, attribution:'&copy; OpenStreetMap contributors'}).addTo(map);" +
            markerJs.toString() +
            "</script></body></html>";

        engine.loadContent(html);

        Label allTitle = new Label("All Brand Showrooms in Hyderabad:");
        allTitle.setStyle("-fx-text-fill:white; -fx-font-weight:bold; -fx-font-size:13px;");

        VBox allList = new VBox(6);
        allList.setPadding(new Insets(8));
        allList.setStyle("-fx-background-color:#1e1e1e; -fx-border-color:#333;");
        allList.getChildren().add(allTitle);

        for (Map.Entry<String, String> entry : SHOWROOM_ADDR.entrySet()) {
            String bName = entry.getKey();
            double[] coords = SHOWROOM_COORDS.get(bName);
            boolean isOwn = bName.equalsIgnoreCase(brandName);
            Label row = new Label((isOwn ? "★ " : "   ") + bName + " — " + entry.getValue() +
                " (" + coords[0] + ", " + coords[1] + ")");
            row.setStyle(isOwn
                ? "-fx-text-fill:#4CAF50; -fx-font-weight:bold; -fx-font-size:12px;"
                : "-fx-text-fill:#aaaaaa; -fx-font-size:11px;");
            allList.getChildren().add(row);
        }

        Button back = new Button("Back");
        back.setStyle("-fx-background-color:#9E9E9E;-fx-text-fill:white;-fx-background-radius:20;-fx-font-weight:bold;");
        back.setPrefWidth(120);
        back.setOnAction(e -> new BrandDashboard().show(stage));

        VBox root = new VBox(10, title, brandLbl, webView, allList, back);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color:#121212;");

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#121212;-fx-background-color:#121212;");

        stage.setScene(new Scene(sp, 760, 750));
        stage.setTitle("Showroom Map");
        stage.show();
    }
}
