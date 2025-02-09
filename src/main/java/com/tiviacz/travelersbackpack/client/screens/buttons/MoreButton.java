package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoreButton extends Button
{
    public MoreButton(BackpackScreen screen) {
        super(screen, screen.getWidthAdditions() + 157, screen.getImageHeight() - 96, 12, 12);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS, 4, 44, 78, 82);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(inButton(mouseX, mouseY)) {
            guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.show_more_buttons"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(inButton((int)mouseX, (int)mouseY)) {
            screen.showAllButtons = !screen.showAllButtons;
            screen.playUIClickSound();
            return true;
        }
        return false;
    }
}