package com.spacex.calypso.test;

import com.spacex.calypso.Parser;

public class SimpleJSONTest {
    public static void main(String[] args) throws Exception {
        String jsonString = "{\"date\":\"20190218\"}";
        CustomDateTime customDateTime = Parser.fromJson(jsonString, CustomDateTime.class);
        System.out.println(customDateTime);

    }

    public static class CustomDateTime {
        private String date;

        public CustomDateTime() {
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return "CustomDateTime{" +
                    "date='" + date + '\'' +
                    '}';
        }
    }
}
