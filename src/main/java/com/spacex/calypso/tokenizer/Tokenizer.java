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

    private int currentChar;//current char

    public Tokenizer(Reader reader) {
        this.reader = reader;
    }

    /**
     * read json content and build tokens
     */
    public void tokenize() throws Exception {
        Token token = null;
        do {
            token = start();
            tokens.add(token);
        } while (token.getTokenType() != TokenType.END_DOC);
    }

    /**
     *
     */
    private Token start() throws Exception {
        currentChar = '?';

        do {
            currentChar = read();
        } while (isSpace(currentChar));

        if (isNull(currentChar)) {
            return new Token(TokenType.NULL, null);
        } else if (currentChar == ',') {
            return new Token(TokenType.COMMA, ",");
        } else if (currentChar == ':') {
            return new Token(TokenType.COLON, ":");
        } else if (currentChar == '{') {
            return new Token(TokenType.START_OBJ, "{");
        } else if (currentChar == '[') {
            return new Token(TokenType.START_ARRAY, "[");
        } else if (currentChar == ']') {
            return new Token(TokenType.END_ARRAY, "]");
        } else if (currentChar == '}') {
            return new Token(TokenType.END_OBJ, "}");
        } else if (isTrue(currentChar)) {
            return new Token(TokenType.BOOLEAN, "true"); //the value of TRUE is not null
        } else if (isFalse(currentChar)) {
            return new Token(TokenType.BOOLEAN, "false"); //the value of FALSE is null
        } else if (currentChar == '"') {
            return readString();
        } else if (isNum(currentChar)) {
            unread();
            return readNum();
        } else if (currentChar == -1) {
            return new Token(TokenType.END_DOC, "EOF");
        } else {
            throw new JsonParseException("Invalid JSON input.");
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
        return c >= 0 && c <= ' ';
    }

    private boolean isTrue(int c) throws IOException {
        if (c == 't') {
            c = read();
            if (c == 'r') {
                c = read();
                if (c == 'u') {
                    c = read();
                    if (c == 'e') {
                        return true;
                    } else {
                        throw new JsonParseException("Invalid JSON input.");
                    }

                } else {
                    throw new JsonParseException("Invalid JSON input");
                }

            } else {
                throw new JsonParseException("Invalid JSON input");
            }

        }
        return false;
    }

    private boolean isFalse(int c) throws IOException {
        if (c == 'f') {
            c = read();
            if (c == 'a') {
                c = read();
                if (c == 'l') {
                    c = read();
                    if (c == 's') {
                        c = read();
                        if (c == 'e') {
                            return true;
                        } else {
                            throw new JsonParseException("Invalid JSON input");
                        }
                    } else {
                        throw new JsonParseException("Invalid JSON input");
                    }
                } else {
                    throw new JsonParseException("Invalid JSON input");
                }
            } else {
                throw new JsonParseException("Invalid JSON input");
            }
        } else {
            return false;
        }
    }

    private boolean isEscape() throws IOException {
        if (currentChar == '\\') {
            currentChar = read();
            if (currentChar == '"' || currentChar == '\\' || currentChar == '/' || currentChar == 'b' ||
                    currentChar == 'f' || currentChar == 'n' || currentChar == 't' || currentChar == 'r' || currentChar == 'u') {
                return true;
            } else {
                throw new JsonParseException("Invalid JSON input.");
            }
        } else {
            return false;
        }
    }

    private boolean isNull(int c) throws IOException {
        if (c == 'n') {
            c = read();

            if (c == 'u') {
                c = read();
                if (c == 'l') {
                    c = read();
                    if (c == 'l') {
                        return true;
                    } else {
                        throw new JsonParseException("Invalid JSON input");
                    }
                } else {
                    throw new JsonParseException("Invalid JSON input");
                }
            } else {
                throw new JsonParseException("Invalid JSON input");
            }
        } else {
            return false;
        }
    }

    private boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    private boolean isDigitOne2Nine(int c) {
        return c >= '1' && c <= '9';
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
        return isDigit(c) || c == '-';
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

    private boolean isExp(int c) {
        return c == 'e' || c == 'E';
    }

    private Token readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            currentChar = read();
            if (isEscape()) {    //判断是否为\", \\, \/, \b, \f, \n, \t, \r.
                if (currentChar == 'u') {
                    sb.append('\\' + (char) currentChar);
                    for (int i = 0; i < 4; i++) {
                        currentChar = read();
                        if (isHex(currentChar)) {
                            sb.append((char) currentChar);
                        } else {
                            throw new JsonParseException("Invalid Json input.");
                        }
                    }
                } else {
                    sb.append("\\" + (char) currentChar);
                }
            } else if (currentChar == '"') {
                return new Token(TokenType.STRING, sb.toString());
            } else if (currentChar == '\r' || currentChar == '\n') {
                throw new JsonParseException("Invalid JSON input.");
            } else {
                sb.append((char) currentChar);
            }
        }
    }

    private Token readNum() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c = read();
        if (c == '-') { //-
            sb.append((char) c);
            c = read();
            if (c == '0') { //-0
                sb.append((char) c);
                numAppend(sb);

            } else if (isDigitOne2Nine(c)) { //-digit1-9
                do {
                    sb.append((char) c);
                    c = read();
                } while (isDigit(c));
                unread();
                numAppend(sb);
            } else {
                throw new JsonParseException("- not followed by digit");
            }
        } else if (c == '0') { //0
            sb.append((char) c);
            numAppend(sb);
        } else if (isDigitOne2Nine(c)) { //digit1-9
            do {
                sb.append((char) c);
                c = read();
            } while (isDigit(c));
            unread();
            numAppend(sb);
        }
        return new Token(TokenType.NUMBER, sb.toString()); //the value of 0 is null
    }

    private void appendFrac(StringBuilder sb) throws IOException {
        currentChar = read();
        while (isDigit(currentChar)) {
            sb.append((char) currentChar);
            currentChar = read();
        }
    }

    private void appendExp(StringBuilder sb) throws IOException {
        int c = read();
        if (c == '+' || c == '-') {
            sb.append((char) c); //append '+' or '-'
            c = read();
            if (!isDigit(c)) {
                throw new JsonParseException("e+(-) or E+(-) not followed by digit");
            } else { //e+(-) digit
                do {
                    sb.append((char) c);
                    c = read();
                } while (isDigit(c));
                unread();
            }
        } else if (!isDigit(c)) {
            throw new JsonParseException("e or E not followed by + or - or digit.");
        } else { //e digit
            do {
                sb.append((char) c);
                c = read();
            } while (isDigit(c));
            unread();
        }
    }

    private void numAppend(StringBuilder sb) throws IOException {
        currentChar = read();
        if (currentChar == '.') { //int frac
            sb.append((char) currentChar); //apppend '.'
            appendFrac(sb);
            if (isExp(currentChar)) { //int frac exp
                sb.append((char) currentChar); //append 'e' or 'E';
                appendExp(sb);
            }

        } else if (isExp(currentChar)) { // int exp
            sb.append((char) currentChar); //append 'e' or 'E'
            appendExp(sb);
        } else {
            unread();
        }
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
