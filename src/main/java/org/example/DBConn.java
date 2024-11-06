package org.example;

import java.sql.*;

public class DBConn {
    private Connection connection;

    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:costamteges.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTables() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS links (url TEXT PRIMARY KEY, seen INTEGER DEFAULT 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertRow(String url) {
        try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO links (url) VALUES (?)")) {
            pstmt.setString(1, url);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isVisited(String url) {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM links WHERE url = ?")) {
            pstmt.setString(1, url);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if URL exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void incrementSeen(String url) {
        try (PreparedStatement pstmt = connection.prepareStatement("UPDATE links SET seen = seen + 1 WHERE url = ?")) {
            pstmt.setString(1, url);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLink() {
        String link = null;
        String query = "SELECT url FROM links WHERE seen = 0";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                link = rs.getString("url");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return link;
    }

    public void disconnect() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}