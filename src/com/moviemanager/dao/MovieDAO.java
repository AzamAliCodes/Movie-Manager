package com.moviemanager.dao;

import com.moviemanager.db.DatabaseConnection;
import com.moviemanager.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS movies (\n" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                " title TEXT NOT NULL,\n" +
                " year TEXT,\n" +
                " genre TEXT,\n" +
                " rating REAL,\n" +
                " description TEXT,\n" +
                " posterUrl TEXT,\n" +
                " imdbID TEXT UNIQUE\n" +
                ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int addMovie(Movie movie) {
        Movie existingMovie = getMovieByImdbID(movie.getImdbID());
        if (existingMovie != null) {
            return existingMovie.getId();
        }

        String sql = "INSERT INTO movies(title, year, genre, rating, description, posterUrl, imdbID) VALUES(?,?,?,?,?,?,?)";
        int id = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getYear());
            pstmt.setString(3, movie.getGenre());
            pstmt.setDouble(4, movie.getImdbRating());
            pstmt.setString(5, movie.getPlot());
            pstmt.setString(6, movie.getPosterUrl());
            pstmt.setString(7, movie.getImdbID());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return id;
    }

    public List<Movie> getAllMovies() {
        String sql = "SELECT * FROM movies";
        List<Movie> movies = new ArrayList<>();

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
                movies.add(movie);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return movies;
    }
    
    public Movie getMovieByImdbID(String imdbID) {
    String sql = "SELECT * FROM movies WHERE imdbID = ?";
    Movie movie = null;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, imdbID);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            movie = new Movie(
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
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
    return movie;
}

    public Movie getMovieById(int id) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        Movie movie = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                movie = new Movie(
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
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return movie;
    }
}
