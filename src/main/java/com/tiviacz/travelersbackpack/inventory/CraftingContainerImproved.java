package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.util.ContainerUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class CraftingContainerImproved implements CraftingContainer
{
    private final ItemStackHandler handler;
    private final AbstractContainerMenu menu;
    public boolean checkChanges = true;

    public CraftingContainerImproved(ITravelersBackpackContainer container, AbstractContainerMenu menu)
    {
        super();
        this.handler = container.getCraftingGridHandler();
        this.menu = menu;
    }

    @Override
    public int getContainerSize()
    {
        return this.handler.getSlots();
    }

    public NonNullList<ItemStack> getStackList()
    {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for(int i = 0; i < handler.getSlots(); i++)
        {
            stacks.add(i, getItem(i));
        }
        return stacks;
    }

    @Override
    public boolean isEmpty()
    {
        for(int i = 0; i < getContainerSize(); i++)
        {
            if(!handler.getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return slot >= this.getContainerSize() ? ItemStack.EMPTY : this.handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot)
    {
        return ContainerUtils.takeItem(this.handler, slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount)
    {
        ItemStack stack = ContainerUtils.removeItem(this.handler, slot, amount);
        if(!stack.isEmpty())
        {
            if(checkChanges)
            {
                this.menu.slotsChanged(this);
            }
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack)
    {
        this.handler.setStackInSlot(slot, stack);
        if(checkChanges)this.menu.slotsChanged(this);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player p_39340_) {
        return true;
    }

    @Override
    public void clearContent()
    {
        for(int i = 0; i < getContainerSize(); i++)
        {
            setItem(i, ItemStack.EMPTY);
        }
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public List<ItemStack> getItems()
    {
        return List.copyOf(this.getStackList());
    }

    @Override
    public void fillStackedContents(StackedContents contents)
    {
        for(int i = 0; i < getContainerSize(); i++)
        {
            contents.accountSimpleStack(getItem(i));
        }
    }
}