package com.feinno.superpojo.type;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.io.XmlInputStream;
import com.feinno.superpojo.io.XmlOutputStream;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 可以表示任意类型的XML
 *
 * @author Lv.Mingwei
 */
public class AnyNode extends SuperPojo {

    /**
     * 此字段定义了当前AnyNode的xml节点名称
     */
    @Field(id = 1)
    private String name;

    /**
     * 如果当前节点的内容仅仅为一个简单的字符串，那么此处用于存储字符串， <br>
     * 与node_elements是互斥的，因为如果有了简单字符串，就不可能再包含其他节点了
     */
    @Field(id = 2)
    private String value;

    /**
     * 如果当前节点的内容包含了很多个Attribute对象，那么此处用于存储这些Attribute，<br>
     */
    @Field(id = 3)
    private Map<String, String> attrElements = new HashMap<String, String>();

    /**
     * 如果当前节点的内容包含了很多个子对象，那么此处用于存储这些子对象，<br>
     * 与value是互斥的，因为如果有了其他节点了，那当前节点就无法存储字符串了
     */
    @Field(id = 4)
    private List<AnyNode> nodeElements = new ArrayList<AnyNode>();

    public <T extends SuperPojo> T convertTo(Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            StringBuffer xmlBuffer = new StringBuffer();
            xmlBuffer.append(new String(this.toXmlByteArray()));
            t.parseXmlFrom(xmlBuffer.toString());
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static AnyNode convertTo(SuperPojo superPojo) throws XMLStreamException {
        XmlInputStream inputStream = XmlInputStream.newInstance(superPojo.toXmlByteArray());
        return inputStream.readNode();
    }

    public static <T extends Object> AnyNode convertTo(List<T> args, Class<T> genericsClazz)throws XMLStreamException,IOException {
        byte[] buffer = SuperPojoManager.toXmlByteArray(args,genericsClazz);
        XmlInputStream inputStream = XmlInputStream.newInstance(buffer);
        return inputStream.readNode();
    }

    public static AnyNode convertTo(String xml) throws XMLStreamException {
        XmlInputStream inputStream = XmlInputStream.newInstance(xml.getBytes());
        return inputStream.readNode();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttrElements() {
        return attrElements;
    }

    public void setAttrElements(Map<String, String> attrElements) {
        this.attrElements = attrElements;
    }

    public List<AnyNode> getNodeElements() {
        return nodeElements;
    }

    public void setNodeElements(List<AnyNode> nodeElements) {
        this.nodeElements = nodeElements;
    }

    public void addNodeElement(AnyNode value) {
        nodeElements.add(value);
    }

    public void putAttrElements(String key, String value) {
        attrElements.put(key, value);
    }

    public byte[] toXmlByteArray() {
        return toXmlByteArray(true);
    }


    public byte[] toXmlByteArray(boolean isExistHeader) {
        try {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            final XmlOutputStream output = XmlOutputStream.newInstance(byteOutput, isExistHeader);
            output.writeStartRoot(name);
            // 写入 Attribute
            if (attrElements != null && attrElements.size() > 0) {
                for (Entry<String, String> entry : attrElements.entrySet()) {
                    output.writeAttribute(entry.getKey(), entry.getValue());
                }
            }
            if (nodeElements != null && nodeElements.size() > 0) {
                for (AnyNode node : nodeElements) {
                    node.toXmlByteArray(output);
                }
            } else if (value != null && value.length() > 0) {
                output.write(value);
            }

            output.close();
            return byteOutput.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void toXmlByteArray(XmlOutputStream output) {
        try {
            output.writeStartElement(name);
            // 写入 Attribute
            if (attrElements != null && attrElements.size() > 0) {
                for (Entry<String, String> entry : attrElements.entrySet()) {
                    output.writeAttribute(entry.getKey(), entry.getValue());
                }
            }
            if (nodeElements != null && nodeElements.size() > 0) {
                for (AnyNode node : nodeElements) {
                    node.toXmlByteArray(output);
                }
            } else if (value != null && value.length() > 0) {
                output.write(value);
            }
            output.writeEndElement(name);
            output.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
