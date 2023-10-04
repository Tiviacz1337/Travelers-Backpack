package com.tiviacz.travelersbackpack.inventory.screen.slot;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class FluidSlot extends Slot
{
    private final int index;
    private final ITravelersBackpackInventory inventory;

    public FluidSlot(ITravelersBackpackInventory inventory, int index, int x, int y)
    {
        super(inventory.getFluidSlotsInventory(), index, x, y);
        this.index = index;
        this.inventory = inventory;
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity)
    {
        if(inventory.getTier().getOrdinal() <= 1)
        {
            if(index == inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT) || index == inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT))
            {
                return this.hasStack();
            }
        }
        return true;
    }

    @Environment(value= EnvType.CLIENT)
    @Override
    public boolean doDrawHoveringEffect()
    {
        if(inventory.getTier().getOrdinal() <= 1)
        {
            if(index == inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT) || index == inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT))
            {
                return this.hasStack();
            }
        }
        return true;
    }

    @Override
    public boolean canInsert(ItemStack stack)
    {
        Storage<FluidVariant> storage = ContainerItemContext.withInitial(stack).find(FluidStorage.ITEM);

        if(index == this.inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT) || index == this.inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT))
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
        if(inventory.getScreenID() == Reference.ITEM_SCREEN_ID) inventory.updateTankSlots();
    }
}