package com.feinno.superpojo.io;

import java.lang.reflect.Type;

import com.feinno.superpojo.UnknownField;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Guid序列化成Json格式时的适配器
 * 
 * @author Lv.Mingwei
 * 
 */
@SuppressWarnings("rawtypes")
public class UnknownFieldAdapter implements JsonDeserializer<UnknownField> {

	@Override
	public UnknownField deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json == null || json.isJsonNull()) {
			return null;
		}
		String t = json.getAsJsonObject().get("t").getAsString();
		int wireFormat = json.getAsJsonObject().get("wireFormat").getAsInt();

		switch (wireFormat) {
		case WireFormat.WIRETYPE_VARINT:
			return new UnknownField<Long>(Long.valueOf(t), wireFormat);
		case WireFormat.WIRETYPE_FIXED64:
			return new UnknownField<Long>(Long.valueOf(t), wireFormat);
		case WireFormat.WIRETYPE_LENGTH_DELIMITED:
			String[] dataArray = t.split(" ");
			return new UnknownField<ByteString>(toByteString(dataArray), wireFormat);
		case WireFormat.WIRETYPE_START_GROUP:
			return null;
		case WireFormat.WIRETYPE_END_GROUP:
			return null;
		case WireFormat.WIRETYPE_FIXED32:
			return new UnknownField<Integer>(Integer.valueOf(t), wireFormat);
		default:
			return null;
		}
	}

	private ByteString toByteString(String[] dataArray) {
		byte[] buffer = new byte[dataArray.length];
		for (int i = 0; i < dataArray.length; i++) {
			buffer[i] = (byte) Integer.parseInt(dataArray[i], 16);
		}
		return ByteString.copyFrom(buffer);
	}
}