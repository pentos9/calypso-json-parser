package com.spacex.calypso.test;

import com.spacex.calypso.Parser;
import com.spacex.calypso.parser.JsonArray;
import lombok.Data;

import java.util.List;

public class JSONObjectListTest {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        String jsonRawArray = "[{\"id\":12345678,\"isNew\":true,\"name\":\"lucas\"},{\"id\":987654,\"isNew\":false,\"name\":\"heather\"}]";
        try {
            JsonArray jsonArray = Parser.parseJSONArray(jsonRawArray);
            List<User> userList = Parser.inflateList(jsonArray, User.class);
            System.out.println(userList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Data
    public static class User {
        private int id;
        private String name;
        private boolean isNew;

        public User() {
        }
    }
}
