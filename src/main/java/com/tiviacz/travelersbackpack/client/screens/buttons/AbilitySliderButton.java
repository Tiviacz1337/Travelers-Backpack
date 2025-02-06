package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundAbilitySliderPacket;
import com.tiviacz.travelersbackpack.util.BackpackDeathHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbilitySliderButton extends Button {
    private final WidgetElement abilitySliderElement = new WidgetElement(new Point(133, -95), new Point(18, 11));
    private final boolean isBlock;

    public AbilitySliderButton(BackpackScreen screen, boolean isBlock, boolean isSleepingBagPresent) {
        super(screen, screen.getWidthAdditions() + (isSleepingBagPresent ? 115 : 133), screen.getImageHeight() - 95, 18, 11);
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
            this.drawButton(guiGraphics, mouseX, mouseY, texture, 42, 54, 42, 76);
        } else {
            this.drawButton(guiGraphics, mouseX, mouseY, texture, 42, 65, 42, 76);
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
                components.add(Component.translatable("screen.travelersbackpack.ability_cooldown", BackpackDeathHelper.getConvertedTime(screen.getWrapper().getCooldown())));
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
                PacketDistributorHelper.sendToServer(new ServerboundAbilitySliderPacket(screen.getWrapper().getScreenID(), !screen.getWrapper().isAbilityEnabled()));
                screen.playUIClickSound();
                return true;
            }
        } else {
            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, screen.getWrapper().getBackpackStack()) && this.inButton((int)mouseX, (int)mouseY)) {
                PacketDistributorHelper.sendToServer(new ServerboundAbilitySliderPacket(screen.getWrapper().getScreenID(), !screen.getWrapper().isAbilityEnabled()));
                screen.playUIClickSound();
                return true;
            }
        }
        return false;
    }
}