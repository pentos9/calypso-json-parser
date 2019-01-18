package com.spacex.calypso.parser;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class JsonObject implements Json {

    private Map<String, Value> map = new HashMap<String, Value>();

    public JsonObject(Map<String, Value> map) {
        this.map = map;
    }

    public int getInt(String key) {
        return Integer.parseInt((String) map.get(key).value());
    }

    public String getString(String key) {
        return (String) map.get(key).value();
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean((String) map.get(key).value());
    }

    public JsonArray getJArray(String key) {
        return (JsonArray) map.get(key).value();
    }
}
