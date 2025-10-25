package com.moviemanager.ui;

import com.moviemanager.api.LogoAPI;
import com.moviemanager.api.MovieAPI;
import com.moviemanager.api.TmdbAPI;
import com.moviemanager.api.WatchmodeAPI;
import com.moviemanager.dao.MovieDAO;
import com.moviemanager.dao.WatchlistDAO;
import com.moviemanager.model.Movie;
import com.moviemanager.model.StreamingInfo;
import com.moviemanager.model.WatchlistItem;
import com.moviemanager.theme.Theme;
import com.moviemanager.theme.RoundedBorder;
import java.awt.FlowLayout;
import com.moviemanager.ui.WrapLayout;
import com.moviemanager.ui.TrendingPanel;

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
    private LogoAPI logoAPI;

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
        this.logoAPI = new LogoAPI();

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

        // Trending Panel
        TrendingPanel trendingPanel = new TrendingPanel();
        tabbedPane.addTab("Trending", trendingPanel);

        // Watchlist Panel
        watchlistPanel = new JPanel(new BorderLayout());
        watchlistPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        tabbedPane.addTab("Watchlist", watchlistPanel);

        tabbedPane.setSelectedIndex(0); // Set Home tab as default

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2) { // Watchlist tab
                populateWatchlist();
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
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(Theme.PRIMARY_TEXT);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel subHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        subHeaderPanel.setBackground(Theme.SECONDARY_BACKGROUND);

        JLabel yearLabel = new JLabel("📅 " + movie.getYear());
        yearLabel.setForeground(Theme.SECONDARY_TEXT);
        yearLabel.setFont(Theme.PLAIN_FONT);
        subHeaderPanel.add(yearLabel);

        JLabel genreLabel = new JLabel("🎬 " + movie.getGenre());
        genreLabel.setForeground(Theme.SECONDARY_TEXT);
        genreLabel.setFont(Theme.PLAIN_FONT);
        subHeaderPanel.add(genreLabel);

        JLabel ratingLabel = new JLabel("⭐ " + movie.getImdbRating());
        ratingLabel.setForeground(Theme.SECONDARY_TEXT);
        ratingLabel.setFont(Theme.PLAIN_FONT);
        subHeaderPanel.add(ratingLabel);

        headerPanel.add(subHeaderPanel, BorderLayout.CENTER);

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
        JPanel posterPanel = new JPanel(new BorderLayout());
        posterPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        posterPanel.setBorder(BorderFactory.createEmptyBorder());
        posterPanel.setOpaque(false);

        JLabel posterLabel = new JLabel();
        posterLabel.setHorizontalAlignment(JLabel.CENTER);
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            try {
                URL posterUrl = new URL(movie.getPosterUrl());
                ImageIcon posterIcon = new ImageIcon(posterUrl);
                Image image = posterIcon.getImage();
                Image newimg = image.getScaledInstance(200, 300,  java.awt.Image.SCALE_SMOOTH);
                posterIcon = new ImageIcon(newimg);
                posterLabel.setIcon(posterIcon);
            } catch (Exception e) {
                e.printStackTrace();
                posterLabel.setText("No Poster Available");
                posterLabel.setForeground(Theme.SECONDARY_TEXT);
            }
        } else {
            posterLabel.setText("No Poster Available");
            posterLabel.setForeground(Theme.SECONDARY_TEXT);
        }
        posterPanel.add(posterLabel, BorderLayout.CENTER);
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
        detailsGbc.weighty = 1;
        detailsGbc.fill = GridBagConstraints.BOTH;

        JPanel plotPanel = new JPanel(new BorderLayout());
        plotPanel.setBackground(Theme.SECONDARY_BACKGROUND);

        JLabel plotTitleLabel = new JLabel("Plot");
        plotTitleLabel.setFont(Theme.BOLD_FONT);
        plotTitleLabel.setForeground(Theme.PRIMARY_TEXT);
        plotTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        plotPanel.add(plotTitleLabel, BorderLayout.NORTH);

        JTextArea plotArea = new JTextArea(movie.getPlot());
        plotArea.setLineWrap(true);
        plotArea.setWrapStyleWord(true);
        plotArea.setEditable(false);
        plotArea.setBackground(Theme.COMPONENT_BACKGROUND);
        plotArea.setForeground(Theme.SECONDARY_TEXT);
        plotArea.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane plotScrollPane = new JScrollPane(plotArea);
        plotScrollPane.setBorder(BorderFactory.createEmptyBorder());
        plotPanel.add(plotScrollPane, BorderLayout.CENTER);

        detailsPanel.add(plotPanel, detailsGbc);

        // Director
        detailsGbc.gridy = 1;
        detailsGbc.weighty = 0;
        detailsGbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel directorLabel = new JLabel("Director: " + movie.getDirector());
        directorLabel.setForeground(Theme.SECONDARY_TEXT);
        directorLabel.setFont(Theme.PLAIN_FONT);
        detailsPanel.add(directorLabel, detailsGbc);

        // Actors
        detailsGbc.gridy = 2;
        JLabel actorsLabel = new JLabel("Actors: " + movie.getActors());
        actorsLabel.setForeground(Theme.SECONDARY_TEXT);
        actorsLabel.setFont(Theme.PLAIN_FONT);
        detailsPanel.add(actorsLabel, detailsGbc);

        return detailsPanel;
    }

    private JPanel createStreamingPanel(Movie movie) {
        JPanel rightPanel = new JPanel(new GridLayout(0, 1, 0, 5)); // Add vertical gap
        rightPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(new RoundedBorder(10), "Available on", TitledBorder.LEFT, TitledBorder.TOP, Theme.PLAIN_FONT, Theme.SECONDARY_TEXT);
        rightPanel.setBorder(titledBorder);
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

                try {
                    String domain = new URL(info.getUrl()).getHost();
                    String logoUrl = logoAPI.getLogoUrl(domain);
                    ImageIcon logoIcon = new ImageIcon(new URL(logoUrl));
                    if (logoIcon.getIconWidth() != -1 && logoIcon.getIconHeight() != -1) {
                        Image image = logoIcon.getImage();
                        Image newimg = image.getScaledInstance(32, 32,  java.awt.Image.SCALE_SMOOTH);
                        logoIcon = new ImageIcon(newimg);
                        streamingButton.setIcon(logoIcon);
                        streamingButton.setHorizontalTextPosition(SwingConstants.RIGHT);
                        streamingButton.setIconTextGap(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
            int movieId = movieDAO.addMovie(movie);
            if (watchlistDAO.addToWatchlist(movieId, "To Watch")) {
                JOptionPane.showMessageDialog(this, "Movie added to watchlist!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Movie is already in the watchlist.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(addToWatchlistButton);
        return buttonPanel;
    }

    private void populateWatchlist() {
        watchlistPanel.removeAll();
        watchlistPanel.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("My Watchlist");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Theme.PRIMARY_TEXT);
        headerPanel.add(titleLabel);
        watchlistPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Theme.SECONDARY_BACKGROUND);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        contentPanel.add(progressBar);
        watchlistPanel.add(contentPanel, BorderLayout.CENTER);

        watchlistPanel.revalidate();
        watchlistPanel.repaint();

        SwingWorker<List<WatchlistItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<WatchlistItem> doInBackground() throws Exception {
                return watchlistDAO.getWatchlist();
            }

            @Override
            protected void done() {
                try {
                    List<WatchlistItem> watchlistItems = get();
                    contentPanel.removeAll();
                    contentPanel.setLayout(new BorderLayout());

                    if (watchlistItems.isEmpty()) {
                        JLabel emptyLabel = new JLabel("Your watchlist is empty.");
                        emptyLabel.setFont(Theme.PLAIN_FONT);
                        emptyLabel.setForeground(Theme.SECONDARY_TEXT);
                        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        contentPanel.add(emptyLabel, BorderLayout.CENTER);
                    } else {
                        JPanel cardsPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // 0 rows, 2 columns, 10px hgap, 10px vgap
                        cardsPanel.setBackground(Theme.SECONDARY_BACKGROUND);
                        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                        for (WatchlistItem item : watchlistItems) {
                            cardsPanel.add(createWatchlistCardPanel(item));
                        }

                        JScrollPane scrollPane = new JScrollPane(cardsPanel);
                        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                        scrollPane.getViewport().setBackground(Theme.SECONDARY_BACKGROUND);
                        scrollPane.setBorder(BorderFactory.createEmptyBorder());
                        contentPanel.add(scrollPane, BorderLayout.CENTER);
                    }

                    contentPanel.revalidate();
                    contentPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private JPanel createWatchlistCardPanel(WatchlistItem item) {
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setPreferredSize(new Dimension(350, 170));
        cardPanel.setBackground(Theme.COMPONENT_BACKGROUND);
        cardPanel.setBorder(new RoundedBorder(10));
        cardPanel.setOpaque(false);

        Movie movie = item.getMovie();

        // Poster
        JLabel posterLabel = new JLabel();
        posterLabel.setPreferredSize(new Dimension(100, 150));
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            try {
                URL posterUrl = new URL(movie.getPosterUrl());
                ImageIcon posterIcon = new ImageIcon(posterUrl);
                Image image = posterIcon.getImage();
                Image newimg = image.getScaledInstance(100, 150,  java.awt.Image.SCALE_SMOOTH);
                posterIcon = new ImageIcon(newimg);
                posterLabel.setIcon(posterIcon);
            } catch (Exception e) {
                e.printStackTrace();
                posterLabel.setText("No Poster");
            }
        } else {
            posterLabel.setText("No Poster");
        }
        cardPanel.add(posterLabel, BorderLayout.WEST);

        // Details and Actions
        JPanel detailsActionsPanel = new JPanel(new BorderLayout());
        detailsActionsPanel.setBackground(Theme.COMPONENT_BACKGROUND);

        // Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Theme.COMPONENT_BACKGROUND);

        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Theme.PRIMARY_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(titleLabel);

        JLabel yearLabel = new JLabel("📅 " + movie.getYear());
        yearLabel.setFont(Theme.PLAIN_FONT);
        yearLabel.setForeground(Theme.SECONDARY_TEXT);
        yearLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(yearLabel);

        JLabel genreLabel = new JLabel("🎬 " + movie.getGenre());
        genreLabel.setFont(Theme.PLAIN_FONT);
        genreLabel.setForeground(Theme.SECONDARY_TEXT);
        genreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(genreLabel);

        JLabel ratingLabel = new JLabel("⭐ " + movie.getImdbRating());
        ratingLabel.setFont(Theme.PLAIN_FONT);
        ratingLabel.setForeground(Theme.SECONDARY_TEXT);
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(ratingLabel);

        detailsActionsPanel.add(detailsPanel, BorderLayout.CENTER);

        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(Theme.COMPONENT_BACKGROUND);

        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"To Watch", "Watched", "On Hold", "Dropped"});
        statusComboBox.setSelectedItem(item.getStatus());
        statusComboBox.addActionListener(e -> {
            String newStatus = (String) statusComboBox.getSelectedItem();
            if (!item.getStatus().equals(newStatus)) {
                watchlistDAO.updateWatchlistStatus(item.getId(), newStatus);
                item.setStatus(newStatus);
            }
        });
        actionsPanel.add(statusComboBox);

        JButton removeButton = new JButton("Remove");
        removeButton.setBackground(Theme.ACCENT_ORANGE);
        removeButton.setForeground(Theme.PRIMARY_TEXT);
        removeButton.addActionListener(e -> removeWatchlistItem(item.getId()));
        actionsPanel.add(removeButton);

        detailsActionsPanel.add(actionsPanel, BorderLayout.SOUTH);

        cardPanel.add(detailsActionsPanel, BorderLayout.CENTER);

        return cardPanel;
    }

    public void removeWatchlistItem(int watchlistItemId) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                watchlistDAO.removeFromWatchlist(watchlistItemId);
                return null;
            }

            @Override
            protected void done() {
                populateWatchlist();
            }
        };
        worker.execute();
    }

}
