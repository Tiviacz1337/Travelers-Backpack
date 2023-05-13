package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class RenderData
{
    private final ItemStack stack;
    private final PlayerEntity player;
    private final FluidTank leftTank = createFluidHandler();
    private final FluidTank rightTank = createFluidHandler();

    private final String LEFT_TANK = "LeftTank";
    private final String RIGHT_TANK = "RightTank";
    private final String COLOR = "Color";
    private final String SLEEPING_BAG_COLOR = "SleepingBagColor";

    public RenderData(PlayerEntity player, ItemStack stack, boolean loadData)
    {
        this.player = player;
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
        if(this.stack.getOrCreateTag().contains(SLEEPING_BAG_COLOR))
        {
            return this.stack.getOrCreateTag().getInt(SLEEPING_BAG_COLOR);
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

    public void loadTanks(CompoundNBT compound)
    {
        this.leftTank.readFromNBT(compound.getCompound(LEFT_TANK));
        this.rightTank.readFromNBT(compound.getCompound(RIGHT_TANK));
    }

    private FluidTank createFluidHandler()
    {
        return new FluidTank(Tiers.LEATHER.getTankCapacity())
        {
            @Override
            public FluidTank readFromNBT(CompoundNBT nbt)
            {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                setCapacity(Tiers.of(RenderData.this.stack.getOrCreateTag().getString(Tiers.TIER)).getTankCapacity());
                setFluid(fluid);
                return this;
            }
        };
    }
}