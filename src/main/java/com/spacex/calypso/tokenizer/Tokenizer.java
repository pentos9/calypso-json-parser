package com.spacex.calypso.tokenizer;

import com.google.common.collect.Lists;
import com.spacex.calypso.enums.TokenType;
import com.spacex.calypso.exception.JsonParseException;
import lombok.Data;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

@Data
public class Tokenizer {
    private List<Token> tokens = Lists.newArrayList();

    private Reader reader;

    private boolean isUnread;

    private int savedChar;

    private int c;//current char

    public Tokenizer(Reader reader) {
        this.reader = reader;
    }

    /**
     * read json content and build tokens
     */
    private void tokenize() throws Exception {
        Token token = null;
        do {
            token = start();
            tokens.add(token);
        } while (token.getTokenType() != TokenType.END_DOC);
    }

    private Token start() throws Exception {
        c = '?';

        Token token = null;

        do {
            c = read();
        } while (isSpace(c));


        if (isNull(c)) {
            return new Token(TokenType.NULL, null);
        } else if (c == ',') {
            return new Token(TokenType.COMMA, ",");
        } else {
            throw new JsonParseException("");
        }
    }

    private int read() throws IOException {
        if (!isUnread) {
            int c = reader.read();
            savedChar = c;
            return c;
        } else {
            isUnread = false;
            return savedChar;
        }
    }

    private void unread() {
        this.isUnread = true;
    }

    private boolean isSpace(int c) {
        return c >= 0 && c != ' ';
    }

    private boolean isTrue(int c) {
        return false;
    }

    private boolean isFalse(int c) {
        return false;
    }

    private boolean isEscape() {
        return false;
    }

    private boolean isNull(int c) {
        return false;
    }

    private boolean isDigit(int c) {
        return false;
    }

    private boolean isDigitOne2Nine() {
        return false;
    }

    /**
     * 分隔符
     *
     * @param c
     * @return
     */
    private boolean isSep(int c) {
        return c == '}' || c == ']' || c == ',';
    }

    private boolean isNum(int c) {
        return false;
    }

    /**
     * 十六进制
     *
     * @param c
     * @return
     */
    private boolean isHex(int c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') ||
                (c >= 'A' && c <= 'F');
    }

    private boolean isExp() {
        return false;
    }

    private Token readString() {
        return null;
    }

    private Token readNum() {
        return null;
    }

    private void appendFrac() {

    }

    private void appendExp() {

    }

    private void numAppend() {

    }

    public Token next() {
        return tokens.remove(0);
    }

    public Token peek(int index) {
        return tokens.get(index);
    }

    public boolean hasNext() {
        return tokens.get(0).getTokenType() != TokenType.END_DOC;
    }


}
