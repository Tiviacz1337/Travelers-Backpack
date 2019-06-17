package com.tiviacz.travellersbackpack.client.render;

import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.util.RenderUtils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class RendererFluid
{
    public void render(EntityPlayer player, float scale)
    {
    	GlStateManager.pushMatrix();
    	GlStateManager.enableRescaleNormal();
    	GlStateManager.scale(1D, 1.05D, 1D);

    	RenderUtils.renderFluidInTank(CapabilityUtils.getBackpackInv(player).getRightTank(), 0.24, -0.485, -0.235);
        RenderUtils.renderFluidInTank(CapabilityUtils.getBackpackInv(player).getLeftTank(), -0.66, -0.485, -0.235);
        
        GlStateManager.popMatrix();
    }
}