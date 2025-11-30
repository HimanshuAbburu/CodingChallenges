package models;

import java.util.List;
import java.util.stream.Collectors;

public class JsonArray implements JsonValue {
    private final List<JsonValue> values;

    public JsonArray(List<JsonValue> values) {
        this.values = values;
    }
    public List<JsonValue> getValues() {
        return values;
    }

    @Override
    public String toJson() {
        // Join each element’s toJson() with commas, wrap in []
        return values.stream()
                .map(JsonValue::toJson)
                .collect(Collectors.joining(",", "[", "]"));
    }
}
