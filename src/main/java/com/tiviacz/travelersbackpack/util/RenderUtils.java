package com.tiviacz.travelersbackpack.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

public class RenderUtils
{
    public static void renderScreenTank(GuiGraphics guiGraphics, FluidTank tank, double x, double y, double z, double height, double width)
    {
        renderScreenTank(guiGraphics, tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, z, height, width);
    }

    public static void renderScreenTank(GuiGraphics guiGraphics, FluidStack fluid, int capacity, int amount, double x, double y, double z, double height, double width)
    {
        if(fluid == null || fluid.getFluid() == null || amount <= 0)
        {
            return;
        }

        TextureAtlasSprite icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType()).getStillTexture());

        if(icon == null)
        {
            icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MissingTextureAtlasSprite.getLocation());
        }

        int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1);
        int posY = (int) (y + height - renderAmount);

        int color = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType()).getTintColor(fluid);

        guiGraphics.pose().pushPose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, 1);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.disableBlend();

        for(int i = 0; i < width; i += 16)
        {
            for(int j = 0; j < renderAmount; j += 16)
            {
                int drawWidth = (int) Math.min(width - i, 16);
                int drawHeight = Math.min(renderAmount - j, 16);

                int drawX = (int) (x + i);
                int drawY = posY + j;

                float minU;
                float minV;

                minU = icon.getU0();
                minV = icon.getV0();

                float maxU = icon.getU1();
                float maxV = icon.getV1();

                Matrix4f matrix4f = guiGraphics.pose().last().pose();
                BufferBuilder builder = Tesselator.getInstance().getBuilder();
                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                builder.vertex(matrix4f, drawX, drawY + drawHeight, (float)z).uv(minU, minV + (maxV - minV) * (float)drawHeight / 16F).endVertex();
                builder.vertex(matrix4f, drawX + drawWidth, drawY + drawHeight, (float)z).uv(minU + (maxU - minU) * (float)drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F).endVertex();
                builder.vertex(matrix4f, drawX + drawWidth, drawY, (float)z).uv(minU + (maxU - minU) * drawWidth / 16F, minV).endVertex();
                builder.vertex(matrix4f, drawX, drawY, (float)z).uv(minU, minV).endVertex();
                BufferUploader.drawWithShader(builder.end());
            }
        }
        //RenderSystem.enableBlend();
        //RenderSystem.clearColor(1, 1, 1, 1);
        guiGraphics.pose().popPose();
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

    public static void renderFluidSides(@Nullable ITravelersBackpackContainer inv, PoseStack poseStack, MultiBufferSource buffer, float height, FluidStack fluid, int brightness)
    {
        Triple<Float, Float, Float> colorParts = getFluidVertexBufferColor(fluid);
        float r = colorParts.getLeft();
        float g = colorParts.getMiddle();
        float b = colorParts.getRight();
        float a = 1.0F;

        Matrix4f matrix4f = poseStack.last().pose();

        for(Direction direction : Direction.values())
        {
            TextureAtlasSprite icon = getFluidIcon(inv, fluid, direction);

            VertexConsumer renderer = buffer.getBuffer(RenderType.text(icon.atlasLocation()));

            float[][] c = coordinates[direction.ordinal()];
            float replacedMaxV = (direction == Direction.UP || direction == Direction.DOWN) ? icon.getV(4F / 16) : ((icon.getV1() - icon.getV0()) * height + icon.getV0());
            float replacedU1 = (direction == Direction.UP || direction == Direction.DOWN) ? icon.getU(4F/ 16) : icon.getU(7F/ 16);
            float replacedU2 = (direction == Direction.UP || direction == Direction.DOWN) ? icon.getU(8F/ 16) : icon.getU(8F/ 16);

            renderer.vertex(matrix4f, c[0][0], getHeight(c[0][1], height), c[0][2]).color(r, g, b, a).uv(replacedU1, replacedMaxV).uv2(brightness).endVertex();
            renderer.vertex(matrix4f, c[1][0], getHeight(c[1][1], height), c[1][2]).color(r, g, b, a).uv(replacedU1, icon.getV0()).uv2(brightness).endVertex();
            renderer.vertex(matrix4f, c[2][0], getHeight(c[2][1], height), c[2][2]).color(r, g, b, a).uv(replacedU2, icon.getV0()).uv2(brightness).endVertex();
            renderer.vertex(matrix4f, c[3][0], getHeight(c[3][1], height), c[3][2]).color(r, g, b, a).uv(replacedU2, replacedMaxV).uv2(brightness).endVertex();
        }
    }

    private static float getHeight(float height, float replaceHeight)
    {
        if(height == MAX)
        {
            return replaceHeight;
        }
        return height;
    }

    public static void renderFluidInTank(@Nullable ITravelersBackpackContainer inv, FluidTank tank, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, float x, float y, float z)
    {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180F));

        if(!tank.isEmpty() && !tank.getFluid().isEmpty())
        {
            poseStack.translate(x,y,z);
            float height = getTankFillRatio(tank) * 0.99F;
            RenderUtils.renderFluidSides(inv, poseStack, buffer, height, tank.getFluid(), combinedLightIn);
        }

        poseStack.popPose();
    }

    public static TextureAtlasSprite getFluidIcon(@Nullable ITravelersBackpackContainer inv, FluidStack fluidstack, Direction direction)
    {
        Block defaultBlock = Blocks.WATER;
        Block block = defaultBlock;

        if(fluidstack.getFluid().getFluidType().getBlockForFluidState(Minecraft.getInstance().level, inv == null ? BlockPos.ZERO : inv.getPosition(), fluidstack.getFluid().defaultFluidState()) != null)
        {
            block = fluidstack.getFluid().getFluidType().getBlockForFluidState(Minecraft.getInstance().level, inv == null ? BlockPos.ZERO : inv.getPosition(), fluidstack.getFluid().defaultFluidState()).getBlock();
        }

        if(direction == null)
        {
            direction = Direction.UP;
        }

        TextureAtlasSprite icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidstack.getFluid().getFluidType()).getFlowingTexture());

        if(icon == null || (direction == Direction.UP || direction == Direction.DOWN))
        {
            icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidstack.getFluid().getFluidType()).getStillTexture());
        }

        if(icon == null)
        {
            icon = getBlockIcon(block);

            if(icon == null)
            {
                icon = getBlockIcon(defaultBlock);
            }
        }

        return icon;
    }

    public static TextureAtlasSprite getBlockIcon(Block block)
    {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(block.defaultBlockState());
    }

    public static float getTankFillRatio(FluidTank tank)
    {
        return Math.min(1.0F, ((float)tank.getFluidAmount()) / (float)tank.getCapacity()) * 0.5F;
    }

    public static Triple<Float, Float, Float> getFluidVertexBufferColor(FluidStack fluidStack)
    {
        int color = IClientFluidTypeExtensions.of(fluidStack.getFluid().getFluidType()).getTintColor(fluidStack);
        return intToRGB(color);
    }

    public static Triple<Float, Float, Float> intToRGB(int color)
    {
        float red, green, blue;
        red = (float)(color >> 16 & 255) / 255.0F;
        green = (float)(color >> 8 & 255) / 255.0F;
        blue = (float)(color & 255) / 255.0F;
        return Triple.of(red, green, blue);
    }
}