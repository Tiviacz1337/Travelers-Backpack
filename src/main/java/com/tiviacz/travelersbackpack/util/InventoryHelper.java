package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
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

    public static boolean isEmpty(ItemStackHandler handler) {
        for(int i = 0; i < handler.getSlots(); i++) {
            if(!handler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static BackpackContainerContents itemsToList(int size, ItemStackHandler handler) {
        List<ItemStack> list = new ArrayList<>(size);

        for(int i = 0; i < handler.getSlots(); i++) {
            list.add(handler.getStackInSlot(i));
        }
        for(int i = handler.getSlots(); i < size; i++) {
            list.add(ItemStack.EMPTY);
        }
        return BackpackContainerContents.fromItems(size, list);
    }

    public static boolean iterateHandler(ItemStackHandler handler, BiFunction<Integer, ItemStack, Boolean> function) {
        for(int i = 0; i < handler.getSlots(); i++) {
            boolean matches = function.apply(i, handler.getStackInSlot(i).copy());
            if(matches) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack addItemStackToHandler(IItemHandlerModifiable handler, ItemStack stack, boolean simulate) {
        return ItemHandlerHelper.insertItemStacked(handler, stack, simulate);
    }
}