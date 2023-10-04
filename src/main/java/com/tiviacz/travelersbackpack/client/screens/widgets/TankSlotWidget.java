package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.Tiers;
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
        Tiers.Tier tier = screen.container.getTier();

        isVisible = this == screen.leftTankSlotWidget ?
                !screen.container.getFluidSlotsHandler().getStackInSlot(tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT)).isEmpty() || !screen.container.getFluidSlotsHandler().getStackInSlot(tier.getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT)).isEmpty() :
                !screen.container.getFluidSlotsHandler().getStackInSlot(tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT)).isEmpty() || !screen.container.getFluidSlotsHandler().getStackInSlot(tier.getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT)).isEmpty();

        if(isVisible() && !screen.container.getSettingsManager().showToolSlots())
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