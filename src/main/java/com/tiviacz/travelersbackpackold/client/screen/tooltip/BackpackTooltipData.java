package com.tiviacz.travelersbackpackold.client.screen.tooltip;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpackold.components.FluidTanks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackpackTooltipData implements TooltipData
{
    protected List<ItemStack> storage = new ArrayList<>();
    protected List<ItemStack> tools = new ArrayList<>();
    protected List<ItemStack> crafting = new ArrayList<>();
    protected Pair<FluidVariant, Long> leftTank = Pair.of(FluidVariant.blank(), (long)0);
    protected Pair<FluidVariant, Long> rightTank = Pair.of(FluidVariant.blank(), (long)0);

    public BackpackTooltipData(ItemStack stack)
    {
        this.loadComponentData(stack);
    }

    public void loadComponentData(ItemStack stack)
    {
        this.loadFluidStacks(stack);

        this.storage = this.loadInventory(stack);
        this.crafting = this.loadCraftingInventory(stack);
        this.storage.addAll(this.crafting);
        this.storage = this.mergeStacks(this.storage);
        this.tools = this.loadTools(stack);
    }

    public void loadFluidStacks(ItemStack stack)
    {
        if(stack.contains(ModDataComponents.FLUID_TANKS))
        {
            FluidTanks tanks = stack.get(ModDataComponents.FLUID_TANKS);

            this.leftTank = Pair.of(tanks.leftTank().fluidVariant(), tanks.leftTank().amount());
            this.rightTank = Pair.of(tanks.rightTank().fluidVariant(), tanks.rightTank().amount());
        }
    }

    public List<ItemStack> loadInventory(ItemStack stack)
    {
        if(stack.contains(ModDataComponents.BACKPACK_CONTAINER))
        {
            return new ArrayList<>(stack.get(ModDataComponents.BACKPACK_CONTAINER).getStacks().stream().filter(itemStack -> !itemStack.isEmpty()).toList());
        }
        return new ArrayList<>();
    }

    public List<ItemStack> mergeStacks(List<ItemStack> stacks)
    {
        if(!stacks.isEmpty())
        {
            List<ItemStack> uniqueList = new ArrayList<>();

            for(ItemStack stack : stacks)
            {
                //if(stack.isEmpty()) continue; //#TODO add on NEO add filter to remove ItemStack.EMPTYs

                if(uniqueList.isEmpty())
                {
                    uniqueList.add(stack);
                    continue;
                }

                boolean flag = false;

                for(int i = 0; i < uniqueList.size(); i++)
                {
                    if(ItemStack.areItemsAndComponentsEqual(stack, uniqueList.get(i)))
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

    public List<ItemStack> loadTools(ItemStack stack)
    {
        if(stack.contains(ModDataComponents.TOOLS_CONTAINER))
        {
            return new ArrayList<>(stack.get(ModDataComponents.TOOLS_CONTAINER).getStacks().stream().filter(itemStack -> !itemStack.isEmpty()).toList());
        }
        return new ArrayList<>();
    }

    public List<ItemStack> loadCraftingInventory(ItemStack stack)
    {
        if(stack.contains(ModDataComponents.CRAFTING_CONTAINER))
        {
            return new ArrayList<>(stack.get(ModDataComponents.CRAFTING_CONTAINER).getStacks().stream().filter(itemStack -> !itemStack.isEmpty()).toList());
        }
        return new ArrayList<>();
    }
}