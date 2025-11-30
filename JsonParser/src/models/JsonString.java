package models;

public class JsonString implements JsonValue{

    private final String value;
    public JsonString(String value) {
        this.value = value;

    }
    public String getValue() {
        return value;
    }

    @Override
    public String toJson() {
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }
}
