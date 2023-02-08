package com.tiviacz.travelersbackpack.inventory.screen.slot;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class FluidSlot extends Slot
{
    private final int index;
    private final ITravelersBackpackInventory inventory;

    public FluidSlot(ITravelersBackpackInventory inventory, int index, int x, int y)
    {
        super(inventory.getInventory(), index, x, y);
        this.index = index;
        this.inventory = inventory;
    }

    @Override
    public boolean canInsert(ItemStack stack)
    {
        Storage<FluidVariant> storage = ContainerItemContext.withInitial(stack).find(FluidStorage.ITEM);

        if(index == Reference.BUCKET_OUT_LEFT || index == Reference.BUCKET_OUT_RIGHT)
        {
            return false;
        }

        if(stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE || stack.getItem() == Items.MILK_BUCKET)
        {
            return true;
        }

        return storage != null;
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        inventory.updateTankSlots();
    }
}