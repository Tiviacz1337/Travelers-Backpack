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
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		
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
	}
}