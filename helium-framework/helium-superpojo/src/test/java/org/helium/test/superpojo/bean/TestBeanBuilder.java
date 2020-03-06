package org.helium.test.superpojo.bean;

import com.feinno.superpojo.io.JsonInputStream;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class TestBeanBuilder extends com.feinno.superpojo.Builder<TestBean> {

	public TestBeanBuilder(final TestBean data) {
		super(data);
	}

	@Override
	public void parsePbFrom(final com.feinno.superpojo.io.CodedInputStream input) throws IOException {
		while (true) {
			int tag = input.readTag();
			switch (tag) {
			case 0:
				return;
			case 10: {
				data.putSerializationFieldTag(1);
				data.setId(input.readString());
				break;
			}
			case 18: {
				data.putSerializationFieldTag(2);
				data.setName(input.readString());
				break;
			}

			default:
				data.getUnknownFields().parseUnknownField(tag, input);
				break;
			}
		}
	}

	@Override
	public void writePbTo(final com.feinno.superpojo.io.CodedOutputStream output) throws IOException {
		if (!isInitialized()) {
			throw new RuntimeException("required field is null,so stop write.");
		}
		getSerializedSize();
		if (data.getId() != null)
			output.writeString(1, data.getId());
		if (data.getName() != null)
			output.writeString(2, data.getName());
		data.getUnknownFields().writeUnknownField(output);
	}

	private int memoizedSerializedSize = -1;

	@Override
	public int getSerializedSize() {
		int size = memoizedSerializedSize;
		if (size != -1)
			return size;

		size = 0;

		if (data.getId() != null)
			size += com.feinno.superpojo.io.CodedOutputStream.computeStringSize(1, data.getId());
		if (data.getName() != null)
			size += com.feinno.superpojo.io.CodedOutputStream.computeStringSize(2, data.getName());

		size += data.getUnknownFields().getSerializedSize();
		memoizedSerializedSize = size;
		return size;
	}

	@Override
	public com.google.gson.JsonObject toJsonObject() {
		com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();
		jsonObject.addProperty("id", data.getId() != null ? data.getId().toString() : null);
		jsonObject.addProperty("name", data.getName() != null ? data.getName().toString() : null);
		if (this.getData() != null && this.getData().getUnknownFields() != null
				&& this.getData().getUnknownFields().getNumbers() != null) {
			com.feinno.superpojo.UnknownFieldSet unknownFields = this.getData().getUnknownFields();
			java.util.Iterator<Integer> numbers = unknownFields.getNumbers();
			com.google.gson.JsonObject unknowFieldJson = new com.google.gson.JsonObject();
			while (numbers.hasNext()) {
				com.google.gson.JsonArray unknowFieldValueJson = new com.google.gson.JsonArray();
				Integer number = numbers.next();
				java.util.Iterator<com.feinno.superpojo.UnknownField<?>> fields = unknownFields.getUnknowFields(number);
				if (fields == null) {
					continue;
				}
				while (fields.hasNext()) {
					com.feinno.superpojo.UnknownField<?> field = fields.next();
					com.google.gson.JsonObject fieldJson = new com.google.gson.JsonObject();
					fieldJson.addProperty("t", field.getData().toString());
					fieldJson.addProperty("wireFormat", field.getWireFormat());
					unknowFieldValueJson.add(fieldJson);
				}
				unknowFieldJson.add(String.valueOf(number), unknowFieldValueJson);
			}
			com.google.gson.JsonObject fieldMapJson = new com.google.gson.JsonObject();
			fieldMapJson.add("fieldMap", unknowFieldJson);
			jsonObject.add("unknownFieldSet", fieldMapJson);
		}
		return jsonObject;
	}

	@Override
	public void parseJsonFrom(JsonInputStream input) {
		input.read(getData().getClass());
	}

	public boolean isInitialized() {
		return true;
	}

	@Override
	public void writeXmlTo(final com.feinno.superpojo.io.XmlOutputStream output) throws XMLStreamException {
		output.writeStartRoot("test");
		if (data.getId() != null) {
			output.writeAttribute("id", data.getId());
		}
		if (data.getName() != null) {
			output.writeStartElement("name");
			output.write(data.getName());
			output.writeEndElement("name");
		}
	}

	@Override
	public void parseXmlFrom(final com.feinno.superpojo.io.XmlInputStream input) throws XMLStreamException {
		String parentName = "";
		input.moveStartRoot("test");
		int seq = input.getCurrentSeq();
		while (input.hasAttributeNext()) {
			input.nextAttribute();
			String name = input.readName();
			if (name == null) {
				break;
			} else if (name.equals("id")) {
				data.setId(input.readString());
			} else {
				String value = input.readString();
				System.err.println(String.format("Not found [%s] attribute.skip value [%s]", name, value));
			}
		}
		// AnyNode or Inner
		while (input.hasNodeNext()) {
			input.nextEvent();
			String name = input.readName(seq);
			if (name == null) {
				break;
			} else if (name.equals("name")) {
				input.nextEvent();
				data.setName(input.readString());
			} else {
				data.getUnknownFields().parseAnyNode(name, input);
				System.err.println(String.format("Not found [%s] node.", name));
			}
		}
	}
}