package com.heretere.hch.spigot.modern.serializers;

import java.lang.reflect.Type;

import org.bukkit.NamespacedKey;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class ModernNamespacedKeySerializer implements JsonSerializer<NamespacedKey>, JsonDeserializer<NamespacedKey> {
    private static final String NAMESPACE = "namespace";
    private static final String KEY = "key";

    @Override
    @SuppressWarnings("deprecation")
    public NamespacedKey deserialize(
            final JsonElement json,
            final Type typeOfT,
            final JsonDeserializationContext context
    )
            throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();

        if (!object.has(NAMESPACE)) {
            throw new JsonParseException("No namespace defined.");
        }

        if (!object.has(KEY)) {
            throw new JsonParseException("No key defined.");
        }

        return new NamespacedKey(object.get(NAMESPACE).getAsString(), object.get(KEY).getAsString());
    }

    @Override
    public JsonElement serialize(
            final NamespacedKey src,
            final Type typeOfSrc,
            final JsonSerializationContext context
    ) {
        final JsonObject output = new JsonObject();

        output.addProperty(NAMESPACE, src.getNamespace());
        output.addProperty(KEY, src.getKey());

        return output;
    }
}
