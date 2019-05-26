package com.tiviacz.travellersbackpack.gui;

import java.util.ArrayList;
import java.util.List;

import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class GuiTank 
{
	private int height;
	private int width;
	private int startX;
	private int startY;
	private FluidTank tank;
	
	public GuiTank(FluidTank tank, int x, int y, int height, int width)
	{
		this.startX = x;
		this.startY = y;
		this.height = height;
		this.width = width;
		this.tank = tank;
	}
	
	public List<String> getTankTooltip()
	{
		FluidStack fluid = tank.getFluid();
		String fluidName = (fluid != null) ? fluid.getLocalizedName() : I18n.format("gui.none.name");
		String fluidAmount = (fluid != null) ? fluid.amount + "/" + Reference.BASIC_TANK_CAPACITY : I18n.format("gui.empty.name");
		ArrayList<String> tankTips = new ArrayList<String>();
		tankTips.add(fluidName);
		tankTips.add(fluidAmount);
		
		return tankTips;
	}
	
	public void drawGuiFluidBar(GuiTravellersBackpack gui, int fluidHeight)
    {
        Fluid fluid = tank.getFluid().getFluid();
        TextureAtlasSprite fluidTexture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        
        GlStateManager.disableBlend();
        gui.drawTexturedModalRect(this.startX, this.startY + (this.height - fluidHeight), fluidTexture, this.width, fluidHeight);
    }
	
	public boolean inTank(GuiTravellersBackpack gui, int mouseX, int mouseY)
    {
        mouseX -= gui.getGuiLeft();
        mouseY -= gui.getGuiTop();
        return startX <= mouseX && mouseX <= startX + width && startY <= mouseY && mouseY <= startY + height;
    }
}
