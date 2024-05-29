package com.feed_the_beast.ftblib.lib.io;

import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.gson.JsonElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author LatvianModder
 */
public class FileDataReader extends DataReader {
    public final File file;

    FileDataReader(File f) {
        file = f;
    }

    public String toString() {
        return file.getAbsolutePath();
    }

    @Override
    public boolean canRead() {
        return file.exists() && file.isFile() && file.canRead();
    }

    private void checkFile() throws Exception {
        if (!canRead()) {
            throw new FileNotFoundException("File is not found/readable!");
        }
    }

    @Override
    public String string() throws Exception {
        return string(1024);
    }

    @Override
    public String string(int bufferSize) throws Exception {
        checkFile();

        try (FileInputStream stream = new FileInputStream(file)) {
            return readStringFromStream(stream, bufferSize);
        }
    }

    @Override
    public List<String> stringList() throws Exception {
        checkFile();

        try (FileInputStream stream = new FileInputStream(file)) {
            return readStringListFromStream(stream);
        }
    }

    @Override
    public JsonElement json() throws Exception {
        checkFile();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            return JsonUtils.parse(reader);
        }
    }

    @Override
    public BufferedImage image() throws Exception {
        checkFile();
        return ImageIO.read(file);
    }
}