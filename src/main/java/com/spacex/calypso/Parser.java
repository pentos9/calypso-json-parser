package com.spacex.calypso;

import com.spacex.calypso.enums.TokenType;
import com.spacex.calypso.parser.Json;
import com.spacex.calypso.parser.JsonArray;
import com.spacex.calypso.parser.JsonObject;
import com.spacex.calypso.parser.Value;
import com.spacex.calypso.tokenizer.Token;
import com.spacex.calypso.tokenizer.Tokenizer;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Parser {
    private Tokenizer tokenizer;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    private JsonObject object() {
        return null;
    }

    private Map<String, Value> key(Map<String, Value> map) {
        return null;
    }

    private JsonArray array() {
        return null;
    }

    private List<Json> element(List<Json> list) {
        return null;
    }

    private Json json() {
        return null;
    }

    private boolean isToken(TokenType tokenType) {
        Token t = tokenizer.peek(0);
        return t.getTokenType() == tokenType;
    }

    private boolean isToken(String name) {
        Token t = tokenizer.peek(0);
        return t.getValue().equals(name);
    }

    private boolean isPrimary() {
        return false;
    }

    public Json parse() {
        Json json = json();
        return json;
    }

    public static JsonObject parseJSONObject(String s) {
        return null;
    }

    public static JsonArray parseJSONArray(String s) {
        return null;
    }

    public static <T> T fromJson(String jsonString, Class<T> classOfT) throws Exception {
        return null;
    }

    public static <T> List<T> inflateList(JsonArray array, Class<T> clz) throws Exception {
        return null;
    }
}
