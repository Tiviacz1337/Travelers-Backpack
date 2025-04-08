package com.tiviacz.travelersbackpack.items.upgrades;

import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Optional;

public abstract class UpgradeItem extends Item {
    private final Component tooltipComponent;

    public UpgradeItem(Properties pProperties, String tooltipKey) {
        super(pProperties);
        this.tooltipComponent = tooltipKey == null ? null : Component.translatable("item.travelersbackpack." + tooltipKey + "_tooltip").withStyle(ChatFormatting.BLUE);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(this.tooltipComponent != null) {
            tooltipComponents.add(this.tooltipComponent);
        }
        tooltipComponents.add(Component.translatable("item.travelersbackpack.upgrade_apply_tooltip"));
    }

    public boolean isTickingUpgrade() {
        return false;
    }

    public boolean requiresEquippedBackpack() {
        return true;
    }

    public abstract Class<? extends UpgradeBase<?>> getUpgradeClass();

    public abstract TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade();
}