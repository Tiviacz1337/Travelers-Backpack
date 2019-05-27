package com.tiviacz.travellersbackpack.client.render;

import com.tiviacz.travellersbackpack.util.RenderUtils;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RendererFluid extends ModelRenderer
{
	public RendererFluid(ModelBase modelBase, EntityPlayer player)
    {
        super(modelBase);
        addChild(new ModelStack(modelBase, player));
    }

    private class ModelStack extends ModelRenderer
    {
    	private EntityPlayer player;
    	
        public ModelStack(ModelBase modelBase, EntityPlayer player)
        {
            super(modelBase);
            this.player = player;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(float scale)
        {
        	GlStateManager.pushMatrix();
        	GlStateManager.enableRescaleNormal();
        	GlStateManager.scale(1D, 1.05D, 1D);

        	RenderUtils.renderFluidInTank(WearableUtils.getBackpackInv(player).getRightTank(), 0.24, -0.485, -0.235);
            RenderUtils.renderFluidInTank(WearableUtils.getBackpackInv(player).getLeftTank(), -0.66, -0.485, -0.235);
            
            GlStateManager.popMatrix();
        }
    }
}