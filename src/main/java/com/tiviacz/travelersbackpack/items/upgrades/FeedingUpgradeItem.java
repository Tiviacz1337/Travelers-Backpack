package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class FeedingUpgradeItem extends UpgradeItem {
    public FeedingUpgradeItem(Properties pProperties) {
        super(pProperties, "feeding_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        if(TravelersBackpackConfig.serverSpec.isLoaded()) {
            return TravelersBackpackConfig.SERVER.backpackUpgrades.feedingUpgradeSettings.enableUpgrade.get() && super.isEnabled(enabledFeatures);
        }
        return super.isEnabled(enabledFeatures);//return TravelersBackpackConfig.SERVER.backpackUpgrades.feedingUpgradeSettings.enableUpgrade.get() && super.isEnabled(enabledFeatures);
    }
}