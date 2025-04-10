package com.tiviacz.travelersbackpack.inventory.upgrades.pickup;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterSettingsBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AutoPickupFilterSettings extends FilterSettingsBase<AutoPickupUpgrade> {
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

    public AutoPickupFilterSettings(ItemStackHandler storage, List<ItemStack> items, List<Integer> filterSettings) {
        super(storage, items, filterSettings, TravelersBackpackConfig.getConfig().backpackUpgrades.pickupUpgradeSettings.filterSlotCount);
    }

    @Override
    public boolean matchesFilter(@Nullable Player player, ItemStack stack) {
        if(filterSettings.get(ALLOW_MODE) == ALLOW) {
            return this.filterItems.stream().anyMatch(filterStack -> compare(filterStack, stack));
        }
        if(filterSettings.get(ALLOW_MODE) == BLOCK) {
            return this.filterItems.stream().noneMatch(filterStack -> compare(filterStack, stack));
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
}