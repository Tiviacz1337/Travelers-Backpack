package com.tiviacz.travelersbackpack.components;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record Slots(List<Integer> unsortables, List<Pair<Integer, Pair<ItemStack, Boolean>>> memory) {
    public static final Slots EMPTY = new Slots(List.of(), List.of());

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