package com.tiviacz.travelersbackpack.item.upgrades;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class MagnetUpgradeItem extends UpgradeItem {
    public MagnetUpgradeItem(Properties pProperties) {
        super(pProperties, null);
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.magnetUpgradeSettings.enableUpgrade && super.isEnabled(enabledFeatures);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.travelersbackpack.magnet_upgrade_tooltip", TravelersBackpackConfig.getConfig().backpackUpgrades.magnetUpgradeSettings.pullRange).withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}