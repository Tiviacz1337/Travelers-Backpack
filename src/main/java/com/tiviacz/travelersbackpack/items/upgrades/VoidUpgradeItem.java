package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidUpgrade;
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
            BackpackContainerContents filter = provider.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(9));
            filter = filter.updateSlot(new BackpackContainerContents.Slot(0, ItemStack.EMPTY.copy())); //#TODO TO REMOVE IN THE FUTURE, KEEP IT NOW TO PREVENT DUPLICATION WHILE UPDATING FROM PREV VERSION
            List<String> filterTags = new ArrayList<>(provider.getOrDefault(ModDataComponents.FILTER_TAGS, new ArrayList<>()));
            return Optional.of(new VoidUpgrade(upgradeManager, dataHolderSlot, filter.getItems(), filterTags));
        };
    }
}