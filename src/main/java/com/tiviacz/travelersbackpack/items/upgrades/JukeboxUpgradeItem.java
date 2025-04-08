package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.jukebox.JukeboxUpgrade;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public class JukeboxUpgradeItem extends UpgradeItem {
    public JukeboxUpgradeItem(Properties pProperties) {
        super(pProperties, "jukebox_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.enableJukeboxUpgrade.get() && super.isEnabled(enabledFeatures);
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return JukeboxUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> {
            BackpackContainerContents musicDisk = provider.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(1));
            return Optional.of(new JukeboxUpgrade(upgradeManager, dataHolderSlot, musicDisk.getItems()));
        };
    }
}