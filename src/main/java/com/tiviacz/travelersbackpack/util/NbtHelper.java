package com.tiviacz.travelersbackpack.util;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NbtHelper {
    public static boolean isInteger(String key) {
        return key.equals(ModDataHelper.STORAGE_SLOTS) || key.equals(ModDataHelper.UPGRADE_SLOTS) || key.equals(ModDataHelper.TOOL_SLOTS) || key.equals(ModDataHelper.TIER) || key.equals(ModDataHelper.COOLDOWN) ||
                key.equals(ModDataHelper.SORT_TYPE) || key.equals(ModDataHelper.SLEEPING_BAG_COLOR) || key.equals(ModDataHelper.UPGRADE_TICK_INTERVAL) || key.equals(ModDataHelper.COLOR) || key.equals(ModDataHelper.COOKING_TOTAL_TIME) || key.equals(ModDataHelper.BURN_TOTAL_TIME);
    }

    public static boolean isBoolean(String key) {
        return key.equals(ModDataHelper.TAB_OPEN) || key.equals(ModDataHelper.UPGRADE_ENABLED) || key.equals(ModDataHelper.SHIFT_CLICK_TO_BACKPACK) || key.equals(ModDataHelper.SHOW_TOOL_SLOTS) ||
                key.equals(ModDataHelper.SHOW_MORE_BUTTONS) || key.equals(ModDataHelper.IS_PLAYING) || key.equals(ModDataHelper.ABILITY_ENABLED) || key.equals(ModDataHelper.IS_VISIBLE);
    }

    public static boolean isLong(String key) {
        return key.equals(ModDataHelper.BURN_FINISH_TIME) || key.equals(ModDataHelper.COOKING_FINISH_TIME);
    }

    public static void set(ItemStack stack, String key, Object value) {
        if(isInteger(key)) {
            stack.getOrCreateTag().putInt(key, (int)value);
            return;
        }
        if(isBoolean(key)) {
            stack.getOrCreateTag().putBoolean(key, (boolean)value);
            return;
        }
        if(isLong(key)) {
            stack.getOrCreateTag().putLong(key, (Long)value);
            return;
        }
        switch(key) {
            case ModDataHelper.BACKPACK_CONTAINER:
                stack.getOrCreateTag().put(key, serializeHandler((ItemStackHandler)value));
                break;
            case ModDataHelper.UPGRADES:
                stack.getOrCreateTag().put(key, serializeHandler((ItemStackHandler)value));
                break;
            case ModDataHelper.TOOLS_CONTAINER:
                stack.getOrCreateTag().put(key, serializeHandler((ItemStackHandler)value));
                break;
            case ModDataHelper.STARTER_UPGRADES:
                stack.getOrCreateTag().put(key, serializeStarterUpgrades((List<ItemStack>)value));
                break;
            case ModDataHelper.RENDER_INFO:
                stack.getOrCreateTag().put(key, ((RenderInfo)value).compoundTag());
                break;
            case ModDataHelper.FLUIDS:
                stack.getOrCreateTag().put(key, serializeFluids((Fluids)value));
                break;
            case ModDataHelper.FILTER_SETTINGS:
                stack.getOrCreateTag().put(key, serializeIntList((List<Integer>)value, key));
                break;
            case ModDataHelper.FILTER_TAGS:
                stack.getOrCreateTag().put(key, serializeStringList((List<String>)value, key));
                break;
            case ModDataHelper.UNSORTABLE_SLOTS:
                stack.getOrCreateTag().put(key, serializeIntList((List<Integer>)value, key));
                break;
            case ModDataHelper.MEMORY_SLOTS:
                stack.getOrCreateTag().put(key, serializeMemorySlots((List<Pair<Integer, Pair<ItemStack, Boolean>>>)value));
                break;
            case ModDataHelper.HOSE_MODES:
                stack.getOrCreateTag().put(key, serializeIntList((List<Integer>)value, key));
                break;
            default:
                stack.getOrCreateTag().put(key, (CompoundTag)value);
                break;
        }
    }

    public static <T> T getOrDefault(ItemStack stack, String key, T defaultValue) {
        if(stack.hasTag() && stack.getTag().contains(key)) {
            if(isInteger(key)) {
                return (T)Integer.valueOf(stack.getTag().getInt(key));
            }
            if(isBoolean(key)) {
                return (T)Boolean.valueOf(stack.getTag().getBoolean(key));
            }
            if(isLong(key)) {
                return (T)Long.valueOf(stack.getTag().getLong(key));
            }

            if(stack.getTag().contains(key)) {
                switch(key) {
                    case ModDataHelper.BACKPACK_CONTAINER:
                        return (T)deserializeHandler(stack, key);
                    case ModDataHelper.UPGRADES:
                        return (T)deserializeHandler(stack, key);
                    case ModDataHelper.TOOLS_CONTAINER:
                        return (T)deserializeHandler(stack, key);
                    case ModDataHelper.STARTER_UPGRADES:
                        return (T)deserializeStarterUpgrades(stack.getTag().getCompound(key));
                    case ModDataHelper.RENDER_INFO:
                        return (T)new RenderInfo(stack.getTag().getCompound(key));
                    case ModDataHelper.FLUIDS:
                        return (T)new Fluids(deserializeLeftFluidStack(stack.getTag().getCompound(key)), deserializeRightFluidStack(stack.getTag().getCompound(key)));
                    case ModDataHelper.FILTER_SETTINGS:
                        return (T)deserializeIntList(stack.getTag(), key);
                    case ModDataHelper.FILTER_TAGS:
                        return (T)deserializeStringList(stack.getTag(), key);
                    case ModDataHelper.UNSORTABLE_SLOTS:
                        return (T)deserializeIntList(stack.getTag(), key);
                    case ModDataHelper.MEMORY_SLOTS:
                        return (T)deserializeMemorySlots(stack.getTag());
                    case ModDataHelper.HOSE_MODES:
                        return (T)deserializeIntList(stack.getTag(), key);
                    default:
                        return (T)stack.getTag().getCompound(key);
                }
            }
        }
        return defaultValue;
    }

    @Nullable
    public static <T> T get(ItemStack stack, String key) {
        if(stack.hasTag() && stack.getTag().contains(key)) {
            if(isInteger(key)) {
                return (T)Integer.valueOf(stack.getTag().getInt(key));
            }
            if(isBoolean(key)) {
                return (T)Boolean.valueOf(stack.getTag().getBoolean(key));
            }
            if(isLong(key)) {
                return (T)Long.valueOf(stack.getTag().getLong(key));
            }

            if(stack.getTag().contains(key)) {
                switch(key) {
                    case ModDataHelper.BACKPACK_CONTAINER:
                        return (T)deserializeHandler(stack, key);
                    case ModDataHelper.UPGRADES:
                        return (T)deserializeHandler(stack, key);
                    case ModDataHelper.TOOLS_CONTAINER:
                        return (T)deserializeHandler(stack, key);
                    case ModDataHelper.STARTER_UPGRADES:
                        return (T)deserializeStarterUpgrades(stack.getTag().getCompound(key));
                    case ModDataHelper.RENDER_INFO:
                        return (T)new RenderInfo(stack.getTag().getCompound(key));
                    case ModDataHelper.FLUIDS:
                        return (T)new Fluids(deserializeLeftFluidStack(stack.getTag().getCompound(key)), deserializeRightFluidStack(stack.getTag().getCompound(key)));
                    case ModDataHelper.FILTER_SETTINGS:
                        return (T)deserializeIntList(stack.getTag(), key);
                    case ModDataHelper.FILTER_TAGS:
                        return (T)deserializeStringList(stack.getTag(), key);
                    case ModDataHelper.UNSORTABLE_SLOTS:
                        return (T)deserializeIntList(stack.getTag(), key);
                    case ModDataHelper.MEMORY_SLOTS:
                        return (T)deserializeMemorySlots(stack.getTag());
                    case ModDataHelper.HOSE_MODES:
                        return (T)deserializeIntList(stack.getTag(), key);
                    default:
                        return (T)stack.getTag().getCompound(key);
                }
            }
        }
        return null;
    }

    public static boolean has(ItemStack stack, String key) {
        if(stack.hasTag()) {
            return stack.getTag().contains(key);
        }
        return false;
    }

    public static void remove(ItemStack stack, String key) {
        if(stack.hasTag()) {
            stack.getTag().remove(key);
        }
    }

    public static CompoundTag serializeHandler(ItemStackHandler handler) {
        return serializeNBT(handler);
    }

    public static CompoundTag serializeNBT(ItemStackHandler handler) {
        ListTag nbtTagList = new ListTag();
        for(int i = 0; i < handler.getSlots(); i++) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putInt("Slot", i);
            handler.getStackInSlot(i).save(itemTag);
            nbtTagList.add(itemTag);
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", nbtTagList.size());
        return nbt;
    }

    public static CompoundTag serializeStarterUpgrades(List<ItemStack> upgrades) {
        return serializeList(upgrades);
    }

    public static List<ItemStack> deserializeStarterUpgrades(CompoundTag tag) {
        return deserializeList(tag);
    }

    public static CompoundTag getHandlerNbt(ItemStack stack, String key) {
        return stack.getTag().getCompound(key);
    }

    public static CompoundTag expandTag(ItemStack stack, String key, int defaultSize) {
        CompoundTag tag = getHandlerNbt(stack, key);
        NonNullList<ItemStack> stacks = NonNullList.withSize(defaultSize, ItemStack.EMPTY);
        ListTag tagList = tag.getList("Items", 10);
        for(int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if(slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, ItemStack.of(itemTags));
            }
        }
        return serializeHandler(new ItemStackHandler(stacks));
    }

    public static NonNullList<ItemStack> deserializeHandler(ItemStack stack, String key) {
        ListTag tagList = stack.getTag().getCompound(key).getList("Items", 10);
        int size = stack.getTag().contains("Size") ? stack.getTag().getCompound(key).getInt("Size") : tagList.size();
        NonNullList<ItemStack> stacks = NonNullList.withSize(size, ItemStack.EMPTY);

        for(int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if(slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, ItemStack.of(itemTags));
            }
        }
        return stacks;
    }

    public static void update(ItemStack backpack, String key, int defaultSize, int slot, ItemStack stack) {
        if(backpack.hasTag() && backpack.getTag().contains(key)) {
            CompoundTag unboxed = backpack.getTag().getCompound(key);
            ListTag itemsListed = unboxed.getList("Items", 10);

            CompoundTag newItemTag = new CompoundTag();
            newItemTag.putInt("Slot", slot);
            stack.save(newItemTag);

            if(slot >= 0 && slot < itemsListed.size()) {
                itemsListed.set(slot, newItemTag);
            }
        } else {
            ListTag nbtTagList = new ListTag();

            for(int i = 0; i < defaultSize; ++i) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                ItemStack.EMPTY.copy().save(itemTag);
                nbtTagList.add(itemTag);
            }

            CompoundTag newItemTag = new CompoundTag();
            newItemTag.putInt("Slot", slot);
            stack.save(newItemTag);

            if(slot >= 0 && slot < nbtTagList.size()) {
                nbtTagList.set(slot, newItemTag);
            }

            CompoundTag nbt = new CompoundTag();
            nbt.put("Items", nbtTagList);
            nbt.putInt("Size", nbtTagList.size());
            backpack.getOrCreateTag().put(key, nbt);
        }
    }

    public static FluidStack deserializeLeftFluidStack(CompoundTag tag) {
        return FluidStack.loadFluidStackFromNBT(tag.getCompound("leftFluidStack"));
    }

    public static FluidStack deserializeRightFluidStack(CompoundTag tag) {
        return FluidStack.loadFluidStackFromNBT(tag.getCompound("rightFluidStack"));
    }

    public static CompoundTag serializeFluids(Fluids fluids) {
        CompoundTag tag = new CompoundTag();
        tag.put("leftFluidStack", fluids.leftFluidStack().writeToNBT(new CompoundTag()));
        tag.put("rightFluidStack", fluids.rightFluidStack().writeToNBT(new CompoundTag()));
        return tag;
    }

    public static CompoundTag serializeList(List<ItemStack> stacks) {
        ListTag nbtTagList = new ListTag();

        for(int i = 0; i < stacks.size(); ++i) {
            if(!stacks.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                stacks.get(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        return nbt;
    }

    public static List<ItemStack> deserializeList(CompoundTag tag) {
        ListTag tagList = tag.getList("Items", 10);
        List<ItemStack> stacks = new ArrayList<>();
        for(int i = 0; i < tagList.size(); i++) {
            stacks.add(ItemStack.of(tagList.getCompound(i)));
        }
        return stacks;
    }

    public static ListTag serializeIntList(List<Integer> ints, String key) {
        ListTag nbtTagList = new ListTag();
        for(int i = 0; i < ints.size(); i++) {
            nbtTagList.add(i, IntTag.valueOf(ints.get(i)));
        }
        return nbtTagList;
    }

    public static List<Integer> deserializeIntList(CompoundTag tag, String key) {
        ListTag tagList = tag.getList(key, Tag.TAG_INT);
        /**
         * PATCH FOR ISSUE
         */
        if(tagList.isEmpty()) {
            ListTag oldList = tag.getCompound(key).getList(key, Tag.TAG_INT);
            tag.put(key, oldList);
            tagList = oldList;
        }
        List<Integer> filter = new ArrayList<>();
        for(int i = 0; i < tagList.size(); i++) {
            filter.add(tagList.getInt(i));
        }
        return filter;
    }

    public static ListTag serializeStringList(List<String> strings, String key) {
        ListTag nbtTagList = new ListTag();
        for(int i = 0; i < strings.size(); i++) {
            nbtTagList.add(i, StringTag.valueOf(strings.get(i)));
        }
        return nbtTagList;
    }

    public static List<String> deserializeStringList(CompoundTag tag, String key) {
        ListTag tagList = tag.getList(key, Tag.TAG_STRING);
        List<String> filter = new ArrayList<>();
        for(int i = 0; i < tagList.size(); i++) {
            filter.add(tagList.getString(i));
        }
        return filter;
    }

    public static ListTag serializeMemorySlots(List<Pair<Integer, Pair<ItemStack, Boolean>>> memorySlots) {
        ListTag nbtTagList = new ListTag();
        for(int i = 0; i < memorySlots.size(); i++) {
            CompoundTag pairTag = new CompoundTag();
            pairTag.putInt("Slot", memorySlots.get(i).getFirst());
            pairTag.put("Pair", serializeMemoryStack(memorySlots.get(i).getSecond()));
            nbtTagList.add(pairTag);
        }
        return nbtTagList;
    }

    public static CompoundTag serializeMemoryStack(Pair<ItemStack, Boolean> memoryStack) {
        CompoundTag pair = new CompoundTag();
        memoryStack.getFirst().save(pair);
        pair.putBoolean("matchNbt", memoryStack.getSecond());
        return pair;
    }

    public static List<Pair<Integer, Pair<ItemStack, Boolean>>> deserializeMemorySlots(CompoundTag tag) {
        ListTag tagList = tag.getList(ModDataHelper.MEMORY_SLOTS, Tag.TAG_COMPOUND);
        /**
         * PATCH FOR ISSUE
         */
        if(tagList.isEmpty()) {
            ListTag oldList = tag.getCompound(ModDataHelper.MEMORY_SLOTS).getList(ModDataHelper.MEMORY_SLOTS, Tag.TAG_COMPOUND);
            tag.put(ModDataHelper.MEMORY_SLOTS, oldList);
            tagList = oldList;
        }
        List<Pair<Integer, Pair<ItemStack, Boolean>>> memorySlots = new ArrayList<>();
        for(int i = 0; i < tagList.size(); i++) {
            CompoundTag pairTag = tagList.getCompound(i);
            int index = pairTag.getInt("Slot");
            Pair<ItemStack, Boolean> pair = deserializeMemoryStack(pairTag.getCompound("Pair"));
            memorySlots.add(Pair.of(index, pair));
        }
        return memorySlots;
    }

    public static Pair<ItemStack, Boolean> deserializeMemoryStack(CompoundTag tag) {
        return Pair.of(ItemStack.of(tag), tag.getBoolean("matchNbt"));
    }

    public static CompoundTag serializeMemorySlotsPacket(List<Pair<Integer, Boolean>> memorySlots) {
        ListTag nbtTagList = new ListTag();
        for(int i = 0; i < memorySlots.size(); i++) {
            CompoundTag pairTag = new CompoundTag();
            pairTag.putInt("Slot", memorySlots.get(i).getFirst());
            pairTag.putBoolean("matchNbt", memorySlots.get(i).getSecond());
            nbtTagList.add(pairTag);
        }
        CompoundTag tag = new CompoundTag();
        tag.put(ModDataHelper.MEMORY_SLOTS, nbtTagList);
        return tag;
    }

    public static List<Pair<Integer, Boolean>> deserializeMemorySlotsPacket(CompoundTag tag) {
        ListTag tagList = tag.getList(ModDataHelper.MEMORY_SLOTS, Tag.TAG_COMPOUND);
        List<Pair<Integer, Boolean>> memorySlots = new ArrayList<>();
        for(int i = 0; i < tagList.size(); i++) {
            CompoundTag pairTag = tagList.getCompound(i);
            int index = pairTag.getInt("Slot");
            boolean matchNbt = pairTag.getBoolean("matchNbt");
            memorySlots.add(Pair.of(index, matchNbt));
        }
        return memorySlots;
    }
}
