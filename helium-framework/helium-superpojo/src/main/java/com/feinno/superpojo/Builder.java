package com.feinno.superpojo;

import com.feinno.superpojo.io.*;
import com.google.gson.JsonObject;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * 序列化POJO对象的辅助类
 *
 * @param <T>
 * @author lvmingwei
 */
public abstract class Builder<T> {

    protected T data;

    /**
     * 构造方法，构造时需要传递将要序列化或反序列化的Java对象
     *
     * @param data 将要序列化或反序列化的Java对象
     */
    public Builder(T data) {
        this.data = data;
    }

    /**
     * 获得被操作的Java对象
     *
     * @return
     */
    public T getData() {
        return data;
    }

    public abstract int getSerializedSize();

    public abstract JsonObject toJsonObject();

    public abstract void parseJsonFrom(final JsonInputStream input);

    public abstract void writePbTo(final CodedOutputStream output) throws IOException;

    public abstract void parsePbFrom(final CodedInputStream input) throws IOException;

    public abstract void writeXmlTo(final XmlOutputStream output) throws XMLStreamException;

    public abstract void parseXmlFrom(final XmlInputStream input) throws XMLStreamException;
}
