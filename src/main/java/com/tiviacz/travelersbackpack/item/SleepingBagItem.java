package com.tiviacz.travelersbackpack.item;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class SleepingBagItem extends BedItem {
    public SleepingBagItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.enableSleepingBag && super.isEnabled(enabledFeatures);
    }

    public static int getDefaultColor() {
        return TravelersBackpackConfig.getConfig().backpackUpgrades.enableSleepingBag ? DyeColor.RED.getId() : -1;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(TravelersBackpackConfig.getConfig().backpackUpgrades.enableSleepingBag) {
            tooltipComponents.add(Component.translatable("item.travelersbackpack.sleeping_bag_attach"));
        }
    }
}