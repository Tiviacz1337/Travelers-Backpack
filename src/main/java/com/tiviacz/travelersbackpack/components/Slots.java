package com.tiviacz.travelersbackpack.components;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record Slots(List<Integer> unsortables, List<Pair<Integer, Pair<ItemStack, Boolean>>> memory) {
    public static final Slots EMPTY = new Slots(List.of(), List.of());
    public static final Codec<Slots> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            Codec.INT.listOf().fieldOf("unsortables").forGetter(Slots::unsortables),
                            Codec.mapPair(Codec.INT.fieldOf("index"), Codec.mapPair(ItemStack.CODEC.fieldOf("item"), Codec.BOOL.fieldOf("matchComponents"))).codec().listOf().fieldOf("memory").forGetter(Slots::memory))
                    .apply(instance, Slots::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Slots> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), Slots::unsortables,
            ByteBufCodecs.fromCodecWithRegistries(Codec.mapPair(Codec.INT.fieldOf("index"), Codec.mapPair(ItemStack.CODEC.fieldOf("item"), Codec.BOOL.fieldOf("matchComponents"))).codec()).apply(ByteBufCodecs.list()), Slots::memory,
            Slots::new
    );

    public static Slots updateUnsortables(Slots oldSlots, List<Integer> data) {
        return new Slots(data, oldSlots.memory());
    }

    public static Slots updateMemory(Slots oldSlots, List<Pair<Integer, Pair<ItemStack, Boolean>>> data) {
        return new Slots(oldSlots.unsortables(), data);
    }

    public List<Integer> unsortables() {
        return unsortables;
    }

    public List<Pair<Integer, Pair<ItemStack, Boolean>>> memory() {
        return memory;
    }
}