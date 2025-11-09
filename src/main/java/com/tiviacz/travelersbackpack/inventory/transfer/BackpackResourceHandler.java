package com.tiviacz.travelersbackpack.inventory.transfer;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@Deprecated(forRemoval = true)
public class BackpackResourceHandler extends ItemStacksResourceHandler {
    public BackpackResourceHandler(int size) {
        super(size);
    }

    public BackpackResourceHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    public ItemStack getStackInSlot(int slot) {
        return getResource(slot).toStack(getAmountAsInt(slot));
    }

    public int getSlots() {
        return size();
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        set(slot, ItemResource.of(stack), stack.getCount());
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(amount <= 0) {
            return ItemStack.EMPTY;
        }
        var resource = getResource(slot);
        if(resource.isEmpty()) {
            return ItemStack.EMPTY;
        }
        // We have to limit to the max stack size, per the contract of extractItem
        amount = Math.min(amount, resource.getMaxStackSize());
        try(var tx = Transaction.openRoot()) {
            int extracted = extract(slot, resource, amount, tx);
            if(!simulate) {
                tx.commit();
            }
            return resource.toStack(extracted);
        }
    }
}