package models;

import java.math.BigDecimal;

public class JsonNumber implements JsonValue {
    private final BigDecimal values;

    public JsonNumber(BigDecimal values) {
        this.values = values;
    }

    public BigDecimal getValues() {
        return values;
    }

    @Override
    public String toJson() {
        // Serialize each key/value as "key":value
        return values.toString();
    }
}
