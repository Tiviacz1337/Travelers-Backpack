package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class TankSlotWidget extends WidgetBase
{
    public TankSlotWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = false;
        this.showTooltip = false;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, Minecraft minecraft, int mouseX, int mouseY)
    {
        isVisible = this == screen.leftTankSlotWidget ?
                !screen.container.getFluidSlotsHandler().getStackInSlot(0).isEmpty() || !screen.container.getFluidSlotsHandler().getStackInSlot(1).isEmpty() :
                !screen.container.getFluidSlotsHandler().getStackInSlot(2).isEmpty() || !screen.container.getFluidSlotsHandler().getStackInSlot(3).isEmpty();

        if(isVisible() && (this == screen.rightTankSlotWidget || !screen.container.getSettingsManager().showToolSlots()))
        {
            guiGraphics.blit(TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, x, y, 184, 0, width, height);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {

    }

    @Override
    public boolean isSettingsChild()
    {
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        return false;
    }

    @Override
    public void setFocused(boolean p_265728_) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }
}