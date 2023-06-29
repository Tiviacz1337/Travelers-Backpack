package com.tiviacz.travelersbackpack.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;

public class RenderUtils
{
    public static void renderScreenTank(PoseStack matrixStackIn, FluidTank tank, double x, double y, double height, double width)
    {
        renderScreenTank(matrixStackIn, tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, height, width);
    }

    public static void renderScreenTank(PoseStack matrixStackIn, FluidStack fluid, int capacity, int amount, double x, double y, double height, double width)
    {
        if(fluid == null || fluid.getFluid() == null || amount <= 0)
        {
            return;
        }

        TextureAtlasSprite icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluid.getFluid().getAttributes().getStillTexture());

        if(icon == null)
        {
            icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MissingTextureAtlasSprite.getLocation());
        }

        int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1);
        int posY = (int) (y + height - renderAmount);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        int color = fluid.getFluid().getAttributes().getColor(fluid);

        matrixStackIn.pushPose();

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

                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder builder = tessellator.getBuilder();
                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                builder.vertex(drawX, drawY + drawHeight, 0).uv(minU, minV + (maxV - minV) * (float)drawHeight / 16F).endVertex();
                builder.vertex(drawX + drawWidth, drawY + drawHeight, 0).uv(minU + (maxU - minU) * (float)drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F).endVertex();
                builder.vertex(drawX + drawWidth, drawY, 0).uv(minU + (maxU - minU) * drawWidth / 16F, minV).endVertex();
                builder.vertex(drawX, drawY, 0).uv(minU, minV).endVertex();
                tessellator.end();
            }
        }
        RenderSystem.enableBlend();
        RenderSystem.clearColor(1, 1, 1, 1);
        matrixStackIn.popPose();
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

    public static void renderFluidSides(@Nullable ITravelersBackpackContainer inv, PoseStack matrixStackIn, MultiBufferSource buffer, float height, FluidStack fluid, int brightness)
    {
        Triple<Float, Float, Float> colorParts = getFluidVertexBufferColor(fluid);
        float r = colorParts.getLeft();
        float g = colorParts.getMiddle();
        float b = colorParts.getRight();
        float a = 1.0F;

        Matrix4f matrix4f = matrixStackIn.last().pose();

        for(Direction direction : Direction.values())
        {
            TextureAtlasSprite icon = getFluidIcon(inv, fluid, direction);

            VertexConsumer renderer = buffer.getBuffer(RenderType.text(icon.atlas().location()));

            float[][] c = coordinates[direction.ordinal()];
            float replacedMaxV = (direction == Direction.UP || direction == Direction.DOWN) ? icon.getV(4D) : ((icon.getV1() - icon.getV0()) * height + icon.getV0());
            float replacedU1 = (direction == Direction.UP || direction == Direction.DOWN) ? icon.getU(4D) : icon.getU(7D);
            float replacedU2 = (direction == Direction.UP || direction == Direction.DOWN) ? icon.getU(8D) : icon.getU(8D);

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

    public static void renderFluidInTank(@Nullable ITravelersBackpackContainer inv, FluidTank tank, PoseStack matrixStackIn, MultiBufferSource buffer, int combinedLightIn, float x, float y, float z)
    {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180F));

        if(!tank.isEmpty() && !tank.getFluid().isEmpty())
        {
            matrixStackIn.translate(x,y,z);
            float height = getTankFillRatio(tank) * 0.99F;
            RenderUtils.renderFluidSides(inv, matrixStackIn, buffer, height, tank.getFluid(), combinedLightIn);
        }

        matrixStackIn.popPose();
    }

    public static TextureAtlasSprite getFluidIcon(@Nullable ITravelersBackpackContainer inv, FluidStack fluidstack, Direction direction)
    {
        Block defaultBlock = Blocks.WATER;
        Block block = defaultBlock;

        if(fluidstack.getFluid().getAttributes().getBlock(Minecraft.getInstance().level, inv == null ? BlockPos.ZERO : inv.getPosition(), fluidstack.getFluid().defaultFluidState()) != null)
        {
            block = fluidstack.getFluid().getAttributes().getBlock(Minecraft.getInstance().level, inv == null ? BlockPos.ZERO : inv.getPosition(), fluidstack.getFluid().defaultFluidState()).getBlock();
        }

        if(direction == null)
        {
            direction = Direction.UP;
        }

        TextureAtlasSprite icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidstack.getFluid().getAttributes().getFlowingTexture());

        if(icon == null || (direction == Direction.UP || direction == Direction.DOWN))
        {
            icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidstack.getFluid().getAttributes().getStillTexture());
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
        int color = fluidStack.getFluid().getAttributes().getColor(fluidStack);
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