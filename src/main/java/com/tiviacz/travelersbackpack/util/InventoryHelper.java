package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.inventory.transfer.BackpackResourceHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class InventoryHelper {
    public static ItemStack removeItem(BackpackResourceHandler handler, int slot, int amount) {
        if(slot >= 0 && slot < handler.getSlots() && !handler.getStackInSlot(slot).isEmpty() && amount > 0) {
            ItemStack currentStack = handler.getStackInSlot(slot).copy();
            ItemStack stackAtPointer = currentStack.copy();
            currentStack.split(amount);
            handler.setStackInSlot(slot, currentStack);
            return stackAtPointer;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack removeItemShiftClick(BackpackResourceHandler handler, int slot, int amount) {
        if(slot >= 0 && slot < handler.getSlots() && !handler.getStackInSlot(slot).isEmpty() && amount > 0) {
            ItemStack currentStack = handler.getStackInSlot(slot);
            currentStack.split(amount);
            handler.setStackInSlot(slot, currentStack);
            return handler.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack takeItem(BackpackResourceHandler handler, int slot) {
        return slot >= 0 && slot < handler.getSlots() ? ItemUtil.insertItemReturnRemaining(handler, slot, ItemStack.EMPTY, false, null) : ItemStack.EMPTY;
    }

    public static boolean isEmpty(BackpackResourceHandler handler) {
        for(int i = 0; i < handler.getSlots(); i++) {
            if(!handler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static BackpackContainerContents itemsToList(int size, BackpackResourceHandler handler) {
        List<ItemStack> list = new ArrayList<>(size);

        for(int i = 0; i < handler.getSlots(); i++) {
            list.add(handler.getStackInSlot(i));
        }
        for(int i = handler.getSlots(); i < size; i++) {
            list.add(ItemStack.EMPTY);
        }
        return BackpackContainerContents.fromItems(size, list);
    }

    public static void iterateHandler(ResourceHandler<ItemResource> handler, BiConsumer<Integer, ItemStack> consumer) {
        for(int i = 0; i < handler.size(); i++) {
            ItemStack stack = handler.getResource(i).toStack(handler.getAmountAsInt(i));
            consumer.accept(i, stack);
        }
    }

    public static void iterateHandler(IItemHandler handler, BiConsumer<Integer, ItemStack> consumer) {
        for(int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            consumer.accept(i, stack);
        }
    }

    public static boolean iterate(ResourceHandler<ItemResource> handler, BiFunction<Integer, ItemStack, Boolean> function) {
        for(int i = 0; i < handler.size(); i++) {
            boolean matches = function.apply(i, handler.getResource(i).toStack(handler.getAmountAsInt(i)).copy());
            if(matches) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack addItemStackToHandler(IItemHandlerModifiable handler, ItemStack stack, boolean simulate) {
        return ItemHandlerHelper.insertItemStacked(handler, stack, simulate);
    }

    public static ItemStack extractFromBackpack(BackpackResourceHandler handler, ItemStack stack, int amount, boolean simulate) {
        for(int i = 0; i < handler.getSlots(); i++) {
            if(ItemStack.isSameItemSameComponents(stack, handler.getStackInSlot(i))) {
                return extractItem(handler, i, amount, simulate);
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack extractItem(ResourceHandler<ItemResource> handler, int slot, int amount, boolean simulate) {
        if(amount <= 0) {
            return ItemStack.EMPTY;
        }
        var resource = handler.getResource(slot);
        if(resource.isEmpty()) {
            return ItemStack.EMPTY;
        }
        // We have to limit to the max stack size, per the contract of extractItem
        amount = Math.min(amount, resource.getMaxStackSize());
        try(var tx = Transaction.openRoot()) {
            int extracted = handler.extract(slot, resource, amount, tx);
            if(!simulate) {
                tx.commit();
            }
            return resource.toStack(extracted);
        }
    }
}