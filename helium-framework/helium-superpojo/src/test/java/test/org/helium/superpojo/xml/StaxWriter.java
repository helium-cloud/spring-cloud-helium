package test.org.helium.superpojo.xml;

import java.io.FileOutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StaxWriter {
	private String configFile;

	public void setFile(String configFile) {
		this.configFile = configFile;
	}

	public void saveConfig() throws Exception {
		// Create a XMLOutputFactory
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// Create XMLEventWriter
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(configFile));
		// Create a EventFactory
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		// Create and write Start Tag
		StartDocument startDocument = eventFactory.createStartDocument();
		eventWriter.add(startDocument);

		// Create config open tag
		StartElement configStartElement = eventFactory.createStartElement("", "", "config");
		eventWriter.add(configStartElement);
		eventWriter.add(end);
		// Write the different nodes
		createNode(eventWriter, eventFactory, "mode", "1");
		createNode(eventWriter, eventFactory, "unit", "901");
		createNode(eventWriter, eventFactory, "current", "0");
		createNode(eventWriter, eventFactory, "interactive", "0");

		eventWriter.add(eventFactory.createEndElement("", "", "config"));
		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
	}

	private void createNode(XMLEventWriter eventWriter, XMLEventFactory eventFactory, String name, String value)
			throws XMLStreamException {

		// XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent tab = eventFactory.createDTD("\t");
		XMLEvent end = eventFactory.createDTD("\n");
		// Create Start node
		StartElement sElement = eventFactory.createStartElement("", "", name);
		eventWriter.add(tab);
		eventWriter.add(sElement);
		// Create Content
		Characters characters = eventFactory.createCharacters(value);
		eventWriter.add(characters);
		// Create End node
		EndElement eElement = eventFactory.createEndElement("", "", name);
		eventWriter.add(eElement);
		eventWriter.add(end);

	}

}
