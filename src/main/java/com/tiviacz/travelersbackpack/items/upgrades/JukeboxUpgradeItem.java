package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class JukeboxUpgradeItem extends UpgradeItem {
    public JukeboxUpgradeItem(Properties pProperties) {
        super(pProperties, "jukebox_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        if(TravelersBackpackConfig.serverSpec.isLoaded()) {
            return TravelersBackpackConfig.SERVER.backpackUpgrades.enableJukeboxUpgrade.get() && super.isEnabled(enabledFeatures);
        }
        return super.isEnabled(enabledFeatures); //return TravelersBackpackConfig.SERVER.backpackUpgrades.enableJukeboxUpgrade.get() && super.isEnabled(enabledFeatures);
    }
}