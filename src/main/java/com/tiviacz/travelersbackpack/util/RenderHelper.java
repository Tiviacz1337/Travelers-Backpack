package com.tiviacz.travelersbackpack.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public class RenderHelper {
    public static void renderScreenTank(GuiGraphics guiGraphics, FluidTank tank, double x, double y, double z, double height, double width) {
        renderScreenTank(guiGraphics, tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, z, height, width);
    }

    public static void renderScreenTank(GuiGraphics guiGraphics, FluidStack fluid, int capacity, int amount, double x, double y, double z, double height, double width) {
        if(fluid == null || fluid.getFluid() == null || amount <= 0) {
            return;
        }

        TextureAtlasSprite icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType()).getStillTexture());

        if(icon == null) {
            icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MissingTextureAtlasSprite.getLocation());
        }

        int renderAmount = (int)Math.max(Math.min(height, amount * height / capacity), 1);
        int posY = (int)(y + height - renderAmount);

        int color = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType()).getTintColor(fluid);

        guiGraphics.pose().pushPose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, 1);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.disableBlend();

        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for(int i = 0; i < width; i += 16) {
            for(int j = 0; j < renderAmount; j += 16) {
                int drawWidth = (int)Math.min(width - i, 16);
                int drawHeight = Math.min(renderAmount - j, 16);

                int drawX = (int)(x + i);
                int drawY = posY + j;

                float minU;
                float minV;

                minU = icon.getU0();
                minV = icon.getV0();

                float maxU = icon.getU1();
                float maxV = icon.getV1();

                builder.addVertex(matrix4f, drawX, drawY + drawHeight, (float)z).setUv(minU, minV + (maxV - minV) * (float)drawHeight / 16F);
                builder.addVertex(matrix4f, drawX + drawWidth, drawY + drawHeight, (float)z).setUv(minU + (maxU - minU) * (float)drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F);
                builder.addVertex(matrix4f, drawX + drawWidth, drawY, (float)z).setUv(minU + (maxU - minU) * drawWidth / 16F, minV);
                builder.addVertex(matrix4f, drawX, drawY, (float)z).setUv(minU, minV);
            }
        }
        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.pose().popPose();
    }
}