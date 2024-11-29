package com.tiviacz.travelersbackpackneo.items.upgrades;

import com.tiviacz.travelersbackpackneo.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class MagnetUpgradeItem extends UpgradeItem {
    public MagnetUpgradeItem(Properties pProperties) {
        super(pProperties, "magnet_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.enableMagnetUpgrade.get() && super.isEnabled(enabledFeatures);
    }
}
