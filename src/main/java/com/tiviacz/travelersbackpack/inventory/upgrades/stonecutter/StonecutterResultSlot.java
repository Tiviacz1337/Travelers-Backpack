package com.tiviacz.travelersbackpack.inventory.upgrades.stonecutter;

import com.tiviacz.travelersbackpack.inventory.menu.slot.PickAllPreventable;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ShiftDirectionProvider;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class StonecutterResultSlot<T extends UpgradeBase<?>> extends Slot implements ShiftDirectionProvider<T>, PickAllPreventable {
    private final T upgradeParent;

    public StonecutterResultSlot(T upgradeParent, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.upgradeParent = upgradeParent;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public T getUpgradeParent() {
        return this.upgradeParent;
    }
}