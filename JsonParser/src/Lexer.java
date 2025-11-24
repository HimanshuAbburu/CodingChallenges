import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private int pos = 0;

    public Lexer(String input) { this.input = input; }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
            } else if (c == '{') {
                tokens.add(new Token(TokenType.START_OBJECT, "{")); pos++;
            } else if (c == '}') {
                tokens.add(new Token(TokenType.END_OBJECT, "}")); pos++;
            } else if (c == '[') {
                tokens.add(new Token(TokenType.START_ARRAY, "[")); pos++;
            } else if (c == ']') {
                tokens.add(new Token(TokenType.END_ARRAY, "]")); pos++;
            } else if (c == ':') {
                tokens.add(new Token(TokenType.COLON, ":")); pos++;
            } else if (c == ',') {
                tokens.add(new Token(TokenType.COMMA, ",")); pos++;
            } else if (Character.isDigit(c) || c == '-') {
                StringBuilder number = new StringBuilder();
                while (pos < input.length() &&
                        (Character.isDigit(input.charAt(pos)) ||
                                input.charAt(pos) == '.' ||
                                input.charAt(pos) == 'e' ||
                                input.charAt(pos) == 'E' ||
                                input.charAt(pos) == '+' ||
                                input.charAt(pos) == '-')) {
                    number.append(input.charAt(pos++));
                    // For strict JSON handling, refine this logic.
                }
                tokens.add(new Token(TokenType.NUMBER, number.toString()));
            } else if (c == '"') {
                StringBuilder str = new StringBuilder();
                pos++; // skip opening quote
                while (pos < input.length()) {
                    char current = input.charAt(pos);
                    if (current == '\\') {
                        // Handle escaped characters
                        pos++;
                        if (pos < input.length()) {
                            char escape = input.charAt(pos++);
                            switch (escape) {
                                case '"': str.append('"'); break;
                                case '\\': str.append('\\'); break;
                                case '/': str.append('/'); break;
                                case 'b': str.append('\b'); break;
                                case 'f': str.append('\f'); break;
                                case 'n': str.append('\n'); break;
                                case 'r': str.append('\r'); break;
                                case 't': str.append('\t'); break;
                                case 'u':
                                    if (pos + 4 <= input.length()) {
                                        String hex = input.substring(pos, pos + 4);
                                        str.append((char) Integer.parseInt(hex, 16));
                                        pos += 4;
                                    }
                                    break;
                                default: str.append(escape);
                            }
                        }
                    } else if (current == '"') {
                        pos++;
                        break;
                    } else {
                        str.append(current);
                        pos++;
                    }
                }
                tokens.add(new Token(TokenType.STRING, str.toString()));
            } else if (input.startsWith("true", pos)) {
                tokens.add(new Token(TokenType.TRUE, "true")); pos += 4;
            } else if (input.startsWith("false", pos)) {
                tokens.add(new Token(TokenType.FALSE, "false")); pos += 5;
            } else if (input.startsWith("null", pos)) {
                tokens.add(new Token(TokenType.NULL, "null")); pos += 4;
            } else {
                // Unexpected input, skip but could error!
                pos++;
            }
        }
        return tokens;
    }
}