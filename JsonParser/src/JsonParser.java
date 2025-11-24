import exceptions.JsonParseException;
import models.*;

import java.math.BigDecimal;
import java.util.*;

public class JsonParser {
    private final List<Token> tokens;
    private int pos = 0;

    public JsonParser(List<Token> tokens) { this.tokens = tokens; }

    public JsonValue parse() {
        JsonValue value = parseValue();
        if (pos != tokens.size()) throw new JsonParseException("Extra data after JSON end.");
        return value;
    }

    private JsonValue parseValue() {
        if (match(TokenType.START_OBJECT)) return parseObject();
        else if (match(TokenType.START_ARRAY)) return parseArray();
        else if (match(TokenType.STRING)) return new JsonString(consume().getValue());
        else if (match(TokenType.NUMBER)) {
            String numberStr = consume().getValue();
            BigDecimal bigDecimal = new BigDecimal(numberStr);
            return new JsonNumber(bigDecimal);
        }
        else if (match(TokenType.TRUE)) {
            consume();
            return JsonBoolean.TRUE;
        } else if (match(TokenType.FALSE)) {
            consume();
            return JsonBoolean.FALSE;
        } else if (match(TokenType.NULL)) {
            consume();
            return JsonNull.getInstance();
    } else {
      throw new JsonParseException("Unexpected token at " + pos);
    }
    }

    private JsonObject parseObject() {
        consume(TokenType.START_OBJECT);
        Map<String, JsonValue> members = new LinkedHashMap<>();
        if (match(TokenType.END_OBJECT)) { consume(TokenType.END_OBJECT); return new JsonObject(members); }
        do {
            if (!match(TokenType.STRING)) throw new JsonParseException("Expected STRING key at " + pos);
            String key = consume().getValue();
            consume(TokenType.COLON);
            JsonValue value = parseValue();
            if (members.containsKey(key)) throw new JsonParseException("Duplicate key: " + key);
            members.put(key, value);
        } while (consumeIf(TokenType.COMMA));
        consume(TokenType.END_OBJECT);
        return new JsonObject(members);
    }

    private JsonArray parseArray() {
        consume(TokenType.START_ARRAY);
        List<JsonValue> values = new ArrayList<>();
        if (match(TokenType.END_ARRAY)) { consume(TokenType.END_ARRAY); return new JsonArray(values); }
        do {
            values.add(parseValue());
        } while (consumeIf(TokenType.COMMA));
        consume(TokenType.END_ARRAY);
        return new JsonArray(values);
    }

    private boolean match(TokenType t) { return pos < tokens.size() && tokens.get(pos).getType() == t; }
    private Token consume(TokenType t) {
        if (!match(t)) throw new JsonParseException("Expected " + t + " at " + pos);
        return tokens.get(pos++);
    }
    private Token consume() { return tokens.get(pos++); }
    private boolean consumeIf(TokenType t) {
        if (match(t)) { pos++; return true; } return false;
    }
}
