package com.heretere.hch.spigot.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    @Override public ItemStack deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context
    ) throws JsonParseException {
        

        return null;
    }

    @Override public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
