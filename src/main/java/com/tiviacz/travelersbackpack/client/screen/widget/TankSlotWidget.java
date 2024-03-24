package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
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
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK);

        isVisible = this == screen.leftTankSlotWidget ?
                !screen.inventory.getFluidSlotsInventory().getStack(0).isEmpty() || !screen.inventory.getFluidSlotsInventory().getStack(1).isEmpty() :
                !screen.inventory.getFluidSlotsInventory().getStack(2).isEmpty() || !screen.inventory.getFluidSlotsInventory().getStack(3).isEmpty();

        if(isVisible() && (this == screen.rightTankSlotWidget || !screen.inventory.getSettingsManager().showToolSlots()))
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