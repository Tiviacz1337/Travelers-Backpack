package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.client.Minecraft;

public class TankSlotWidget extends WidgetBase
{
    public TankSlotWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = false;
        this.showTooltip = false;
    }

    @Override
    void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bind(TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK);

        Tiers.Tier tier = screen.inv.getTier();

        isVisible = this == screen.leftTankSlotWidget ?
                !screen.inv.getFluidSlotsInventory().getStackInSlot(tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT)).isEmpty() || !screen.inv.getFluidSlotsInventory().getStackInSlot(tier.getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT)).isEmpty() :
                !screen.inv.getFluidSlotsInventory().getStackInSlot(tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT)).isEmpty() || !screen.inv.getFluidSlotsInventory().getStackInSlot(tier.getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT)).isEmpty();

        if(isVisible() && !screen.inv.getSettingsManager().showToolSlots())
        {
            blit(matrixStack, x, y, 184, 0, width, height);
        }
    }

    @Override
    void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {

    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        return false;
    }

    @Override
    public boolean isSettingsChild()
    {
        return false;
    }
}