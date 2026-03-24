package com.tiviacz.travelersbackpack.item.upgrades;

import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class UpgradeItem extends Item {
    private final Component tooltipComponent;

    public UpgradeItem(Properties pProperties, String tooltipKey) {
        super(pProperties);
        this.tooltipComponent = tooltipKey == null ? null : Component.translatable("item.travelersbackpack." + tooltipKey + "_tooltip").withStyle(ChatFormatting.BLUE);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, componentConsumer, tooltipFlag);
        if(this.tooltipComponent != null) {
            componentConsumer.accept(this.tooltipComponent);
        }
        componentConsumer.accept(Component.translatable("item.travelersbackpack.upgrade_apply_tooltip"));
    }

    public boolean isTickingUpgrade() {
        return false;
    }

    public boolean requiresEquippedBackpack() {
        return true;
    }

    public boolean hasBlockFunctionality() {
        return true;
    }

    public abstract Class<? extends UpgradeBase<?>> getUpgradeClass();

    public abstract TriFunction<UpgradeManager, Integer, ItemStack, Optional<? extends UpgradeBase<?>>> getUpgrade();
}