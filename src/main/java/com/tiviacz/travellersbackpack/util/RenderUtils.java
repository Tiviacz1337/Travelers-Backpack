package com.tiviacz.travellersbackpack.util;

import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import com.tiviacz.travellersbackpack.handlers.ConfigHandler;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class RenderUtils 
{
	//EnderCore part https://github.com/SleepyTrousers/EnderCore/blob/1.12/src/main/java/com/enderio/core/client/render/RenderUtil.java#L431
	
	public static void renderGuiTank(FluidTank tank, double x, double y, double height, double width)
	{
		renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, height, width);
	}
	
	public static void renderGuiTank(FluidStack fluid, int capacity, int amount, double x, double y, double height, double width) 
	{
		if(fluid == null || fluid.getFluid() == null || amount <= 0) 
	    {
	      return;
	    }

	    TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill().toString());
	    
	    if(icon == null)
	    {
	    	icon = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	    }

	    int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1);
	    int posY = (int) (y + height - renderAmount);

	    Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	    int color = fluid.getFluid().getColor(fluid);
	    GlStateManager.color((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f);

	    GlStateManager.disableBlend();
	    
	    for(int i = 0; i < width; i += 16) 
	    {
	    	for(int j = 0; j < renderAmount; j += 16) 
	    	{
	    		int drawWidth = (int) Math.min(width - i, 16);
	    		int drawHeight = Math.min(renderAmount - j, 16);

	    		int drawX = (int) (x + i);
	    		int drawY = posY + j;
	    		
	    		double minU;
	    		double minV;
	    		
	    		if(ConfigHandler.client.oldGuiTankRender)
	    		{
	    			minU = icon.getInterpolatedU(15D);
	    			minV = icon.getInterpolatedV(15D);
	    		}
	    		else
	    		{
	    			minU = icon.getMinU();
	    			minV = icon.getMinV();
	    		}

	    		double maxU = icon.getMaxU();
	    		double maxV = icon.getMaxV();

	    		Tessellator tessellator = Tessellator.getInstance();
	    		BufferBuilder tes = tessellator.getBuffer();
	    		tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	    		tes.pos(drawX, drawY + drawHeight, 0).tex(minU, minV + (maxV - minV) * drawHeight / 16F).endVertex();
	    		tes.pos(drawX + drawWidth, drawY + drawHeight, 0).tex(minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F).endVertex();
	    		tes.pos(drawX + drawWidth, drawY, 0).tex(minU + (maxU - minU) * drawWidth / 16F, minV).endVertex();
	    		tes.pos(drawX, drawY, 0).tex(minU, minV).endVertex();
	    		tessellator.draw();
	    	}
	    }
	 	GlStateManager.enableBlend();
	    GlStateManager.color(1, 1, 1);
	}
	
	//CyclopsCore part https://github.com/CyclopsMC/EvilCraft/blob/master-1.12/src/main/java/org/cyclops/evilcraft/client/render/tileentity/RenderTileEntityDarkTank.java#L87
	//https://minecraft.curseforge.com/projects/cyclops-core
	//https://github.com/CyclopsMC/CyclopsCore
	//Author: https://minecraft.curseforge.com/members/kroeser
	
	private static final double OFFSET = 0.01D;
	private static final double MINY = OFFSET;
	private static final double MIN = 0.125D + OFFSET;
	private static final double MAX = 0.3D - OFFSET;
	private static double[][][] coordinates = {
        { // DOWN
            {MIN, MINY, MIN},
            {MIN, MINY, MAX},
            {MAX, MINY, MAX},
            {MAX, MINY, MIN}
        },
        { // UP
            {MIN, MAX, MIN},
            {MIN, MAX, MAX},
            {MAX, MAX, MAX},
            {MAX, MAX, MIN}
        },
        { // NORTH
            {MIN, MINY, MIN},
            {MIN, MAX, MIN},
            {MAX, MAX, MIN},
            {MAX, MINY, MIN}
        },
        { // SOUTH
            {MIN, MINY, MAX},
            {MIN, MAX, MAX},
            {MAX, MAX, MAX},
            {MAX, MINY, MAX}
        },
        { // WEST
            {MIN, MINY, MIN},
            {MIN, MAX, MIN},
            {MIN, MAX, MAX},
            {MIN, MINY, MAX}
        },
        { // EAST
            {MAX, MINY, MIN},
            {MAX, MAX, MIN},
            {MAX, MAX, MAX},
            {MAX, MINY, MAX}
        }
    };
	
	public static void renderFluidSides(double height, FluidStack fluid, int brightness) 
	{
        Triple<Float, Float, Float> colorParts = getFluidVertexBufferColor(fluid);
        float r = colorParts.getLeft();
        float g = colorParts.getMiddle();
        float b = colorParts.getRight();
        float a = 1.0F;
        
		for(EnumFacing side : EnumFacing.VALUES) 
		{
			TextureAtlasSprite icon = getFluidIcon(fluid, side);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldRenderer = tessellator.getBuffer();
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			
			double[][] c = coordinates[side.ordinal()];
			double replacedMaxV = (side == EnumFacing.UP || side == EnumFacing.DOWN) ? icon.getInterpolatedV(4D) : ((icon.getMaxV() - icon.getMinV()) * height + icon.getMinV());
			double replacedU1 = (side == EnumFacing.UP || side == EnumFacing.DOWN) ? icon.getInterpolatedU(4D) : icon.getInterpolatedU(7D);
			double replacedU2 = (side == EnumFacing.UP || side == EnumFacing.DOWN) ? icon.getInterpolatedU(8D) : icon.getInterpolatedU(8D);
			
			worldRenderer.pos(c[0][0], getHeight(side, c[0][1], height), c[0][2]).tex(replacedU1, replacedMaxV).color(r, g, b, a).endVertex();
		    worldRenderer.pos(c[1][0], getHeight(side, c[1][1], height), c[1][2]).tex(replacedU1, icon.getMinV()).color(r, g, b, a).endVertex();
		    worldRenderer.pos(c[2][0], getHeight(side, c[2][1], height), c[2][2]).tex(replacedU2, icon.getMinV()).color(r, g, b, a).endVertex();
		    worldRenderer.pos(c[3][0], getHeight(side, c[3][1], height), c[3][2]).tex(replacedU2, replacedMaxV).color(r, g, b, a).endVertex();
			
			tessellator.draw();
		}
	}
	
	private static double getHeight(EnumFacing side, double height, double replaceHeight) 
	{
		if(height == MAX) 
		{
			return replaceHeight;
		}
		return height;
	}
	
	public static void renderFluidInTank(FluidTank tank, double x, double y, double z)
	{
		GlStateManager.pushMatrix();
		GlStateManager.rotate(180F , 0, 0, 1);
		
		renderFluidContext(tank.getFluid(), x, y, z, new IFluidContextRender() 
        {
            @Override
            public void renderFluid(FluidStack fluid) 
            {
                double height = getTankFillRatio(tank) * 0.99D;
                RenderUtils.renderFluidSides(height, fluid, 100);
            }
        });
		
		GlStateManager.popMatrix();
	}
	
	public static TextureAtlasSprite getFluidIcon(FluidStack fluid, EnumFacing side) 
	{
        Block defaultBlock = Blocks.WATER;
        Block block = defaultBlock;
        
        if(fluid.getFluid().getBlock() != null)
        {
            block = fluid.getFluid().getBlock();
        }

        if(side == null) 
        {
        	side = EnumFacing.UP;
        }

        TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getFlowing(fluid).toString());
        
        if(icon == null || (side == EnumFacing.UP || side == EnumFacing.DOWN)) 
        {
            icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill(fluid).toString());
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
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(block.getDefaultState());
    }
	
	public static double getTankFillRatio(FluidTank tank)
    {
    	return Math.min(1.0D, ((double) tank.getFluidAmount()) / (double) tank.getCapacity()) * 0.5D;
    }
	
	public static void renderFluidContext(FluidStack fluid, double x, double y, double z, IFluidContextRender render) 
	{
        if(fluid != null && fluid.amount > 0) 
        {
            GlStateManager.pushMatrix();

            GlStateManager.disableBlend();
            GlStateManager.disableCull();

            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GlStateManager.translate(x, y, z);

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            render.renderFluid(fluid);

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
	
	public static Triple<Float, Float, Float> getFluidVertexBufferColor(FluidStack fluidStack) 
	{
        int color = fluidStack.getFluid().getColor(fluidStack);
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
	 
	public static interface IFluidContextRender 
	{
        public void renderFluid(FluidStack fluid);
    }
}