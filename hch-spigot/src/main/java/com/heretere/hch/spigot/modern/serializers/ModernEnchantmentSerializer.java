package com.heretere.hch.spigot.modern.serializers;

import java.lang.reflect.Type;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class ModernEnchantmentSerializer implements JsonSerializer<Enchantment>, JsonDeserializer<Enchantment> {
    private static final String NAMESPACED_KEY = "namespaced_key";
    private static final String NAME = "name";

    @Override
    @SuppressWarnings("deprecation")
    public Enchantment deserialize(
            final JsonElement json,
            final Type typeOfT,
            final JsonDeserializationContext context
    )
            throws JsonParseException {

        final JsonObject object = new JsonObject();

        final Enchantment enchantment;
        if (object.has(NAMESPACED_KEY)) {
            final NamespacedKey key = context.deserialize(object.getAsJsonObject(NAMESPACED_KEY), NamespacedKey.class);
            enchantment = Enchantment.getByKey(key);

            if (enchantment == null) {
                throw new JsonParseException("Couldn't find enchant with key: " + key);
            }
        } else if (object.has(NAME)) {
            final String name = object.get(NAME).getAsString();

            enchantment = Enchantment.getByName(name);

            if (enchantment == null) {
                throw new JsonParseException("Couldn't find enchant with name: " + name);
            }
        } else {
            throw new JsonParseException("No namespace or key provided for enchantment.");
        }

        return enchantment;
    }

    @Override
    public JsonElement serialize(
            final Enchantment src,
            final Type typeOfSrc,
            final JsonSerializationContext context
    ) {
        final JsonObject output = new JsonObject();

        output.add(NAMESPACED_KEY, context.serialize(src.getKey(), NamespacedKey.class));

        return output;
    }
}
