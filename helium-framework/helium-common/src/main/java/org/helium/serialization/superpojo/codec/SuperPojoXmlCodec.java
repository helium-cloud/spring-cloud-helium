package org.helium.serialization.superpojo.codec;

import com.feinno.superpojo.SuperPojoManager;

import java.io.IOException;

/**
 * 使用SuperPojo进行XML序列化及反序列化的Codec
 * <p>
 * Created by Coral on 2015/5/7.
 */
public class SuperPojoXmlCodec extends AbstractCodec {

    public SuperPojoXmlCodec(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public byte[] encode(Object obj) throws IOException {
        return SuperPojoManager.toXmlByteArray(obj);
    }

    @Override
    public <E> E decode(byte[] buffer) throws IOException {
        String s = new String(buffer);
        //noinspection unchecked
        return (E) SuperPojoManager.parseXmlFrom(s, clazz);
    }
}
