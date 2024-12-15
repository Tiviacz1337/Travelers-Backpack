package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BackpackSlotItemHandler extends SlotItemHandler {
    public BackpackSlotItemHandler(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public static boolean isItemValid(ItemStack stack) {
        if(TravelersBackpackConfig.isItemBlacklisted(stack)) return false;

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.is(ModTags.BLACKLISTED_ITEMS) && (TravelersBackpackConfig.getConfig().backpackSettings.allowShulkerBoxes || stack.getItem().canFitInsideContainerItems());
    }

    //Fixes JEI
    @Override
    public boolean mayPlace(ItemStack stack) {
        return getItemHandler().isItemValid(index, stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return true;
    }
}