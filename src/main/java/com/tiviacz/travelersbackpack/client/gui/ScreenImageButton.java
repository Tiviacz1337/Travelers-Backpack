package com.tiviacz.travelersbackpack.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

public class ScreenImageButton
{
    private final int X;
    private final int Y;
    private final int W;
    private final int H;

    public ScreenImageButton(int U, int V, int W, int H)
    {
        this.X = U;
        this.Y = V;
        this.W = W;
        this.H = H;
    }

    public void draw(PoseStack poseStack, TravelersBackpackScreen screen, int U, int V)
    {
        screen.blit(poseStack, screen.getGuiLeft() + X, screen.getGuiTop() + Y, U, V, W, H);
    }

    public boolean inButton(TravelersBackpackScreen screen, int mouseX, int mouseY)
    {
        mouseX -= screen.getGuiLeft();
        mouseY -= screen.getGuiTop();
        return X <= mouseX && mouseX <= X + W && Y <= mouseY && mouseY <= Y + H;
    }
}
