package com.tiviacz.travelersbackpack.inventory.sorter.wrappers;

import com.google.common.base.Preconditions;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class RangedWrapper implements Inventory
{
    private final ITravelersBackpackInventory inv;
    private final Inventory compose;
    private final int minSlot;
    private final int maxSlot;

    public RangedWrapper(ITravelersBackpackInventory inv, Inventory compose, int minSlot, int maxSlotExclusive)
    {
        Preconditions.checkArgument(maxSlotExclusive > minSlot, "Max slot must be greater than min slot");
        this.inv = inv;
        this.compose = compose;
        this.minSlot = minSlot;
        this.maxSlot = maxSlotExclusive;
    }

    @Override
    public int size()
    {
        return maxSlot - minSlot;
    }

    @Override
    @Nonnull
    public ItemStack getStack(int slot)
    {
        if (checkSlot(slot))
        {
            return compose.getStack(slot + minSlot);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, @Nonnull ItemStack stack)
    {
        if (checkSlot(slot))
        {
            compose.setStack(slot + minSlot, stack);
        }
    }

    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        if (checkSlot(slot))
        {
            return compose.isValid(slot + minSlot, stack);
        }

        return false;
    }

    private boolean checkSlot(int localSlot)
    {
        return localSlot + minSlot < maxSlot;
    }

    //Dummy

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return null;
    }

    @Override
    public void markDirty()
    {
        inv.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {}
}