package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class MoreButton extends Button {
    public MoreButton(BackpackScreen screen) {
        super(screen, screen.getWidthAdditions() + 157, screen.getMiddleBar(), 12, 12);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS, 4, 44, 78, 82);
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if(inButton(mouseX, mouseY)) {
            guiGraphics.setTooltipForNextFrame(screen.getFont(), Component.translatable("screen.travelersbackpack.show_more_buttons"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean button) {
        if(inButton(event)) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.TOGGLE_BUTTONS_VISIBILITY);
            screen.playUIClickSound();
            return true;
        }
        return false;
    }
}