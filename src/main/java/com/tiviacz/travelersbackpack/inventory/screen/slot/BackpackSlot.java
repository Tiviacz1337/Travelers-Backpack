package com.tiviacz.travelersbackpack.inventory.screen.slot;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BackpackSlot extends Slot
{
    public static final List<Item> BLACKLISTED_ITEMS = new ArrayList<>();

    public BackpackSlot(Inventory inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack)
    {
        Identifier blacklistedItems = new Identifier(TravelersBackpack.MODID, "blacklisted_items");

        if(BLACKLISTED_ITEMS.contains(stack.getItem())) return false;

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.isIn(ItemTags.getTagGroup().getTag(blacklistedItems)) && (TravelersBackpackConfig.allowShulkerBoxes || stack.getItem().canBeNested());
    }
}