package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.init.ModDataComponents;

public interface IEnable {
    default boolean isEnabled(UpgradeBase<?> upgrade) {
        return upgrade.getDataHolderStack().getOrDefault(ModDataComponents.UPGRADE_ENABLED, true);
    }

    default void setEnabled(boolean enabled) {

    }
}
