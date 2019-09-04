package com.feinno.superpojo.io;

import com.feinno.superpojo.Builder;
import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.type.AnyNode;
import com.feinno.superpojo.util.DateUtil;
import com.feinno.superpojo.type.EnumInteger;
import com.feinno.superpojo.util.EnumParser;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author lvmingwei
 *
 */
public class XmlInputStream {

	private InputStream input;

	private static XMLInputFactory inputFactory;

	private XMLEventReader eventReader;

	private XMLEvent currentEvent;

	private Iterator<Attribute> currentAttributes;

	private Iterator<Attribute> currentNameSpaces;

	private int currentSeq;

	private AtomicInteger seqCounter;

	private Stack<Integer> elementStack;

	private String root;

	static {
		inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty("javax.xml.stream.isCoalescing", true);
	}

	public static XmlInputStream newInstance(final byte[] buffer) {
		return newInstance(new ByteArrayInputStream(buffer));
	}

	public static XmlInputStream newInstance(final InputStream input) {
		return new XmlInputStream(input);
	}

	private XmlInputStream(final InputStream input) {
		try {
			this.input = input;
			// Setup a new eventReader
			eventReader = inputFactory.createXMLEventReader(input);
			seqCounter = new AtomicInteger();
			elementStack = new Stack<Integer>();
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean moveStartRoot(String root) throws XMLStreamException {
		if (this.root != null) {
			return false;
		}
		this.root = root;
		while (hasNodeNext()) {
			nextEvent();
			if (currentEvent.isStartElement() && readName().equals(root)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean hasAttributeNext() {
		if (isAttribute()) {
			return currentAttributes.hasNext() || currentNameSpaces.hasNext();
		} else if (currentEvent.isStartElement() && currentEvent.asStartElement().getAttributes() != null) {
			currentAttributes = currentEvent.asStartElement().getAttributes();
			currentNameSpaces = currentEvent.asStartElement().getNamespaces();
			return currentAttributes.hasNext() || currentNameSpaces.hasNext();
		} else {
			return false;
		}
	}

	public boolean hasNodeNext() {
		return eventReader.hasNext();
	}

	public XMLEvent nextEvent() throws XMLStreamException {
		currentEvent = eventReader.nextEvent();
		if (currentEvent.isStartElement()) {
			currentSeq = seqCounter.incrementAndGet();
			elementStack.push(currentSeq);
		} else if (currentEvent.isEndElement()) {
			elementStack.pop();
		}
		return currentEvent;
	}

	public int getCurrentSeq() {
		return currentSeq;
	}

	public boolean isEnd(int seq) {
		return !elementStack.contains(seq);
	}

	public Attribute nextAttribute() throws XMLStreamException {
		if (currentAttributes.hasNext()) {
			Attribute attr = currentAttributes.next();
			currentEvent = attr;
			return attr;
		} else if (currentNameSpaces.hasNext()) {
			Attribute attr = currentNameSpaces.next();
			currentEvent = attr;
			return attr;
		} else {
			return null;
		}

	}

	/**
	 * 读取一个Node节点
	 *
	 * @return
	 * @throws XMLStreamException
	 */
	public AnyNode readNode() throws XMLStreamException {
		AnyNode node = new AnyNode();
		int seq = this.getCurrentSeq();
		while (hasAttributeNext()) {
			nextAttribute();
			String name = readName();
			if (name == null) {
				break;
			} else {
				String value = readString();
				node.putAttrElements(name, value);
			}
		}
		while (hasNodeNext()) {
			nextEvent();
			if (currentEvent.isCharacters()) {
				String value = currentEvent.asCharacters().getData();
				node.setValue(value);
			}

			String name = readName(seq);
			if (name == null) {
				break;
			} else {
				AnyNode childNode = readNode();
				childNode.setName(name);
				node.addNodeElement(childNode);
			}
		}
		return node;
	}

	public String readName() throws XMLStreamException {
		while (!currentEvent.isStartElement() && !isAttribute() && hasNodeNext()) {
			nextEvent();
		}
		String name = name();
		return name;
	}

	public String readName(int endSeq) throws XMLStreamException {
		while (!currentEvent.isStartElement() && !isAttribute() && hasNodeNext()) {
			if (isEnd(endSeq)) {
				return null;
			}
			nextEvent();
		}
		String name = name();
		return name;
	}

	public String readString() throws XMLStreamException {
		if (isAttribute()) {
			return ((Attribute) currentEvent).getValue();
		} else if (currentEvent.isCharacters()) {
			String value = currentEvent.asCharacters().getData();
			nextEvent();
			return value;
		} else if (currentEvent.isStartElement()) {
			nextEvent();
			return readString();
		}
		return null;
	}

	public XMLEvent getCurrentEvent() {
		return currentEvent;
	}

	public Integer readInt() throws XMLStreamException {
		String value = readString();
		return value != null && value.trim().length() > 0 ? Integer.parseInt(value) : null;
	}

	public Long readLong() throws XMLStreamException {
		String value = readString();
		return value != null && value.trim().length() > 0 ? Long.parseLong(value) : null;
	}

	public Float readFloat() throws XMLStreamException {
		String value = readString();
		return value != null && value.trim().length() > 0 ? Float.parseFloat(value) : null;
	}

	public int readFlags() throws XMLStreamException {
		return readInt();
	}

	public Double readDouble() throws XMLStreamException {
		String value = readString();
		return value != null && value.trim().length() > 0 ? Double.parseDouble(value) : null;
	}

	public Boolean readBoolean() throws XMLStreamException {
		String value = readString();
		if (value == null) {
			return null;
		}
		if (value.equalsIgnoreCase("true")) {
			return true;
		}
		try {
			int result = Integer.parseInt(value);
			if (result > 0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public Character readChar() throws XMLStreamException {
		String value = readString();
		if (value != null && value.trim().length() != 1) {
			throw new ClassCastException(value + " cannot be cast to char.");
		}
		return value != null && value.trim().length() > 0 ? value.trim().charAt(0) : null;
	}

	public Byte readByte() throws XMLStreamException {
		String value = readString();
		if (value == null || value.trim().length() == 0) {
			return null;
		}
		if (value != null && value.length() == 4) {
			value = value.substring(2);
		}
		return (byte) Integer.parseInt(value, 16);
	}

	public Short readShort() throws XMLStreamException {
		String value = readString();
		return value != null && value.trim().length() > 0 ? Short.parseShort(value) : null;
	}

	public java.sql.Date readSqlDate(String format) throws XMLStreamException {
		String value = readString();
		if (value == null || value.trim().length() == 0) {
			return null;
		}
		if (format != null && format.trim().length() > 0) {
			DateFormat DATA_FORMAT = new SimpleDateFormat(format);
			try {
				return DateUtil.getSqlDate(DATA_FORMAT.parse(value));
			} catch (ParseException e) {
				return null;
			}
		} else {

			long time = Long.parseLong(value);
			return DateUtil.getSqlDate(new java.sql.Date(time));
		}
	}

	public java.util.Date readUtilDate(String format) throws XMLStreamException {
		String value = readString();
		if (value == null || value.trim().length() == 0) {
			return null;
		}
		if (format != null && format.trim().length() > 0) {
			DateFormat DATA_FORMAT = new SimpleDateFormat(format);
			try {
				return DATA_FORMAT.parse(value);
			} catch (ParseException e) {
				return null;
			}
		} else {

			long time = Long.parseLong(value);
			return new java.util.Date(time);
		}
	}

	public java.sql.Date readSqlDate() throws XMLStreamException {
		String value = readString();
		long time = Long.parseLong(value);
		return DateUtil.getSqlDate(new java.sql.Date(time));
	}

	public java.util.Date readUtilDate() throws XMLStreamException {
		String value = readString();
		long time = Long.parseLong(value);
		return new java.util.Date(time);
	}

	@SuppressWarnings("unchecked")
	public <T extends EnumInteger> T readEnum(Class<T> clazz) throws XMLStreamException {
		String value = readString();
		if (value == null || value.trim().length() == 0) {
			return null;
		}
		return (T) EnumParser.parse(clazz, value, true);
	}

	public int readEnum() throws XMLStreamException {
		String value = readString();
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	public <T extends SuperPojo> T readMessage(Class<T> clazz) throws XMLStreamException {
		try {
			Builder<T> builder = SuperPojoManager.getSuperPojoBuilder(clazz.newInstance());
			builder.parseXmlFrom(this);
			return builder.getData();
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public String readEndName() throws XMLStreamException {
		while (!currentEvent.isEndElement() && hasNodeNext()) {
			nextEvent();
		}
		String name = name();
		return name;
	}

	private String name() {
		QName name = null;
		if (isAttribute()) {
			name = ((Attribute) currentEvent).getName();
		} else if (currentEvent.isStartElement()) {
			name = currentEvent.asStartElement().getName();
		} else if (currentEvent.isEndElement()) {
			name = currentEvent.asEndElement().getName();
		} else {
			return null;
		}
		if (name.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
			return name.getLocalPart();
		} else {
			if (name.getPrefix() != null && name.getPrefix().length() > 0) {
				if (name.getLocalPart() == null || name.getLocalPart().length() == 0) {
					return name.getPrefix();
				} else {
					return name.getPrefix() + ":" + name.getLocalPart();
				}
			} else {
				return name.getLocalPart();
			}
		}
	}

	private boolean isAttribute() {
		if (currentEvent.isAttribute() || currentEvent.isNamespace()) {
			return true;
		} else {
			return false;
		}
	}

	public void close() throws IOException {
		input.close();
		currentSeq = 0;
		elementStack.clear();
		seqCounter = new AtomicInteger(0);
	}
}
