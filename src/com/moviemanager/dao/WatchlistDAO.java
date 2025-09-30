package com.moviemanager.dao;

import com.moviemanager.db.DatabaseConnection;
import com.moviemanager.model.Movie;
import com.moviemanager.model.WatchlistItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDAO {

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS watchlist (\n" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                " movie_id INTEGER REFERENCES movies(id) UNIQUE,\n" +
                " status TEXT\n" +
                ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean addToWatchlist(int movieId, String status) {
        if (isMovieInWatchlist(movieId)) {
            System.out.println("Movie is already in the watchlist.");
            return false;
        }
        String sql = "INSERT INTO watchlist(movie_id, status) VALUES(?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setString(2, status);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean isMovieInWatchlist(int movieId) {
        String sql = "SELECT COUNT(*) FROM watchlist WHERE movie_id = ?";
        boolean exists = false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return exists;
    }

    public List<WatchlistItem> getWatchlist() {
        String sql = "SELECT w.id as watchlist_id, w.status, m.* FROM watchlist w JOIN movies m ON w.movie_id = m.id";
        List<WatchlistItem> watchlist = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("year"),
                        rs.getString("genre"),
                        null, // director not in db
                        null, // actors not in db
                        rs.getString("description"),
                        rs.getString("posterUrl"),
                        rs.getDouble("rating"),
                        rs.getString("imdbID")
                );
                WatchlistItem item = new WatchlistItem(
                        rs.getInt("watchlist_id"),
                        rs.getInt("id"),
                        rs.getString("status"),
                        movie
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

    public void updateWatchlistStatus(int watchlistItemId, String newStatus) {
        String sql = "UPDATE watchlist SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, watchlistItemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
