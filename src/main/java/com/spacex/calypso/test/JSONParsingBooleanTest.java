package com.spacex.calypso.test;

import com.spacex.calypso.Parser;
import com.spacex.calypso.parser.JsonObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class JSONParsingBooleanTest {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        String jsonString = "{\"date\":\"20190218\",\"isNew\":true,\"ok\":true}";
        try {
            JsonObject jsonObject = Parser.parseJSONObject(jsonString);
            System.out.println(jsonObject.getBoolean("newUser"));
            System.out.println(jsonObject.getBoolean("isNew"));
            System.out.println(jsonObject.getBoolean("ok"));
            Story story = Parser.fromJson(jsonString, Story.class);
            System.out.println(story);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Data
    public static class Story {
        private String date;
        private boolean isNew;
        private boolean ok;

        private int points;

        public Story() {
        }
    }
}
