package com.spacex.calypso;

import com.spacex.calypso.enums.TokenType;
import lombok.Data;

@Data
public class Token {
    private TokenType tokenType;
    private String value;

    public Token(TokenType tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }
}
