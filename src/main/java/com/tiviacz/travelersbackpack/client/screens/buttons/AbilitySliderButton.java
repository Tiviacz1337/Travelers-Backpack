package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbilitySliderButton extends Button {
    private final boolean isBlock;

    public AbilitySliderButton(BackpackScreen screen, boolean isBlock, int xOffset) {
        super(screen, screen.getWidthAdditions() + 145 - xOffset, screen.getMiddleBar(), 12, 12);
        this.isBlock = isBlock;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(isBlock) {
            drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS);
        } else {
            if(AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
                drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS);
            }
        }
    }

    public void drawButton(GuiGraphics guiGraphics, int mouseX, int mouseY, ResourceLocation texture) {
        if(screen.getWrapper().isAbilityEnabled()) {
            this.drawButton(guiGraphics, mouseX, mouseY, texture, 44, 56, 78, 82);
        } else {
            this.drawButton(guiGraphics, mouseX, mouseY, texture, 44, 67, 78, 82);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(inButton(mouseX, mouseY)) {
            //If disabled in config
            if(!BackpackAbilities.isAbilityEnabledInConfig(screen.getWrapper().getBackpackStack())) {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.ability_disabled_config"), mouseX, mouseY);
                return;
            }
            List<Component> components = new ArrayList<>();

            //Ability on/off
            if(screen.getWrapper().isAbilityEnabled()) {
                components.add(Component.translatable("screen.travelersbackpack.ability_enabled"));
            } else {
                components.add(Component.translatable("screen.travelersbackpack.ability_disabled"));
            }

            //Show cooldown
            if(BackpackAbilities.hasCooldown(screen.getWrapper().getBackpackStack())) {
                components.add(Component.translatable("screen.travelersbackpack.ability_cooldown", TextUtils.getConvertedTime(screen.getWrapper().getCooldown())));
            } else {
                components.add(Component.translatable("screen.travelersbackpack.ability_ready"));
            }

            guiGraphics.renderTooltip(screen.getFont(), components, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() || !BackpackAbilities.isAbilityEnabledInConfig(screen.getWrapper().getBackpackStack())) {
            return false;
        }

        if(isBlock) {
            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, screen.getWrapper().getBackpackStack()) && this.inButton((int)mouseX, (int)mouseY)) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.ABILITY_SLIDER, !screen.getWrapper().isAbilityEnabled());
                screen.playUIClickSound();
                return true;
            }
        } else {
            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, screen.getWrapper().getBackpackStack()) && this.inButton((int)mouseX, (int)mouseY)) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.ABILITY_SLIDER, !screen.getWrapper().isAbilityEnabled());
                screen.playUIClickSound();
                return true;
            }
        }
        return false;
    }
}