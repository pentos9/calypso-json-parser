package com.spacex.calypso.parser;

import com.google.common.collect.Lists;

import java.util.List;

public class JsonArray implements Json, Value {

    private List<Json> list = Lists.newArrayList();

    public JsonArray(List<Json> list) {
        this.list = list;
    }

    public Object value() {
        return this;
    }

    public int length() {
        return list.size();
    }

    public void add(Json element) {
        list.add(element);
    }

    public Json get(int i) {
        return list.get(i);
    }
}
