package com.tiviacz.travellersbackpack.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.client.model.ModelTravellersBackpackBlock;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class RendererTileTravellersBackpack extends TileEntitySpecialRenderer<TileEntityTravellersBackpack>
{
	private ModelTravellersBackpackBlock model;

	public RendererTileTravellersBackpack()
	{
		this.model = new ModelTravellersBackpackBlock();
	}

	@Override
	public void render(TileEntityTravellersBackpack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5f, (float) z + 0.5F);

		GL11.glPushMatrix();
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

		GL11.glPushMatrix();
		
		if(te != null)
		{
			TileEntity tile = getWorld().getTileEntity(te.getPos());
			
			if(tile instanceof TileEntityTravellersBackpack)
			{
				EnumFacing facing = te.getBlockFacing(getWorld().getTileEntity(te.getPos()));
				
				if(facing == EnumFacing.NORTH)
				{
					GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
				}
				if(facing == EnumFacing.SOUTH)
				{
					GL11.glRotatef(0F, 0.0F, 1.0F, 0.0F);
				}
				if(facing == EnumFacing.EAST)
				{
					GL11.glRotatef(270F, 0.0F, 1.0F, 0.0F);
				}
				if(facing == EnumFacing.WEST)
				{
					GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
				} 
			}
		}
	        
		ResourceLocation modelTexture = new ResourceLocation(TravellersBackpack.MODID + ":textures/blocks/travellers_backpack_" + te.getColor().toLowerCase() + ".png");
		bindTexture(modelTexture);
		model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1 / 20F, te);

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}
	    
/*	@Override
    public void render(TileEntityTravellersBackpack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
		GlStateManager.pushMatrix();
        int capacityRight = te.getRightTank().getCapacity();
        FluidStack fluidRight = te.getRightTank().getFluid();
        int capacityLeft = te.getLeftTank().getCapacity();
        FluidStack fluidLeft = te.getLeftTank().getFluid();
        
        if(fluidRight != null && te.getBlockFacing() == EnumFacing.NORTH) 
        {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            
            TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidRight.getFluid().getStill().toString());
            TextureAtlasSprite flow = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidRight.getFluid().getFlowing().toString());
            
            double posY = 0.01 + ((0.985 * ((float)fluidRight.amount / (float)capacityRight) * 8.95F/16F));
            int icolor = fluidRight.getFluid().getColor(fluidRight);

            float red = (icolor >> 16 & 0xFF) / 255.0F;
            float green = (icolor >> 8 & 0xFF) / 255.0F;
            float blue = (icolor & 0xFF) / 255.0F;
            float alph = 1.0F;
           
            float f1 = 1.5F/16F, f2 = 3.5F/16F, f3 = 8F/16F, f4 = 10F/16F, f5 = 19.3F/16F, f6 = 17.3F/16F, f7 = 3.3F/16F;
            float B = 0.1F / 16F;
            int S = 6, E = 8;
            
            //TOP SIDE
            buffer.setTranslation(x, y, z);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(f1, posY, f3).tex(still.getInterpolatedU(S), still.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f1, posY, f4).tex(still.getInterpolatedU(E), still.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f2, posY, f4).tex(still.getInterpolatedU(E), still.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f2, posY, f3).tex(still.getInterpolatedU(S), still.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            tess.draw();
            
            //South
            buffer.setTranslation(x, y, z);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(f7, B, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f7, posY, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f1, posY, f4).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f1, B, f4).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            tess.draw();
            
            //North 
            buffer.setTranslation(x, y, z + 1);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(f7, posY, -1 * f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f7, B, -1 * f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f1, B, -1 * f3).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f1, posY, -1 * f3).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            tess.draw();  

            //West
            buffer.setTranslation(x - 1 + 2 * B, y, z);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(f5, posY, f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f5, B, f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f5, B, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f5, posY, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            tess.draw(); 
            buffer.setTranslation(0, 0, 0); 
            
            //East
            buffer.setTranslation(x - 1 + 2 * B, y, z);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(f6, posY, f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f6, B, f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f6, B, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f6, posY, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            tess.draw(); 
            
            buffer.setTranslation(0, 0, 0);
        }
        
        if(fluidLeft != null && te.getBlockFacing() == EnumFacing.NORTH)
        {
        	Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            
            TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidLeft.getFluid().getStill().toString());
            TextureAtlasSprite flow = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidLeft.getFluid().getFlowing().toString());
            
            double posY = 0.01 + ((0.985 * ((float)fluidLeft.amount / (float)capacityLeft) * 8.95F/16F));
            int icolor = fluidLeft.getFluid().getColor(fluidLeft);

            float red = (icolor >> 16 & 0xFF) / 255.0F;
            float green = (icolor >> 8 & 0xFF) / 255.0F;
            float blue = (icolor & 0xFF) / 255.0F;
            float alph = 1.0F;
           
            float f1 = 1.5F/16F, f2 = 3.5F/16F, f3 = 8F/16F, f4 = 10F/16F, f5 = 19.3F/16F, f6 = 17.3F/16F, f7 = 3.3F/16F;
            float B = 0.1F / 16F;
            int S = 6, E = 8;
            
            //TOP SIDE
            buffer.setTranslation(x, y, z);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(f1, posY, f3).tex(still.getInterpolatedU(S), still.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f1, posY, f4).tex(still.getInterpolatedU(E), still.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f2, posY, f4).tex(still.getInterpolatedU(E), still.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f2, posY, f3).tex(still.getInterpolatedU(S), still.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            tess.draw();
            
            //South
            buffer.setTranslation(x, y, z);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(f7, B, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f7, posY, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f1, posY, f4).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f1, B, f4).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            tess.draw();
            
            //North 
            buffer.setTranslation(x, y, z + 1);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(f7, posY, -1 * f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f7, B, -1 * f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f1, B, -1 * f3).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f1, posY, -1 * f3).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            tess.draw();  

            //West
            buffer.setTranslation(x - 1 + 2 * B, y, z);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(f5, posY, f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f5, B, f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f5, B, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f5, posY, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            tess.draw(); 
            buffer.setTranslation(0, 0, 0); 
            
            //East
            buffer.setTranslation(x - 1 + 2 * B, y, z);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(f6, posY, f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            buffer.pos(f6, B, f3).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f6, B, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
            buffer.pos(f6, posY, f4).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
            tess.draw(); 
            
            buffer.setTranslation(-50, -50, -50);
        }
        GlStateManager.popMatrix(); 
    } */