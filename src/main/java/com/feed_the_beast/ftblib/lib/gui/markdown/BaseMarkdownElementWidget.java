package com.feed_the_beast.ftblib.lib.gui.markdown;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Widget;

public abstract class BaseMarkdownElementWidget extends Widget implements IMarkdownElement {
    protected Alignment alignment = Alignment.LEFT;

    public BaseMarkdownElementWidget(Panel panel) {
        super(panel);
    }

    @Override
    public Alignment getAlignment() {
        return this.alignment;
    }

    @Override
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}