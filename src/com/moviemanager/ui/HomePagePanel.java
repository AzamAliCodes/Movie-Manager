package com.moviemanager.ui;

import com.moviemanager.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class HomePagePanel extends JPanel {

    public HomePagePanel() {
        setBackground(Theme.PRIMARY_BACKGROUND);
        setLayout(new GridBagLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Movie Manager!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        welcomeLabel.setForeground(Theme.PRIMARY_TEXT);
        add(welcomeLabel);
    }
}
