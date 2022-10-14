package com.tiviacz.travelersbackpack.inventory.container.slot;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackSlotItemHandler extends SlotItemHandler
{
    public BackpackSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        ResourceLocation blacklistedItems = new ResourceLocation(TravelersBackpack.MODID, "blacklisted_items");

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.getItem().is(ItemTags.getAllTags().getTag(blacklistedItems)) && !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }
}