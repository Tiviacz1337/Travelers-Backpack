package com.tiviacz.travelersbackpack.client.screen.buttons;

import net.minecraft.client.gui.DrawContext;

public interface IButton
{
    void render(DrawContext context, int mouseX, int mouseY, float delta);

    void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY);

    boolean mouseClicked(double mouseX, double mouseY, int button);
}