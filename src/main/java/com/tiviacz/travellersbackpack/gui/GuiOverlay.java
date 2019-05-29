package com.tiviacz.travellersbackpack.gui;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.gui.inventory.IInventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.items.ItemHose;
import com.tiviacz.travellersbackpack.util.Reference;
import com.tiviacz.travellersbackpack.util.RenderUtils;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;

public class GuiOverlay extends Gui
{
    public Minecraft mc;
    public RenderItem itemRender;
    public ScaledResolution resolution;
    
    public GuiOverlay()
    {
        super();
        
        this.mc = Minecraft.getMinecraft();
        this.itemRender = Minecraft.getMinecraft().getRenderItem();
        this.resolution = new ScaledResolution(this.mc);
    }
    
    public void renderOverlay()
    {
    	EntityPlayer player = mc.player;
    	
    	GlStateManager.pushAttrib();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		
		int scaledHeight = resolution.getScaledHeight() - 30;
    	int scaledWidth = resolution.getScaledWidth() - 20;
    	
		int textureX = 10;
		int textureY = 0;
    	
    	if(WearableUtils.isWearingBackpack(player))
    	{
    		IInventoryTravellersBackpack inv = WearableUtils.getBackpackInv(player);
    		FluidTank rightTank = inv.getRightTank();
    		FluidTank leftTank = inv.getLeftTank();

    		if(rightTank.getFluid() != null)
    		{
    			this.drawRightTank(rightTank, scaledWidth + 1, scaledHeight, 21, 8);
    		}
    		
    		if(leftTank.getFluid() != null)
    		{
    			this.drawLeftTank(leftTank, scaledWidth - 11, scaledHeight, 21, 8);
    		}
    		
    		if(!inv.getStackInSlot(Reference.TOOL_UPPER).isEmpty())
    		{
    			this.drawItemStack(inv.getStackInSlot(Reference.TOOL_UPPER), scaledWidth - 30, scaledHeight - 4);
    		}
    		
    		if(!inv.getStackInSlot(Reference.TOOL_LOWER).isEmpty())
    		{
    			this.drawItemStack(inv.getStackInSlot(Reference.TOOL_LOWER), scaledWidth - 30, scaledHeight + 9);
    		}
    		
    		ResourceLocation texture = new ResourceLocation(TravellersBackpack.MODID, "textures/gui/overlay.png");
        	mc.getTextureManager().bindTexture(texture);
        	
        	if(player.getHeldItemMainhand().getItem() instanceof ItemHose)
        	{
        		int tank = ItemHose.getHoseTank(player.getHeldItemMainhand());
        		
        		int selectedTextureX = 0;
        		int selectedTextureY = 0;		
        		
        		if(tank == 1)
        		{
        			drawTexturedModalRect(scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                	drawTexturedModalRect(scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
        		}
        		
        		if(tank == 2)
        		{
        			drawTexturedModalRect(scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                	drawTexturedModalRect(scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        		}
        		
        		if(tank == 0)
        		{
        			drawTexturedModalRect(scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                	drawTexturedModalRect(scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        		}
        	}
        	else
        	{
        		drawTexturedModalRect(scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            	drawTexturedModalRect(scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        	}
    	}
    	
    	GlStateManager.popAttrib();
    }

    public void drawRightTank(FluidTank tank, int startX, int startY, int height, int width)
    {
    	RenderUtils.renderGuiTank(tank, startX, startY, height, width);
    }
    
    public void drawLeftTank(FluidTank tank, int startX, int startY, int height, int width)
    {
    	RenderUtils.renderGuiTank(tank, startX, startY, height, width);
    }
    
    private void drawItemStack(ItemStack stack, int x, int y)
    {
    	RenderHelper.enableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableLighting();
    	GlStateManager.pushMatrix();
        this.itemRender.renderItemIntoGUI(stack, x, y);
        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}