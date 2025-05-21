package com.feed_the_beast.ftblib.lib.gui.markdown;

public interface IMarkdownElement {
    int getX();
    int getY();
    void setX(int x);
    void setY(int y);
    int getWidth();
    int getHeight();
    Alignment getAlignment();
    void setAlignment(Alignment alignment);

}