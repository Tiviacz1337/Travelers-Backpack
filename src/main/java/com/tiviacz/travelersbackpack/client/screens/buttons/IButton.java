package com.tiviacz.travelersbackpack.client.screens.buttons;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

public interface IButton {
    void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);

    void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY);

    boolean mouseClicked(MouseButtonEvent event, boolean b1);
}