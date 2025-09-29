package com.moviemanager.model;

public class WatchlistItem {
    private int id;
    private int movieId;
    private String status;

    public WatchlistItem(int id, int movieId, String status) {
        this.id = id;
        this.movieId = movieId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
