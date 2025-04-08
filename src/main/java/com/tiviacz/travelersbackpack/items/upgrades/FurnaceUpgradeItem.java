package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.smelting.FurnaceUpgrade;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public class FurnaceUpgradeItem extends UpgradeItem {
    public FurnaceUpgradeItem(Properties pProperties) {
        super(pProperties, "furnace_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.enableFurnaceUpgrade.get() && super.isEnabled(enabledFeatures);
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
            BackpackContainerContents contents = provider.getOrDefault(ModDataComponents.BACKPACK_CONTAINER.get(), new BackpackContainerContents(3));
            return Optional.of(new FurnaceUpgrade(upgradeManager, dataHolderSlot, contents.getItems()));
        };
    }
}