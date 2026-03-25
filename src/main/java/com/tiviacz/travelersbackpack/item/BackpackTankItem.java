package com.tiviacz.travelersbackpack.item;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;

public class BackpackTankItem extends Item {
    public BackpackTankItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        if(TravelersBackpackConfig.serverSpec.isLoaded()) {
            return TravelersBackpackConfig.SERVER.backpackUpgrades.enableTanksUpgrade.get() && super.isEnabled(enabledFeatures);
        }
        return super.isEnabled(enabledFeatures);
    }
}