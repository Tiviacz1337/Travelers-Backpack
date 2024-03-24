package com.tiviacz.travelersbackpack.client.screen.widget;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class TankSlotWidget extends WidgetBase
{
    public TankSlotWidget(TravelersBackpackHandledScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = false;
        this.showTooltip = false;
    }

    @Override
    void drawBackground(DrawContext context, MinecraftClient minecraft, int mouseX, int mouseY)
    {
        isVisible = this == screen.leftTankSlotWidget ?
                !screen.inventory.getFluidSlotsInventory().getStack(0).isEmpty() || !screen.inventory.getFluidSlotsInventory().getStack(1).isEmpty() :
                !screen.inventory.getFluidSlotsInventory().getStack(2).isEmpty() || !screen.inventory.getFluidSlotsInventory().getStack(3).isEmpty();

        if(isVisible() && (this == screen.rightTankSlotWidget || !screen.inventory.getSettingsManager().showToolSlots()))
        {
            context.drawTexture(TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK, x, y, 184, 0, width, height);
        }
    }

    @Override
    void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY)
    {

    }

    @Override
    public boolean isSettingsChild()
    {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return false;
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }
}