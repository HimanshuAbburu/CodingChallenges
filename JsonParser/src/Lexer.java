import exceptions.JsonParseException;

import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0;

    public Lexer(String input) { this.input = input; }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) { pos++; }
            else if (c == '{') { tokens.add(new Token(TokenType.START_OBJECT, "{")); pos++; }
            else if (c == '}') { tokens.add(new Token(TokenType.END_OBJECT, "}")); pos++; }
            else if (c == '[') { tokens.add(new Token(TokenType.START_ARRAY, "[")); pos++; }
            else if (c == ']') { tokens.add(new Token(TokenType.END_ARRAY, "]")); pos++; }
            else if (c == ':') { tokens.add(new Token(TokenType.COLON, ":")); pos++; }
            else if (c == ',') { tokens.add(new Token(TokenType.COMMA, ",")); pos++; }
            else if (Character.isDigit(c) || c == '-') { tokens.add(readNumber()); }
            else if (c == '"') { tokens.add(readString()); }
            else if (input.startsWith("true", pos)) { tokens.add(new Token(TokenType.TRUE, "true")); pos += 4; }
            else if (input.startsWith("false", pos)) { tokens.add(new Token(TokenType.FALSE, "false")); pos += 5; }
            else if (input.startsWith("null", pos)) { tokens.add(new Token(TokenType.NULL, "null")); pos += 4; }
            else { throw new JsonParseException("Unexpected character at " + pos); }
        }
        return tokens;
    }

    private Token readNumber() {
        int start = pos;
        while (pos < input.length() &&
                (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.' ||
                        input.charAt(pos) == 'e' || input.charAt(pos) == 'E' ||
                        input.charAt(pos) == '-' || input.charAt(pos) == '+')) {
            pos++;
        }
        return new Token(TokenType.NUMBER, input.substring(start, pos));
    }

    private Token readString() {
        StringBuilder str = new StringBuilder();
        pos++; // skip opening quote
        while (pos < input.length() && input.charAt(pos) != '"') {
            char current = input.charAt(pos);
            if (current == '\\') {
                pos++;
                if (pos < input.length()) {
                    char escaped = input.charAt(pos++);
                    switch (escaped) {
                        case '"': str.append('"'); break;
                        case '\\': str.append('\\'); break;
                        case '/': str.append('/'); break;
                        case 'b': str.append('\b'); break;
                        case 'f': str.append('\f'); break;
                        case 'n': str.append('\n'); break;
                        case 'r': str.append('\r'); break;
                        case 't': str.append('\t'); break;
                        default: str.append(escaped);
                    }
                }
            } else {
                str.append(current);
                pos++;
            }
        }
        pos++; // skip closing quote
        return new Token(TokenType.STRING, str.toString());
    }
}
