package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidUpgrade;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoidUpgradeItem extends UpgradeItem {
    public VoidUpgradeItem(Properties pProperties) {
        super(pProperties, "void_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.enableUpgrade.get() && super.isEnabled(enabledFeatures);
    }

    @Override
    public boolean requiresEquippedBackpack() {
        return false;
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return VoidUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> {
            NonNullList<ItemStack> filter = NbtHelper.getOrDefault(provider, ModDataHelper.BACKPACK_CONTAINER, NonNullList.withSize(9, ItemStack.EMPTY));
            List<String> filterTags = NbtHelper.getOrDefault(provider, ModDataHelper.FILTER_TAGS, new ArrayList<>());
            return Optional.of(new VoidUpgrade(upgradeManager, dataHolderSlot, filter, filterTags));
        };
    }
}