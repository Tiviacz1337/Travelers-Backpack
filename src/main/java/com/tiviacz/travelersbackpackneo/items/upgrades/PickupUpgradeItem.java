package com.tiviacz.travelersbackpackneo.items.upgrades;

import com.tiviacz.travelersbackpackneo.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class PickupUpgradeItem extends UpgradeItem {
    public PickupUpgradeItem(Properties pProperties) {
        super(pProperties, "pickup_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.pickupUpgradeSettings.enableUpgrade.get() && super.isEnabled(enabledFeatures);
    }
}