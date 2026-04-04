package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.smelting.FurnaceUpgrade;
import com.tiviacz.travelersbackpack.util.ContainerContentsHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public class FurnaceUpgradeItem extends UpgradeItem {
    public FurnaceUpgradeItem(Properties pProperties) {
        super(pProperties, "furnace_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        if(TravelersBackpackConfig.serverSpec.isLoaded()) {
            return TravelersBackpackConfig.SERVER.backpackUpgrades.enableFurnaceUpgrade.get() && super.isEnabled(enabledFeatures);
        }
        return super.isEnabled(enabledFeatures);
    }

    @Override
    public boolean isTickingUpgrade() {
        return true;
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return FurnaceUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> {
            ItemContainerContents contents = provider.getOrDefault(ModDataComponents.BACKPACK_CONTAINER.get(), ItemContainerContents.EMPTY);
            NonNullList<ItemStack> items = ContainerContentsHelper.getItems(contents, 3);
            return Optional.of(new FurnaceUpgrade(upgradeManager, dataHolderSlot, items));
        };
    }
}