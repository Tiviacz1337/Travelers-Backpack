package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.item.upgrades.TanksUpgradeItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class UpgradeSlotItemHandler extends SlotItemHandler {
    public BackpackBaseMenu menu;
    public boolean isLocked = false;
    public boolean isHidden = false;

    public UpgradeSlotItemHandler(BackpackBaseMenu menu, ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;

        if (!itemHandler.getStackInSlot(index).isEmpty()) {
            setLocked(true);
        }
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }

    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.getItem() instanceof TanksUpgradeItem && !getItemHandler().isItemValid(index, stack)) {
            if (!TanksUpgradeItem.canBePutInBackpack(menu.getWrapper().getBackpackTankCapacity(), stack)) {
                BackpackScreen.displayTanksUpgradeWarning(menu.player);
            }
        }
        return super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return super.mayPickup(playerIn) && !isLocked && !isHidden;
    }

    @Override
    public boolean isActive() {
        return super.isActive() && !isLocked && !isHidden;
    }

    @Override
    public void setByPlayer(ItemStack pNewStack, ItemStack pOldStack) {
        if (menu.player.level().isClientSide) {
            menu.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
        }
        super.setByPlayer(pNewStack, pOldStack);
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }
}
