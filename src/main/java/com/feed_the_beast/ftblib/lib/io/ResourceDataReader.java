package com.feed_the_beast.ftblib.lib.io;

import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.gson.JsonElement;
import net.minecraft.client.resources.IResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ResourceDataReader extends DataReader {
    public final IResource resource;

    ResourceDataReader(IResource r) {
        resource = r;
    }

    public String toString() {
        return resource.getResourceLocation().toString();
    }

    @Override
    public String string(int bufferSize) throws Exception {
        return readStringFromStream(resource.getInputStream(), bufferSize);
    }

    @Override
    public List<String> stringList() throws Exception {
        return readStringListFromStream(resource.getInputStream());
    }

    @Override
    public JsonElement json() throws Exception {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return JsonUtils.parse(reader);
        }
    }

    @Override
    public BufferedImage image() throws Exception {
        return ImageIO.read(resource.getInputStream());
    }
}