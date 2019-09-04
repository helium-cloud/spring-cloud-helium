package com.feinno.superpojo.io;

import java.lang.reflect.Type;

import com.feinno.superpojo.type.Flags;
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
public class FlagsAdapter implements JsonDeserializer<Flags> {

	@Override
	public Flags deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json == null || json.isJsonNull()) {
			return null;
		}
		String flagsString = json.getAsString();
		return new Flags(Integer.parseInt(flagsString));
	}
}