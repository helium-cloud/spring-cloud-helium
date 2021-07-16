package org.helium.serialization.superpojo;

import org.helium.serialization.Codec;
import org.helium.serialization.CodecFactory;
import org.helium.serialization.superpojo.codec.SuperPojoJsonCodec;
import org.helium.serialization.superpojo.codec.SuperPojoProtobufCodec;

/**
 * SuperPojo Codec 工厂类，该工厂类实现{@link CodecFactory}, 根据给出的 class 和 codecName 自动初始化响应的编码器{@link Codec}实例
 * <p/>
 * Created by Coral on 2015/5/7.
 */
public class SuperPojoCodecFactory implements CodecFactory {

    /**
     * 检查该工厂是否能生成对应指定编码啊的编码器
     *
     * @param codecName 编码名称，如 json、xml、protobuf
     * @return 支持返回true
     */
    @Override
    public Boolean checkSupport(String codecName) {
        return SuperPojoSupportCode.valueOf(codecName) != null;
    }

    /**
     * 获取一个针对指定类及指定编码的编码器{@link Codec}实例
     *
     * @param clazz     待序列化的Java类型
     * @param codecName 编码名称，如 json、xml、protobuf
     * @return 编码器实例
     */
    @Override
    public Codec getCodec(Class<?> clazz, String codecName) {
        switch (SuperPojoSupportCode.valueOf(codecName)) {
            case json:
                return new SuperPojoJsonCodec(clazz);
            case protobuf:
                return new SuperPojoProtobufCodec(clazz);
        }
        return null;
    }
}
