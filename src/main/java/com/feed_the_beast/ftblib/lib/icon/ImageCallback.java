package com.feed_the_beast.ftblib.lib.icon;

import javafx.scene.image.Image;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface ImageCallback {
    void imageLoaded(boolean queued, @Nullable Image image);
}