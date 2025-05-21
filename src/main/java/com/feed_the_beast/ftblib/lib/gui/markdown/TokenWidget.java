package com.feed_the_beast.ftblib.lib.gui.markdown;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import net.minecraft.client.renderer.GlStateManager;

public class TokenWidget extends BaseMarkdownElementWidget {
    protected String text;
    protected int textFlags = 0;

    public TokenWidget(Panel panel, String text) {
        super(panel);
        this.text = text;

        Theme theme = panel.getGui().getTheme();
        this.setWidth(theme.getStringWidth(text));
        this.setHeight(theme.getFontHeight());
    }

    public TokenWidget addFlags(int flags) {
        this.textFlags |= flags;
        return this;
    }

    public String getText() {
        return text;
    }

    @Override
    public void draw(Theme theme, int x, int y, int w, int h) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + posX, y + posY, 0);
        theme.drawString(text, 0, 0, textFlags);

        GlStateManager.popMatrix();
    }
}