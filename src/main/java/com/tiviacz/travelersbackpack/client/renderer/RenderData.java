package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class RenderData
{
    private final ItemStack stack;
    private final Player player;
    private final FluidTank leftTank = createFluidHandler();
    private final FluidTank rightTank = createFluidHandler();

    private final String LEFT_TANK = "LeftTank";
    private final String RIGHT_TANK = "RightTank";
    private final String COLOR = "Color";
    private final String SLEEPING_BAG_COLOR = "SleepingBagColor";

    public RenderData(Player player, ItemStack stack, boolean loadData)
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

    public void loadTanks(CompoundTag compound)
    {
        this.leftTank.readFromNBT(compound.getCompound(LEFT_TANK));
        this.rightTank.readFromNBT(compound.getCompound(RIGHT_TANK));
    }

    private FluidTank createFluidHandler()
    {
        return new FluidTank(Tiers.LEATHER.getTankCapacity())
        {
            @Override
            public FluidTank readFromNBT(CompoundTag nbt)
            {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                setCapacity(Tiers.of(RenderData.this.stack.getOrCreateTag().getInt(Tiers.TIER)).getTankCapacity());
                setFluid(fluid);
                return this;
            }
        };
    }
}