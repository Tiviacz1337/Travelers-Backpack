package com.tiviacz.travelersbackpack.client.screens.buttons;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public interface IButton {
    void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks);

    void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY);

    boolean mouseClicked(MouseButtonEvent event, boolean b1);
}