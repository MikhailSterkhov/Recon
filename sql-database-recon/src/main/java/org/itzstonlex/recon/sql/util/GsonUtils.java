package org.itzstonlex.recon.sql.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class GsonUtils {

    private static Gson gson;
    private static boolean hasChanges;

    private static final Map<Type, TypeAdapter<?>> gsonAdapters = new HashMap<>();

    public static void registerAdapter(Type type, TypeAdapter<?> typeAdapter) {
        gsonAdapters.put(type, typeAdapter);
        hasChanges = true;
    }

    public static Gson newGson() {
        if (gson == null || hasChanges) {

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonAdapters.forEach(gsonBuilder::registerTypeAdapter);

            hasChanges = false;
            return gson = gsonBuilder.create();
        }

        return gson;
    }

    public static String toJsonString(Object obj) {
        return GsonUtils.newGson().toJson(obj);
    }

    public static JsonElement toJsonTree(Object obj) {
        return GsonUtils.newGson().toJsonTree(obj);
    }

    public static <R> R fromJsonString(String json, Class<R> type) {
        return GsonUtils.newGson().fromJson(json, type);
    }

    public static <R> R fromJsonString(Reader json, Class<R> type) {
        return GsonUtils.newGson().fromJson(json, type);
    }

}
