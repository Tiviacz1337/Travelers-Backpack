package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class BackpackSlotItemHandler extends SlotItemHandler
{
    public BackpackSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        ResourceLocation blacklistedItems = new ResourceLocation(TravelersBackpack.MODID, "blacklisted_items");

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.is(ItemTags.getAllTags().getTag(blacklistedItems));
    }
}