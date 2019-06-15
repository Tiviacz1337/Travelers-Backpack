package com.tiviacz.travellersbackpack.client.render;

import org.lwjgl.opengl.GL11;

import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.gui.inventory.IInventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.handlers.ConfigHandler;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RendererStack extends ModelRenderer
{
	public RendererStack(ModelBase modelBase, EntityPlayer player)
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
        	if(ConfigHandler.renderTools)
        	{
        		renderTools(player);
        	}
        }
        
        public void renderTools(EntityPlayer player)
        {
    		IInventoryTravellersBackpack inv = CapabilityUtils.getBackpackInv(player);
    		ItemStack toolUpper = inv.getStackInSlot(Reference.TOOL_UPPER);
    		ItemStack toolLower = inv.getStackInSlot(Reference.TOOL_LOWER);
    		
    		if(!toolUpper.isEmpty())
    		{
    			IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(toolUpper, player.world, player);
				model = ForgeHooksClient.handleCameraTransforms(model, TransformType.NONE, false);

				GlStateManager.enableRescaleNormal();
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.05, 0.075, 0.26);
				GlStateManager.rotate(45F, 0, 0, 1);
				GlStateManager.rotate(180F, 1, 0, 0);
				GlStateManager.scale(0.65F, 0.65F, 0.65F);
			
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				Minecraft.getMinecraft().getRenderItem().renderItem(toolUpper, model);
			
				GlStateManager.popMatrix();
				GlStateManager.disableRescaleNormal();
				GlStateManager.disableBlend();
    		}
    		
    		if(!toolLower.isEmpty())
    		{
    			IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(toolLower, player.world, player);
				model = ForgeHooksClient.handleCameraTransforms(model, TransformType.NONE, false);

				GlStateManager.enableRescaleNormal();
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.35, 0.925, 0);
				GlStateManager.rotate(90F, 0, 1, 0);
				GlStateManager.rotate(45F, 0, 0, 1);
				GlStateManager.scale(0.65F, 0.65F, 0.65F);
			
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				Minecraft.getMinecraft().getRenderItem().renderItem(toolLower, model);
			
				GlStateManager.popMatrix();
				GlStateManager.disableRescaleNormal();
				GlStateManager.disableBlend();
    		}
        } 
    }
}