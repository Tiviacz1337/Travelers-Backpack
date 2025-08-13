package com.tiviacz.travelersbackpack.inventory.upgrades.magnet;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterSettingsBase;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagnetFilterSettings extends FilterSettingsBase {
    //Button Types
    public static final int ALLOW_MODE = 0;
    public static final int OBJECT_CATEGORY = 1;
    public static final int IGNORE_MODE = 2;

    //Options
    public static final int ALLOW = 0;
    public static final int BLOCK = 1;

    public static final int ITEM = 0;
    public static final int MOD_ID = 1;
    public static final int TAG_ID = 2;

    public static final int MATCH_COMPONENTS = 0;
    public static final int IGNORE_COMPONENTS = 1;

    public MagnetFilterSettings(ItemStackHandler storage, List<ItemStack> items, List<Integer> filterSettings, List<String> filterTags) {
        super(storage, items, filterSettings, filterTags, TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.filterSlotCount.get());
    }

    @Override
    public boolean matchesFilter(@Nullable Player player, ItemStack stack) {
        if(filterSettings.get(ALLOW_MODE) == ALLOW) {
            if(isTagFilter()) {
                for(TagKey<Item> tag : this.tags) {
                    if(stack.is(tag)) {
                        return true;
                    }
                }
                return false;
            }
            return this.filterItems.stream().anyMatch(filterStack -> compare(filterStack, stack));
        }
        if(filterSettings.get(ALLOW_MODE) == BLOCK) {
            if(isTagFilter()) {
                for(TagKey<Item> tag : this.tags) {
                    if(stack.is(tag)) {
                        return false;
                    }
                }
                return true;
            }
            return this.filterItems.stream().noneMatch(filterStack -> compare(filterStack, stack));
        }
        return false;
    }

    @Override
    public boolean isTagFilter() {
        return filterSettings.get(OBJECT_CATEGORY) == TAG_ID;
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