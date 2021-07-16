package org.helium.serialization.superpojo.codec;

import org.helium.superpojo.SuperPojoManager;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 使用SuperPojo进行JSON序列化及反序列化的Codec
 * <p/>
 * Created by Coral on 2015/5/7.
 */
public class SuperPojoJsonCodec extends AbstractCodec {

    public SuperPojoJsonCodec(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public byte[] encode(Object obj) throws IOException {
        String jsonObject = SuperPojoManager.toJsonString(obj);
        return jsonObject.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public <E> E decode(byte[] buffer) throws IOException {
        String s = new String(buffer);
        return (E) SuperPojoManager.jsonToObject(s, clazz);
    }
}
