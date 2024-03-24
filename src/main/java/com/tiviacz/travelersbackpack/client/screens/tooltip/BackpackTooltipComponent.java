package com.tiviacz.travelersbackpack.client.screens.tooltip;

import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackpackTooltipComponent implements TooltipComponent
{
    protected List<ItemStack> storage = new ArrayList<>();
    protected List<ItemStack> tools = new ArrayList<>();
    protected List<ItemStack> crafting = new ArrayList<>();
    protected FluidStack leftFluidStack = FluidStack.EMPTY;
    protected FluidStack rightFluidStack = FluidStack.EMPTY;

    private final ItemStackHandler inventory = new ItemStackHandler(Tiers.NETHERITE.getStorageSlots());
    private final ItemStackHandler toolSlotsInventory = new ItemStackHandler(Tiers.NETHERITE.getToolSlots());
    private final ItemStackHandler craftingInventory = new ItemStackHandler(9);

    public BackpackTooltipComponent(ItemStack stack)
    {
        this.loadComponentData(stack.getTag());
    }

    public void loadComponentData(CompoundTag compound)
    {
        if(compound == null) return;

        this.loadFluidStacks(compound);

        this.storage = this.loadInventory(compound);
        this.crafting = this.loadCraftingInventory(compound);
        this.storage.addAll(this.crafting);
        this.storage = this.mergeStacks(this.storage);
        this.tools = this.loadTools(compound);
    }

    public void loadFluidStacks(CompoundTag compound)
    {
        if(compound.contains(ITravelersBackpackContainer.LEFT_TANK))
        {
            this.leftFluidStack = FluidStack.loadFluidStackFromNBT(compound.getCompound(ITravelersBackpackContainer.LEFT_TANK));
        }
        if(compound.contains(ITravelersBackpackContainer.RIGHT_TANK))
        {
            this.rightFluidStack = FluidStack.loadFluidStackFromNBT(compound.getCompound(ITravelersBackpackContainer.RIGHT_TANK));
        }
    }

    public List<ItemStack> loadInventory(CompoundTag compound)
    {
        ArrayList<ItemStack> list = new ArrayList<>();

        if(!compound.contains(ITravelersBackpackContainer.INVENTORY))
        {
            return Collections.emptyList();
        }

        this.inventory.deserializeNBT(compound.getCompound(ITravelersBackpackContainer.INVENTORY));

        for(int i = 0; i < this.inventory.getSlots(); i++)
        {
            if(!this.inventory.getStackInSlot(i).isEmpty())
            {
                list.add(this.inventory.getStackInSlot(i));
            }
        }
        return list;
    }

    public List<ItemStack> mergeStacks(List<ItemStack> stacks)
    {
        if(!stacks.isEmpty())
        {
            List<ItemStack> uniqueList = new ArrayList<>();

            for(ItemStack stack : stacks)
            {
                if(uniqueList.isEmpty())
                {
                    uniqueList.add(stack);
                    continue;
                }

                boolean flag = false;

                for(int i = 0; i < uniqueList.size(); i++)
                {
                    if(ItemStack.isSameItemSameTags(stack, uniqueList.get(i)))
                    {
                        int count = stack.getCount() + uniqueList.get(i).getCount();
                        uniqueList.set(i, stack.copyWithCount(count));
                        flag = true;
                        break;
                    }
                }

                if(!flag)
                {
                    uniqueList.add(stack);
                }
            }

            //Split >999 stacks
            List<ItemStack> splittedList = new ArrayList<>();

            for(ItemStack itemStack : uniqueList)
            {
                if(itemStack.getCount() > 999)
                {
                    int count = itemStack.getCount();
                    int c = count / 999;
                    int reminder = count % 999;

                    for(int j = 0; j < c; j++)
                    {
                        splittedList.add(itemStack.copyWithCount(999));
                    }
                    splittedList.add(itemStack.copyWithCount(reminder));
                }
                else
                {
                    splittedList.add(itemStack);
                }
            }
            return splittedList;
        }
        return Collections.emptyList();
    }

    public List<ItemStack> loadTools(CompoundTag compound)
    {
        ArrayList<ItemStack> list = new ArrayList<>();

        if(!compound.contains(ITravelersBackpackContainer.TOOLS_INVENTORY))
        {
            return Collections.emptyList();
        }

        this.toolSlotsInventory.deserializeNBT(compound.getCompound(ITravelersBackpackContainer.TOOLS_INVENTORY));

        for(int i = 0; i < this.toolSlotsInventory.getSlots(); i++)
        {
            if(!this.toolSlotsInventory.getStackInSlot(i).isEmpty())
            {
                list.add(this.toolSlotsInventory.getStackInSlot(i));
            }
        }
        return list;
    }

    public List<ItemStack> loadCraftingInventory(CompoundTag compound)
    {
        ArrayList<ItemStack> list = new ArrayList<>();

        if(!compound.contains(ITravelersBackpackContainer.CRAFTING_INVENTORY))
        {
            return Collections.emptyList();
        }

        this.craftingInventory.deserializeNBT(compound.getCompound(ITravelersBackpackContainer.CRAFTING_INVENTORY));

        for(int i = 0; i < this.craftingInventory.getSlots(); i++)
        {
            if(!this.craftingInventory.getStackInSlot(i).isEmpty())
            {
                list.add(this.craftingInventory.getStackInSlot(i));
            }
        }
        return list;
    }
}