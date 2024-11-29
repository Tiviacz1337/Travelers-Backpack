package com.tiviacz.travelersbackpackneo.items.upgrades;

import com.tiviacz.travelersbackpackneo.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class FeedingUpgradeItem extends UpgradeItem {
    public FeedingUpgradeItem(Properties pProperties) {
        super(pProperties, "feeding_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.feedingUpgradeSettings.enableUpgrade.get() && super.isEnabled(enabledFeatures);
    }
}