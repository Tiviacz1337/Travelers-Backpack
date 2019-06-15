package com.tiviacz.travellersbackpack.client.render;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.client.model.ModelTravellersBackpackWearable;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
		if(entitylivingbaseIn instanceof EntityPlayer)
		{
			renderLayer((EntityPlayer)entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		}
	}
	
	private void renderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) 
	{
		if(CapabilityUtils.isWearingBackpack(player))
		{
			ModelTravellersBackpackWearable model = new ModelTravellersBackpackWearable(player);
			ItemStack stack = CapabilityUtils.getWearingBackpack(player);
			model.setModelAttributes(this.renderer.getMainModel());
			this.renderer.bindTexture(new ResourceLocation(TravellersBackpack.MODID, "textures/backpacks/wearable/" + Reference.BACKPACK_NAMES[stack.getMetadata()].toLowerCase() + "_wearable.png"));
			model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		}  
	}

	@Override
	public boolean shouldCombineTextures() 
	{
		return false;
	}
}