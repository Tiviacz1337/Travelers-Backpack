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
                            Codec.mapPair(Codec.INT.fieldOf("index"), Codec.mapPair(ItemStack.OPTIONAL_CODEC.fieldOf("item"), Codec.BOOL.fieldOf("matchComponents"))).codec().listOf().fieldOf("memory").forGetter(Slots::memory))
                    .apply(instance, Slots::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Slots> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), Slots::unsortables,
            ByteBufCodecs.fromCodecWithRegistries(Codec.mapPair(Codec.INT.fieldOf("index"), Codec.mapPair(ItemStack.OPTIONAL_CODEC.fieldOf("item"), Codec.BOOL.fieldOf("matchComponents"))).codec()).apply(ByteBufCodecs.list()), Slots::memory,
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

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(!(obj instanceof Slots o)) {
            return false;
        } else {
            return this.unsortables.equals(o.unsortables) && memoryMatch(o.memory);
        }
    }

    public boolean memoryMatch(List<Pair<Integer, Pair<ItemStack, Boolean>>> memory) {
        if(this.memory.size() != memory.size()) {
            return false;
        }

        for(int i = 0; i < this.memory.size(); i++) {
            Pair<Integer, Pair<ItemStack, Boolean>> thisEntry = this.memory.get(i);
            Pair<Integer, Pair<ItemStack, Boolean>> otherEntry = memory.get(i);

            if(!thisEntry.getFirst().equals(otherEntry.getFirst())) {
                return false;
            }

            Pair<ItemStack, Boolean> thisPair = thisEntry.getSecond();
            Pair<ItemStack, Boolean> otherPair = otherEntry.getSecond();

            if(!thisPair.getSecond().equals(otherPair.getSecond())) {
                return false;
            }

            if(thisPair.getSecond()) {
                if(!ItemStack.matches(thisPair.getFirst(), otherPair.getFirst())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return hashSlots(this.unsortables, this.memory);
    }

    public static int hashSlots(List<Integer> unsortables, List<Pair<Integer, Pair<ItemStack, Boolean>>> memory) {
        int hash = 0;
        for(Integer i : unsortables) {
            hash = 31 * hash + (i != null ? i.hashCode() : 0);
        }

        for(Pair<Integer, Pair<ItemStack, Boolean>> entry : memory) {
            int innerHash = 0;

            innerHash = 31 * innerHash + (entry.getFirst() != null ? entry.getFirst().hashCode() : 0);

            Pair<ItemStack, Boolean> innerPair = entry.getSecond();
            innerHash = 31 * innerHash + ItemStack.hashItemAndComponents(innerPair.getFirst());
            innerHash = 31 * innerHash + (innerPair.getSecond() != null ? innerPair.getSecond().hashCode() : 0);

            hash = 31 * hash + innerHash;
        }

        return hash;
    }
}