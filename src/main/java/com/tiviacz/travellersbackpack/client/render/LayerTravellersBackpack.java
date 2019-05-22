package com.tiviacz.travellersbackpack.client.render;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.client.model.ModelTravellersBackpackWearable;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class LayerTravellersBackpack implements LayerRenderer<EntityLivingBase>
{
	private final RenderLivingBase<?> renderer;
	
	public LayerTravellersBackpack(RenderLivingBase<?> renderer) 
	{
		this.renderer = renderer;
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) 
	{
		renderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
	}
	
	private void renderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) 
	{
		if(entitylivingbaseIn instanceof EntityPlayer)
		{
			if(WearableUtils.isWearingBackpack((EntityPlayer)entitylivingbaseIn))
			{
				ModelTravellersBackpackWearable model = new ModelTravellersBackpackWearable((EntityPlayer)entitylivingbaseIn);
				model.setModelAttributes(this.renderer.getMainModel());
				this.renderer.bindTexture(new ResourceLocation(TravellersBackpack.MODID + ":textures/blocks/travellers_backpack_wearable.png"));
				model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			}
		}
	}

	@Override
	public boolean shouldCombineTextures() 
	{
		return false;
	}
}