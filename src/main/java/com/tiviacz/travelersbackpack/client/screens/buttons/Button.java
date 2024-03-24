package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import net.minecraft.resources.ResourceLocation;

public abstract class Button implements IButton
{
    protected final TravelersBackpackScreen screen;
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;

    public Button(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        this.screen = screen;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawButton(PoseStack poseStack, int mouseX, int mouseY, ResourceLocation texture, int u1, int v1, int u2, int v2)
    {
        RenderSystem.setShaderTexture(0, texture);

        if(this.inButton(mouseX, mouseY))
        {
            screen.blit(poseStack, screen.getGuiLeft() + x, screen.getGuiTop() + y, u1, v1, width, height);
        }
        else
        {
            screen.blit(poseStack, screen.getGuiLeft() + x, screen.getGuiTop() + y, u2, v2, width, height);
        }
    }

    public boolean inButton(int mouseX, int mouseY)
    {
        mouseX -= screen.getGuiLeft();
        mouseY -= screen.getGuiTop();
        return x <= mouseX && mouseX <= x + width && y <= mouseY && mouseY <= y + height;
    }
}