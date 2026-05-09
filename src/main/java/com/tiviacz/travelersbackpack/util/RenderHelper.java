package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluids;

public class RenderHelper {
    public static void renderScreenTank(GuiGraphicsExtractor guiGraphics, FluidTank tank, double x, double y, double z, double height, double width) {
        renderScreenTank(guiGraphics, tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, z, height, width);
    }

    public static void renderScreenTank(GuiGraphicsExtractor guiGraphics, FluidVariantWrapper fluid, long capacity, long amount, double x, double y, double z, double height, double width) {
        if(fluid == null || fluid.fluidVariant().getFluid() == null || amount <= 0) {
            return;
        }

        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.fluidVariant().getFluid().defaultFluidState());
        TextureAtlasSprite icon = fluidModel.stillMaterial().sprite();

        int renderAmount = (int)Math.max(Math.min(height, amount * height / capacity), 1);
        int posY = (int)(y + height - renderAmount);

        int color = FluidVariantRendering.getColor(fluid.fluidVariant());
        if(fluid.fluidVariant().is(Fluids.WATER)) {
            color = 0xFF3F76E4;
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

    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");

    public static void renderSlotHighlightBack(GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, x - 4, y - 4, 24, 24);
    }

    public static void renderSlotHighlightFront(GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, x - 4, y - 4, 24, 24);
    }
}