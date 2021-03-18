package com.tiviacz.travelersbackpack.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

public class TankScreen
{
    private final int height;
    private final int width;
    private final int startX;
    private final int startY;
    private final FluidTank tank;

    public TankScreen(FluidTank tank, int x, int y, int height, int width)
    {
        this.startX = x;
        this.startY = y;
        this.height = height;
        this.width = width;
        this.tank = tank;
    }

    public List<ITextComponent> getTankTooltip()
    {
        FluidStack fluidStack = tank.getFluid();
        List<ITextComponent> tankTips = new ArrayList<>();
        String fluidName = !fluidStack.isEmpty() ? fluidStack.getDisplayName().getString(): I18n.format("screen.travelersbackpack.none");
        String fluidAmount = !fluidStack.isEmpty() ? fluidStack.getAmount() + "/" + tank.getCapacity() : I18n.format("screen.travelersbackpack.empty");

        if(!fluidStack.isEmpty())
        {
            if(fluidStack.getTag() != null)
            {
                if(fluidStack.getTag().contains("Potion"))
                {
                    fluidName = I18n.format(PotionUtils.getPotionFromItem(FluidUtils.getItemStackFromFluidStack(fluidStack)).getNamePrefixed("potion.effect."));
                    //setPotionDescription(fluidStack, tankTips);
                }
            }
        }

        tankTips.add(new StringTextComponent(fluidName));
        tankTips.add(new StringTextComponent(fluidAmount));

        return tankTips;
    }

    public void setPotionDescription(FluidStack fluidStack, List<String> lores)
    {
        List<EffectInstance> list = PotionUtils.getEffectsFromStack(FluidUtils.getItemStackFromFluidStack(fluidStack));

        if(list.isEmpty())
        {
            String s = I18n.format("effect.none").trim();
            lores.add(TextFormatting.GRAY + s);
        }
        else
        {
            for(EffectInstance effectInstance : list)
            {
                String s1 = I18n.format(effectInstance.getEffectName()).trim();
           //     Potion potion = new Potion(effect.toString());

                if(effectInstance.getAmplifier() > 0)
                {
                    s1 = s1 + " " + I18n.format("potion.potency." + effectInstance.getAmplifier()).trim();
                }

                if(effectInstance.getDuration() > 20)
                {
                    s1 = s1 + " (" + effectInstance.getDuration() + ")";
                }

                if(!effectInstance.getPotion().isBeneficial())
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

    public void drawScreenFluidBar(MatrixStack matrixStackIn)
    {
        RenderUtils.renderScreenTank(matrixStackIn, tank, this.startX, this.startY, this.height, this.width);
    }

    public boolean inTank(TravelersBackpackScreen screen, int mouseX, int mouseY)
    {
        mouseX -= screen.getGuiLeft();
        mouseY -= screen.getGuiTop();
        return startX <= mouseX && mouseX <= startX + width && startY <= mouseY && mouseY <= startY + height;
    }
}