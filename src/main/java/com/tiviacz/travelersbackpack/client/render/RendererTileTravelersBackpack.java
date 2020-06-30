package com.tiviacz.travelersbackpack.client.render;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.ModelTravelersBackpackBlock;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RendererTileTravelersBackpack extends TileEntitySpecialRenderer<TileEntityTravelersBackpack>
{
	private ModelTravelersBackpackBlock model;

	public RendererTileTravelersBackpack()
	{
		this.model = new ModelTravelersBackpackBlock();
	}

	@Override
	public void render(TileEntityTravelersBackpack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate((float) x + 0.5F, (float) y + 0.5f, (float) z + 0.5F);
		GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		
		if(te != null)
		{
			TileEntity tile = getWorld().getTileEntity(te.getPos());
			
			if(tile instanceof TileEntityTravelersBackpack)
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
	        
		ResourceLocation modelTexture = new ResourceLocation(TravelersBackpack.MODID, "textures/backpacks/" + te.getColor().toLowerCase() + ".png");
		bindTexture(modelTexture);
		model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1 / 20F, te);

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}
}