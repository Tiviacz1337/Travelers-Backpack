package com.tiviacz.travelersbackpack.gui;

import com.tiviacz.travelersbackpack.api.events.TankTooltipEvent;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.ArrayList;
import java.util.List;

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
		FluidStack fluidStack = tank.getFluid();
		ArrayList<String> tankTips = new ArrayList<>();

		if(MinecraftForge.EVENT_BUS.post(new TankTooltipEvent(fluidStack, tankTips))) return tankTips;

		String fluidName = (fluidStack != null) ? fluidStack.getLocalizedName() : I18n.format("gui.none.name");
		String fluidAmount = (fluidStack != null) ? fluidStack.amount + "/" + tank.getCapacity() : I18n.format("gui.empty.name");

		if(fluidStack != null)
		{
			if(fluidStack.tag != null)
			{
				if(fluidStack.tag.hasKey("Potion"))
				{
					fluidName = I18n.format(PotionUtils.getPotionFromItem(FluidUtils.getItemStackFromFluidStack(fluidStack)).getNamePrefixed("potion.effect."));
					setPotionDescription(fluidStack, tankTips);
				}
			}
		}
		tankTips.add(fluidName);
		tankTips.add(fluidAmount);
		
		return tankTips;
	}
	
	public void setPotionDescription(FluidStack fluidStack, List<String> lores)
	{
		List<PotionEffect> list = PotionUtils.getEffectsFromStack(FluidUtils.getItemStackFromFluidStack(fluidStack));
		
		if(list.isEmpty())
        {
            String s = I18n.format("effect.none").trim();
            lores.add(TextFormatting.GRAY + s);
        }
        else
        {
            for(PotionEffect potioneffect : list)
            {
                String s1 = I18n.format(potioneffect.getEffectName()).trim();
                Potion potion = potioneffect.getPotion();

                if(potioneffect.getAmplifier() > 0)
                {
                    s1 = s1 + " " + I18n.format("potion.potency." + potioneffect.getAmplifier()).trim();
                }

                if(potioneffect.getDuration() > 20)
                {
                    s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, 1.0F) + ")";
                }

                if(potion.isBadEffect())
                {
                    lores.add(TextFormatting.RED + s1);
                }
                else
                {
                    lores.add(TextFormatting.BLUE + s1);
                }
            }
        }
	}
	
	public void drawGuiFluidBar()
    {
		RenderUtils.renderGuiTank(tank, this.startX, this.startY, this.height, this.width);
    }
	
	public boolean inTank(GuiTravelersBackpack gui, int mouseX, int mouseY)
    {
        mouseX -= gui.getGuiLeft();
        mouseY -= gui.getGuiTop();
        return startX <= mouseX && mouseX <= startX + width && startY <= mouseY && mouseY <= startY + height;
    }
}
