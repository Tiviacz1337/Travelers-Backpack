package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class MagnetUpgradeItem extends UpgradeItem {
    public MagnetUpgradeItem(Properties pProperties) {
        super(pProperties, "magnet_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        if(TravelersBackpackConfig.serverSpec.isLoaded()) {
            return TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.enableMagnetUpgrade.get() && super.isEnabled(enabledFeatures);
        }
        return super.isEnabled(enabledFeatures); //return TravelersBackpackConfig.SERVER.backpackUpgrades.magnetUpgradeSettings.enableMagnetUpgrade.get() && super.isEnabled(enabledFeatures);
    }
}
