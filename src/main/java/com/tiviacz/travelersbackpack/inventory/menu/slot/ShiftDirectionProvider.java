package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.upgrades.IMoveSelector;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;

public interface ShiftDirectionProvider<T extends UpgradeBase<?>> extends SlotUpgradeProvider<T> {
    default boolean shiftClickToBackpack() {
        T upgrade = getUpgradeParent();

        if(upgrade instanceof IMoveSelector moveSelector) {
            return moveSelector.shiftClickToBackpack(upgrade.getDataHolderStack());
        }
        return true;
    }
}