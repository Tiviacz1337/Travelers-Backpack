package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public abstract class WidgetBase extends GuiComponent implements Renderable, GuiEventListener, NarratableEntry
{
    public final TravelersBackpackScreen screen;
    protected int x;
    protected int y;
    protected int zOffset = 0;
    protected int width;
    protected int height;
    protected boolean isWidgetActive = false;
    protected boolean isVisible;
    protected boolean showTooltip;

    public WidgetBase(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        if(zOffset != 0)
        {
            poseStack.pushPose();
            poseStack.translate(0, 0, zOffset);
        }

        RenderSystem.enableDepthTest();
        renderBg(poseStack, Minecraft.getInstance(), mouseX, mouseY);
        renderTooltip(poseStack, mouseX, mouseY);

        if(zOffset != 0)
        {
            poseStack.popPose();
        }
    }

    abstract void renderBg(PoseStack poseStack, Minecraft minecraft, int mouseX, int mouseY);

    abstract void renderTooltip(PoseStack poseStack, int mouseX, int mouseY);

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY)
    {
        return pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        if(isMouseOver(pMouseX, pMouseY))
        {
            setWidgetStatus(!this.isWidgetActive);
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    public void setWidgetStatus(boolean status)
    {
        this.isWidgetActive = status;
    }

    public boolean isWidgetActive()
    {
        return this.isWidgetActive;
    }

    public boolean isVisible()
    {
        return this.isVisible;
    }

    public void setVisible(boolean visibility)
    {
        this.isVisible = visibility;
    }

    public void setTooltipVisible(boolean visible)
    {
        this.showTooltip = visible;
    }

    public boolean isSettingsChild()
    {
        return true;
    }

    public boolean in(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        //mouseX -= screen.getGuiLeft();
        //mouseY -= screen.getGuiTop();
        return x <= mouseX && mouseX <= x + width && y <= mouseY && mouseY <= y + height;
    }

    @Override
    public NarrationPriority narrationPriority()
    {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput)
    {

    }

    public int[] getWidgetSizeAndPos()
    {
        int[] size = new int[4];
        size[0] = x;
        size[1] = y;
        size[2] = width;
        size[3] = height;
        return size;
    }
}
