package com.tiviacz.travelersbackpack.item.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class PickupUpgradeItem extends UpgradeItem {
    public PickupUpgradeItem(Properties pProperties) {
        super(pProperties, "pickup_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.pickupUpgradeSettings.enableUpgrade && super.isEnabled(enabledFeatures);
    }
}