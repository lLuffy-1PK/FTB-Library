package com.feed_the_beast.ftblib.lib.util.markdown;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.markdown.MarkdownPanel;
/**
 * Builder for creating and customizing MarkdownPanel
 */
public class MarkdownBuilder {
    private final Panel parentPanel;
    private final String id;
    private int maxWidth = 0;
    private int imageMargin = 5;
    private String markdownText;

    /**
     * Creates a new builder for MarkdownPanel
     *
     * @param parentPanel parent panel
     * @param id panel id
     */
    public MarkdownBuilder(Panel parentPanel, String id) {
        this.parentPanel = parentPanel;
        this.id = id;
    }

    /**
     * Sets the maximum width.
     *
     * @param width maximum width
     * @return builder for call chain
     */
    public MarkdownBuilder maxWidth(int width) {
        this.maxWidth = width;
        return this;
    }
    /**
     * Sets padding for images
     *
     * @param margin padding
     * @return builder for call chain
     */
    public MarkdownBuilder imageMargin(int margin) {
        this.imageMargin = margin;
        return this;
    }

    /**
     * Set Markdown text
     *
     * @param text Markdown text
     * @return builder for call chain
     */
    public MarkdownBuilder markdown(String text) {
        this.markdownText = text;
        return this;
    }

    /**
     * Creates and configures MarkdownPanel
     *
     * @return configured MarkdownPanel
     */
    public MarkdownPanel build() {
        MarkdownPanel panel = new MarkdownPanel(parentPanel, id)
                .setMaxWidth(maxWidth)
                .setImageMargin(imageMargin);

        if (markdownText != null) {
            panel.setText(markdownText);
        }

        return panel;
    }
}