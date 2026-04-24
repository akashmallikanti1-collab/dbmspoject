package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Oracle JDBC driver not found. Ensure ojdbc jar is on the classpath.");
            e.printStackTrace();
            return null;
        }

        String user = System.getenv().getOrDefault("DB_USER", "system");
        String pass = System.getenv().getOrDefault("DB_PASS", "it130");

        String[] urls = new String[] {
            System.getenv().getOrDefault("DB_URL", "jdbc:oracle:thin:@localhost:1521:xe"),
            "jdbc:oracle:thin:@127.0.0.1:1521:xe",
            "jdbc:oracle:thin:@//localhost:1521/xe",
            "jdbc:oracle:thin:@localhost:1521/XE"
        };

        for (String url : urls) {
            try {
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("✔ DB CONNECTED SUCCESSFULLY via " + url);
                return con;
            } catch (Exception e) {
                System.out.println("⚠️ DB connection attempt failed for " + url + ": " + e.getMessage());
            }
        }

        System.out.println("❌ DB CONNECTION FAILED on all attempted URLs");
        return null;
    }
}