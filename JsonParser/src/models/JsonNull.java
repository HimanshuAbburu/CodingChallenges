package models;

public class JsonNull implements JsonValue {

    // Singleton instance since null is unique and stateless
    private static final JsonNull INSTANCE = new JsonNull();

    private JsonNull() {
        // Private constructor to prevent instantiation
    }

    public static JsonNull getInstance() {
        return INSTANCE;
    }

    @Override
    public String toJson() {
        return "null";
    }

    // Optional: override equals and hashCode
    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonNull;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
