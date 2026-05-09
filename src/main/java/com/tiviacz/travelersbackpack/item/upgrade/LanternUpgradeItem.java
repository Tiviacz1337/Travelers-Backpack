package com.tiviacz.travelersbackpack.item.upgrade;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.lantern.LanternUpgrade;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;
import java.util.function.Consumer;

public class LanternUpgradeItem extends UpgradeItem {
    public LanternUpgradeItem(Properties pProperties) {
        super(pProperties, "lantern_upgrade");
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        if(TravelersBackpackConfig.serverSpec.isLoaded()) {
            return TravelersBackpackConfig.SERVER.backpackUpgrades.enableLanternUpgrade.get() && super.isEnabled(enabledFeatures);
        }
        return super.isEnabled(enabledFeatures);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag) {
        addStatusTooltip(componentConsumer);
        super.appendHoverText(stack, context, tooltipDisplay, componentConsumer, tooltipFlag);
    }

    public static void addStatusTooltip(Consumer<Component> consumer) {
        if(TravelersBackpack.lambDynamicLightsLoaded) {
            consumer.accept(Component.translatable("item.travelersbackpack.lantern_upgrade_active"));
        } else {
            consumer.accept(Component.translatable("item.travelersbackpack.lantern_upgrade_missing"));
        }
    }

    @Override
    public boolean hasBlockFunctionality() {
        return false;
    }

    @Override
    public Class<? extends UpgradeBase<?>> getUpgradeClass() {
        return LanternUpgrade.class;
    }

    @Override
    public TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade() {
        return (upgradeManager, dataHolderSlot, provider) -> Optional.of(new LanternUpgrade(upgradeManager, dataHolderSlot));
    }
}