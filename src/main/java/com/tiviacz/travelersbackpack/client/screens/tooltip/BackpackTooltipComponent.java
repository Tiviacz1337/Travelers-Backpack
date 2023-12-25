package com.tiviacz.travelersbackpack.client.screens.tooltip;

import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BackpackTooltipComponent implements TooltipComponent
{
    protected ItemStackHandler inventory = new ItemStackHandler(54);
    protected ItemStackHandler craftingInventory = new ItemStackHandler(9);
    protected FluidTank leftTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    protected FluidTank rightTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    protected ItemStack stack;
    protected Tiers.Tier tier = Tiers.LEATHER;

    public BackpackTooltipComponent(ItemStack stack)
    {
        this.stack = stack;
        this.loadTier(stack.getTag());
        this.loadComponentData(stack.getTag());
    }

    public void loadTier(CompoundTag compound)
    {
        if(compound != null)
        {
            this.tier = Tiers.of(compound.getInt(Tiers.TIER));
        }
        else
        {
            this.tier = Tiers.LEATHER;
        }
    }

    public void loadComponentData(CompoundTag compound)
    {
        if(compound == null) return;

        this.loadInventory(compound);
        this.loadCraftingInventory(compound);
        this.loadLeftTank(compound);
        this.loadRightTank(compound);
    }

    public void loadInventory(CompoundTag compound)
    {
        if(compound.contains("Inventory"))
        {
            this.inventory.deserializeNBT(compound.getCompound("Inventory"));
        }
    }

    public void loadCraftingInventory(CompoundTag compound)
    {
        if(compound.contains("CraftingInventory"))
        {
            this.craftingInventory.deserializeNBT(compound.getCompound("CraftingInventory"));
        }
    }

    public void loadLeftTank(CompoundTag compound)
    {
        if(compound.contains("LeftTank"))
        {
            this.leftTank.readFromNBT(compound.getCompound("LeftTank"));
        }
    }

    public void loadRightTank(CompoundTag compound)
    {
        if(compound.contains("RightTank"))
        {
            this.rightTank.readFromNBT(compound.getCompound("RightTank"));
        }
    }

    public boolean hasToolInSlot(Tiers.SlotType type)
    {
        return !inventory.getStackInSlot(tier.getSlotIndex(type)).isEmpty();
    }

    private FluidTank createFluidHandler(int capacity)
    {
        return new FluidTank(capacity)
        {
            @Override
            public FluidTank readFromNBT(CompoundTag nbt)
            {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                setCapacity(BackpackTooltipComponent.this.tier.getTankCapacity());
                setFluid(fluid);
                return this;
            }
        };
    }
}