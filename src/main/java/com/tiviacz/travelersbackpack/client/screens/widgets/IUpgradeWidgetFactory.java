package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;

public interface IUpgradeWidgetFactory<T extends UpgradeBase> {
    WidgetBase<BackpackScreen> create(BackpackScreen screen, T upgrade, int x, int y);
}