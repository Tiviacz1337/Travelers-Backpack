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
            if(screen.getWrapper().isAbilityEnabled()) {
                List<FormattedCharSequence> list = new ArrayList<>();
                list.add(Component.translatable("screen.travelersbackpack.ability_enabled").getVisualOrderText());
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST, screen.getWrapper().getBackpackStack()) || BackpackAbilities.isOnList(BackpackAbilities.BLOCK_TIMER_ABILITIES_LIST, screen.getWrapper().getBackpackStack())) {
                    list.add(screen.getWrapper().getCooldown() == 0 ? Component.translatable("screen.travelersbackpack.ability_ready").getVisualOrderText() : Component.translatable(BackpackDeathHelper.getConvertedTime(screen.getWrapper().getCooldown())).getVisualOrderText());
                }
                guiGraphics.renderTooltip(screen.getFont(), list, mouseX, mouseY);
            } else {
                if(!TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() || !BackpackAbilities.ALLOWED_ABILITIES.contains(screen.getWrapper().getBackpackStack().getItem())) {
                    guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.ability_disabled_config"), mouseX, mouseY);
                } else {
                    guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.ability_disabled"), mouseX, mouseY);
                }
            }
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