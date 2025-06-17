package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.smelting.BlastFurnaceUpgrade;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public class BlastFurnaceUpgradeItem extends UpgradeItem {
    public BlastFurnaceUpgradeItem(Properties pProperties) {
        super(pProperties, "blast_furnace_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.enableBlastFurnaceUpgrade.get() && super.isEnabled(enabledFeatures);
    }

    @Override
    public boolean isTickingUpgrade() {
        return true;
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return BlastFurnaceUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> {
            NonNullList<ItemStack> contents = NbtHelper.getOrDefault(provider, ModDataHelper.BACKPACK_CONTAINER, NonNullList.withSize(3, ItemStack.EMPTY));
            return Optional.of(new BlastFurnaceUpgrade(upgradeManager, dataHolderSlot, contents));
        };
    }
}