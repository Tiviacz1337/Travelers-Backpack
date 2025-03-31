package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.util.NbtHelper;

public interface IEnable {
    default boolean isEnabled(UpgradeBase<?> upgrade) {
        return NbtHelper.getOrDefault(upgrade.getDataHolderStack(), ModDataHelper.UPGRADE_ENABLED, true);
    }

    default void setEnabled(boolean enabled) {

    }
}