package org.helium.serialization.superpojo.codec;

import com.feinno.superpojo.SuperPojoManager;

import java.io.IOException;

/**
 * 使用SuperPojo进行Protobuf序列化及反序列化的Codec
 * <p/>
 * Created by Coral on 2015/5/7.
 */
public class SuperPojoProtobufCodec extends AbstractCodec {

    public SuperPojoProtobufCodec(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public byte[] encode(Object obj) throws IOException {
        return SuperPojoManager.toPbByteArray(obj);
    }

    @Override
    public <E> E decode(byte[] buffer) throws IOException {
        //noinspection unchecked
        return (E) SuperPojoManager.parsePbFrom(buffer, clazz);
    }

}
