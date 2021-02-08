package com.heretere.hch.spigot;

import java.util.Collections;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Maps;
import com.heretere.hch.spigot.modern.serializers.ModernEnchantmentSerializer;
import com.heretere.hch.spigot.modern.serializers.ModernItemStackSerializer;
import com.heretere.hch.spigot.modern.serializers.ModernNamespacedKeySerializer;

public final class SpigotSerializers {

    private SpigotSerializers() {
        throw new IllegalStateException("Utility Class.");
    }

    private static final @NotNull Map<@NotNull Class<?>, @NotNull Object> SERIALIZER_ADAPTERS;

    static {
        SERIALIZER_ADAPTERS = Maps.newHashMap();

        SERIALIZER_ADAPTERS.put(Enchantment.class, new ModernEnchantmentSerializer());
        SERIALIZER_ADAPTERS.put(ItemStack.class, new ModernItemStackSerializer());
        SERIALIZER_ADAPTERS.put(NamespacedKey.class, new ModernNamespacedKeySerializer());
    }

    public static @NotNull Map<@NotNull Class<?>, @NotNull Object> getDefaultSpigotSerializerAdapters() {
        return Collections.unmodifiableMap(SERIALIZER_ADAPTERS);
    }
}
