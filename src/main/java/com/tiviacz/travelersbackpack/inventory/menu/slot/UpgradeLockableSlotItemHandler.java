package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.transfer.BackpackResourceHandler;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.items.upgrades.UpgradeItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class UpgradeLockableSlotItemHandler extends ResourceHandlerSlot {
    public BackpackBaseMenu menu;
    public boolean isLocked = false;
    public boolean isHidden = false;
    public final int containerIndex;

    public UpgradeLockableSlotItemHandler(BackpackBaseMenu menu, BackpackResourceHandler handler, int index, int xPosition, int yPosition) {
        super(handler, handler::set, index, xPosition, yPosition);
        this.menu = menu;
        this.containerIndex = index;

        //If item in slot is not an Upgrade Item - do not lock
        if(handler.getStackInSlot(index).getItem() instanceof UpgradeItem && menu.getWrapper().getUpgradeManager().hasUpgradeInSlot(index)) {
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
        if(stack.getItem() instanceof TanksUpgradeItem && !getResourceHandler().isValid(getSlotIndex(), ItemResource.of(stack))) {
            if(!TanksUpgradeItem.canBePutInBackpack(menu.getWrapper().getBackpackTankCapacity(), stack)) {
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
        if(menu.player.level().isClientSide()) {
            menu.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
        }
        super.setByPlayer(pNewStack, pOldStack);
    }
}
