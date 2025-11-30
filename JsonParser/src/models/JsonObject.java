package models;

import java.util.Map;
import java.util.stream.Collectors;

public class JsonObject implements JsonValue {
    private final Map<String, JsonValue> values;

    public JsonObject(Map<String, JsonValue> values) {
        this.values = values;
    }

    public Map<String, JsonValue> getValues() {
        return values;
    }

    @Override
    public String toJson() {
        // Serialize each key/value as "key":value
        return values.entrySet().stream()
                .map(e -> "\"" + e.getKey().replace("\"", "\\\"") + "\":" + e.getValue().toJson())
                .collect(Collectors.joining(",", "{", "}"));
    }
}
