package com.moviemanager.ui;

import com.moviemanager.api.TmdbAPI;
import com.moviemanager.model.TmdbShow;
import com.moviemanager.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

public class TrendingShowsPanel extends JPanel {

    private TmdbAPI tmdbAPI;
    private MainFrame mainFrame; // Reference to the main frame to open details

    public TrendingShowsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.tmdbAPI = new TmdbAPI();
        setLayout(new BorderLayout());
        setBackground(Theme.PRIMARY_BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        // Trending Lists
        JPanel trendingListsPanel = new JPanel();
        trendingListsPanel.setLayout(new BoxLayout(trendingListsPanel, BoxLayout.Y_AXIS));
        trendingListsPanel.setBackground(Theme.PRIMARY_BACKGROUND);

        trendingListsPanel.add(createTrendingList("Trending Today", "day"));
        trendingListsPanel.add(createTrendingList("Trending This Week", "week"));

        add(new JScrollPane(trendingListsPanel), BorderLayout.CENTER);
    }

    private JPanel createTrendingList(String title, String timeWindow) {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(Theme.PRIMARY_BACKGROUND);
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel listTitle = new JLabel(title);
        listTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        listTitle.setForeground(Theme.PRIMARY_TEXT);
        listPanel.add(listTitle, BorderLayout.NORTH);

        JPanel showsPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        showsPanel.setBackground(Theme.PRIMARY_BACKGROUND);

        List<TmdbShow> shows = tmdbAPI.getTrendingShows(timeWindow);
        if (shows != null && !shows.isEmpty()) {
            for (TmdbShow show : shows) {
                JPanel showCard = createShowCard(show);
                showsPanel.add(showCard);
            }
        } else {
            JLabel noShowsLabel = new JLabel("No trending shows available.");
            noShowsLabel.setForeground(Theme.SECONDARY_TEXT);
            showsPanel.add(noShowsLabel);
        }

        JScrollPane scrollPane = new JScrollPane(showsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Theme.PRIMARY_BACKGROUND);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        return listPanel;
    }

    private JPanel createShowCard(TmdbShow show) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.SECONDARY_BACKGROUND);
        card.setPreferredSize(new Dimension(150, 250));
        card.setBorder(BorderFactory.createLineBorder(Theme.COMPONENT_BACKGROUND, 1));

        JLabel posterLabel = new JLabel();
        posterLabel.setPreferredSize(new Dimension(150, 200));
        if (show.getPosterPath() != null) {
            try {
                URL imageUrl = new URL(tmdbAPI.getFullPosterPath(show.getPosterPath()));
                ImageIcon imageIcon = new ImageIcon(imageUrl);
                Image image = imageIcon.getImage();
                Image newimg = image.getScaledInstance(150, 200,  java.awt.Image.SCALE_SMOOTH);
                posterLabel.setIcon(new ImageIcon(newimg));
            } catch (Exception e) {
                e.printStackTrace();
                posterLabel.setText("No Poster");
                posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
                posterLabel.setForeground(Theme.SECONDARY_TEXT);
            }
        } else {
            posterLabel.setText("No Poster");
            posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
            posterLabel.setForeground(Theme.SECONDARY_TEXT);
        }
        card.add(posterLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(show.getTitle());
        titleLabel.setForeground(Theme.PRIMARY_TEXT);
        titleLabel.setFont(Theme.PLAIN_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(Theme.COMPONENT_BACKGROUND);
                // Optionally show a tooltip or a small overlay with info
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Theme.SECONDARY_BACKGROUND);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Open details panel
                // For now, just show a message
                JOptionPane.showMessageDialog(mainFrame, "Clicked on: " + show.getTitle(), "Show Details", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return card;
    }
}
