package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

public class BackpackSlotItemHandler extends SlotItemHandler
{
    public static final List<Item> BLACKLISTED_ITEMS = new ArrayList<>();

    public BackpackSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
    }

    public static boolean isItemValid(ItemStack stack)
    {
        if(BackpackSlotItemHandler.BLACKLISTED_ITEMS.contains(stack.getItem())) return false;

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.is(ModTags.BLACKLISTED_ITEMS) && (TravelersBackpackConfig.SERVER.backpackSettings.allowShulkerBoxes.get() || stack.getItem().canFitInsideContainerItems());
    }
}