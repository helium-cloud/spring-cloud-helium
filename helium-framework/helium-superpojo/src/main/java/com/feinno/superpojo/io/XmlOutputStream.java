package com.feinno.superpojo.io;

import com.feinno.superpojo.Builder;
import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.type.EnumInteger;
import com.feinno.superpojo.type.Flags;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * XML的输出流
 *
 * @author lvmingwei
 */
public class XmlOutputStream {

	private String root;
	private OutputStream output;
	private XMLEventWriter eventWriter;

	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String DEFAULT_VERSION = "1.0";
	private static final boolean DEFAULT_STANDALONE = false;

	private static XMLEventFactory eventFactory = XMLEventFactory.newInstance();
	private static XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

	public static XmlOutputStream newInstance(final OutputStream output, boolean isExistHeader) {
		return new XmlOutputStream(output, DEFAULT_ENCODING, DEFAULT_VERSION, DEFAULT_STANDALONE, isExistHeader);
	}

	public static XmlOutputStream newInstance(final OutputStream output, String encoding) {
		return new XmlOutputStream(output, encoding, DEFAULT_VERSION, DEFAULT_STANDALONE, true);
	}

	public static XmlOutputStream newInstance(final OutputStream output, String encoding, String version) {
		return new XmlOutputStream(output, encoding, version, DEFAULT_STANDALONE, true);
	}

	public static XmlOutputStream newInstance(final OutputStream output, String encoding, String version,
											  boolean standalone) {
		return new XmlOutputStream(output, encoding, version, standalone, true);
	}

	private XmlOutputStream(OutputStream output, String encoding, String version, boolean standalone, boolean isExistHeader) {
		try {
			this.output = output;
			this.eventWriter = outputFactory.createXMLEventWriter(output);
			if (isExistHeader) {
				StartDocument startDocument = eventFactory.createStartDocument(encoding, version, standalone);
				eventWriter.add(startDocument);
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean writeStartRoot(String name) throws XMLStreamException {
		// 如果有root，那么返回false(root只能被创建一次)
		if (root != null) {
			return false;
		}
		root = name;
		writeStartElement(name);
		return true;
	}

	public void write(Integer value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		write(String.valueOf(value));
	}

	public void write(Flags<?> flags) throws XMLStreamException {
		if (flags == null) {
			return;
		}
		write(String.valueOf(flags.intValue()));
	}

	public void write(Long value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		write(String.valueOf(value));
	}

	public void write(Float value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		write(String.valueOf(value));
	}

	public void write(Double value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		write(String.valueOf(value));
	}

	public void write(Boolean value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		write(String.valueOf(value));
	}

	public void write(Character value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		write(String.valueOf(value));
	}

	public void write(Byte value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		String str = "0x";
		String hex = Integer.toHexString(value & 0xFF).toUpperCase();
		if (hex.length() == 1) {
			str += 0;
		}
		str += hex;
		write(str);
	}

	public void write(Short value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		write(String.valueOf(value));
	}

	public void write(EnumInteger enumInteger) throws XMLStreamException {
		write(enumInteger != null ? String.valueOf(enumInteger.intValue()) : "");
	}

	public void write(Date date, String format) throws XMLStreamException {
		if (format != null && format.trim().length() > 0) {
			DateFormat DATA_FORMAT = new SimpleDateFormat(format);
			write(DATA_FORMAT.format(date));
		} else {
			write(date != null ? String.valueOf(date.getTime()) : "");
		}
	}

	public void write(java.sql.Date date, String format) throws XMLStreamException {
		if (format != null && format.trim().length() > 0) {
			DateFormat DATA_FORMAT = new SimpleDateFormat(format);
			write(date != null ? DATA_FORMAT.format(date) : "");
		} else {
			write(date != null ? String.valueOf(date.getTime()) : "");
		}
	}

	public void write(Date date) throws XMLStreamException {
		write(date != null ? String.valueOf(date.getTime()) : "");
	}

	public void write(java.sql.Date date) throws XMLStreamException {
		write(date != null ? String.valueOf(date.getTime()) : "");
	}

	public <T extends SuperPojo> void write(final Builder<T> builder) throws XMLStreamException {
		builder.writeXmlTo(this);
	}

	public void write(final SuperPojo superPojo) throws XMLStreamException {
		if (superPojo == null) {
			return;
		}
		Builder<SuperPojo> builder = SuperPojoManager.getSuperPojoBuilder(superPojo);
		builder.writeXmlTo(this);
	}

	public void write(String value) throws XMLStreamException {
		Characters characters = eventFactory.createCharacters(value);
		eventWriter.add(characters);
	}

	public void writeCDATA(String value) throws XMLStreamException {
		Characters characters = eventFactory.createCData(value != null ? value : "");
		eventWriter.add(characters);
	}

	public void writeAttribute(String name, Integer value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		writeAttribute(name, String.valueOf(value));
	}

	public void writeAttribute(String name, Long value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		writeAttribute(name, String.valueOf(value));
	}

	public void writeAttribute(String name, Float value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		writeAttribute(name, String.valueOf(value));
	}

	public void writeAttribute(String name, Double value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		writeAttribute(name, String.valueOf(value));
	}

	public void writeAttribute(String name, Boolean value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		writeAttribute(name, String.valueOf(value));
	}

	public void writeAttribute(String name, Character value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		writeAttribute(name, String.valueOf(value));
	}

	public void writeAttribute(String name, Byte value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		writeAttribute(name, String.valueOf(value));
	}

	public void writeAttribute(String name, Short value) throws XMLStreamException {
		if (value == null) {
			return;
		}
		writeAttribute(name, String.valueOf(value));
	}

	public void writeAttribute(String name, EnumInteger enumInteger) throws XMLStreamException {
		writeAttribute(name, enumInteger != null ? String.valueOf(enumInteger.intValue()) : "");
	}

	public boolean writeEndRoot() throws XMLStreamException {
		writeEndElement(root);
		return true;
	}

	public void writeAttribute(String name, String value) throws XMLStreamException {
		Attribute attr = eventFactory.createAttribute(name, value != null ? value : "");
		eventWriter.add(attr);
	}

	public void writeStartElement(String name) throws XMLStreamException {
		StartElement startElement = eventFactory.createStartElement("", "", name);
		eventWriter.add(startElement);
	}

	public void writeEndElement(String name) throws XMLStreamException {
		EndElement endElement = eventFactory.createEndElement("", "", name);
		eventWriter.add(endElement);
	}

	public void flush() throws IOException {
		output.flush();
	}

	public void close() throws IOException, XMLStreamException {
		writeEndRoot();
		eventWriter.close();
	}
}
