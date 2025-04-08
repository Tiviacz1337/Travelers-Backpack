package com.tiviacz.travelersbackpack.inventory.upgrades.crafting;

import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CraftingContainerImproved implements CraftingContainer {
    private final CraftingUpgrade craftingUpgrade;
    public boolean checkChanges = true;
    public BackpackBaseMenu menu;

    public CraftingContainerImproved(BackpackBaseMenu menu, CraftingUpgrade craftingUpgrade) {
        this.menu = menu;
        this.craftingUpgrade = craftingUpgrade;
    }

    @Override
    public int getContainerSize() {
        return this.craftingUpgrade.crafting.getSlots();
    }

    public NonNullList<ItemStack> getStackList() {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for(int i = 0; i < craftingUpgrade.crafting.getSlots(); i++) {
            stacks.add(i, getItem(i));
        }
        return stacks;
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < getContainerSize(); i++) {
            if(!craftingUpgrade.crafting.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= this.getContainerSize() ? ItemStack.EMPTY : this.craftingUpgrade.crafting.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return InventoryHelper.takeItem(this.craftingUpgrade.crafting, slot);
    }

    public ItemStack removeItemShiftClick(int slot, int amount) {
        ItemStack stack = InventoryHelper.removeItemShiftClick(craftingUpgrade.crafting, slot, amount);
        if(checkChanges) {
            this.menu.slotsChanged(this);
        }
        return stack;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = this.craftingUpgrade.crafting.extractItem(slot, amount, false);
        if(!stack.isEmpty()) {
            if(checkChanges) {
                this.menu.slotsChanged(this);
            }
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.craftingUpgrade.crafting.setStackInSlot(slot, stack);
        if(checkChanges) {
            this.menu.slotsChanged(this);
        }
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player p_39340_) {
        return true;
    }

    @Override
    public void clearContent() {
        for(int i = 0; i < getContainerSize(); i++) {
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
    public List<ItemStack> getItems() {
        return List.copyOf(this.getStackList());
    }

    @Override
    public void fillStackedContents(StackedContents contents) {
        for(int i = 0; i < getContainerSize(); i++) {
            contents.accountSimpleStack(getItem(i));
        }
    }
}