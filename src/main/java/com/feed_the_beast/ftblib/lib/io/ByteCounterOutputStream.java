package com.feed_the_beast.ftblib.lib.io;

import java.io.OutputStream;

/**
 * @author LatvianModder
 */
public class ByteCounterOutputStream extends OutputStream {
    private long size = 0L;

    @Override
    public void write(int b) {
        size++;
    }

    @Override
    public void write(byte[] b) {
        size += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) {
        size += len;
    }

    public long getSize() {
        return size;
    }
}