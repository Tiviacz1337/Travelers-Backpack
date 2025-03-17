package com.tiviacz.travelersbackpack.item.upgrades;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class UpgradeItem extends Item {
    private final Component tooltipComponent;

    public UpgradeItem(Properties pProperties, String tooltipKey) {
        super(pProperties);
        this.tooltipComponent = tooltipKey == null ? null : Component.translatable("item.travelersbackpack." + tooltipKey + "_tooltip").withStyle(ChatFormatting.BLUE);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(this.tooltipComponent != null) {
            tooltipComponents.add(this.tooltipComponent);
        }
        tooltipComponents.add(Component.translatable("item.travelersbackpack.upgrade_apply_tooltip"));
    }
}