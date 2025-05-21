package com.feed_the_beast.ftblib.lib.gui.markdown;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import static com.feed_the_beast.ftblib.lib.gui.GuiHelper.drawTexturedRect;

public class ImageWidget extends BaseMarkdownElementWidget {
    public final String altText;
    public final String url;
    public ResourceLocation resourceLocation;
    public boolean isLoaded;

    public ImageWidget(Panel panel, String altText, String url) {
        super(panel);
        this.altText = altText;
        this.setWidth(32);
        this.setHeight(32);
        this.isLoaded = false;
        this.url = url;

        if (this.url.startsWith("minecraft:")) {
            this.resourceLocation = new ResourceLocation(this.url);
            this.isLoaded = true;
        }
    }

    @Override
    public void draw(Theme theme, int x, int y, int w, int h) {
        int imgX = x + posX;
        int imgY = y + posY;

        if (isLoaded) {
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            textureManager.bindTexture(resourceLocation);
            drawTexturedRect(imgX, imgY, width, height, Color4I.WHITE, 0.0, 0.0, 1.0, 1.0);
            GlStateManager.popMatrix();
        } else {
            Color4I.GRAY.withAlpha(80).draw(imgX, imgY, width, height);
            theme.drawString(altText, imgX + 2, imgY + height / 2 - 4, Color4I.WHITE, 0);
        }
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
}
