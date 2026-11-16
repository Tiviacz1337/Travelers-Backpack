package com.tiviacz.travelersbackpack.inventory.upgrades.anvil;

import com.tiviacz.travelersbackpack.inventory.menu.slot.PickAllPreventable;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ShiftDirectionProvider;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AnvilResultSlot extends Slot implements ShiftDirectionProvider<AnvilUpgrade>, PickAllPreventable {
    private final AnvilUpgrade upgradeParent;

    public AnvilResultSlot(AnvilUpgrade upgradeParent, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.upgradeParent = upgradeParent;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return this.hasItem() && this.upgradeParent.mayPickup(player);
    }

    @Override
    public void onTake(Player player, ItemStack carried) {
        this.upgradeParent.onTake(player, carried);
        super.onTake(player, carried);
    }

    //Think about button placement (?)
    @Override
    public boolean shiftClickToBackpack() {
        return false;
    }

    @Override
    public AnvilUpgrade getUpgradeParent() {
        return this.upgradeParent;
    }
}