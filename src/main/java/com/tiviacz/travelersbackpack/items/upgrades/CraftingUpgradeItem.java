package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public class CraftingUpgradeItem extends UpgradeItem {
    public CraftingUpgradeItem(Properties pProperties) {
        super(pProperties, "crafting_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.enableCraftingUpgrade.get() && super.isEnabled(enabledFeatures);
    }

    @Override
    public boolean requiresEquippedBackpack() {
        return false;
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return CraftingUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> {
            BackpackContainerContents contents = provider.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(9));
            return Optional.of(new CraftingUpgrade(upgradeManager, dataHolderSlot, contents.getItems()));
        };
    }
}