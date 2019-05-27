package com.tiviacz.travellersbackpack.gui;

import java.util.ArrayList;
import java.util.List;

import com.tiviacz.travellersbackpack.util.Reference;
import com.tiviacz.travellersbackpack.util.RenderUtils;

import net.minecraft.client.resources.I18n;
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
		RenderUtils.renderGuiTank(tank, this.startX, this.startY, this.height, this.width);
    }
	
	public boolean inTank(GuiTravellersBackpack gui, int mouseX, int mouseY)
    {
        mouseX -= gui.getGuiLeft();
        mouseY -= gui.getGuiTop();
        return startX <= mouseX && mouseX <= startX + width && startY <= mouseY && mouseY <= startY + height;
    }
}
