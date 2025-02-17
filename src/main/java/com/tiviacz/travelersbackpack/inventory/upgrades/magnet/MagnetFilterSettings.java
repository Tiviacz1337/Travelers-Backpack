package com.tiviacz.travelersbackpack.inventory.upgrades.magnet;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class MagnetFilterSettings {
    //Button Types
    public static final int ALLOW_MODE = 0;
    public static final int OBJECT_CATEGORY = 1;
    public static final int IGNORE_MODE = 2;

    //Options
    public static final int ALLOW = 0;
    public static final int BLOCK = 1;

    public static final int ITEM = 0;
    public static final int MOD_ID = 1;

    public static final int IGNORE_COMPONENTS = 0;
    public static final int MATCH_COMPONENTS = 1;

    private List<ItemStack> filterItems;
    private List<Integer> filterSettings;
    private ItemStackHandler storage;
    private HolderLookup.Provider access;

    public MagnetFilterSettings(ItemStackHandler storage, List<ItemStack> items, List<Integer> filterSettings, HolderLookup.Provider access) {
        this.filterItems = items;
        this.filterSettings = filterSettings;
        this.storage = storage;
        this.access = access;
    }

    public List<Integer> getSettings() {
        return this.filterSettings;
    }

    public boolean canPickup(ItemStack stack) {
        if(filterSettings.get(ALLOW_MODE) == ALLOW) {
            return this.filterItems.stream().anyMatch(filterStack -> compare(filterStack, stack));
        }
        if(filterSettings.get(ALLOW_MODE) == BLOCK) {
            return this.filterItems.stream().noneMatch(filterStack -> compare(filterStack, stack));
        }
        return false;
    }

    public boolean compare(ItemStack stack, ItemStack other) {
        if(filterSettings.get(OBJECT_CATEGORY) == ITEM) {
            return compareItemStack(stack, other);
        } else {
            return compareModId(stack, other);
        }
    }

    public boolean compareItemStack(ItemStack stack, ItemStack other) {
        if(filterSettings.get(IGNORE_MODE) == IGNORE_COMPONENTS) {
            return ItemStack.isSameItem(stack, other);
        } else {
            return ItemStack.isSameItemSameComponents(stack, other);
        }
    }

    public boolean compareModId(ItemStack stack, ItemStack other) {
        return stack.getItem().getCreatorModId(this.access, stack).equals(other.getItem().getCreatorModId(this.access, other));
    }

    public void updateFilter(List<ItemStack> items) {
        this.filterItems = items.stream().limit(TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.filterSlotCount.get()).filter(stack -> !stack.isEmpty()).toList();
    }

    public void updateSettings(List<Integer> settings) {
        this.filterSettings = settings;
    }
}

