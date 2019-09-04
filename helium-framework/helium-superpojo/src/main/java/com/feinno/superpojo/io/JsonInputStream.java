package com.feinno.superpojo.io;

import com.feinno.superpojo.UnknownField;
import com.feinno.superpojo.type.Flags;
import com.feinno.superpojo.type.Guid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by lvmingwei on 5/26/15.
 */
public class JsonInputStream {

    /**
     * 用于从JSON-->ProtoEntity的工具类
     */
    protected static GsonBuilder gsonBuilder = new GsonBuilder();

    static {
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        gsonBuilder.registerTypeAdapter(Guid.class, new GuidAdapter());
        gsonBuilder.registerTypeAdapter(Flags.class, new FlagsAdapter());
        gsonBuilder.registerTypeAdapter(UnknownField.class, new UnknownFieldAdapter());
    }

    private String json;

    public JsonInputStream(String json) {
        this.json = json;
    }

    public JsonInputStream(byte[] buffer) {
        this.json = new String(buffer);
    }

    public <E> E read(Class<E> clazz) {
        Gson gson = gsonBuilder.create();
        return gson.fromJson(json, clazz);
    }

}
