package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.pickup.AutoPickupUpgrade;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PickupUpgradeItem extends UpgradeItem {
    public PickupUpgradeItem(Properties pProperties) {
        super(pProperties, "pickup_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.pickupUpgradeSettings.enableUpgrade.get() && super.isEnabled(enabledFeatures);
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return AutoPickupUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> {
            BackpackContainerContents filter = provider.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(9));
            List<String> filterTags = new ArrayList<>(provider.getOrDefault(ModDataComponents.FILTER_TAGS, new ArrayList<>()));
            return Optional.of(new AutoPickupUpgrade(upgradeManager, dataHolderSlot, filter.getItems(), filterTags));
        };
    }
}