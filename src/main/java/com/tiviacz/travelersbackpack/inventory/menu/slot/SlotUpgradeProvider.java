package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;

public interface SlotUpgradeProvider<T extends UpgradeBase<?>> {
    T getUpgradeParent();
}