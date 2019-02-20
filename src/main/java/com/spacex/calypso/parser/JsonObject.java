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
        if (!map.containsKey(key)) {
            return 0;
        }
        return Integer.parseInt((String) map.get(key).value());
    }

    public String getString(String key) {
        if (!map.containsKey(key)) {
            return null;
        }
        return (String) map.get(key).value();
    }

    public boolean getBoolean(String key) {
        if (!map.containsKey(key)) {
            return false;
        }
        return Boolean.parseBoolean((String) map.get(key).value());
    }

    public JsonArray getJArray(String key) {
        if (!map.containsKey(key)) {
            return null;
        }
        return (JsonArray) map.get(key).value();
    }
}
