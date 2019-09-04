package org.helium.serialization;

/**
 * <b>描述: </b>
 * 序列化编解码器工厂接口，用于从根据codename自动适配并获取对应的序列化编解码器
 * ，关于序列化编解码器可以参见 {@link Codec}
 *
 * @author Panying
 */
public interface CodecFactory {


    /**
     * 检查工厂类是否支持指定的名称的编码
     *
     * @param codecName
     * @return 如果支持则返回true否则返回false
     */
    Boolean checkSupport(String codecName);

    /**
     * 返回一个确定类型的序列化器, 如果不能处理则返回null
     *
     * @param clazz 待序列化的Java类型
     * @return 序列化器或null
     */
    Codec getCodec(Class<?> clazz, String codecName);
}
