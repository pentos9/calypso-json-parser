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

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

    public static JsonObject parseJSONObject(String s) throws Exception {
        Tokenizer tokenizer = new Tokenizer(new BufferedReader(new StringReader(s)));
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        return parser.object();
    }

    public static JsonArray parseJSONArray(String s) throws Exception {
        Tokenizer tokenizer = new Tokenizer(new BufferedReader(new StringReader(s)));
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        return parser.array();
    }

    public static <T> T fromJson(String jsonString, Class<T> classOfT) throws Exception {
        Tokenizer tokenizer = new Tokenizer(new BufferedReader(new StringReader(jsonString)));
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        JsonObject result = parser.object();

        Constructor<T> constructor = classOfT.getConstructor();
        Object targetObject = constructor.newInstance();
        Field[] fields = classOfT.getDeclaredFields();
        int numField = fields.length;

        String[] fieldNames = new String[numField];
        String[] fieldTypes = new String[numField];

        for (int i = 0; i < numField; i++) {
            fieldNames[i] = fields[i].getName();
            fieldTypes[i] = fields[i].getType().getTypeName();
        }

        for (int i = 0; i < numField; i++) {
            if (fieldTypes[i].equals("java.lang.String")) {
                fields[i].setAccessible(true);
                fields[i].set(targetObject, result.getString(fieldNames[i]));
            } else if (fieldTypes[i].equals("java.util.List")) {
                fields[i].setAccessible(true);
                JsonArray jsonArray = result.getJArray(fieldNames[i]);
                ParameterizedType pt = (ParameterizedType) fields[i].getGenericType();
                Type elementType = pt.getActualTypeArguments()[0];
                String elementTypeName = elementType.getTypeName();
                Class<?> elementClass = Class.forName(elementTypeName);
                fields[i].set(targetObject, inflateList(jsonArray, elementClass));
            } else if (fieldTypes[i].equals("int")) {
                fields[i].setAccessible(true);
                fields[i].set(targetObject, result.getInt(fieldNames[i]));
            } else if (fieldTypes[i].equals("boolean")) {
                fields[i].setAccessible(true);
                fields[i].set(targetObject, result.getBoolean(fieldNames[i]));
            }
        }

        return (T) targetObject;
    }

    public static <T> List<T> inflateList(JsonArray array, Class<T> clz) throws Exception {
        int size = array.length();

        List<T> list = new ArrayList<>();
        Constructor<T> constructor = clz.getConstructor();
        String className = clz.getName();
        if (className.equals("java.lang.String")) {
            for (int i = 0; i < size; i++) {
                String element = (String) ((Primary) array.get(i)).getValue();
                list.add((T) element);
                return list;
            }
        }

        Field[] fields = clz.getDeclaredFields();
        int numField = fields.length;

        String[] fieldNames = new String[numField];
        String[] fieldTypes = new String[numField];

        for (int i = 0; i < numField; i++) {
            fieldNames[i] = fields[i].getName();
            fieldTypes[i] = fields[i].getType().getTypeName();
        }

        for (int i = 0; i < size; i++) {
            T element = constructor.newInstance();
            JsonObject jsonObject = (JsonObject) array.get(i);

            for (int j = 0; j < numField; j++) {
                if (fieldTypes[j].equals("java.lang.String")) {
                    fields[j].setAccessible(true);
                    fields[j].set(element, jsonObject.getString(fieldNames[j]));
                } else if (fieldTypes[j].equals("java.util.List")) {
                    fields[j].setAccessible(true);
                    JsonArray nestArray = jsonObject.getJArray(fieldNames[j]);
                    ParameterizedType pt = (ParameterizedType) fields[j].getGenericType();
                    Type elementType = pt.getActualTypeArguments()[0];
                    String elementTypeName = elementType.getTypeName();
                    Class<?> elementClass = Class.forName(elementTypeName);
                    String value = null;
                    fields[j].set(element, inflateList(nestArray, elementClass));
                } else if (fieldTypes[j].equals("int")) {
                    fields[j].setAccessible(true);
                    fields[j].set(element, jsonObject.getInt(fieldNames[j]));
                } else if (fieldTypes[j].equals("boolean")) {
                    fields[j].setAccessible(true);
                    fields[j].set(element, jsonObject.getBoolean(fieldNames[j]));
                }
            }

            list.add(element);
        }

        return list;
    }
}
