package com.tiviacz.travelersbackpack.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class RenderHelper {
    public static void renderScreenTank(GuiGraphicsExtractor guiGraphics, FluidStacksResourceHandler tank, double x, double y, double z, double height, double width) {
        renderScreenTank(guiGraphics, StacksHandlerUtils.getFluid(tank), StacksHandlerUtils.getCapacity(tank), StacksHandlerUtils.getFluidAmount(tank), x, y, z, height, width);
    }

    public static void renderScreenTank(GuiGraphicsExtractor guiGraphics, FluidStack fluid, int capacity, int amount, double x, double y, double z, double height, double width) {
        if(fluid == null || fluid.getFluid() == null || amount <= 0) {
            return;
        }

        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.getFluid().defaultFluidState());
        TextureAtlasSprite icon = fluidModel.stillMaterial().sprite();

        int renderAmount = (int)Math.max(Math.min(height, amount * height / capacity), 1);
        int posY = (int)(y + height - renderAmount);

        int color = -1;
        if(fluidModel.fluidTintSource() != null) {
            color = fluidModel.fluidTintSource().colorAsStack(fluid);
        }
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