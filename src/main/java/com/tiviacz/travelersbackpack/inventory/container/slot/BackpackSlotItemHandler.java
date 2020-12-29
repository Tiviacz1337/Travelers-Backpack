package com.tiviacz.travelersbackpack.inventory.container.slot;

import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackSlotItemHandler extends SlotItemHandler
{
    public BackpackSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return !(stack.getItem() instanceof TravelersBackpackItem);
    }
}