package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.inventory.ItemStackHandler;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class InventoryHelper {
    public static ItemStack removeItem(ItemStackHandler handler, int slot, int amount) {
        if (slot >= 0 && slot < handler.getSlots() && !handler.getStackInSlot(slot).isEmpty() && amount > 0) {
            ItemStack currentStack = handler.getStackInSlot(slot).copy();
            ItemStack stackAtPointer = currentStack.copy();
            currentStack.split(amount);
            handler.setStackInSlot(slot, currentStack);
            return stackAtPointer;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack removeItemShiftClick(ItemStackHandler handler, int slot, int amount) {
        if (slot >= 0 && slot < handler.getSlots() && !handler.getStackInSlot(slot).isEmpty() && amount > 0) {
            ItemStack currentStack = handler.getStackInSlot(slot);
            currentStack.split(amount);
            handler.setStackInSlot(slot, currentStack);
            return handler.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack takeItem(ItemStackHandler handler, int slot) {
        return slot >= 0 && slot < handler.getSlots() ? handler.insertItem(slot, ItemStack.EMPTY, false) : ItemStack.EMPTY;
    }

    public static boolean isEmpty(ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static BackpackContainerContents itemsToList(int size, ItemStackHandler handler) {
        List<ItemStack> list = new ArrayList<>(size);

        for (int i = 0; i < handler.getSlots(); i++) {
            list.add(handler.getStackInSlot(i));
        }
        for (int i = handler.getSlots(); i < size; i++) {
            list.add(ItemStack.EMPTY);
        }
        return BackpackContainerContents.fromItems(size, list);
    }

    public static boolean iterateHandler(ItemStackHandler handler, BiFunction<Integer, ItemStack, Boolean> function) {
        for (int i = 0; i < handler.getSlots(); i++) {
            boolean matches = function.apply(i, handler.getStackInSlot(i).copy());
            if (matches) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack addItemStackToHandler(ItemStackHandler handler, ItemStack stack, boolean simulate) {
        return insertItemStacked(handler, stack, simulate);
    }

    public static ItemStack insertItemStacked(ItemStackHandler inventory, ItemStack stack, boolean simulate) {
        if (inventory == null || stack.isEmpty())
            return stack;

        // not stackable -> just insert into a new slot
        if (!stack.isStackable()) {
            return insertItem(inventory, stack, simulate);
        }

        int sizeInventory = inventory.getSlots();

        // go through the inventory and try to fill up already existing items
        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (ItemStack.isSameItemSameComponents(slot, stack)) {
                stack = inventory.insertItem(i, stack, simulate);

                if (stack.isEmpty()) {
                    break;
                }
            }
        }

        // insert remainder into empty slots
        if (!stack.isEmpty()) {
            // find empty slot
            for (int i = 0; i < sizeInventory; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    stack = inventory.insertItem(i, stack, simulate);
                    if (stack.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return stack;
    }

    public static ItemStack insertItem(ItemStackHandler dest, ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty())
            return stack;

        for (int i = 0; i < dest.getSlots(); i++) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }
}