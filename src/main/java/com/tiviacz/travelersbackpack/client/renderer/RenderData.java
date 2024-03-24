package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;

public class RenderData
{
    private final ItemStack stack;
    private final FluidTank leftTank = createFluidTank();

    private final FluidTank rightTank = createFluidTank();

    public RenderData(ItemStack stack, boolean loadData)
    {
        this.stack = stack;

        if(loadData)
        {
            this.loadDataFromStack(stack);
        }
    }

    public FluidTank getLeftTank()
    {
        return this.leftTank;
    }

    public FluidTank getRightTank()
    {
        return this.rightTank;
    }

    public ItemStack getItemStack()
    {
        return this.stack;
    }

    public int getSleepingBagColor()
    {
        if(this.stack.getOrCreateNbt().contains(ITravelersBackpackInventory.SLEEPING_BAG_COLOR))
        {
            return this.stack.getOrCreateNbt().getInt(ITravelersBackpackInventory.SLEEPING_BAG_COLOR);
        }
        return DyeColor.RED.getId();
    }

    public void loadDataFromStack(ItemStack stack)
    {
        if(!stack.isEmpty() && stack.hasNbt())
        {
            loadTanks(stack.getOrCreateNbt());
        }
    }

    public void loadTanks(NbtCompound compound)
    {
        this.leftTank.readNbt(compound.getCompound(ITravelersBackpackInventory.LEFT_TANK));
        this.rightTank.readNbt(compound.getCompound(ITravelersBackpackInventory.RIGHT_TANK));
    }

    public FluidTank createFluidTank()
    {
        return new FluidTank(Tiers.LEATHER.getTankCapacity())
        {
            @Override
            public FluidTank readNbt(NbtCompound nbt)
            {
                setCapacity(nbt.contains("capacity") ? nbt.getLong("capacity") : Tiers.of(RenderData.this.stack.getOrCreateNbt().getInt(ITravelersBackpackInventory.TIER)).getTankCapacity());
                this.variant = FluidVariantImpl.fromNbt(nbt.getCompound("variant"));
                this.amount = nbt.getLong("amount");
                return this;
            }
        };
    }
}