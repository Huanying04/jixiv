package net.nekomura.utils.jixiv.utils;

public class NameValuePair {
    private String name;
    private String value;

    public NameValuePair(String name, Object value) {
        this.name = name;
        this.value = String.valueOf(value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
