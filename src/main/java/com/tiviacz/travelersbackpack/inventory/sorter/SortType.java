package com.tiviacz.travelersbackpack.inventory.sorter;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortType
{
    public enum Type
    {
        NAME, MOD, CATEGORY
    }

    static String getStringForSort(ItemStack stack, Type type)
    {
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

    public static String getTabID(int tabID)
    {
        return tabID < 10 ? ("00" + tabID) : tabID < 100 ? ("0" + tabID) : "999";
    }

    private static String specialCases(ItemStack stack)
    {
        Item item = stack.getItem();
        CompoundTag tag = stack.getTag();

        //if(tag != null && tag.contains("SkullOwner"))
       // {
       //     return playerHeadCase(stack);
       // }
        if(stack.getCount() != stack.getMaxStackSize())
        {
            return stackSize(stack);
        }
        if(item instanceof EnchantedBookItem)
        {
            return enchantedBookNameCase(stack);
        }
        if(item instanceof TieredItem)
        {
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

    private static String stackSize(ItemStack stack)
    {
        return stack.getItem().toString() + stack.getCount();
    }

    private static String enchantedBookNameCase(ItemStack stack)
    {
        ListTag enchants = EnchantedBookItem.getEnchantments(stack);
        List<String> names = new ArrayList<>();
        StringBuilder enchantNames = new StringBuilder();

        for(int i = 0; i < enchants.size(); i++)
        {
            CompoundTag enchantTag = enchants.getCompound(i);
            ResourceLocation enchantID = ResourceLocation.tryParse(enchantTag.getString("id"));
            if(enchantID == null)
            {
                continue;
            }
            Enchantment enchant = BuiltInRegistries.ENCHANTMENT.get(enchantID);
            if(enchant == null)
            {
                continue;
            }
            names.add(enchant.getFullname(enchantTag.getInt("lvl")).getString());
        }
        Collections.sort(names);
        for(String enchant : names)
        {
            enchantNames.append(enchant).append(" ");
        }
        return stack.getItem() + " " + enchants.size() + " " + enchantNames;
    }

    private static String toolDuribilityCase(ItemStack stack)
    {
        return stack.getItem().toString() + stack.getDamageValue();
    }
}
