package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class SleepingBagItem extends BedItem {
    public SleepingBagItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.enableSleepingBag.get() && super.isEnabled(enabledFeatures);
    }

    public static int getDefaultColor() {
        if(TravelersBackpackConfig.serverSpec.isLoaded()) {
            return TravelersBackpackConfig.SERVER.backpackUpgrades.enableSleepingBag.get() ? DyeColor.RED.getId() : -1;
        }
        return DyeColor.RED.getId();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(TravelersBackpackConfig.SERVER.backpackUpgrades.enableSleepingBag.get()) {
            tooltipComponents.add(Component.translatable("item.travelersbackpack.sleeping_bag_attach"));
        }
    }
}