package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class SleepingBagItem extends BedItem {
    public SleepingBagItem(Block block, Properties properties) {
        super(block, properties.useBlockDescriptionPrefix());
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

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, componentConsumer, tooltipFlag);
        if(TravelersBackpackConfig.SERVER.backpackUpgrades.enableSleepingBag.get()) {
            componentConsumer.accept(Component.translatable("item.travelersbackpack.sleeping_bag_attach"));
        }
    }
}