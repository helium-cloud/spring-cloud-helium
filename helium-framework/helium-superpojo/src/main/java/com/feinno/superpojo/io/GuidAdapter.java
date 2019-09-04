package com.feinno.superpojo.io;

import java.lang.reflect.Type;

import com.feinno.superpojo.type.Guid;
import com.google.gson.JsonArray;
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
public class GuidAdapter implements JsonDeserializer<Guid> {

	@Override
	public Guid deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json == null || json.isJsonNull()) {
			return null;
		}
		byte[] data1 = toByte(json.getAsJsonObject().get("data1").getAsJsonArray());
		byte[] data2 = toByte(json.getAsJsonObject().get("data2").getAsJsonArray());
		Guid guid = new Guid();
		guid.setData1(data1);
		guid.setData2(data2);
		return guid;
	}

	private byte[] toByte(JsonArray jsonArray) {
		byte[] buffer = new byte[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			buffer[i] = jsonArray.get(i).getAsByte();
		}
		return buffer;
	}
}