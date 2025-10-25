package com.moviemanager.ui;

import com.moviemanager.api.TmdbAPI;
import com.moviemanager.model.Movie;
import com.moviemanager.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TrendingPanel extends JPanel {

    private TmdbAPI tmdbAPI;
    private JPanel dailyMoviesPanel;
    private JPanel weeklyMoviesPanel;

    public TrendingPanel() {
        this.tmdbAPI = new TmdbAPI();
        setBackground(Theme.PRIMARY_BACKGROUND);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Daily Trending Panel
        JPanel dailyPanel = new JPanel(new BorderLayout());
        dailyPanel.setBackground(Theme.PRIMARY_BACKGROUND);
        JLabel dailyTitle = new JLabel("Trending Today :");
        dailyTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        dailyTitle.setForeground(Theme.PRIMARY_TEXT);
        dailyPanel.add(dailyTitle, BorderLayout.NORTH);
        dailyMoviesPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        dailyMoviesPanel.setBackground(Theme.PRIMARY_BACKGROUND);
        JScrollPane dailyScrollPane = new JScrollPane(dailyMoviesPanel);
        dailyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dailyScrollPane.setBorder(BorderFactory.createEmptyBorder());
        dailyPanel.add(dailyScrollPane, BorderLayout.CENTER);
        add(dailyPanel);

        // Weekly Trending Panel
        JPanel weeklyPanel = new JPanel(new BorderLayout());
        weeklyPanel.setBackground(Theme.PRIMARY_BACKGROUND);
        JLabel weeklyTitle = new JLabel("Trending This Week :");
        weeklyTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        weeklyTitle.setForeground(Theme.PRIMARY_TEXT);
        weeklyPanel.add(weeklyTitle, BorderLayout.NORTH);
        weeklyMoviesPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        weeklyMoviesPanel.setBackground(Theme.PRIMARY_BACKGROUND);
        JScrollPane weeklyScrollPane = new JScrollPane(weeklyMoviesPanel);
        weeklyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        weeklyScrollPane.setBorder(BorderFactory.createEmptyBorder());
        weeklyPanel.add(weeklyScrollPane, BorderLayout.CENTER);
        add(weeklyPanel);

        populateTrendingMovies();
    }

    private void populateTrendingMovies() {
        // Show loading animation
        dailyMoviesPanel.removeAll();
        weeklyMoviesPanel.removeAll();
        dailyMoviesPanel.setLayout(new GridBagLayout());
        weeklyMoviesPanel.setLayout(new GridBagLayout());
        JProgressBar dailyProgressBar = new JProgressBar();
        dailyProgressBar.setIndeterminate(true);
        dailyMoviesPanel.add(dailyProgressBar);
        JProgressBar weeklyProgressBar = new JProgressBar();
        weeklyProgressBar.setIndeterminate(true);
        weeklyMoviesPanel.add(weeklyProgressBar);
        dailyMoviesPanel.revalidate();
        dailyMoviesPanel.repaint();
        weeklyMoviesPanel.revalidate();
        weeklyMoviesPanel.repaint();

        SwingWorker<List<List<Movie>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<List<Movie>> doInBackground() throws Exception {
                List<List<Movie>> trendingMovies = new ArrayList<>();
                trendingMovies.add(tmdbAPI.getDailyTrendingMovies());
                trendingMovies.add(tmdbAPI.getWeeklyTrendingMovies());
                return trendingMovies;
            }

            @Override
            protected void done() {
                try {
                    List<List<Movie>> movies = get();
                    List<Movie> dailyMovies = movies.get(0);
                    List<Movie> weeklyMovies = movies.get(1);

                    dailyMoviesPanel.removeAll();
                    dailyMoviesPanel.setLayout(new GridLayout(0, 5, 10, 10));
                    for (int i = 0; i < dailyMovies.size(); i++) {
                        dailyMoviesPanel.add(createTrendingCardPanel(dailyMovies.get(i), i + 1));
                    }
                    dailyMoviesPanel.revalidate();
                    dailyMoviesPanel.repaint();

                    weeklyMoviesPanel.removeAll();
                    weeklyMoviesPanel.setLayout(new GridLayout(0, 5, 10, 10));
                    for (int i = 0; i < weeklyMovies.size(); i++) {
                        weeklyMoviesPanel.add(createTrendingCardPanel(weeklyMovies.get(i), i + 1));
                    }
                    weeklyMoviesPanel.revalidate();
                    weeklyMoviesPanel.repaint();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private JPanel createTrendingCardPanel(Movie movie, int rank) {
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setPreferredSize(new Dimension(140, 230));
        cardPanel.setBackground(Theme.COMPONENT_BACKGROUND);
        cardPanel.setBorder(new com.moviemanager.theme.RoundedBorder(10));
        cardPanel.setOpaque(false);

        // Poster with rank
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(140, 200));

        JLabel posterLabel = new JLabel();
        posterLabel.setBounds(0, 0, 140, 200);
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            try {
                URL posterUrl = new URL(movie.getPosterUrl());
                ImageIcon posterIcon = new ImageIcon(posterUrl);
                Image image = posterIcon.getImage();
                Image newimg = image.getScaledInstance(140, 200,  java.awt.Image.SCALE_SMOOTH);
                posterIcon = new ImageIcon(newimg);
                posterLabel.setIcon(posterIcon);
            } catch (Exception e) {
                e.printStackTrace();
                posterLabel.setText("No Poster");
            }
        } else {
            posterLabel.setText("No Poster");
        }
        layeredPane.add(posterLabel, JLayeredPane.DEFAULT_LAYER);

        // Rank number shadow
        JLabel rankLabelShadow = new JLabel(String.valueOf(rank));
        rankLabelShadow.setFont(new Font("SansSerif", Font.BOLD, 40));
        rankLabelShadow.setForeground(new Color(0, 0, 0, 100)); // Semi-transparent black
        rankLabelShadow.setBounds(7, 132, 100, 50); // Offset by 2px
        layeredPane.add(rankLabelShadow, JLayeredPane.PALETTE_LAYER);

        // Rank number
        JLabel rankLabel = new JLabel(String.valueOf(rank));
        rankLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        rankLabel.setForeground(Color.WHITE);
        rankLabel.setBounds(5, 130, 100, 50);
        layeredPane.add(rankLabel, JLayeredPane.DRAG_LAYER);

        cardPanel.add(layeredPane, BorderLayout.CENTER);

        // Title
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(Theme.PRIMARY_TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(titleLabel, BorderLayout.SOUTH);

        return cardPanel;
    }
}