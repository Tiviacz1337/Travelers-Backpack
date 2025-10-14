package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class UpgradeWidgetRegistry {
    private static final Map<Class<? extends UpgradeBase>, IUpgradeWidgetFactory<?>> WIDGET_FACTORIES = new HashMap<>();

    private UpgradeWidgetRegistry() {
    }

    public static <U extends UpgradeBase> void register(Class<U> upgradeClass, IUpgradeWidgetFactory<U> factory) {
        WIDGET_FACTORIES.put(upgradeClass, factory);
    }

    public static <U extends UpgradeBase> Optional<WidgetBase<BackpackScreen>> createWidget(BackpackScreen screen, U upgrade, int x, int y) {
        IUpgradeWidgetFactory<U> factory = (IUpgradeWidgetFactory<U>)WIDGET_FACTORIES.get(upgrade.getClass());
        if(factory != null) {
            return Optional.of(factory.create(screen, upgrade, x, y));
        }
        return Optional.empty();
    }
}