package com.tiviacz.travelersbackpack.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class RenderHelper {
    public static void renderScreenTank(GuiGraphics guiGraphics, FluidTank tank, double x, double y, double z, double height, double width) {
        renderScreenTank(guiGraphics, tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, z, height, width);
    }

    public static void renderScreenTank(GuiGraphics guiGraphics, FluidStack fluid, int capacity, int amount, double x, double y, double z, double height, double width) {
        if(fluid == null || fluid.getFluid() == null || amount <= 0) {
            return;
        }

        TextureAtlasSprite icon = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType()).getStillTexture());

        int renderAmount = (int)Math.max(Math.min(height, amount * height / capacity), 1);
        int posY = (int)(y + height - renderAmount);

        int color = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType()).getTintColor(fluid);
        guiGraphics.pose().pushMatrix();

        for(int i = 0; i < width; i += 16) {
            for(int j = 0; j < renderAmount; j += 16) {
                int drawWidth = (int)Math.min(width - i, 16);
                int drawHeight = Math.min(renderAmount - j, 16);

                int drawX = (int)(x + i);
                int drawY = posY + j;

                float minU = icon.getU0();
                float minV = icon.getV0();
                float maxU = icon.getU1();
                float maxV = minV + (icon.getV1() - minV) * drawHeight / 16F;

                guiGraphics.fill(RenderPipelines.GUI, drawX, drawY, drawX + drawWidth, drawY + drawHeight, color);
                guiGraphics.innerBlit(RenderPipelines.GUI_TEXTURED, icon.atlasLocation(), drawX, drawX + drawWidth, drawY, drawY + drawHeight, minU, maxU, minV, maxV, color);
            }
        }
        guiGraphics.pose().popMatrix();
    }
}