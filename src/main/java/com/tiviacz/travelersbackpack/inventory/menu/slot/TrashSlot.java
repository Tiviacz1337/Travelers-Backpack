package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class TrashSlot extends SlotItemHandler {
    protected final UpgradeBase upgrade;
    protected final int activeSlotCount;

    public TrashSlot(UpgradeBase upgrade, ItemStackHandler itemHandler, int index, int xPosition, int yPosition, int activeSlotCount) {
        super(itemHandler, index, xPosition, yPosition);
        this.upgrade = upgrade;
        this.activeSlotCount = activeSlotCount;
    }

    @Override
    public boolean isActive() {
        return upgrade.isTabOpened();
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return upgrade.isTabOpened() && super.mayPlace(pStack);
    }

    @Override
    public boolean mayPickup(Player player) {
        return true;
    }
}
