package com.tiviacz.travelersbackpack.client.screen.widget;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.inventory.Tiers;
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
        Tiers.Tier tier = screen.inventory.getTier();

        isVisible = this == screen.leftTankSlotWidget ?
                !screen.inventory.getFluidSlotsInventory().getStack(tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT)).isEmpty() || !screen.inventory.getFluidSlotsInventory().getStack(tier.getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT)).isEmpty() :
                !screen.inventory.getFluidSlotsInventory().getStack(tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT)).isEmpty() || !screen.inventory.getFluidSlotsInventory().getStack(tier.getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT)).isEmpty();

        if(isVisible() && !screen.inventory.getSettingsManager().showToolSlots())
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