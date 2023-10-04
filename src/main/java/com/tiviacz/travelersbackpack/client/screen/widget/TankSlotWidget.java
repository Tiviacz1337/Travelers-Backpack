package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class TankSlotWidget extends WidgetBase
{
    public TankSlotWidget(TravelersBackpackHandledScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = false;
        this.showTooltip = false;
    }

    @Override
    void drawBackground(MatrixStack matrixStack, MinecraftClient minecraft, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK);

        Tiers.Tier tier = screen.inventory.getTier();

        isVisible = this == screen.leftTankSlotWidget ?
                !screen.inventory.getFluidSlotsInventory().getStack(tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT)).isEmpty() || !screen.inventory.getFluidSlotsInventory().getStack(tier.getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT)).isEmpty() :
                !screen.inventory.getFluidSlotsInventory().getStack(tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT)).isEmpty() || !screen.inventory.getFluidSlotsInventory().getStack(tier.getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT)).isEmpty();

        if(isVisible() && !screen.inventory.getSettingsManager().showToolSlots())
        {
            drawTexture(matrixStack, x, y, 184, 0, width, height);
        }
    }

    @Override
    void drawMouseoverTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
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
}