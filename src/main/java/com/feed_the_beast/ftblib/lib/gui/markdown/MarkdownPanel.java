package com.feed_the_beast.ftblib.lib.gui.markdown;

import com.feed_the_beast.ftblib.lib.gui.BlankPanel;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownPanel extends BlankPanel {
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)(\\{w=(\\d+)\\s*h=(\\d+)\\})?");
    private static final Pattern ALIGNMENT_PATTERN = Pattern.compile("^\\[\\[(center|left|right)\\]\\](.*)$");
    private static final Pattern BLOCK_PATTERN = Pattern.compile("<(center|left|right)>(.*?)</\\1>", Pattern.DOTALL);
    private static final Pattern LINK_PATTERN = Pattern.compile("(?<!!)\\[(.*?)\\]\\((.*?)\\)");

    private int maxWidth = 0;
    public int imageMargin = 5;

    public MarkdownPanel(Panel panel, String id) {
        super(panel, id);
    }

    public MarkdownPanel setImageMargin(int imageMargin) {
        this.imageMargin = imageMargin;
        return this;
    }

    public MarkdownPanel setMaxWidth(int width) {
        this.maxWidth = width;
        return this;
    }

    public MarkdownPanel setText(String markdown) {
        this.widgets.clear();

        int contentWidth = maxWidth > 0 ? maxWidth : width;
        Theme theme = getGui().getTheme();

        if (markdown == null || markdown.trim().isEmpty()) {
            return resize(theme);
        }

        markdown = markdown.trim();
        String processedMarkdown = processBlockAlignments(markdown);
        renderMarkdownContent(processedMarkdown, contentWidth, theme);

        return resize(theme);
    }

    private String processBlockAlignments(String markdown) {
        Matcher blockMatcher = BLOCK_PATTERN.matcher(markdown);
        StringBuffer processedMarkdown = new StringBuffer();

        while (blockMatcher.find()) {
            String alignment = blockMatcher.group(1);
            String content = blockMatcher.group(2);

            String[] contentLines = content.split("\n");
            StringBuilder markedContent = new StringBuilder();

            for (String line : contentLines) {
                if (!line.trim().isEmpty()) {
                    markedContent.append("[[").append(alignment).append("]]").append(line).append("\n");
                } else {
                    markedContent.append("\n");
                }
            }

            blockMatcher.appendReplacement(processedMarkdown, markedContent.toString());
        }
        blockMatcher.appendTail(processedMarkdown);

        return processedMarkdown.toString();
    }

    private void renderMarkdownContent(String processedMarkdown, int contentWidth, Theme theme) {
        String[] lines = processedMarkdown.split("\n");
        int currentY = 0;

        for (String line : lines) {
            LineInfo lineInfo = extractLineAlignment(line);

            if (isImageLine(lineInfo.text)) {
                currentY = processImageLine(lineInfo.text, lineInfo.alignment, contentWidth, currentY);
            } else if (!lineInfo.text.trim().isEmpty()) {
                currentY = processTextLine(lineInfo.text, lineInfo.alignment, contentWidth, currentY, theme);
            } else {
                currentY += theme.getFontHeight();
            }
        }
    }

    private LineInfo extractLineAlignment(String line) {
        Alignment alignment = Alignment.LEFT;
        String text = line;

        Matcher alignmentMatcher = ALIGNMENT_PATTERN.matcher(line);
        if (alignmentMatcher.matches()) {
            String alignmentType = alignmentMatcher.group(1);
            text = alignmentMatcher.group(2);

            switch (alignmentType) {
                case "center":
                    alignment = Alignment.CENTER;
                    break;
                case "right":
                    alignment = Alignment.RIGHT;
                    break;
                default:
                    alignment = Alignment.LEFT;
                    break;
            }
        }

        return new LineInfo(text, alignment);
    }

    private boolean isImageLine(String line) {
        return IMAGE_PATTERN.matcher(line).matches();
    }

    private int processImageLine(String line, Alignment alignment, int contentWidth, int currentY) {
        Matcher imageMatcher = IMAGE_PATTERN.matcher(line);
        if (imageMatcher.matches()) {
            String altText = imageMatcher.group(1);
            String url = imageMatcher.group(2);

            int width = -1;
            int height = -1;

            if (imageMatcher.group(3) != null) {
                try {
                    width = Integer.parseInt(imageMatcher.group(4));
                    height = Integer.parseInt(imageMatcher.group(5));
                } catch (NumberFormatException e) {
                    // ;3
                }
            }

            ImageWidget imageWidget = new ImageWidget(this, altText, url);
            imageWidget.setAlignment(alignment);

            if (width > 0 && height > 0) {
                imageWidget.setWidth(width);
                imageWidget.setHeight(height);
            }

            imageWidget.align(contentWidth);
            imageWidget.setY(currentY);

            add(imageWidget);
            return currentY + imageWidget.getHeight() + imageMargin;
        }

        return currentY;
    }

    private int processTextLine(String text, Alignment alignment, int contentWidth, int currentY, Theme theme) {
        // Oh no
        String textForFormatting = preprocessLinksForWidth(text);

        List<String> formattedLines = maxWidth > 0
                ? theme.listFormattedStringToWidth(textForFormatting, maxWidth)
                : Collections.singletonList(textForFormatting);

        int newY = currentY;

        for (String formattedLine : formattedLines) {
            // Anyway
            String originalLine = restoreLinksInLine(formattedLine, text);

            LineWidget lineWidget = new LineWidget(this, originalLine);
            lineWidget.setAlignment(alignment);
            lineWidget.align(contentWidth);
            lineWidget.setY(newY);

            add(lineWidget);
            newY += theme.getFontHeight();
        }

        return newY;
    }

    private String preprocessLinksForWidth(String text) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = LINK_PATTERN.matcher(text);

        while (matcher.find()) {
            String linkText = matcher.group(1);
            matcher.appendReplacement(result, linkText);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private String restoreLinksInLine(String formattedLine, String originalText) {
        Map<String, String> linkTextToFullLink = new HashMap<>();
        Matcher linkMatcher = LINK_PATTERN.matcher(originalText);

        while (linkMatcher.find()) {
            String linkText = linkMatcher.group(1);
            String fullLink = linkMatcher.group(0); // [text](link)
            linkTextToFullLink.put(linkText, fullLink);
        }

        String result = formattedLine;
        for (Map.Entry<String, String> entry : linkTextToFullLink.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public MarkdownPanel resize(Theme theme) {
        if (widgets.isEmpty()) {
            setWidth(0);
            setHeight(0);
            return this;
        }

        int maxLineWidth = 0;
        int maxY = 0;

        for (Widget widget : widgets) {
            maxLineWidth = Math.max(maxLineWidth, widget.getX() + widget.width + imageMargin);
            maxY = Math.max(maxY, widget.getY() + widget.height + imageMargin);
        }

        setWidth(maxWidth > 0 ? maxWidth : maxLineWidth);
        setHeight(maxY);

        return this;
    }

    @Override
    public void draw(Theme theme, int x, int y, int w, int h) {
        parent.drawBackground(theme, x, y, w, h);

        if (widgets.isEmpty()) {
            return;
        }

        for (Widget widget : widgets) {
            widget.draw(theme, x, y, w, h);
        }
    }

    private static class LineInfo {
        final String text;
        final Alignment alignment;

        LineInfo(String text, Alignment alignment) {
            this.text = text;
            this.alignment = alignment;
        }
    }
}