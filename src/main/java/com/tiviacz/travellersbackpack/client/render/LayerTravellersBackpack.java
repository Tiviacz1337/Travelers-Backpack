package com.tiviacz.travellersbackpack.client.render;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.client.model.ModelTravellersBackpackWearable;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerTravellersBackpack implements LayerRenderer<EntityLivingBase>
{
	private final RenderPlayer renderer;
	private final ModelTravellersBackpackWearable model;
	
	public LayerTravellersBackpack(RenderPlayer renderer) 
	{
		this.renderer = renderer;
		this.model = new ModelTravellersBackpackWearable();
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) 
	{
		if(entitylivingbaseIn instanceof EntityPlayer)
		{
			renderLayer((EntityPlayer)entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		}
	}
	
	private void renderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) 
	{
		if(CapabilityUtils.isWearingBackpack(player))
		{
			GlStateManager.pushMatrix();
			
			if (player.isSneaking())
	    	{
	    		GlStateManager.translate(0, 0.22F, 0.0F);
	    	}
			
			this.renderer.getMainModel().bipedBody.postRender(scale);
	        GlStateManager.translate(0, 0.2F, 0.4F);
	        
			ItemStack stack = CapabilityUtils.getWearingBackpack(player);
			this.model.setModelAttributes(this.renderer.getMainModel());
			this.renderer.bindTexture(new ResourceLocation(TravellersBackpack.MODID, "textures/backpacks/wearable/" + Reference.BACKPACK_NAMES[stack.getMetadata()].toLowerCase() + "_wearable.png"));
			this.model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() 
	{
		return false;
	}
}