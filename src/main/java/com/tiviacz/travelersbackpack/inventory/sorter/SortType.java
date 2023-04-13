package com.tiviacz.travelersbackpack.inventory.sorter;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

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

        return Registries.ITEM.getId(item) + itemName;
        //ItemGroup group = item.getGroup();
        //return (group != null ? getGroupID(group.getIndex()) : "999") + Registry.ITEM.getId(item) + itemName;

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

    public static String getGroupID(int groupID)
    {
        return groupID < 10 ? ("00" + groupID) : groupID < 100 ? ("0" + groupID) : "999";
    }

    private static String specialCases(ItemStack stack)
    {
        Item item = stack.getItem();
        NbtCompound tag = stack.getNbt();

        //if(tag != null && tag.contains("SkullOwner"))
       // {
       //     return playerHeadCase(stack);
       // }
        if(stack.getCount() != stack.getMaxCount())
        {
            return stackSize(stack);
        }
        if(item instanceof EnchantedBookItem)
        {
            return enchantedBookNameCase(stack);
        }
        if(item instanceof ToolItem)
        {
            return toolDuribilityCase(stack);
        }
        return item.getTranslationKey(stack);
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
        NbtList enchants = EnchantedBookItem.getEnchantmentNbt(stack);
        List<String> names = new ArrayList<>();
        StringBuilder enchantNames = new StringBuilder();

        for(int i = 0; i < enchants.size(); i++)
        {
            NbtCompound enchantTag = enchants.getCompound(i);
            Identifier enchantID = Identifier.tryParse(enchantTag.getString("id"));
            if(enchantID == null)
            {
                continue;
            }
            Enchantment enchant = Registries.ENCHANTMENT.get(enchantID);
            if(enchant == null)
            {
                continue;
            }
            names.add(enchant.getName(enchantTag.getInt("lvl")).getString());
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
        return stack.getItem().toString() + stack.getDamage();
    }
}