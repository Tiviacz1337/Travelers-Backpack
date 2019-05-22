package com.tiviacz.travellersbackpack.gui;

public class GuiImageButtonNormal 
{
    private int X;
    private int Y;
    private int W;
    private int H;

    public GuiImageButtonNormal(int U, int V, int W, int H)
    {
        this.X = U;
        this.Y = V;
        this.W = W;
        this.H = H;
    }

    public void draw(GuiTravellersBackpack gui, int U, int V)
    {
        gui.drawTexturedModalRect(gui.getGuiLeft() + X, gui.getGuiTop() + Y, U, V, W, H);
    }

    public boolean inButton(GuiTravellersBackpack gui, int mouseX, int mouseY)
    {
        mouseX -= gui.getGuiLeft();
        mouseY -= gui.getGuiTop();
        return X <= mouseX && mouseX <= X + W && Y <= mouseY && mouseY <= Y + H;
    }
}