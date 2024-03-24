package com.tiviacz.travelersbackpack.client.screen.buttons;

import net.minecraft.client.util.math.MatrixStack;

public interface IButton
{
    void render(MatrixStack matrices, int mouseX, int mouseY, float delta);

    void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY);

    boolean mouseClicked(double mouseX, double mouseY, int button);
}