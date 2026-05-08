package com.tiviacz.travelersbackpack.inventory.upgrades.lantern;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.item.upgrades.LanternUpgradeItem;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class LanternWidget extends UpgradeWidgetBase<LanternUpgrade> {
    public LanternWidget(BackpackScreen screen, LanternUpgrade upgrade, Point pos, Point tabUv, String upgradeIconTooltip) {
        super(screen, upgrade, pos, tabUv, upgradeIconTooltip);
    }

    @Override
    public void getAdditionalTooltips(Consumer<Component> consumer) {
        LanternUpgradeItem.addStatusTooltip(consumer);
    }
}