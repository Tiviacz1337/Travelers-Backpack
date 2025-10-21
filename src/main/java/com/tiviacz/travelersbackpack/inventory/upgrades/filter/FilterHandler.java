package com.tiviacz.travelersbackpack.inventory.upgrades.filter;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

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
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}