package com.tiviacz.travelersbackpack.client.screen;

import net.minecraft.client.util.math.MatrixStack;

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

    public void draw(MatrixStack matrices, TravelersBackpackHandledScreen handledScreen, int U, int V)
    {
        handledScreen.drawTexture(matrices, X + handledScreen.getX(),handledScreen.getY() + Y, U, V, W, H);
    }

    public boolean inButton(TravelersBackpackHandledScreen handledScreen, int mouseX, int mouseY)
    {
        mouseX -= handledScreen.getX();
        mouseY -= handledScreen.getY();
        return X <= mouseX && mouseX <= X + W && Y <= mouseY && mouseY <= Y + H;
    }

    public boolean inButton(TravelersBackpackHandledScreen handledScreen, int mouseX, int mouseY, int X)
    {
        mouseX -= handledScreen.getX();
        mouseY -= handledScreen.getY();
        return X <= mouseX && mouseX <= X + 9 && -6 <= mouseY && mouseY <= -6 + 9;
    }
}