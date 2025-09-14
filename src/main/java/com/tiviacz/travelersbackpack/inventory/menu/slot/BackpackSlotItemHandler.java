package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.handler.IItemHandlerModifiable;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;

public class BackpackSlotItemHandler extends SlotItemHandler {
    public BackpackSlotItemHandler(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public static boolean isItemValid(ItemStack stack) {
        if(TravelersBackpackConfig.isItemBlacklisted(stack)) return false;

        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.is(ModTags.BLACKLISTED_ITEMS) && (TravelersBackpackConfig.getConfig().backpackSettings.allowShulkerBoxes || stack.getItem().canFitInsideContainerItems());
    }

    @Override
    public void setChanged() {
        if(!getItem().getItem().canFitInsideContainerItems() || getItem().getItem() instanceof BundleItem) {
            ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(getContainerSlot(), getItem()); //fix for EasyShulkerBoxes and BundleItem not calling onContentsChanged
        }
        super.setChanged();
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        set(getItem()); //Emi fix
        super.onTake(player, stack);
    }

    //Fixes JEI
    @Override
    public boolean mayPlace(ItemStack stack) {
        return getItemHandler().isItemValid(getContainerSlot(), stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return true;
    }
}