package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class InventoryHelper {
    public static ItemStack removeItem(ItemStacksResourceHandler handler, int slot, int amount) {
        if(slot >= 0 && slot < StacksHandlerUtils.getSlots(handler) && !StacksHandlerUtils.getStackInSlot(handler, slot).isEmpty() && amount > 0) {
            ItemStack currentStack = StacksHandlerUtils.getStackInSlot(handler, slot).copy();
            ItemStack stackAtPointer = currentStack.copy();
            currentStack.split(amount);
            StacksHandlerUtils.setStackInSlot(handler, slot, currentStack);
            return stackAtPointer;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack removeItemShiftClick(ItemStacksResourceHandler handler, int slot, int amount) {
        if(slot >= 0 && slot < StacksHandlerUtils.getSlots(handler) && !StacksHandlerUtils.getStackInSlot(handler, slot).isEmpty() && amount > 0) {
            ItemStack currentStack = StacksHandlerUtils.getStackInSlot(handler, slot);
            currentStack.split(amount);
            StacksHandlerUtils.setStackInSlot(handler, slot, currentStack);
            return StacksHandlerUtils.getStackInSlot(handler, slot);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack takeItem(ItemStacksResourceHandler handler, int slot) {
        return slot >= 0 && slot < StacksHandlerUtils.getSlots(handler) ? ItemUtil.insertItemReturnRemaining(handler, slot, ItemStack.EMPTY, false, null) : ItemStack.EMPTY;
    }

    public static boolean isEmpty(ItemStacksResourceHandler handler) {
        for(int i = 0; i < StacksHandlerUtils.getSlots(handler); i++) {
            if(!StacksHandlerUtils.getStackInSlot(handler, i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static void iterateHandler(ResourceHandler<ItemResource> handler, BiConsumer<Integer, ItemStack> consumer) {
        for(int i = 0; i < handler.size(); i++) {
            ItemStack stack = handler.getResource(i).toStack(handler.getAmountAsInt(i));
            consumer.accept(i, stack);
        }
    }

    public static boolean iterate(ResourceHandler<ItemResource> handler, BiFunction<Integer, ItemStack, Boolean> function) {
        for(int i = 0; i < StacksHandlerUtils.getSlots(handler); i++) {
            boolean matches = function.apply(i, StacksHandlerUtils.getStackInSlot(handler, i).copy());
            if(matches) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack extractFromBackpack(ItemStacksResourceHandler handler, ItemStack stack, int amount, boolean simulate) {
        for(int i = 0; i < StacksHandlerUtils.getSlots(handler); i++) {
            if(ItemStack.isSameItemSameComponents(stack, StacksHandlerUtils.getStackInSlot(handler, i))) {
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