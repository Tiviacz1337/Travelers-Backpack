package com.tiviacz.travelersbackpack.inventory.upgrades.voiding;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class VoidFilterSettings {
    //Button Types
    public static final int ALLOW_MODE = 0;
    public static final int OBJECT_CATEGORY = 1;
    public static final int IGNORE_MODE = 2;

    //Options
    public static final int ALLOW = 0;
    public static final int BLOCK = 1;
    public static final int MATCH_CONTENTS = 2;

    public static final int ITEM = 0;
    public static final int MOD_ID = 1;

    public static final int IGNORE_COMPONENTS = 0;
    public static final int MATCH_COMPONENTS = 1;

    private List<ItemStack> filterItems;
    private List<Integer> filterSettings;
    private ItemStackHandler storage;

    public VoidFilterSettings(ItemStackHandler storage, List<ItemStack> items, List<Integer> filterSettings) {
        this.filterItems = items;
        this.filterSettings = filterSettings;
        this.storage = storage;
    }

    public List<Integer> getSettings() {
        return this.filterSettings;
    }

    public boolean canVoid(ItemStack stack) {
        if(filterSettings.get(ALLOW_MODE) == ALLOW) {
            return this.filterItems.stream().skip(1).anyMatch(filterStack -> compare(filterStack, stack));
        }
        if(filterSettings.get(ALLOW_MODE) == BLOCK) {
            return this.filterItems.stream().skip(1).noneMatch(filterStack -> compare(filterStack, stack));
        }
        if(filterSettings.get(ALLOW_MODE) == MATCH_CONTENTS) {
            return streamStorageContents().anyMatch(filterStack -> compare(filterStack, stack));
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
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals(BuiltInRegistries.ITEM.getKey(other.getItem()).getNamespace());
        //return stack.getItem().getCreatorModId(stack).equals(other.getItem().getCreatorModId(other));
    }

    public void updateFilter(List<ItemStack> items) {
        this.filterItems = items.stream().limit(TravelersBackpackConfig.getConfig().backpackUpgrades.voidUpgradeSettings.filterSlotCount).filter(stack -> !stack.isEmpty()).toList();
    }

    public void updateSettings(List<Integer> settings) {
        this.filterSettings = settings;
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
}