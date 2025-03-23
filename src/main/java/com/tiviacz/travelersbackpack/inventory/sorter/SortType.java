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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortType {
    public enum Type {
        NAME, MOD, CATEGORY
    }

    static String getStringForSort(ItemStack stack, Type type) {
        Item item = stack.getItem();
        String itemName = specialCases(stack);
        return BuiltInRegistries.ITEM.getKey(item) + itemName;
    }

    private static String specialCases(ItemStack stack) {
        Item item = stack.getItem();
        if(stack.getCount() != stack.getMaxStackSize()) {
            return stackSize(stack);
        }
        if(item instanceof EnchantedBookItem) {
            return enchantedBookNameCase(stack);
        }
        if(item instanceof TieredItem) {
            return toolDurabilityCase(stack);
        }
        return item.getDescriptionId(stack);
    }

    private static String stackSize(ItemStack stack) {
        return stack.getItem().toString() + stack.getCount();
    }

    private static String enchantedBookNameCase(ItemStack stack) {
        ListTag enchants = EnchantedBookItem.getEnchantments(stack);
        List<String> names = new ArrayList<>();
        StringBuilder enchantNames = new StringBuilder();
        for(int i = 0; i < enchants.size(); i++) {
            CompoundTag enchantTag = enchants.getCompound(i);
            ResourceLocation enchantID = ResourceLocation.tryParse(enchantTag.getString("id"));
            if(enchantID == null) {
                continue;
            }
            Enchantment enchant = ForgeRegistries.ENCHANTMENTS.getValue(enchantID);
            if(enchant == null) {
                continue;
            }
            names.add(enchant.getFullname(enchantTag.getInt("lvl")).getString());
        }
        Collections.sort(names);
        for(String enchant : names) {
            enchantNames.append(enchant).append(" ");
        }
        return stack.getItem() + " " + enchants.size() + " " + enchantNames;
    }

    private static String toolDurabilityCase(ItemStack stack) {
        return stack.getItem().toString() + stack.getDamageValue();
    }
}