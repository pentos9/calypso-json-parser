package com.spacex.calypso.parser;

import lombok.Data;

@Data
public class Primary implements Json, Value {

    private String value;

    public Primary() {
    }

    public Primary(String value) {
        this.value = value;
    }

    public Object value() {
        return this.value;
    }
}
