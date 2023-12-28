package com.tiviacz.travelersbackpack.client.screen.tooltip;

import com.tiviacz.travelersbackpack.inventory.InventoryImproved;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.util.InventoryUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class BackpackTooltipData implements TooltipData
{
    protected InventoryImproved inventory = createInventory(54);
    protected InventoryImproved craftingInventory = createInventory(9);
    protected SingleVariantStorage<FluidVariant> leftTank = createFluidTank(Tiers.LEATHER.getTankCapacity());
    protected SingleVariantStorage<FluidVariant> rightTank = createFluidTank(Tiers.LEATHER.getTankCapacity());
    protected ItemStack stack;
    protected Tiers.Tier tier = Tiers.LEATHER;

    public BackpackTooltipData(ItemStack stack)
    {
        this.stack = stack;
        this.loadTier(stack.getNbt());
        this.loadComponentData(stack.getNbt());
    }

    public void loadTier(NbtCompound compound)
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

    public void loadComponentData(NbtCompound compound)
    {
        if(compound == null) return;

        this.loadInventory(compound);
        this.loadCraftingInventory(compound);
        this.loadLeftTank(compound);
        this.loadRightTank(compound);
    }

    public void loadInventory(NbtCompound compound)
    {
        if(compound.contains("Inventory"))
        {
            this.inventory = createInventory(this.tier.getAllSlots());
            InventoryUtils.readNbt(compound, this.inventory.getStacks(), false);
        }
    }

    public void loadCraftingInventory(NbtCompound compound)
    {
        if(compound.contains("CraftingInventory"))
        {
            this.craftingInventory = createInventory(Reference.CRAFTING_GRID_SIZE);
            InventoryUtils.readNbt(compound, this.craftingInventory.getStacks(), true);
        }
    }

    public void loadLeftTank(NbtCompound compound)
    {
        if(compound.contains("LeftTank"))
        {
            this.leftTank.variant = FluidVariantImpl.fromNbt(compound.getCompound("LeftTank"));
            this.leftTank.amount = compound.getLong("LeftTankAmount");
        }
    }

    public void loadRightTank(NbtCompound compound)
    {
        if(compound.contains("RightTank"))
        {
            this.rightTank.variant = FluidVariantImpl.fromNbt(compound.getCompound("RightTank"));
            this.rightTank.amount = compound.getLong("RightTankAmount");
        }
    }

    public boolean hasToolInSlot(Tiers.SlotType type)
    {
        return !inventory.getStack(tier.getSlotIndex(type)).isEmpty();
    }

    public InventoryImproved createInventory(int size)
    {
        return new InventoryImproved(DefaultedList.ofSize(size, ItemStack.EMPTY))
        {
            @Override
            public void markDirty() {}
        };
    }

    public SingleVariantStorage<FluidVariant> createFluidTank(long capacity)
    {
        return new SingleVariantStorage<FluidVariant>()
        {
            @Override
            protected FluidVariant getBlankVariant()
            {
                return FluidVariant.blank();
            }

            @Override
            protected long getCapacity(FluidVariant variant)
            {
                return BackpackTooltipData.this.tier.getTankCapacity();
            }
        };
    }
}