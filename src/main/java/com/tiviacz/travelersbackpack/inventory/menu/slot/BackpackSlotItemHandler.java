package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BackpackSlotItemHandler extends SlotItemHandler
{
    public static final List<Item> BLACKLISTED_ITEMS = new ArrayList<>();

    public BackpackSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        if(BLACKLISTED_ITEMS.contains(stack.getItem())) return false;

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.is(ModTags.BLACKLISTED_ITEMS) && stack.getItem().canFitInsideContainerItems();
    }
}