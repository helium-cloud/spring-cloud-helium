package com.feinno.superpojo;

import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.io.*;
import com.feinno.superpojo.util.ClassUtil;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.*;

/**
 * 序列化管理器，提供通用方法以及支持辅助类的生成与缓存
 *
 * @author Lv.Mingwei
 */
public class SuperPojoManager {

    /**
     * 存储SuperPojo的BuilderFactory的仓库，当本地缓存中找不到BuilderFactory时，向仓库中取用<br>
     * 仓库采用log4j方式，使用静态绑定的方式，将仓库实现类放置于com.feinno.superpojo.SuperPojoManager路径下
     */
    private static IRepository repository = null;

    private static final IRepository REPOSITORY_DEFAULT = new DefaultRepository();

    /**
     * 这里保存了所有继承自SuperPojo的BuilderFactory
     */
    private static final Map<String, BuilderFactory> BUILDER_FACTORY_MAP = Collections
            .synchronizedMap(new HashMap<String, BuilderFactory>());

//	private static final Map<Class<?>, BuilderFactory> BUILDER_FACTORY_MAP = Collections
//			.synchronizedMap(new HashMap<Class<?>, BuilderFactory>());

    static {
        try {
            @SuppressWarnings("unchecked")
            Class<IRepository> clazz = (Class<IRepository>) Class.forName("com.feinno.superpojo.SuperPojoRepository");
            repository = clazz.newInstance();
        } catch (ClassNotFoundException e) {
            repository = new DefaultRepository();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void writePbTo(T t, OutputStream output) {
        try {
            Builder<T> builder = getSuperPojoBuilder(t);
            CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(output);
            builder.writePbTo(codedOutputStream);
            codedOutputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void writeXmlTo(T t, OutputStream output) {
        try {
            Builder<T> builder = getSuperPojoBuilder(t);
            Entity entity = t.getClass().getAnnotation(Entity.class);
            XmlOutputStream xmlOutputStream = XmlOutputStream.newInstance(output, entity != null);
            builder.writeXmlTo(xmlOutputStream);
            xmlOutputStream.flush();
            xmlOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> byte[] toPbByteArray(T t) {
        try {
            Builder<T> builder = getSuperPojoBuilder(t);
            final byte[] result = new byte[builder.getSerializedSize()];
            final CodedOutputStream output = CodedOutputStream.newInstance(result);
            builder.writePbTo(output);
            output.checkNoSpaceLeft();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将List序列化成符合ProtoBuf格式的byte
     *
     * @param args
     * @return
     * @throws IOException
     */
    public static <T extends Object> byte[] toPbByteArray(List<T> args, Class<T> genericsClazz) throws IOException {
        try {
            Builder<List<T>> builder = getSuperPojoBuilder(args, genericsClazz);
            final byte[] result = new byte[builder.getSerializedSize()];
            final CodedOutputStream output = CodedOutputStream.newInstance(result);
            builder.writePbTo(output);
            output.checkNoSpaceLeft();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将List序列化成符合ProtoBuf格式的byte
     *
     * @param args
     * @return
     * @throws IOException
     */
    public static <K extends Object, V extends Object> byte[] toPbByteArray(Map<K, V> args, Class<K> keyClazz,
                                                                            Class<V> valueClazz) throws IOException {
        try {
            Builder<Map<K, V>> builder = getSuperPojoBuilder(args, keyClazz, valueClazz);
            final byte[] result = new byte[builder.getSerializedSize()];
            final CodedOutputStream output = CodedOutputStream.newInstance(result);
            builder.writePbTo(output);
            output.checkNoSpaceLeft();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parsePbFrom(InputStream input, Class<T> clazz) {
        //noinspection unchecked
        return parsePbFrom(input, (T) ClassUtil.newClassInstance(clazz));
    }

    public static <T> T parsePbFrom(InputStream input, T t) {
        try {
            Builder<T> builder = getSuperPojoBuilder(t);
            builder.parsePbFrom(CodedInputStream.newInstance(input));
            return builder.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parsePbFrom(byte[] buffert, Class<T> clazz) {
        //noinspection unchecked
        return parsePbFrom(buffert, (T) ClassUtil.newClassInstance(clazz));
    }

    public static <T> T parsePbFrom(byte[] buffer, T t) {
        try {
            Builder<T> builder = getSuperPojoBuilder(t);
            builder.parsePbFrom(CodedInputStream.newInstance(buffer));
            return builder.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从byte数组中读出List
     *
     * @param buffer
     * @return
     * @throws IOException
     */
    public static <T extends Object> List<T> parsePbFrom(byte[] buffer, List<T> list, Class<T> genericsClazz)
            throws IOException {
        if (list == null) {
            list = new ArrayList<T>();
        }
        try {
            Builder<List<T>> builder = getSuperPojoBuilder(list, genericsClazz);
            builder.parsePbFrom(CodedInputStream.newInstance(buffer));
            return builder.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从byte数组中读出List
     *
     * @param buffer
     * @param map
     * @param keyClazz
     * @param valueClazz
     * @return
     * @throws IOException
     */
    public static <K extends Object, V extends Object> Map<K, V> parsePbFrom(byte[] buffer, Map<K, V> map,
                                                                             Class<K> keyClazz, Class<V> valueClazz) throws IOException {
        if (map == null) {
            map = new HashMap<K, V>();
        }
        try {
            Builder<Map<K, V>> builder = getSuperPojoBuilder(map, keyClazz, valueClazz);
            builder.parsePbFrom(CodedInputStream.newInstance(buffer));
            return builder.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> JsonObject toJsonObject(T t) {
        Builder<T> builder = getSuperPojoBuilder(t);
        return builder.toJsonObject();
    }

    /**
     * 将对象打印成Json格式
     *
     * @param args
     * @return
     * @throws IOException
     */
    public static <T extends Object> JsonObject toJsonObject(List<T> args, Class<T> genericsClazz) {
        Builder<List<T>> builder = getSuperPojoBuilder(args, genericsClazz);
        return builder.toJsonObject();
    }

    /**
     * 从json字符串中解析出并保存成需要的格式
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <E> E parseJsonFrom(String json, Class<E> clazz) {
        try {
            Builder<E> builder = getSuperPojoBuilder(ClassUtil.newClassInstance(clazz));
            builder.parseJsonFrom(new JsonInputStream(json));
            return builder.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> byte[] toXmlByteArray(T t) {
        try {
            Builder<T> builder = getSuperPojoBuilder(t);
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            Entity entity = t.getClass().getAnnotation(Entity.class);
            final XmlOutputStream output = XmlOutputStream.newInstance(byteOutput, entity != null);
            builder.writeXmlTo(output);
            output.close();
            return byteOutput.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将List序列化成符合ProtoBuf格式的byte
     *
     * @param args
     * @return
     * @throws IOException
     */
    public static <T extends Object> byte[] toXmlByteArray(List<T> args, Class<T> genericsClazz) throws IOException {
        try {
            Builder<List<T>> builder = getSuperPojoBuilder(args, genericsClazz);
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            Entity entity = genericsClazz.getClass().getAnnotation(Entity.class);
            final XmlOutputStream output = XmlOutputStream.newInstance(byteOutput, entity != null);
            builder.writeXmlTo(output);
            output.close();
            return byteOutput.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <K extends Object, V extends Object> byte[] toXmlByteArray(Map<K, V> args, Class<K> keyClazz,
                                                                             Class<V> valueClazz) throws IOException {
        try {
            Builder<Map<K, V>> builder = getSuperPojoBuilder(args, keyClazz, valueClazz);
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            Entity entity1 = keyClazz.getClass().getAnnotation(Entity.class);
            Entity entity2 = valueClazz.getClass().getAnnotation(Entity.class);
            final XmlOutputStream output = XmlOutputStream.newInstance(byteOutput, entity1 != null && entity2 != null);
            builder.writeXmlTo(output);
            output.close();
            return byteOutput.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseXmlFrom(InputStream input, Class<T> clazz) {
        //noinspection unchecked
        return parseXmlFrom(input, (T) ClassUtil.newClassInstance(clazz));
    }

    public static <T> T parseXmlFrom(InputStream input, T t) {
        try {
            Builder<T> builder = getSuperPojoBuilder(t);
            builder.parseXmlFrom(XmlInputStream.newInstance(input));
            return builder.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseXmlFrom(String xml, Class<T> clazz) {
        //noinspection unchecked
        return parseXmlFrom(xml, (T) ClassUtil.newClassInstance(clazz));
    }

    public static <T> T parseXmlFrom(String xml, T t) {
        try {
            Builder<T> builder = getSuperPojoBuilder(t);
            builder.parseXmlFrom(XmlInputStream.newInstance(new ByteArrayInputStream(xml.getBytes())));
            return builder.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从byte数组中读出List
     *
     * @param xml
     * @param list
     * @param genericsClazz
     * @return
     * @throws IOException
     */
    public static <T extends Object> List<T> parseXmlFrom(String xml, List<T> list, Class<T> genericsClazz)
            throws IOException {
        if (list == null) {
            list = new ArrayList<T>();
        }
        try {
            Builder<List<T>> builder = getSuperPojoBuilder(list, genericsClazz);
            builder.parseXmlFrom(XmlInputStream.newInstance(new ByteArrayInputStream(xml.getBytes())));
            return builder.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从byte数组中读出List
     *
     * @param xml
     * @param map
     * @param keyClazz
     * @param valueClass
     * @return
     * @throws IOException
     */
    public static <K extends Object, V extends Object> Map<K, V> parseXmlFrom(String xml, Map<K, V> map,
                                                                              Class<K> keyClazz, Class<V> valueClass) throws IOException {
        if (map == null) {
            map = new HashMap<K, V>();
        }
        try {
            Builder<Map<K, V>> builder = getSuperPojoBuilder(map, keyClazz, valueClass);
            builder.parseXmlFrom(XmlInputStream.newInstance(new ByteArrayInputStream(xml.getBytes())));
            return builder.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SuperPojoManager() {
    }

    /**
     * 获得序列实体对应的辅助类
     *
     * @param t
     * @param genericClass
     * @return
     */
    public static <T> Builder<T> getSuperPojoBuilder(T t, Class<?>... genericClass) {
        String key = getSuperPojoBuilderKey(t, genericClass);
        BuilderFactory factory = BUILDER_FACTORY_MAP.get(key);
        if (factory == null) {
            synchronized (BUILDER_FACTORY_MAP) {
                if (!BUILDER_FACTORY_MAP.containsKey(key)) {
                    factory = REPOSITORY_DEFAULT.getBuilderFactory(t.getClass(), genericClass);
                    if (factory != null) {
                        BUILDER_FACTORY_MAP.put(key, factory);
                    } else if (factory == null && repository != null) {
                        factory = repository.getBuilderFactory(t.getClass(), genericClass);
                        BUILDER_FACTORY_MAP.put(key, factory);
                    } else {
                        throw new RuntimeException(String.format("Not found %s SuperPojo BuildFactory.", t.getClass()));
                    }
                } else {
                    factory = BUILDER_FACTORY_MAP.get(key);
                }
            }
        }
        return factory.newBuilder(t);
    }

    /**
     * 获得序列实体的唯一Key
     *
     * @param t
     * @param genericClass
     * @return
     */
    private static <T> String getSuperPojoBuilderKey(T t, Class<?>... genericClass) {
        String retval = t.getClass().getClassLoader() + t.getClass().toString();
        if (genericClass != null) {
            for (Class<?> clazz : genericClass) {
                retval += clazz.toString();
            }
        }
        return retval;
    }

}
