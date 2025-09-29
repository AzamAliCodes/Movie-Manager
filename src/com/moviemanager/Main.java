package com.moviemanager;

import com.moviemanager.dao.MovieDAO;
import com.moviemanager.dao.WatchlistDAO;
import com.moviemanager.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Create and initialize the DAOs
        MovieDAO movieDAO = new MovieDAO();
        WatchlistDAO watchlistDAO = new WatchlistDAO();

        // Create the tables if they don't exist
        movieDAO.createTable();
        watchlistDAO.createTable();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}
