package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class StacksHandlerUtils {
    public static ItemStack getStackInSlot(ItemStacksResourceHandler handler, int slot) {
        return handler.getResource(slot).toStack(handler.getAmountAsInt(slot));
    }

    public static int getSlots(ItemStacksResourceHandler handler) {
        return handler.size();
    }

    public static void setStackInSlot(ItemStacksResourceHandler handler, int slot, ItemStack stack) {
        handler.set(slot, ItemResource.of(stack), stack.getCount());
    }

    public static ItemStack extractItem(ItemStacksResourceHandler handler, int slot, int amount, boolean simulate) {
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

    public static void setFluid(FluidStacksResourceHandler handler, FluidStack stack) {
        handler.set(0, FluidResource.of(stack), stack.getAmount());
    }

    public static FluidStack getFluid(FluidStacksResourceHandler handler) {
        return handler.getResource(0).toStack(handler.getAmountAsInt(0));
    }

    public static int getFluidAmount(FluidStacksResourceHandler handler) {
        return handler.getAmountAsInt(0);
    }

    public static boolean isEmpty(FluidStacksResourceHandler handler) {
        return handler.getResource(0).isEmpty();
    }

    public static int getCapacity(FluidStacksResourceHandler handler) {
        return handler.getCapacityAsInt(0, FluidResource.EMPTY);
    }

    public static int fill(FluidStacksResourceHandler handler, FluidStack fluidStack, boolean simulate) {
        try(var tx = Transaction.openRoot()) {
            int moved = handler.insert(0, FluidResource.of(fluidStack), fluidStack.getAmount(), tx);
            if(moved > 0) {
                if(!simulate) {
                    tx.commit();
                }
            }
            return moved;
        }
    }

    public static int drain(FluidStacksResourceHandler handler, int amount, boolean simulate) {
        try(var tx = Transaction.openRoot()) {
            FluidResource resource = handler.getResource(0);
            int moved = handler.extract(resource, amount, tx);
            if(moved > 0) {
                if(!simulate) {
                    tx.commit();
                }
            }
            return moved;
        }
    }
}
