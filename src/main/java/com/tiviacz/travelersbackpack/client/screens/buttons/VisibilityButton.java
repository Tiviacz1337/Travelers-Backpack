package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class VisibilityButton extends Button {
    public VisibilityButton(TravelersBackpackScreen screen) {
        super(screen, 225, 42 + screen.container.getYOffset(), 18, 18);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (screen.settingsWidget.isWidgetActive() && !screen.isWidgetVisible(3, screen.rightTankSlotWidget)) {
            boolean visibility = screen.container.getItemStack().getOrDefault(ModDataComponents.VISIBILITY, true);
            if (visibility) {
                guiGraphics.blit(TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, screen.getGuiLeft() + this.x, screen.getGuiTop() + this.y, 38, 38, this.width, 18);
            } else {
                guiGraphics.blit(TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, screen.getGuiLeft() + this.x, screen.getGuiTop() + this.y, 57, 38, this.width, 18);
            }
            if (this.inButton(mouseX, mouseY)) {
                guiGraphics.blit(TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, screen.getGuiLeft() + this.x, screen.getGuiTop() + this.y, 19, 0, this.width, 18);
            }
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        if (this.inButton(mouseX, mouseY) && screen.settingsWidget.isWidgetActive() && !screen.isWidgetVisible(3, screen.rightTankSlotWidget)) {
            boolean visibility = screen.container.getItemStack().getOrDefault(ModDataComponents.VISIBILITY, true);
            if (visibility) {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.hide_backpack"), mouseX, mouseY);
            } else {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.show_backpack"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.inButton((int)mouseX, (int)mouseY) && screen.settingsWidget.isWidgetActive() && !screen.isWidgetVisible(3, screen.rightTankSlotWidget)) {
            PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(screen.container.getScreenID(), Reference.TOGGLE_VISIBILITY, 0.0D));
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }
}