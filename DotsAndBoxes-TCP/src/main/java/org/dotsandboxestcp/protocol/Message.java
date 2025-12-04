package org.dotsandboxestcp.protocol;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private String type;
    private Map<String, Object> data = new HashMap<>();

    public Message() {
    }

    public Message(String type) {
        this.type = type;
    }

    public Message add(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public String getType() {
        return type;
    }

    public String getAsString(String key) {
        Object v = data.get(key);
        return v == null ? null : v.toString();
    }
}
