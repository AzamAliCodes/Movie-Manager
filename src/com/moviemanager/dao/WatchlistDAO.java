package com.moviemanager.dao;

import com.moviemanager.db.DatabaseConnection;
import com.moviemanager.model.WatchlistItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDAO {

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS watchlist (\n" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                " movie_id INTEGER REFERENCES movies(id),\n" +
                " status TEXT\n" +
                ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addToWatchlist(int movieId, String status) {
        String sql = "INSERT INTO watchlist(movie_id, status) VALUES(?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setString(2, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<WatchlistItem> getWatchlist() {
        String sql = "SELECT * FROM watchlist";
        List<WatchlistItem> watchlist = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                WatchlistItem item = new WatchlistItem(
                        rs.getInt("id"),
                        rs.getInt("movie_id"),
                        rs.getString("status")
                );
                watchlist.add(item);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return watchlist;
    }

    public void removeFromWatchlist(int id) {
        String sql = "DELETE FROM watchlist WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
