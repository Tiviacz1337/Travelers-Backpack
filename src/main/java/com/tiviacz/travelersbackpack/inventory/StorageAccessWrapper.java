package com.tiviacz.travelersbackpack.inventory;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidUpgrade;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Custom ItemStackHandler for Traveler's Backpack block entity interactions with hoppers, pipes etc. that respects unsortable and memory slots :)
 */
public class StorageAccessWrapper implements IItemHandlerModifiable {
    public final BackpackWrapper wrapper;
    public final ItemStackHandler parent;

    public StorageAccessWrapper(BackpackWrapper wrapper, ItemStackHandler parent) {
        this.wrapper = wrapper;
        this.parent = parent;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        parent.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return parent.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return parent.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        //Voiding
        if(tryVoiding(stack)) {
            if(!simulate) {
                return ItemStack.EMPTY;
            }
        }
        //Try inserting to memory slots first
        if(!wrapper.getMemorySlots().isEmpty()) {
            for(Pair<Integer, Pair<ItemStack, Boolean>> memorizedStack : wrapper.getMemorySlots()) {
                if(memorizedStack.getSecond().getFirst().getItem() != stack.getItem()) {
                    continue;
                }
                int result = matchesStack(stack, memorizedStack);
                if(result != -1) {
                    ItemStack insertResult = parent.insertItem(result, stack, simulate);
                    if(insertResult.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return wrapper.getUnsortableSlots().contains(slot) ? stack : parent.insertItem(slot, stack, simulate);
    }

    public int matchesStack(ItemStack inserted, Pair<Integer, Pair<ItemStack, Boolean>> memorizedStack) {
        if(memorizedStack.getSecond().getSecond()) {
            return ItemStackUtils.isSameItemSameTags(inserted, memorizedStack.getSecond().getFirst()) ? memorizedStack.getFirst() : -1;
        } else {
            return ItemStack.isSameItem(inserted, memorizedStack.getSecond().getFirst()) ? memorizedStack.getFirst() : -1;
        }
    }

    public boolean tryVoiding(ItemStack stack) {
        return wrapper.getUpgradeManager().getUpgrade(VoidUpgrade.class).map(voidUpgrade -> voidUpgrade.canVoid(stack)).orElse(false);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return wrapper.getUnsortableSlots().contains(slot) ? ItemStack.EMPTY : parent.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return parent.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return parent.isItemValid(slot, stack);
    }
}
