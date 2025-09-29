package com.moviemanager.ui;

import com.moviemanager.api.MovieAPI;
import com.moviemanager.api.WatchmodeAPI;
import com.moviemanager.dao.MovieDAO;
import com.moviemanager.dao.WatchlistDAO;
import com.moviemanager.model.Movie;
import com.moviemanager.model.StreamingInfo;
import com.moviemanager.theme.Theme;
import com.moviemanager.theme.RoundedBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.border.TitledBorder;

public class MainFrame extends JFrame {

    private WatchmodeAPI watchmodeAPI;
    private MovieAPI movieAPI;
    private MovieDAO movieDAO;
    private WatchlistDAO watchlistDAO;

    private JComboBox<String> countryComboBox;
    private JTextField searchField;
    private JButton searchButton;
    private JPanel movieDetailsPanel;
    private JTabbedPane tabbedPane;
    private JPanel searchPanel;
    private JPanel watchlistPanel;

    public MainFrame() {
        this.watchmodeAPI = new WatchmodeAPI();
        this.movieAPI = new MovieAPI();
        this.movieDAO = new MovieDAO();
        this.watchlistDAO = new WatchlistDAO();

        // Create the tables if they don't exist
        movieDAO.createTable();
        watchlistDAO.createTable();

        setTitle("Show Search and Watchlist Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        getContentPane().setBackground(Theme.PRIMARY_BACKGROUND);

        // Search Panel (now Home Panel)
        searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        searchField = new JTextField(30);
        searchField.setBackground(Theme.COMPONENT_BACKGROUND);
        searchField.setForeground(Theme.PRIMARY_TEXT);
        searchField.setCaretColor(Theme.PRIMARY_TEXT);
        searchField.setFont(Theme.PLAIN_FONT);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchButton.doClick();
            }
        });

        searchButton = new JButton("Search");
        searchButton.setBackground(Theme.ACCENT_ORANGE);
        searchButton.setForeground(Theme.PRIMARY_TEXT);
        searchButton.setFont(Theme.BOLD_FONT);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(new RoundedBorder(8));

        JButton clearButton = new JButton("\u2715");
        clearButton.setBackground(Theme.ACCENT_ORANGE);
        clearButton.setForeground(Theme.PRIMARY_TEXT);
        clearButton.setFont(Theme.BOLD_FONT);
        clearButton.setFocusPainted(false);
        clearButton.setBorder(new RoundedBorder(8));
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                searchField.requestFocusInWindow();
            }
        });

        String[] countries = {"US", "GB", "CA", "DE", "BR", "IN", "JP", "AU", "FR", "ES", "IT", "MX"};
        countryComboBox = new JComboBox<>(countries);
        countryComboBox.setBackground(Theme.COMPONENT_BACKGROUND);
        countryComboBox.setForeground(Theme.PRIMARY_TEXT);
        countryComboBox.setFont(Theme.PLAIN_FONT);
        countryComboBox.setSelectedItem("IN");
        JLabel searchIconLabel = new JLabel("\uD83D\uDD0D");
        searchIconLabel.setForeground(Theme.PRIMARY_TEXT);
        searchIconLabel.setFont(Theme.BOLD_FONT);
        topPanel.add(searchIconLabel);
        topPanel.add(searchField);
        topPanel.add(clearButton);
        topPanel.add(countryComboBox);
        topPanel.add(searchButton);
        searchPanel.add(topPanel, BorderLayout.NORTH);

        movieDetailsPanel = new JPanel();
        movieDetailsPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        searchPanel.add(movieDetailsPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Home", searchPanel);

        // Watchlist Panel
        watchlistPanel = new JPanel(new BorderLayout());
        watchlistPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        tabbedPane.addTab("Watchlist", watchlistPanel);

        tabbedPane.setSelectedIndex(0); // Set Home tab as default

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) { // Watchlist tab
                populateWatchlistTable();
            }
        });

        add(tabbedPane);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                if (!searchTerm.isEmpty()) {
                    searchMovie(searchTerm);
                }
            }
        });
        searchField.requestFocusInWindow();
    }

    private void searchMovie(String title) {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        movieDetailsPanel.removeAll();
        movieDetailsPanel.setLayout(new GridBagLayout());
        movieDetailsPanel.add(progressBar);
        movieDetailsPanel.revalidate();
        movieDetailsPanel.repaint();

        SwingWorker<Movie, Void> worker = new SwingWorker<Movie, Void>() {
            @Override
            protected Movie doInBackground() throws Exception {
                return movieAPI.searchMovie(title);
            }

            @Override
            protected void done() {
                try {
                    Movie movie = get();
                    if (movie != null) {
                        displayMovieDetails(movie);
                    } else {
                        movieDetailsPanel.removeAll();
                        JOptionPane.showMessageDialog(MainFrame.this, "Show not found!", "Error", JOptionPane.ERROR_MESSAGE);
                        movieDetailsPanel.revalidate();
                        movieDetailsPanel.repaint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    movieDetailsPanel.removeAll();
                    JOptionPane.showMessageDialog(MainFrame.this, "An error occurred during the search.", "Error", JOptionPane.ERROR_MESSAGE);
                    movieDetailsPanel.revalidate();
                    movieDetailsPanel.repaint();
                }
            }
        };

        worker.execute();
    }

    private void displayMovieDetails(Movie movie) {
        movieDetailsPanel.removeAll();
        movieDetailsPanel.setLayout(new BorderLayout());
        movieDetailsPanel.setBackground(Theme.SECONDARY_BACKGROUND);

        // Header
        movieDetailsPanel.add(createHeaderPanel(movie), BorderLayout.NORTH);

        // Main Content
        movieDetailsPanel.add(createMainContentPanel(movie), BorderLayout.CENTER);

        // Footer
        movieDetailsPanel.add(createFooterPanel(movie), BorderLayout.SOUTH);

        movieDetailsPanel.revalidate();
        movieDetailsPanel.repaint();
    }

    private JPanel createHeaderPanel(Movie movie) {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Theme.PRIMARY_TEXT);
        headerPanel.add(titleLabel);

        JPanel subHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subHeaderPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        JLabel yearLabel = new JLabel(movie.getYear());
        yearLabel.setForeground(Theme.SECONDARY_TEXT);
        subHeaderPanel.add(yearLabel);
        JLabel separator1 = new JLabel("|");
        separator1.setForeground(Theme.SECONDARY_TEXT);
        subHeaderPanel.add(separator1);
        JLabel genreLabel = new JLabel(movie.getGenre());
        genreLabel.setForeground(Theme.SECONDARY_TEXT);
        subHeaderPanel.add(genreLabel);
        JLabel separator2 = new JLabel("|");
        separator2.setForeground(Theme.SECONDARY_TEXT);
        subHeaderPanel.add(separator2);
        JLabel ratingLabel = new JLabel("IMDb Rating: " + movie.getImdbRating());
        ratingLabel.setForeground(Theme.SECONDARY_TEXT);
        subHeaderPanel.add(ratingLabel);
        headerPanel.add(subHeaderPanel);
        return headerPanel;
    }

    private JPanel createMainContentPanel(Movie movie) {
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();

        // Poster
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainContentPanel.add(createPosterPanel(movie), gbc);

        // Details
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainContentPanel.add(createDetailsPanel(movie), gbc);

        // Streaming
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        mainContentPanel.add(createStreamingPanel(movie), gbc);

        return mainContentPanel;
    }

    private JPanel createPosterPanel(Movie movie) {
        JPanel posterPanel = new JPanel(new GridBagLayout());
        posterPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            try {
                URL posterUrl = new URL(movie.getPosterUrl());
                ImageIcon posterIcon = new ImageIcon(posterUrl);
                Image image = posterIcon.getImage();
                Image newimg = image.getScaledInstance(200, 300,  java.awt.Image.SCALE_SMOOTH);
                posterIcon = new ImageIcon(newimg);
                JLabel posterLabel = new JLabel(posterIcon);
                posterPanel.add(posterLabel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return posterPanel;
    }

    private JPanel createDetailsPanel(Movie movie) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridBagLayout());
        detailsPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        GridBagConstraints detailsGbc = new GridBagConstraints();
        detailsGbc.insets = new Insets(2, 2, 2, 2);
        detailsGbc.fill = GridBagConstraints.HORIZONTAL;
        detailsGbc.anchor = GridBagConstraints.NORTHWEST;

        // Plot
        detailsGbc.gridx = 0;
        detailsGbc.gridy = 0;
        detailsGbc.weightx = 1;
        detailsGbc.weighty = 1; // Give more vertical space to plot
        detailsGbc.fill = GridBagConstraints.BOTH;
        JTextArea plotArea = new JTextArea(movie.getPlot());
        plotArea.setLineWrap(true);
        plotArea.setWrapStyleWord(true);
        plotArea.setEditable(false);
        plotArea.setBackground(Theme.SECONDARY_BACKGROUND);
        plotArea.setForeground(Theme.PRIMARY_TEXT);
        JScrollPane plotScrollPane = new JScrollPane(plotArea);
        plotScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.SECONDARY_TEXT), "Plot", TitledBorder.LEFT, TitledBorder.TOP, Theme.PLAIN_FONT, Theme.SECONDARY_TEXT));
        detailsPanel.add(plotScrollPane, detailsGbc);

        // Director
        detailsGbc.gridx = 0;
        detailsGbc.gridy = 1;
        detailsGbc.weighty = 0;
        detailsGbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel directorLabel = new JLabel("Director: " + movie.getDirector());
        directorLabel.setForeground(Theme.SECONDARY_TEXT);
        detailsPanel.add(directorLabel, detailsGbc);

        // Actors
        detailsGbc.gridx = 0;
        detailsGbc.gridy = 2;
        JLabel actorsLabel = new JLabel("Actors: " + movie.getActors());
        actorsLabel.setForeground(Theme.SECONDARY_TEXT);
        detailsPanel.add(actorsLabel, detailsGbc);

        return detailsPanel;
    }

    private JPanel createStreamingPanel(Movie movie) {
        JPanel rightPanel = new JPanel(new GridLayout(0, 1));
        rightPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        rightPanel.setBorder(BorderFactory.createTitledBorder(null, "Available on", 0, 0, null, Theme.SECONDARY_TEXT));
        List<StreamingInfo> streamingInfos = watchmodeAPI.getStreamingInfo(movie.getTitle(), (String) countryComboBox.getSelectedItem());
        if (streamingInfos != null && !streamingInfos.isEmpty()) {
            for (StreamingInfo info : streamingInfos) {
                String buttonText = info.getName();
                switch (info.getType()) {
                    case "sub":
                        buttonText += " (Subscription)";
                        break;
                    case "free":
                        buttonText += " (Free)";
                        break;
                    case "buy":
                        buttonText += " (Buy)";
                        break;
                    case "rent":
                        buttonText += " (Rent)";
                        break;
                    default:
                        buttonText += " (" + info.getType() + ")";
                        break;
                }
                JButton streamingButton = new JButton(buttonText);
                streamingButton.setBackground(Theme.COMPONENT_BACKGROUND);
                streamingButton.setForeground(Theme.PRIMARY_TEXT);
                streamingButton.setFont(Theme.PLAIN_FONT);
                streamingButton.setFocusPainted(false);
                streamingButton.setBorder(new RoundedBorder(8));
                streamingButton.addActionListener(e -> {
                    try {
                        Desktop.getDesktop().browse(new URL(info.getUrl()).toURI());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                rightPanel.add(streamingButton);
            }
        } else {
            JLabel noInfoLabel = new JLabel("No streaming information available.");
            noInfoLabel.setForeground(Theme.SECONDARY_TEXT);
            rightPanel.add(noInfoLabel);
        }
        return rightPanel;
    }

    private JPanel createFooterPanel(Movie movie) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        JButton addToWatchlistButton = new JButton("Add to Watchlist");
        addToWatchlistButton.setBackground(Theme.ACCENT_ORANGE);
        addToWatchlistButton.setForeground(Theme.PRIMARY_TEXT);
        addToWatchlistButton.setFont(Theme.BOLD_FONT);
        addToWatchlistButton.setFocusPainted(false);
        addToWatchlistButton.setBorder(new RoundedBorder(8));
        addToWatchlistButton.addActionListener(e -> {
            Movie existingMovie = movieDAO.getMovieByImdbID(movie.getImdbID());
            int movieId;
            if (existingMovie == null) {
                movieId = movieDAO.addMovie(movie);
            } else {
                movieId = existingMovie.getId();
            }
            watchlistDAO.addToWatchlist(movieId, "To Watch");
            JOptionPane.showMessageDialog(this, "Movie added to watchlist!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(addToWatchlistButton);
        return buttonPanel;
    }

    private void populateWatchlistTable() {
        watchlistPanel.removeAll();

        java.util.List<com.moviemanager.model.WatchlistItem> watchlistItems = watchlistDAO.getWatchlist();
        String[] columnNames = {"Title", "Year", "Genre", "Rating", "Status", "Remove"};

        Object[][] data = new Object[watchlistItems.size()][6];
        for (int i = 0; i < watchlistItems.size(); i++) {
            com.moviemanager.model.WatchlistItem item = watchlistItems.get(i);
            Movie movie = movieDAO.getMovieById(item.getMovieId());
            if (movie != null) {
                data[i][0] = movie.getTitle();
                data[i][1] = movie.getYear();
                data[i][2] = movie.getGenre();
                data[i][3] = movie.getImdbRating();
                data[i][4] = item.getStatus();
                data[i][5] = "Remove";
            }
        }

        JTable watchlistTable = new JTable(data, columnNames);
        watchlistTable.getColumn("Remove").setCellRenderer(new ButtonRenderer());
        watchlistTable.getColumn("Remove").setCellEditor(new ButtonEditor(new JCheckBox(), watchlistItems, this));

        JScrollPane scrollPane = new JScrollPane(watchlistTable);
        watchlistPanel.add(scrollPane, BorderLayout.CENTER);

        watchlistPanel.revalidate();
        watchlistPanel.repaint();
    }

    public void removeWatchlistItem(int watchlistItemId) {
        watchlistDAO.removeFromWatchlist(watchlistItemId);
        populateWatchlistTable();
    }

}
