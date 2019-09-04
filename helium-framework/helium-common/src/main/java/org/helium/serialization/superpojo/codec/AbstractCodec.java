package org.helium.serialization.superpojo.codec;

import org.helium.serialization.Codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 基础Codec，实现了一部分跟序列化框架无关的功能
 * <p/>
 * Created by Coral on 2015/5/7.
 */
public abstract class AbstractCodec implements Codec {
    Class<?> clazz;

    public AbstractCodec(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void encode(Object obj, OutputStream stream) throws IOException {
        byte[] buffer = this.encode(obj);
        stream.write(buffer, 0, buffer.length);
    }

    @Override
    public <E> E decode(InputStream stream, int length) throws IOException {
        byte[] buffer = new byte[length];
        //noinspection ResultOfMethodCallIgnored
        stream.read(buffer, 0, length);
        return this.decode(buffer);
    }

}
