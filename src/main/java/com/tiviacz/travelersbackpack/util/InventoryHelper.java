package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class InventoryHelper {
    public static ItemStack removeItem(IItemHandlerModifiable handler, int slot, int amount) {
        if(slot >= 0 && slot < handler.getSlots() && !handler.getStackInSlot(slot).isEmpty() && amount > 0) {
            ItemStack currentStack = handler.getStackInSlot(slot).copy();
            ItemStack stackAtPointer = currentStack.copy();
            currentStack.split(amount);
            handler.setStackInSlot(slot, currentStack);
            return stackAtPointer;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack removeItemShiftClick(IItemHandlerModifiable handler, int slot, int amount) {
        if(slot >= 0 && slot < handler.getSlots() && !handler.getStackInSlot(slot).isEmpty() && amount > 0) {
            ItemStack currentStack = handler.getStackInSlot(slot);
            currentStack.split(amount);
            handler.setStackInSlot(slot, currentStack);
            return handler.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack takeItem(IItemHandler handler, int slot) {
        return slot >= 0 && slot < handler.getSlots() ? handler.insertItem(slot, ItemStack.EMPTY, false) : ItemStack.EMPTY;
    }

    public static boolean isEmpty(IItemHandler handler) {
        for(int i = 0; i < handler.getSlots(); i++) {
            if(!handler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static void iterateHandler(IItemHandler handler, BiConsumer<Integer, ItemStack> consumer) {
        for(int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            consumer.accept(i, stack);
        }
    }

    public static boolean iterate(IItemHandler handler, BiFunction<Integer, ItemStack, Boolean> function) {
        for(int i = 0; i < handler.getSlots(); i++) {
            boolean matches = function.apply(i, handler.getStackInSlot(i).copy());
            if(matches) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack addItemStackToHandler(IItemHandler handler, ItemStack stack, boolean simulate) {
        return ItemHandlerHelper.insertItemStacked(handler, stack, simulate);
    }

    public static ItemStack extractFromBackpack(IItemHandler handler, ItemStack stack, int amount, boolean simulate) {
        for(int i = 0; i < handler.getSlots(); i++) {
            if(ItemStack.isSameItemSameTags(stack, handler.getStackInSlot(i))) {
                return handler.extractItem(i, amount, simulate);
            }
        }
        return ItemStack.EMPTY;
    }
}