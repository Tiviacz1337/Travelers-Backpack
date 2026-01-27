package com.tiviacz.travelersbackpack.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix4f;

public class RenderHelper {
    public static void renderScreenTank(GuiGraphics guiGraphics, FluidTank tank, double x, double y, double z, double height, double width) {
        renderScreenTank(guiGraphics, tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, z, height, width);
    }

    public static void renderScreenTank(GuiGraphics guiGraphics, FluidVariantWrapper fluid, long capacity, long amount, double x, double y, double z, double height, double width) {
        if(fluid == null || fluid.fluidVariant().getFluid() == null || amount <= 0) {
            return;
        }

        TextureAtlasSprite icon = FluidVariantRendering.getSprite(fluid.fluidVariant());

        int renderAmount = (int)Math.max(Math.min(height, amount * height / capacity), 1);
        int posY = (int)(y + height - renderAmount);

        int color = FluidVariantRendering.getColor(fluid.fluidVariant());
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

    private static final float OFFSET = 0.01F;
    private static final float MINY = OFFSET;
    private static final float MIN = 0.125F + OFFSET;
    private static final float MAX = 0.3F - OFFSET;
    private static final float[][][] coordinates = {
            { // DOWN
                    {MIN, MINY, MAX},
                    {MIN, MINY, MIN},
                    {MAX, MINY, MIN},
                    {MAX, MINY, MAX}
            },
            { // UP
                    {MAX, MAX, MAX},
                    {MAX, MAX, MIN},
                    {MIN, MAX, MIN},
                    {MIN, MAX, MAX}
            },
            { // NORTH
                    {MIN, MINY, MIN},
                    {MIN, MAX, MIN},
                    {MAX, MAX, MIN},
                    {MAX, MINY, MIN}
            },
            { // SOUTH
                    {MAX, MINY, MAX},
                    {MAX, MAX, MAX},
                    {MIN, MAX, MAX},
                    {MIN, MINY, MAX}
            },
            { // WEST
                    {MIN, MINY, MAX},
                    {MIN, MAX, MAX},
                    {MIN, MAX, MIN},
                    {MIN, MINY, MIN}
            },
            { // EAST
                    {MAX, MINY, MIN},
                    {MAX, MAX, MIN},
                    {MAX, MAX, MAX},
                    {MAX, MINY, MAX}
            }
    };

    public static void renderFluidSides(PoseStack poseStack, MultiBufferSource buffer, float height, FluidVariantWrapper fluid, int brightness) {
        Triple<Float, Float, Float> colorParts = getFluidVertexBufferColor(fluid);
        float r = colorParts.getLeft();
        float g = colorParts.getMiddle();
        float b = colorParts.getRight();
        float a = 1.0F;
        Matrix4f matrix4f = poseStack.last().pose();

        TextureAtlasSprite icon = FluidVariantRendering.getSprite(fluid.fluidVariant());//getFluidIcon(fluid, direction);
        VertexConsumer renderer = buffer.getBuffer(RenderTypes.text(icon.atlasLocation()));

        for(Direction direction : Direction.values()) {
            float[][] c = coordinates[direction.ordinal()];
            float replacedMaxV = (direction == Direction.UP || direction == Direction.DOWN) ? icon.getV(4F / 16) : ((icon.getV1() - icon.getV0()) * height + icon.getV0());
            float replacedU1 = (direction == Direction.UP || direction == Direction.DOWN) ? icon.getU(4F / 16) : icon.getU(7F / 16);
            float replacedU2 = icon.getU(8F / 16);

            renderer.addVertex(matrix4f, c[0][0], getHeight(c[0][1], height), c[0][2]).setColor(r, g, b, a).setUv(replacedU1, replacedMaxV).setLight(brightness);
            renderer.addVertex(matrix4f, c[1][0], getHeight(c[1][1], height), c[1][2]).setColor(r, g, b, a).setUv(replacedU1, icon.getV0()).setLight(brightness);
            renderer.addVertex(matrix4f, c[2][0], getHeight(c[2][1], height), c[2][2]).setColor(r, g, b, a).setUv(replacedU2, icon.getV0()).setLight(brightness);
            renderer.addVertex(matrix4f, c[3][0], getHeight(c[3][1], height), c[3][2]).setColor(r, g, b, a).setUv(replacedU2, replacedMaxV).setLight(brightness);
        }
    }

    private static float getHeight(float height, float replaceHeight) {
        if(height == MAX) {
            return replaceHeight;
        }
        return height;
    }

    public static void renderFluidInTank(FluidTank tank, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, float x, float y, float z) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
        if(!tank.isEmpty() && !tank.getFluid().isEmpty()) {
            poseStack.translate(x, y, z);
            float height = getTankFillRatio(tank) * 0.99F;
            RenderHelper.renderFluidSides(poseStack, buffer, height, tank.getFluid(), combinedLightIn);
        }
        poseStack.popPose();
    }

 /*   public static TextureAtlasSprite getFluidIcon(FluidVariantWrapper fluidstack, Direction direction) {
        Block defaultBlock = Blocks.WATER;
        Block block = defaultBlock;
        block = fluidstack.getFluid().getFluidType().getBlockForFluidState(Minecraft.getInstance().level, BlockPos.ZERO, fluidstack.getFluid().defaultFluidState()).getBlock();

        if(direction == null) {
            direction = Direction.UP;
        }

        TextureAtlasSprite icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidstack.getFluid().getFluidType()).getFlowingTexture());

        if(icon == null || (direction == Direction.UP || direction == Direction.DOWN)) {
            //icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidstack.getFluid().getFluidType()).getStillTexture());
            icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(FluidVariantRendering.getSprite(fluidstack.fluidVariant().getFluidType()).getStillTexture());
        }
        if(icon == null) {
            icon = getBlockIcon(block);
            if(icon == null) {
                icon = getBlockIcon(defaultBlock);
            }
        }
        return icon;
    } */

    public static TextureAtlasSprite getBlockIcon(Block block) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(block.defaultBlockState());
    }

    public static float getTankFillRatio(FluidTank tank) {
        return Math.min(1.0F, ((float)tank.getFluidAmount()) / (float)tank.getCapacity()) * 0.5F;
    }

    public static Triple<Float, Float, Float> getFluidVertexBufferColor(FluidVariantWrapper fluidStack) {
        int color = FluidVariantRendering.getColor(fluidStack.fluidVariant());
        return intToRGB(color);
    }

    public static Triple<Float, Float, Float> intToRGB(int color) {
        float red, green, blue;
        red = (float)(color >> 16 & 255) / 255.0F;
        green = (float)(color >> 8 & 255) / 255.0F;
        blue = (float)(color & 255) / 255.0F;
        return Triple.of(red, green, blue);
    }

    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");

    public static void renderSlotHighlightBack(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, x - 4, y - 4, 24, 24);
    }

    public static void renderSlotHighlightFront(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, x - 4, y - 4, 24, 24);
    }
}