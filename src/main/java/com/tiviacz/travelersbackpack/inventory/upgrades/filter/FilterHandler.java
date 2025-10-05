package com.tiviacz.travelersbackpack.inventory.upgrades.filter;

import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class FilterHandler extends ItemStackHandler {
    public FilterHandler(NonNullList<ItemStack> stacks, int size) {
        super(size);
        for(int i = 0; i < this.stacks.size(); i++) {
            if(stacks.size() > i) {
                this.stacks.set(i, stacks.get(i));
            }
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}