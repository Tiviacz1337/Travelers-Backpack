package com.tiviacz.travelersbackpack.client.screen.buttons;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public abstract class Button implements IButton
{
    protected final TravelersBackpackHandledScreen screen;
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;

    public Button(TravelersBackpackHandledScreen screen, int x, int y, int width, int height)
    {
        this.screen = screen;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawButton(DrawContext context, int mouseX, int mouseY, Identifier texture, int u1, int v1, int u2, int v2)
    {
        if(this.inButton(mouseX, mouseY))
        {
            context.drawTexture(texture, screen.getX() + x, screen.getY() + y, u1, v1, width, height);
        }
        else
        {
            context.drawTexture(texture, screen.getX() + x, screen.getY() + y, u2, v2, width, height);
        }
    }

    public boolean inButton(int mouseX, int mouseY)
    {
        mouseX -= screen.getX();
        mouseY -= screen.getY();
        return x <= mouseX && mouseX <= x + width && y <= mouseY && mouseY <= y + height;
    }
}