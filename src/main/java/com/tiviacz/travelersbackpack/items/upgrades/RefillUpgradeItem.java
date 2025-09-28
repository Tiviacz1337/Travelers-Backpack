package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.refill.RefillUpgrade;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public class RefillUpgradeItem extends UpgradeItem {
    public RefillUpgradeItem(Properties pProperties) {
        super(pProperties, "refill_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.refillUpgradeSettings.enableRefillUpgrade.get() && super.isEnabled(enabledFeatures);
    }

    @Override
    public boolean isTickingUpgrade() {
        return true;
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return RefillUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> {
            NonNullList<ItemStack> filter = NbtHelper.getOrDefault(provider, ModDataHelper.BACKPACK_CONTAINER, NonNullList.withSize(9, ItemStack.EMPTY));
            return Optional.of(new RefillUpgrade(upgradeManager, dataHolderSlot, filter));
        };
    }
}