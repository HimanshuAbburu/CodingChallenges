package models;

public class JsonBoolean implements JsonValue {

    public static final JsonBoolean TRUE = new JsonBoolean(true);
    public static final JsonBoolean FALSE = new JsonBoolean(false);

    private final boolean value;

    private JsonBoolean(boolean value) {
        this.value = value;
    }

    public static JsonBoolean valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toJson() {
        return Boolean.toString(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof JsonBoolean)) return false;
        return this.value == ((JsonBoolean) obj).value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }
}
