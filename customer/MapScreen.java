package customer;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.sql.*;
import java.util.*;

import util.DBConnection;

public class MapScreen {

    // Showroom coordinates for each brand in Hyderabad
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

        Label title = new Label("HYDERABAD SHOWROOM LOCATIONS");
        title.setStyle("-fx-font-size:18px; -fx-text-fill:#4CAF50; -fx-font-weight:bold;");

        Label subtitle = new Label("Find all automobile showrooms in Hyderabad with live booking data");
        subtitle.setStyle("-fx-text-fill:#b0bec5; -fx-font-size:12px;");

        // ── FETCH LIVE BOOKING COUNTS PER BRAND FROM DB ──────────
        Map<String, Integer> brandSales = new LinkedHashMap<>();
        for (String b : SHOWROOM_COORDS.keySet()) brandSales.put(b, 0);

        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT bd.brand_name, COUNT(bk.booking_id) AS total " +
                "FROM brand_data bd " +
                "JOIN car_data c ON bd.brand_id = c.brand_id " +
                "LEFT JOIN booking_data bk ON c.car_id = bk.car_id " +
                "GROUP BY bd.brand_name"
            );
            while (rs.next()) {
                String brand = rs.getString("brand_name");
                int    count = rs.getInt("total");
                if (brandSales.containsKey(brand)) {
                    brandSales.put(brand, count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        webView.setPrefSize(720, 450);

        StringBuilder markerJs = new StringBuilder();
        for (Map.Entry<String, double[]> entry : SHOWROOM_COORDS.entrySet()) {
            String brand = entry.getKey();
            double lat = entry.getValue()[0];
            double lng = entry.getValue()[1];
            int sales = brandSales.getOrDefault(brand, 0);
            String address = SHOWROOM_ADDR.getOrDefault(brand, "Hyderabad, Telangana");
            markerJs.append("L.marker([")
                    .append(lat).append(", ").append(lng).append(")")
                    .append(".addTo(map)")
                    .append(".bindPopup('<strong>")
                    .append(brand.replace("'", "\\'"))
                    .append("</strong><br>")
                    .append(address.replace("'", "\\'"))
                    .append("<br><em>")
                    .append(sales)
                    .append(" bookings</em>');\n");
        }

        String html = "<!DOCTYPE html><html><head>" +
            "<meta charset='utf-8'/>" +
            "<title>Hyderabad Showrooms</title>" +
            "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>" +
            "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>" +
            "<style>body{margin:0;background:#121212;color:#f8fafc;font-family:Segoe UI,Arial,sans-serif;}" +
            "#map{height:450px;width:100%;border-radius:14px;box-shadow:0 10px 30px rgba(0,0,0,0.35);}"
            +".panel{padding:16px;background:#111827;border-bottom:1px solid #1f2937;}" +
            ".tag{display:inline-block;background:#22c55e;color:#0f172a;padding:4px 10px;border-radius:999px;margin:3px 3px 0 0;font-size:12px;}" +
            "</style></head><body>" +
            "<div class='panel'><div style='font-size:18px;font-weight:700;color:#34d399;'>🏢 Hyderabad Showrooms</div>" +
            "<div style='margin-top:8px;color:#94a3b8;'>Live booking counts and showroom locations for all brands.</div>" +
            "<div style='margin-top:12px;'>";

        for (Map.Entry<String, Integer> entry : brandSales.entrySet()) {
            html += "<span class='tag'>" + entry.getKey() + " " + entry.getValue() + " bookings</span>";
        }

        html += "</div></div>" +
            "<div id='map'></div>" +
            "<script>var map = L.map('map').setView([17.3850, 78.4867], 12);" +
            "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
            "maxZoom: 19, attribution: '&copy; OpenStreetMap contributors'}).addTo(map);" +
            markerJs.toString() +
            "</script></body></html>";

        engine.loadContent(html);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color:#2980b9; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:8 16; -fx-background-radius:8;");
        backBtn.setOnAction(e -> new CustomerDashboard().show(stage));

        VBox root = new VBox(15, title, subtitle, webView, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#121212;");

        Scene scene = new Scene(root, 750, 600);
        stage.setScene(scene);
        stage.setTitle("Showroom Map - Hyderabad");
        stage.show();
    }
}