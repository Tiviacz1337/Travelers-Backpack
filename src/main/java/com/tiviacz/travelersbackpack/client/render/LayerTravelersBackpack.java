package com.tiviacz.travelersbackpack.client.render;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.model.ModelTravelersBackpackWearable;
import com.tiviacz.travelersbackpack.handlers.ConfigHandler;
import com.tiviacz.travelersbackpack.util.Reference;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerTravelersBackpack implements LayerRenderer<EntityLivingBase>
{
	private final RenderPlayer renderer;
	private final ModelTravelersBackpackWearable model;
	
	public LayerTravelersBackpack(RenderPlayer renderer)
	{
		this.renderer = renderer;
		this.model = new ModelTravelersBackpackWearable();
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) 
	{
		if(entitylivingbaseIn instanceof EntityPlayer)
		{
			ItemStack stack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			
			if(!ConfigHandler.client.renderBackpackWithElytra)
			{
				if(isColytraPresent(stack) || stack.getItem() instanceof ItemElytra)
				{
					return;
				}
				else
				{
					renderLayer((EntityPlayer)entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
				}
			}
			else
			{
				renderLayer((EntityPlayer)entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
			}
		}
	}
	
	private void renderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) 
	{
		if(CapabilityUtils.isWearingBackpack(player))
		{
			GlStateManager.pushMatrix();
			
			if(player.isSneaking())
	    	{
	    		GlStateManager.translate(0, 0.22F, 0.0F);
	    	}
			
			this.renderer.getMainModel().bipedBody.postRender(scale);
	        GlStateManager.translate(0, 0.175F, 0.325F);
	        GlStateManager.scale(0.9F, 0.9F, 0.9F);
			ItemStack stack = CapabilityUtils.getWearingBackpack(player);
			this.model.setModelAttributes(this.renderer.getMainModel());
			this.renderer.bindTexture(new ResourceLocation(TravelersBackpack.MODID, "textures/backpacks/" + Reference.BACKPACK_NAMES[stack.getMetadata()].toLowerCase() + ".png"));
			this.model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() 
	{
		return false;
	}

	private boolean isColytraPresent(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			if(stack.getTagCompound().hasKey("Elytra Upgrade"))
			{
				return true;
			}
		}
		return false;
	}
}