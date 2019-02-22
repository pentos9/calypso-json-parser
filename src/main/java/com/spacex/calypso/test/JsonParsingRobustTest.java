package com.spacex.calypso.test;

import com.spacex.calypso.Parser;
import com.spacex.calypso.parser.JsonObject;

public class JsonParsingRobustTest {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        String jsonString = "{";

        try {
            JsonObject jsonObject = Parser.parseJSONObject(jsonString);
            System.out.println(jsonObject.getBoolean("isOk"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
