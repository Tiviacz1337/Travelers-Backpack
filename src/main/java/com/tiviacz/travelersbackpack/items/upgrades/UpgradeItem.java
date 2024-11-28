package com.tiviacz.travelersbackpack.items.upgrades;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class UpgradeItem extends Item {
    private final Component tooltipComponent;

    public UpgradeItem(Properties pProperties, String tooltipKey) {
        super(pProperties);
        this.tooltipComponent = Component.translatable("item.travelersbackpack." + tooltipKey + "_tooltip").withStyle(ChatFormatting.BLUE);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(this.tooltipComponent);
    }
}