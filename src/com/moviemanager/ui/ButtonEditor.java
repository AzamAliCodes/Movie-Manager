package com.moviemanager.ui;

import com.moviemanager.model.WatchlistItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ButtonEditor extends DefaultCellEditor {

    protected JButton button;
    private String label;
    private boolean isPushed;
    private List<WatchlistItem> watchlistItems;
    private MainFrame mainFrame;
    private JTable table;

    public ButtonEditor(JCheckBox checkBox, List<WatchlistItem> watchlistItems, MainFrame mainFrame) {
        super(checkBox);
        this.watchlistItems = watchlistItems;
        this.mainFrame = mainFrame;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1 && selectedRow < watchlistItems.size()) {
                WatchlistItem item = watchlistItems.get(selectedRow);
                mainFrame.removeWatchlistItem(item.getId());
            }
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}
