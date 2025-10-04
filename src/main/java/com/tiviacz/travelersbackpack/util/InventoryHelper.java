package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.inventory.handler.IItemHandlerModifiable;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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

    public static ItemStack takeItem(ItemStackHandler handler, int slot) {
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

    public static void iteratePlayerInv(Inventory playerInv, BiConsumer<Integer, ItemStack> consumer) {
        for(int i = 0; i < playerInv.getContainerSize(); i++) {
            ItemStack stack = playerInv.getItem(i);
            consumer.accept(i, stack);
        }
    }

    public static void iterateHandler(ItemStackHandler handler, BiConsumer<Integer, ItemStack> consumer) {
        for(int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            consumer.accept(i, stack);
        }
    }

    public static boolean iterate(ItemStackHandler handler, BiFunction<Integer, ItemStack, Boolean> function) {
        for(int i = 0; i < handler.getSlots(); i++) {
            boolean matches = function.apply(i, handler.getStackInSlot(i).copy());
            if(matches) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack addItemStackToHandler(ItemStackHandler handler, ItemStack stack, boolean simulate) {
        return insertItemStacked(handler, stack, simulate);
    }

    public static ItemStack extractFromBackpack(ItemStackHandler handler, ItemStack stack, int amount, boolean simulate) {
        for(int i = 0; i < handler.getSlots(); i++) {
            if(ItemStack.isSameItemSameTags(stack, handler.getStackInSlot(i))) {
                return handler.extractItem(i, amount, simulate);
            }
        }
        return ItemStack.EMPTY;
    }

    public static @NotNull ItemStack insertItemStacked(ItemStackHandler inventory, @NotNull ItemStack stack, boolean simulate) {
        if(inventory != null && !stack.isEmpty()) {
            if(!stack.isStackable()) {
                return insertItem(inventory, stack, simulate);
            } else {
                int sizeInventory = inventory.getSlots();

                for(int i = 0; i < sizeInventory; ++i) {
                    ItemStack slot = inventory.getStackInSlot(i);
                    if(canItemStacksStackRelaxed(slot, stack)) {
                        stack = inventory.insertItem(i, stack, simulate);
                        if(stack.isEmpty()) {
                            break;
                        }
                    }
                }

                if(!stack.isEmpty()) {
                    for(int i = 0; i < sizeInventory; ++i) {
                        if(inventory.getStackInSlot(i).isEmpty()) {
                            stack = inventory.insertItem(i, stack, simulate);
                            if(stack.isEmpty()) {
                                break;
                            }
                        }
                    }
                }

                return stack;
            }
        } else {
            return stack;
        }
    }

    public static boolean canItemStacksStackRelaxed(@NotNull ItemStack a, @NotNull ItemStack b) {
        if(!a.isEmpty() && !b.isEmpty() && a.getItem() == b.getItem()) {
            if(!a.isStackable()) {
                return false;
            } else if(a.hasTag() != b.hasTag()) {
                return false;
            } else {
                return (!a.hasTag() || a.getTag().equals(b.getTag()));
            }
        } else {
            return false;
        }
    }

    public static @NotNull ItemStack insertItem(ItemStackHandler dest, @NotNull ItemStack stack, boolean simulate) {
        if(dest != null && !stack.isEmpty()) {
            for(int i = 0; i < dest.getSlots(); ++i) {
                stack = dest.insertItem(i, stack, simulate);
                if(stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }

            return stack;
        } else {
            return stack;
        }
    }

    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        if(!a.isEmpty() && ItemStack.isSameItem(a, b) && a.hasTag() == b.hasTag()) {
            return (!a.hasTag() || a.getTag().equals(b.getTag()));
        } else {
            return false;
        }
    }

    public static @NotNull ItemStack copyStackWithSize(@NotNull ItemStack itemStack, int size) {
        if(size == 0) {
            return ItemStack.EMPTY;
        } else {
            ItemStack copy = itemStack.copy();
            copy.setCount(size);
            return copy;
        }
    }
}