package org.dotsandboxestcp.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MessageSerializer {
    private static final Gson gson = new GsonBuilder().create();

    private static final boolean DEBUG = false;

    public static String toJson(Object o) {
        try {
            String json = gson.toJson(o);
            if (DEBUG) System.out.println("Serialized object to JSON");
            return json;
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to serialize object: " + e.getMessage());
            return "{}";
        }
    }

    public static Message fromJson(String json) {
        try {
            Message msg = gson.fromJson(json, Message.class);
            System.out.println("Deserialized JSON to TCPMessage");
            return msg;
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to deserialize JSON: " + e.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        try {
            T obj = gson.fromJson(json, cls);
            System.out.println("Deserialized JSON to " + cls.getSimpleName());
            return obj;
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to deserialize JSON to " + cls.getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }
}
