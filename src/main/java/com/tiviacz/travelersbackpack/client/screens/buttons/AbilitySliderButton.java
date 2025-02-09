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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbilitySliderButton extends Button {
    private final WidgetElement abilitySliderElement = new WidgetElement(new Point(133, -95), new Point(18, 11));
    private final boolean isBlock;

    public AbilitySliderButton(BackpackScreen screen, boolean isBlock, int xOffset) {
        super(screen, screen.getWidthAdditions() + 145 - xOffset, screen.getImageHeight() - 96, 12, 12);
        this.isBlock = isBlock;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(isBlock) {
            drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS);
        } else {
            if(!screen.showAllButtons) {
                return;
            }
            if(AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
                drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS);
            }
        }
    }

    public void drawButton(GuiGraphics guiGraphics, int mouseX, int mouseY, ResourceLocation texture) {
        if(!screen.showAllButtons) {
            return;
        }
        if(screen.getWrapper().isAbilityEnabled()) {
            this.drawButton(guiGraphics, mouseX, mouseY, texture, 44, 56, 78, 82);
            //if(inButton(mouseX, mouseY)) {
                //guiGraphics.fillGradient(RenderType.guiOverlay(), screen.getGuiLeft() + x + 3, screen.getGuiTop() + y + 3, screen.getGuiLeft() + x + 8, screen.getGuiTop() + y + 8, -2130706433, -2130706433, 0);
            //}
        } else {
            this.drawButton(guiGraphics, mouseX, mouseY, texture, 44, 67, 78, 82);
            //if(inButton(mouseX, mouseY)) {
                //guiGraphics.fillGradient(RenderType.guiOverlay(), screen.getGuiLeft() + x + 10, screen.getGuiTop() + y + 3, screen.getGuiLeft() + x + 15, screen.getGuiTop() + y + 8, -2130706433, -2130706433, 0);
            //}
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(!screen.showAllButtons) {
            return;
        }
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
        if(!screen.showAllButtons) {
            return false;
        }
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