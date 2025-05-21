package com.feed_the_beast.ftblib.lib.gui.markdown;

import com.feed_the_beast.ftblib.lib.gui.BlankPanel;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineWidget extends BlankPanel {
    private static final Pattern LINK_PATTERN = Pattern.compile("(?<!!)\\[(.*?)\\]\\((.*?)\\)");
    private Alignment alignment;
    public String text;
    private final List<BaseMarkdownElementWidget> tokens;

    public LineWidget(Panel panel, String text) {
        super(panel);
        this.text = text;
        this.tokens = new ArrayList<>();
        this.alignment = Alignment.LEFT;

        Theme theme = panel.getGui().getTheme();
        this.setHeight(theme.getFontHeight() + 1);

        parseText();
    }

    public void align(int contentWidth) {
        switch (alignment) {
            case LEFT:
                posX = 0;
                break;
            case CENTER:
                posX = (contentWidth - width) / 2;
                break;
            case RIGHT:
                posX = contentWidth - width;
                break;
        }
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    private void parseText() {
        if (text == null || text.isEmpty()) {
            return;
        }

        Theme theme = getGui().getTheme();

        // Find all links in the text
        Matcher matcher = LINK_PATTERN.matcher(text);
        List<LinkInfo> links = new ArrayList<>();

        while (matcher.find()) {
            String linkText = matcher.group(1);
            String url = matcher.group(2);
            links.add(new LinkInfo(matcher.start(), matcher.end(), linkText, url));
        }

        // If no links found, add the whole text as a single token
        if (links.isEmpty()) {
            TokenWidget token = new TokenWidget(this, text);
            tokens.add(token);
            setWidth(theme.getStringWidth(text));
            return;
        }

        // Process text with links
        int currentX = 0;
        int lastEnd = 0;

        for (LinkInfo link : links) {
            // Add normal text token before the link
            if (link.start > lastEnd) {
                String normalText = text.substring(lastEnd, link.start);
                TokenWidget normalToken = new TokenWidget(this, normalText);
                normalToken.setX(currentX);
                tokens.add(normalToken);
                currentX += normalToken.getWidth();
            }

            // Add link token
            LinkTokenWidget linkToken = new LinkTokenWidget(this, link.text, link.url);
            linkToken.setX(currentX);
            tokens.add(linkToken);
            currentX += linkToken.getWidth();

            lastEnd = link.end;
        }

        // Add remaining text after the last link
        if (lastEnd < text.length()) {
            String remainingText = text.substring(lastEnd);
            TokenWidget remainingToken = new TokenWidget(this, remainingText);
            remainingToken.setX(currentX);
            tokens.add(remainingToken);
            currentX += remainingToken.getWidth();
        }

        // Set the total width of the line
        setWidth(currentX);
        addWidgets();
    }

    @Override
    public void draw(Theme theme, int x, int y, int w, int h) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + posX, y + posY, 0);

        // Draw all tokens
        for (BaseMarkdownElementWidget token : tokens) {
            token.draw(theme, 0, 0, w, h);
        }
        GlStateManager.popMatrix();

    }

    @Override
    public void addWidgets() {
        widgets.clear();

        // Добавляем токены как дочерние виджеты
        for (BaseMarkdownElementWidget token : tokens) {
            add(token);
        }
    }

    /**
     * Helper class to store link information
     */
    private static class LinkInfo {
        int start;
        int end;
        String text;
        String url;

        LinkInfo(int start, int end, String text, String url) {
            this.start = start;
            this.end = end;
            this.text = text;
            this.url = url;
        }
    }
}