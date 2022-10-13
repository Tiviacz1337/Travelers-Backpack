package com.tiviacz.travelersbackpack.inventory.screen.slot;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

public class BackpackSlot extends Slot
{
    public BackpackSlot(Inventory inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack)
    {
        Identifier blacklistedItems = new Identifier(TravelersBackpack.MODID, "blacklisted_items");

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.getItem().isIn(ItemTags.getTagGroup().getTag(blacklistedItems)) && !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }
}