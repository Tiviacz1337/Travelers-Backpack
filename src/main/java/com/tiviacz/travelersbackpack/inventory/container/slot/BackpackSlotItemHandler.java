package com.tiviacz.travelersbackpack.inventory.container.slot;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

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
    public boolean mayPlace(ItemStack stack)
    {
        ResourceLocation blacklistedItems = new ResourceLocation(TravelersBackpack.MODID, "blacklisted_items");

        if(BLACKLISTED_ITEMS.contains(stack.getItem())) return false;

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.getItem().is(ItemTags.getAllTags().getTag(blacklistedItems)) && (TravelersBackpackConfig.allowShulkerBoxes || !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock));
    }
}