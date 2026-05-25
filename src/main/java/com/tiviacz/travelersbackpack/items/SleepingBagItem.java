package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        if(TravelersBackpackConfig.getConfig().backpackUpgrades.enableSleepingBag) {
            tooltipComponents.add(Component.translatable("item.travelersbackpack.sleeping_bag_attach"));
        }
    }
}