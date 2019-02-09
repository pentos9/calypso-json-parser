package com.spacex.calypso;

import com.google.common.collect.Lists;
import com.spacex.calypso.enums.TokenType;
import com.spacex.calypso.exception.JsonParseException;
import com.spacex.calypso.parser.Json;
import com.spacex.calypso.parser.JsonArray;
import com.spacex.calypso.parser.JsonObject;
import com.spacex.calypso.parser.Primary;
import com.spacex.calypso.parser.Value;
import com.spacex.calypso.tokenizer.Token;
import com.spacex.calypso.tokenizer.Tokenizer;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Parser {
    private Tokenizer tokenizer;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    private JsonObject object() throws JsonParseException {
        tokenizer.next();
        Map<String, Value> map = new HashMap<String, Value>();
        if (isToken(TokenType.END_OBJ)) {
            tokenizer.next();
            return new JsonObject(map);
        } else if (isToken(TokenType.STRING)) {
            map = key(map);
        }
        return new JsonObject(map);
    }

    private Map<String, Value> key(Map<String, Value> map) throws JsonParseException {
        String key = tokenizer.next().getValue();
        if (!isToken(TokenType.COLON)) {
            throw new JsonParseException("Invalid Json String!");
        } else {
            tokenizer.next();
            if (isPrimary()) {
                Value primary = new Primary(tokenizer.next().getValue());
                map.put(key, primary);
            } else if (isToken(TokenType.START_ARRAY)) {
                Value array = array();
                map.put(key, array);
            }

            if (isToken(TokenType.COMMA)) {
                tokenizer.next();//consume ','
                if (isToken(TokenType.STRING)) {
                    map = key(map);
                }
            } else if (isToken(TokenType.END_OBJ)) {
                tokenizer.next();
                return map;
            } else {
                throw new JsonParseException("Invalid Json String!");
            }
        }
        return map;
    }

    private JsonArray array() throws JsonParseException {
        tokenizer.next();
        List<Json> list = Lists.newArrayList();
        JsonArray jsonArray = null;
        if (isToken(TokenType.START_ARRAY)) {
            jsonArray = array();
            list.add(jsonArray);
            if (isToken(TokenType.COMMA)) {
                tokenizer.next();
                list = element(list);
            }
        } else if (isPrimary()) {
            list = element(list);
        } else if (isToken(TokenType.START_OBJ)) {
            list.add(object());
            while (isToken(TokenType.COMMA)) {
                tokenizer.next();
                list.add(object());
            }
        } else if (isToken(TokenType.END_ARRAY)) {
            tokenizer.next();
            jsonArray = new JsonArray(list);
            return jsonArray;
        }

        tokenizer.next();
        jsonArray = new JsonArray(list);
        return jsonArray;
    }

    private List<Json> element(List<Json> list) throws JsonParseException {
        list.add(new Primary(tokenizer.next().getValue()));
        if (isToken(TokenType.COMMA)) {
            tokenizer.next();
            if (isPrimary()) {
                list = element(list);
            } else if (isToken(TokenType.START_OBJ)) {
                list.add(object());
            } else if (isToken(TokenType.END_OBJ)) {
                list.add(array());
            } else {
                throw new JsonParseException("Invalid Json String!");
            }
        } else if (isToken(TokenType.END_ARRAY)) {
            return list;
        } else {
            throw new JsonParseException("Invalid Json String!");
        }
        return list;
    }

    private Json json() throws JsonParseException {
        TokenType type = tokenizer.peek(0).getTokenType();
        if (type == TokenType.START_ARRAY) {
            return array();
        } else if (type == TokenType.START_OBJ) {
            return object();
        } else {
            throw new JsonParseException("Invalid Json String!");
        }
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
        TokenType type = tokenizer.peek(0).getTokenType();
        return type == TokenType.BOOLEAN || type == TokenType.NULL ||
                type == TokenType.NUMBER || type == TokenType.STRING;
    }

    public Json parse() throws JsonParseException {
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
