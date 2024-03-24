package com.tiviacz.travelersbackpack.client.screen.tooltip;

import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.InventoryImproved;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackpackTooltipData implements TooltipData
{
    protected List<ItemStack> storage = new ArrayList<>();
    protected List<ItemStack> tools = new ArrayList<>();
    protected List<ItemStack> crafting = new ArrayList<>();
    protected FluidTank leftTank = createFluidTank(Tiers.NETHERITE.getTankCapacity());
    protected FluidTank rightTank = createFluidTank(Tiers.NETHERITE.getTankCapacity());

    protected InventoryImproved inventory = createInventory(Tiers.NETHERITE.getStorageSlots());
    protected InventoryImproved toolSlotsInventory = createInventory(Tiers.NETHERITE.getToolSlots());
    protected InventoryImproved craftingInventory = createInventory(9);

    public BackpackTooltipData(ItemStack stack)
    {
        this.loadComponentData(stack.getNbt());
    }

    public void loadComponentData(NbtCompound compound)
    {
        if(compound == null) return;

        this.loadFluidStacks(compound);

        this.storage = this.loadInventory(compound);
        this.crafting = this.loadCraftingInventory(compound);
        this.storage.addAll(this.crafting);
        this.storage = this.mergeStacks(this.storage);
        this.tools = this.loadTools(compound);
    }

    public void loadFluidStacks(NbtCompound compound)
    {
        if(compound.contains(ITravelersBackpackInventory.LEFT_TANK))
        {
            this.leftTank.readNbt(compound.getCompound(ITravelersBackpackInventory.LEFT_TANK));
        }
        if(compound.contains(ITravelersBackpackInventory.RIGHT_TANK))
        {
            this.rightTank.readNbt(compound.getCompound(ITravelersBackpackInventory.RIGHT_TANK));
        }
    }

    public List<ItemStack> loadInventory(NbtCompound compound)
    {
        ArrayList<ItemStack> list = new ArrayList<>();

        if(!compound.contains(ITravelersBackpackInventory.INVENTORY))
        {
            return Collections.emptyList();
        }

        this.inventory.readNbt(compound.getCompound(ITravelersBackpackInventory.INVENTORY));

        for(int i = 0; i < this.inventory.size(); i++)
        {
            if(!this.inventory.getStack(i).isEmpty())
            {
                list.add(this.inventory.getStack(i));
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
                    if(ItemStack.canCombine(stack, uniqueList.get(i)))
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

    public List<ItemStack> loadTools(NbtCompound compound)
    {
        ArrayList<ItemStack> list = new ArrayList<>();

        if(!compound.contains(ITravelersBackpackInventory.TOOLS_INVENTORY))
        {
            return Collections.emptyList();
        }

        this.toolSlotsInventory.readNbt(compound.getCompound(ITravelersBackpackInventory.TOOLS_INVENTORY));

        for(int i = 0; i < this.toolSlotsInventory.size(); i++)
        {
            if(!this.toolSlotsInventory.getStack(i).isEmpty())
            {
                list.add(this.toolSlotsInventory.getStack(i));
            }
        }
        return list;
    }

    public List<ItemStack> loadCraftingInventory(NbtCompound compound)
    {
        ArrayList<ItemStack> list = new ArrayList<>();

        if(!compound.contains(ITravelersBackpackInventory.CRAFTING_INVENTORY))
        {
            return Collections.emptyList();
        }

        this.craftingInventory.readNbt(compound.getCompound(ITravelersBackpackInventory.CRAFTING_INVENTORY));

        for(int i = 0; i < this.craftingInventory.size(); i++)
        {
            if(!this.craftingInventory.getStack(i).isEmpty())
            {
                list.add(this.craftingInventory.getStack(i));
            }
        }
        return list;
    }

    public InventoryImproved createInventory(int size)
    {
        return new InventoryImproved(size)
        {
            @Override
            public void markDirty() {}
        };
    }

    public FluidTank createFluidTank(long tankCapacity)
    {
        return new FluidTank(tankCapacity)
        {
            @Override
            protected void onFinalCommit() {}
        };
    }
}