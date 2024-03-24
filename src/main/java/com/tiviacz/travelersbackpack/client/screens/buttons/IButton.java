package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IButton
{
    void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks);

    void renderTooltip(PoseStack poseStack, int mouseX, int mouseY);

    boolean mouseClicked(double mouseX, double mouseY, int button);
}