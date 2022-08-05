package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

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

    public void draw(MatrixStack matrixStack, TravelersBackpackScreen screen, int U, int V)
    {
        screen.blit(matrixStack, screen.getGuiLeft() + X, screen.getGuiTop() + Y, U, V, W, H);
    }

    public boolean inButton(TravelersBackpackScreen screen, int mouseX, int mouseY)
    {
        mouseX -= screen.getGuiLeft();
        mouseY -= screen.getGuiTop();
        return X <= mouseX && mouseX <= X + W && Y <= mouseY && mouseY <= Y + H;
    }

    public boolean inButton(TravelersBackpackScreen screen, int mouseX, int mouseY, int X)
    {
        mouseX -= screen.getGuiLeft();
        mouseY -= screen.getGuiTop();
        return X <= mouseX && mouseX <= X + 9 && -6 <= mouseY && mouseY <= -6 + 9;
    }
}