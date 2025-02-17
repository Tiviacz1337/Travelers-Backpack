package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class VoidUpgradeItem extends UpgradeItem {
    public VoidUpgradeItem(Properties pProperties) {
        super(pProperties, "void_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        if(TravelersBackpackConfig.serverSpec.isLoaded()) {
            return TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.enableUpgrade.get() && super.isEnabled(enabledFeatures);
        }
        return super.isEnabled(enabledFeatures); //return TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.enableUpgrade.get() && super.isEnabled(enabledFeatures);
    }
}