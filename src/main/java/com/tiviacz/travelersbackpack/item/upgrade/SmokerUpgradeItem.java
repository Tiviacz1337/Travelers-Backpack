package com.tiviacz.travelersbackpack.item.upgrade;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.smelting.SmokerUpgrade;
import com.tiviacz.travelersbackpack.util.ContainerContentsHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public class SmokerUpgradeItem extends UpgradeItem {
    public SmokerUpgradeItem(Properties pProperties) {
        super(pProperties, "smoker_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        if(TravelersBackpackConfig.serverSpec.isLoaded()) {
            return TravelersBackpackConfig.SERVER.backpackUpgrades.enableSmokerUpgrade.get() && super.isEnabled(enabledFeatures);
        }
        return super.isEnabled(enabledFeatures);
    }

    @Override
    public boolean isTickingUpgrade() {
        return true;
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return SmokerUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> {
            ItemContainerContents contents = provider.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.EMPTY);
            NonNullList<ItemStack> items = ContainerContentsHelper.getItems(contents, 3);
            return Optional.of(new SmokerUpgrade(upgradeManager, dataHolderSlot, items));
        };
    }
}