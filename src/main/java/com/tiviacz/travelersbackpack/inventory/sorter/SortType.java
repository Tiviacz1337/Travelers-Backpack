package com.tiviacz.travelersbackpack.inventory.sorter;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SortType {
    public enum Type {
        NAME, MOD, CATEGORY
    }

    static String getStringForSort(ItemStack stack, Type type) {
        Item item = stack.getItem();
        String itemName = specialCases(stack);

        return BuiltInRegistries.ITEM.getKey(item) + itemName;
        /*switch(type)
        {
            case CATEGORY:
                ItemGroup group = item.getItemCategory();
                return (group != null ? String.valueOf(group.getId()) : "999") + Registry.ITEM.getId(item) + itemName;
            case MOD:
                return Registry.ITEM.getId(item) + itemName;
            case NAME:
                if(stack.hasCustomHoverName())
                {
                    return stack + itemName;
                }
        } */
        //return itemName;
    }

    public static String getTabID(int tabID) {
        return tabID < 10 ? ("00" + tabID) : tabID < 100 ? ("0" + tabID) : "999";
    }

    private static String specialCases(ItemStack stack) {
        Item item = stack.getItem();
        //CompoundTag tag = stack.getTag();

        //if(tag != null && tag.contains("SkullOwner"))
        // {
        //     return playerHeadCase(stack);
        // }
        if(stack.getCount() != stack.getMaxStackSize()) {
            return stackSize(stack);
        }
        if(item instanceof EnchantedBookItem) {
            return enchantedBookNameCase(stack);
        }
        if(item instanceof TieredItem) {
            return toolDuribilityCase(stack);
        }
        return item.getDescriptionId(stack);
    }

  /*  private static String playerHeadCase(ItemStack stack)
    {
        CompoundNBT tag = stack.getTag();
        CompoundNBT skullOwner = tag.getCompound("SkullOwner");
        String ownerName = skullOwner.getString("Name");

        // this is duplicated logic, so we should probably refactor
        String count = "";
        if(stack.getCount() != stack.getMaxStackSize())
        {
            count = Integer.toString(stack.getCount());
        }

        return stack.getItem() + " " + ownerName + count;
    } */

    private static String stackSize(ItemStack stack) {
        return stack.getItem().toString() + stack.getCount();
    }

    private static String enchantedBookNameCase(ItemStack stack) {
        Set<Object2IntMap.Entry<Holder<Enchantment>>> enchants = stack.get(DataComponents.STORED_ENCHANTMENTS).entrySet();
        List<String> names = new ArrayList<>();
        StringBuilder enchantNames = new StringBuilder();

        for(Object2IntMap.Entry<Holder<Enchantment>> e : enchants) {
            names.add(Enchantment.getFullname(e.getKey(), e.getIntValue()).getString());
        }

        Collections.sort(names);
        for(String enchant : names) {
            enchantNames.append(enchant).append(" ");
        }
        return stack.getItem() + " " + enchants.size() + " " + enchantNames;
    }

    private static String toolDuribilityCase(ItemStack stack) {
        return stack.getItem().toString() + stack.getDamageValue();
    }
}
