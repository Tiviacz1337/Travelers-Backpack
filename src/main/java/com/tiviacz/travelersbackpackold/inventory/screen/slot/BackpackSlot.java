package com.tiviacz.travelersbackpackold.inventory.screen.slot;

import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpackneo.init.ModTags;
import com.tiviacz.travelersbackpackold.inventory.InventoryImproved;
import com.tiviacz.travelersbackpackold.items.TravelersBackpackItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class BackpackSlot extends Slot
{
    public BackpackSlot(Inventory inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack)
    {
        return this.inventory.isValid(this.getIndex(), stack) && super.canInsert(stack);
    }

    public static boolean isValid(ItemStack stack)
    {
        if(TravelersBackpackConfig.isItemBlacklisted(stack)) return false;

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.isIn(ModTags.BLACKLISTED_ITEMS) && (TravelersBackpackConfig.getConfig().backpackSettings.allowShulkerBoxes || stack.getItem().canBeNested());
    }

    @Override
    public void markDirty()
    {
        if(inventory instanceof InventoryImproved improved)
        {
            improved.onContentsChanged(this.getIndex());
        }
        else
        {
            super.markDirty();
        }
    }
}