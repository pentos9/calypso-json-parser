package com.spacex.calypso.test;

import com.spacex.calypso.Parser;
import lombok.Data;

public class JSONParseRandomTest {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        String jsonString = "{\"name\":\"NewYork\"}";

        try {
            Bookmark bookmark = Parser.fromJson(jsonString, Bookmark.class);
            System.out.println(bookmark);

            Account account = Parser.fromJson(jsonString, Account.class);
            System.out.println(account);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Data
    public static class Bookmark {
        private String name;
    }

    @Data
    public static class Account {
        private String name;
    }
}
