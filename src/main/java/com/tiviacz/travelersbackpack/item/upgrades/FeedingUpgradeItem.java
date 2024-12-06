package com.tiviacz.travelersbackpack.item.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class FeedingUpgradeItem extends UpgradeItem {
    public FeedingUpgradeItem(Properties pProperties) {
        super(pProperties, "feeding_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.feedingUpgradeSettings.enableUpgrade && super.isEnabled(enabledFeatures);
    }
}