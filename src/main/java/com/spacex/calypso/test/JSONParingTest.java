package com.spacex.calypso.test;

import com.spacex.calypso.parser.JsonArray;
import com.spacex.calypso.parser.JsonObject;

import static com.spacex.calypso.Parser.parseJSONObject;

public class JSONParingTest {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        String jsonString = new String("{\"date\":\"20190218\",\"numbers\":[1,2,4]}");
        try {
            JsonObject jsonObject = parseJSONObject(jsonString);
            String date = jsonObject.getString("date");
            System.out.println(date);

            JsonArray jsonArray = jsonObject.getJArray("numbers");
            System.out.println(jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                System.out.println(jsonArray.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println(String.format("executeTime:%s", endTime - startTime));
    }
}
