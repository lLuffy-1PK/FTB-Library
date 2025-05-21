package com.feed_the_beast.ftblib.lib.gui.markdown;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;

import java.net.URI;

public class LinkTokenWidget extends TokenWidget implements GuiYesNoCallback {
    private final String url;
    Minecraft mc = Minecraft.getMinecraft();

    public LinkTokenWidget(Panel panel, String text, String url) {
        super(panel, text);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void draw(Theme theme, int x, int y, int w, int h) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + posX, y + posY, 0);

        Color4I currentColor = isMouseOver ? theme.getColorLinkHover() : theme.getColorLink();

        theme.drawString(text, 0, 0, currentColor, textFlags);

        GlStateManager.popMatrix();
    }

    @Override
    public boolean mousePressed(MouseButton button) {
        if (button.isLeft()) {
            openLink();
            return true;
        }
        return false;
    }

    private void openLink() {
        GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.url, 13, true);
        guiconfirmopenlink.disableSecurityWarning();
        mc.displayGuiScreen(guiconfirmopenlink);
    }

    public void confirmClicked(boolean result, int id)
    {
        if (id == 13)
        {
            if (result)
            {
                try
                {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop").invoke(null);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(this.url));
                }
                catch (Throwable ignored)
                {

                }
            }

            this.mc.displayGuiScreen(this.parent.getGui().getPrevScreen());
        }
    }
}