package com.heretere.hch.spigot.modern.serializers;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public final class ModernItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    private static final String NAME = "name";
    private static final String MATERIAL = "material";
    private static final String AMOUNT = "amount";
    private static final String DURABILITY = "durability";
    private static final String LORE = "lore";
    private static final String ENCHANTMENTS = "enchantments";

    @Override
    public ItemStack deserialize(
            final JsonElement json,
            final Type typeOfT,
            final JsonDeserializationContext context
    )
            throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();

        if (!object.has(MATERIAL)) {
            throw new JsonParseException("No material name defined.");
        }

        final Material material = Material.matchMaterial(object.get(MATERIAL).getAsString());

        if (material == null) {
            throw new JsonParseException(
                    String.format(
                        "No material with name '%s'",
                        object.get(MATERIAL).getAsString()
                    )
            );
        }

        final ItemStack itemStack = new ItemStack(material);
        final ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            Optional.ofNullable(object.get(NAME)).ifPresent(name -> meta.setDisplayName(name.getAsString()));
            Optional.ofNullable(object.get(DURABILITY))
                .ifPresent(durability -> {
                    if (meta instanceof Damageable) {
                        ((Damageable) meta).setDamage(object.getAsInt());
                    }
                });
            Optional.ofNullable(object.get(LORE))
                .ifPresent(
                    lore -> meta.setLore(
                        Stream
                            .of(lore.getAsJsonArray())
                            .map(JsonArray::getAsString)
                            .collect(Collectors.toList())
                    )
                );
        }

        Optional.ofNullable(object.get(AMOUNT)).ifPresent(amount -> itemStack.setAmount(amount.getAsInt()));

        Optional.ofNullable(object.get(ENCHANTMENTS))
            .ifPresent(
                enchantments -> itemStack.addEnchantments(
                    context.deserialize(
                        enchantments,
                        new TypeToken<HashMap<Enchantment, Integer>>() {}.getType()
                    )
                )
            );

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
