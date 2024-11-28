package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;

public class BackpackTankItem extends Item {
    public BackpackTankItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.enableTanksUpgrade.get() && super.isEnabled(enabledFeatures);
    }
}