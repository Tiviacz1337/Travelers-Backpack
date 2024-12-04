package com.tiviacz.travelersbackpack.item.upgrades;

import com.tiviacz.travelersbackpackneo.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class JukeboxUpgradeItem extends UpgradeItem {
    public JukeboxUpgradeItem(Properties pProperties) {
        super(pProperties, "jukebox_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.enableJukeboxUpgrade.get() && super.isEnabled(enabledFeatures);
    }
}