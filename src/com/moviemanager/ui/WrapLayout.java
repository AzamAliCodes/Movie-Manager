package com.moviemanager.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A FlowLayout subclass that provides a more consistent wrapping behavior.
 * It correctly calculates the preferred size of the container, allowing it to
 * wrap components to the next row when the width of the container is exceeded.
 * This addresses a common issue with the standard FlowLayout where the preferred
 * size does not account for wrapping.
 *
 * @author Rob Camick (from https://tips4java.wordpress.com/2008/11/06/wrap-layout/)
 */
public class WrapLayout extends FlowLayout {
    /**
     * Constructs a new WrapLayout with a left alignment and a default 5-unit
     * horizontal and vertical gap.
     */
    public WrapLayout() {
        super();
    }

    /**
     * Constructs a new WrapLayout with the specified alignment and a default
     * 5-unit horizontal and vertical gap.
     * The value of the alignment argument must be one of
     * {@code WrapLayout.LEFT}, {@code WrapLayout.RIGHT},
     * or {@code WrapLayout.CENTER}.
     *
     * @param align the alignment value
     */
    public WrapLayout(int align) {
        super(align);
    }

    /**
     * Creates a new flow layout manager with the indicated alignment
     * and the indicated horizontal and vertical gaps.
     * <p>
     * The value of the alignment argument must be one of
     * {@code WrapLayout.LEFT}, {@code WrapLayout.RIGHT},
     * or {@code WrapLayout.CENTER}.
     *
     * @param align the alignment value
     * @param hgap the horizontal gap between components
     * @param vgap the vertical gap between components
     */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * Returns the preferred dimensions for this layout given the visible
     * components in the specified target container.
     *
     * @param target the component which needs to be laid out
     * @return the preferred dimensions to lay out the subcomponents of the
     *         specified container
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    /**
     * Returns the minimum dimensions needed to layout the visible
     * components contained in the specified target container.
     *
     * @param target the component which needs to be laid out
     * @return the minimum dimensions to lay out the subcomponents of the
     *         specified container
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    /**
     * Calculates the dimensions for the layout.
     *
     * @param target the component which needs to be laid out
     * @param preferred true to calculate preferred size, false for minimum size
     * @return the dimensions to lay out the subcomponents of the specified container
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            // Each row must fit with the maximum available width.
            // The preferred height will be the sum of the preferred heights of each row.

            int targetWidth = target.getSize().width;

            // When the container is not yet displayed, the targetWidth will be 0.
            // In this case, we will use the maximum width of the screen.
            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsets = insets.left + insets.right;
            int verticalInsets = insets.top + insets.bottom;

            int maxWidth = targetWidth - horizontalInsets + hgap;
            int width = 0;
            int height = 0;

            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                    if (rowWidth + d.width + hgap > maxWidth) {
                        // Start a new row
                        width = Math.max(width, rowWidth);
                        height += rowHeight + vgap;
                        rowWidth = d.width + hgap;
                        rowHeight = d.height;
                    } else {
                        // Add to the current row
                        rowWidth += d.width + hgap;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
            }

            width = Math.max(width, rowWidth);
            height += rowHeight;

            return new Dimension(width + horizontalInsets, height + verticalInsets);
        }
    }
}