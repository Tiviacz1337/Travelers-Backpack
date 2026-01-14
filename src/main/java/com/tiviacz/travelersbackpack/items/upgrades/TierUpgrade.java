package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class TierUpgrade extends Item {
    private final Upgrade type;

    public TierUpgrade(Properties pProperties, Upgrade type) {
        super(pProperties);
        this.type = type;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        switch(type) {
            case BLANK_UPGRADE:
                tooltipComponents.add(Component.translatable("item.travelersbackpack.blank_upgrade_tooltip").withStyle(ChatFormatting.BLUE));
                break;
            case IRON_TIER_UPGRADE:
                tooltipComponents.add(Component.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.LEATHER.getLocalizedName()).withStyle(ChatFormatting.BLUE));
                break;
            case GOLD_TIER_UPGRADE:
                tooltipComponents.add(Component.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.IRON.getLocalizedName()).withStyle(ChatFormatting.BLUE));
                break;
            case DIAMOND_TIER_UPGRADE:
                tooltipComponents.add(Component.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.GOLD.getLocalizedName()).withStyle(ChatFormatting.BLUE));
                break;
            case NETHERITE_TIER_UPGRADE:
                tooltipComponents.add(Component.translatable("item.travelersbackpack.tier_upgrade_tooltip", Tiers.DIAMOND.getLocalizedName()).withStyle(ChatFormatting.BLUE));
                break;
        }
    }

    public enum Upgrade {
        BLANK_UPGRADE,
        IRON_TIER_UPGRADE,
        GOLD_TIER_UPGRADE,
        DIAMOND_TIER_UPGRADE,
        NETHERITE_TIER_UPGRADE
    }
}