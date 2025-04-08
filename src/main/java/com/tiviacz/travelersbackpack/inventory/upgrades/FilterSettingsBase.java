package com.tiviacz.travelersbackpack.inventory.upgrades;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class FilterSettingsBase<T extends UpgradeBase<T>> {
    protected List<ItemStack> filterItems;
    protected List<Integer> filterSettings;
    protected ItemStackHandler storage;
    protected HolderLookup.Provider access;
    private final int slotLimit;

    public FilterSettingsBase(ItemStackHandler storage, List<ItemStack> items, List<Integer> filterSettings, HolderLookup.Provider access, int slotLimit) {
        this.filterItems = items;
        this.filterSettings = filterSettings;
        this.storage = storage;
        this.access = access;
        this.slotLimit = slotLimit;
    }

    public abstract boolean matchesFilter(@Nullable Player player, ItemStack stack);

    public List<Integer> getSettings() {
        return this.filterSettings;
    }

    public boolean compareModId(ItemStack stack, ItemStack other) {
        return stack.getItem().getCreatorModId(this.access, stack).equals(other.getItem().getCreatorModId(this.access, other));
    }

    public Stream<ItemStack> streamStorageContents() {
        List<ItemStack> arrayList = new ArrayList<>();
        for(int i = 0; i < storage.getSlots(); i++) {
            if(!storage.getStackInSlot(i).isEmpty()) {
                arrayList.add(storage.getStackInSlot(i));
            }
        }
        return arrayList.stream();
    }

    public void updateFilter(@Nullable List<ItemStack> items) {
        if(items == null) return;
        this.filterItems = items.stream().limit(this.slotLimit).filter(stack -> !stack.isEmpty()).toList();
    }

    public void updateSettings(List<Integer> settings) {
        this.filterSettings = settings;
    }
}