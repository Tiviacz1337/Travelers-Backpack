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

import java.util.*;

public class SortSelector {
    public enum SortType {
        CATEGORY, NAME, COUNT;

        public SortType next() {
            SortType[] values = values();
            int nextIndex = (this.ordinal() + 1) % values.length;
            return values[nextIndex];
        }
    }

    public static Comparator<ItemStack> getSortTypeComparator(List<ItemStack> stacks, SortType type) {
        return switch(type) {
            case CATEGORY -> Comparator.comparing(SortSelector::getStringForCategorySort);
            case NAME -> Comparator.comparing(SortSelector::getStringForNameSort);
            case COUNT -> getCountTypeComparator(stacks);
        };
    }

    public static Comparator<ItemStack> getCountTypeComparator(List<ItemStack> stacks) {
        HashMap<Item, Integer> counts = calculateCount(stacks);
        List<Item> sortedItems = new ArrayList<>(counts.keySet());
        sortedItems.sort((item1, item2) -> {
            int countCompare = Integer.compare(counts.get(item2), counts.get(item1));
            if(countCompare != 0) {
                return countCompare;
            }
            return item1.getDescriptionId().compareTo(item2.getDescriptionId());
        });

        return (stack1, stack2) -> {
            if(stack1.isEmpty() && stack2.isEmpty()) return 0;
            if(stack1.isEmpty()) return 1;
            if(stack2.isEmpty()) return -1;

            int index1 = sortedItems.indexOf(stack1.getItem());
            int index2 = sortedItems.indexOf(stack2.getItem());

            return Integer.compare(index1, index2);
        };
    }

    public static HashMap<Item, Integer> calculateCount(List<ItemStack> stacks) {
        HashMap<Item, Integer> itemCounts = new HashMap<>();

        for(ItemStack stack : stacks) {
            if(!stack.isEmpty()) {
                Item item = stack.getItem();
                int count = stack.getCount();

                itemCounts.put(item, itemCounts.getOrDefault(item, 0) + count);
            }
        }
        return itemCounts;
    }

    public static String getStringForCategorySort(ItemStack stack) {
        String name = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        name = specialCases(stack, name);
        return name;
    }

    public static String getStringForNameSort(ItemStack stack) {
        String key = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        String itemName = key.split(":")[1];
        itemName = specialCases(stack, itemName);
        return itemName;
    }

    private static String specialCases(ItemStack stack, String name) {
        Item item = stack.getItem();
        name = stackSize(stack, name);
        if(item instanceof EnchantedBookItem) {
            return enchantedBookNameCase(stack, name);
        }
        if(item instanceof TieredItem) {
            return toolDurabilityCase(stack, name);
        }
        return name;
    }

    private static String stackSize(ItemStack stack, String name) {
        int invertedCount = 9999 - stack.getCount();
        return name + String.format("%04d", invertedCount);
    }

    private static String enchantedBookNameCase(ItemStack stack, String name) {
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
        return name + " " + enchants.size() + " " + enchantNames;
    }

    private static String toolDurabilityCase(ItemStack stack, String name) {
        return name + stack.getDamageValue();
    }
}