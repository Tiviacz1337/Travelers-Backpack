package com.tiviacz.travelersbackpack.client.screens.buttons;

import net.minecraft.client.gui.GuiGraphics;

public interface IButton
{
    void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);

    void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY);

    boolean mouseClicked(double mouseX, double mouseY, int button);
}