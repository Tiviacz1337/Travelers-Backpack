package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.flag.FeatureFlagSet;

public class CraftingUpgradeItem extends UpgradeItem {
    public CraftingUpgradeItem(Properties pProperties) {
        super(pProperties, "crafting_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.enableCraftingUpgrade.get() && super.isEnabled(enabledFeatures);
    }
}