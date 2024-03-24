package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class RenderData
{
    private final ItemStack stack;
    private final FluidTank leftTank = createFluidHandler();
    private final FluidTank rightTank = createFluidHandler();

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
        if(this.stack.getOrCreateTag().contains(ITravelersBackpackContainer.SLEEPING_BAG_COLOR))
        {
            return this.stack.getOrCreateTag().getInt(ITravelersBackpackContainer.SLEEPING_BAG_COLOR);
        }
        return DyeColor.RED.getId();
    }

    public void loadDataFromStack(ItemStack stack)
    {
        if(!stack.isEmpty() && stack.hasTag())
        {
            loadTanks(stack.getOrCreateTag());
        }
    }

    public void loadTanks(CompoundTag compound)
    {
        this.leftTank.readFromNBT(compound.getCompound(ITravelersBackpackContainer.LEFT_TANK));
        this.rightTank.readFromNBT(compound.getCompound(ITravelersBackpackContainer.RIGHT_TANK));
    }

    private FluidTank createFluidHandler()
    {
        return new FluidTank(Tiers.LEATHER.getTankCapacity())
        {
            @Override
            public FluidTank readFromNBT(CompoundTag nbt)
            {
                setCapacity(nbt.contains("Capacity", 3) ? nbt.getInt("Capacity") : Tiers.of(RenderData.this.stack.getOrCreateTag().getInt(ITravelersBackpackContainer.TIER)).getTankCapacity());
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                setFluid(fluid);
                return this;
            }

            @Override
            public CompoundTag writeToNBT(CompoundTag nbt)
            {
                if(!nbt.contains("Capacity", 3)) nbt.putInt("Capacity", Tiers.of(RenderData.this.stack.getOrCreateTag().getInt(ITravelersBackpackContainer.TIER)).getTankCapacity());
                fluid.writeToNBT(nbt);
                return nbt;
            }
        };
    }
}