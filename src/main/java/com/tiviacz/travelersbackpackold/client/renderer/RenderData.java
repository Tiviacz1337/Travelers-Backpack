package com.tiviacz.travelersbackpackold.client.renderer;

import com.tiviacz.travelersbackpackold.components.FluidTanks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpackold.inventory.FluidTank;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

public class RenderData
{
    private final ItemStack stack;
    private final FluidTank leftTank = new FluidTank(81000); // createFluidTank();

    private final FluidTank rightTank = new FluidTank(81000); //createFluidTank();

    public RenderData(ItemStack stack, boolean loadData)
    {
        this.stack = stack;

        if(loadData)
        {
            this.loadDataFromStack();
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
        return stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId());
    }

    public void loadDataFromStack()
    {
        if(this.stack.contains(ModDataComponents.FLUID_TANKS))
        {
            loadTanks();
        }
    }

    public void loadTanks()
    {
        FluidTanks tanks = stack.get(ModDataComponents.FLUID_TANKS);

        this.leftTank.setCapacity(tanks.capacity());
        this.leftTank.setFluidVariant(tanks.leftTank().fluidVariant(), tanks.leftTank().amount());

        this.rightTank.setCapacity(tanks.capacity());
        this.rightTank.setFluidVariant(tanks.rightTank().fluidVariant(), tanks.rightTank().amount());
    }
}