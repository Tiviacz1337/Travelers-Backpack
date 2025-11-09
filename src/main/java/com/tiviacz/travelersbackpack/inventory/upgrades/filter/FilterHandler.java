package com.tiviacz.travelersbackpack.inventory.upgrades.filter;

import com.tiviacz.travelersbackpack.inventory.transfer.BackpackResourceHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class FilterHandler extends BackpackResourceHandler {
    public FilterHandler(NonNullList<ItemStack> stacks, int size) {
        super(size);
        for(int i = 0; i < this.stacks.size(); i++) {
            if(stacks.size() > i) {
                this.stacks.set(i, stacks.get(i));
            }
        }
    }

    @Override
    public boolean isValid(int slot, ItemResource resource) {
        return true;
    }

    @Override
    public int getCapacity(int index, ItemResource resource) {
        return 1;
    }
}