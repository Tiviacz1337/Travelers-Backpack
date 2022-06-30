package com.tiviacz.travelersbackpack.client.screen;


import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TankScreen
{
    private final int height;
    private final int width;
    private final int startX;
    private final int startY;
    private final SingleVariantStorage<FluidVariant> tank;

    public TankScreen(SingleVariantStorage<FluidVariant> tank, int x, int y, int height, int width)
    {
        this.startX = x;
        this.startY = y;
        this.height = height;
        this.width = width;
        this.tank = tank;
    }

    public List<Text> getTankTooltip()
    {
        FluidVariant fluidVariant = tank.getResource();
        List<Text> tankTips = new ArrayList<>();
        String fluidName = !fluidVariant.isBlank() ? FluidVariantAttributes.getName(fluidVariant).getString(): I18n.translate("screen.travelersbackpack.none");
        String fluidAmount = !fluidVariant.isBlank() ? tank.getAmount() + "/" + tank.getCapacity() : I18n.translate("screen.travelersbackpack.empty");

        if(!fluidVariant.isBlank())
        {
         /*   if(fluidStack.getTag() != null)
            {
                if(fluidStack.getTag().contains("Potion"))
                {
                    fluidName = I18n.format(PotionUtils.getPotionFromItem(FluidUtils.getItemStackFromFluidStack(fluidStack)).getNamePrefixed("potion.effect."));
                    //setPotionDescription(fluidStack, tankTips);
                }
            } */
        }

        tankTips.add(Text.literal(fluidName));
        tankTips.add(Text.literal(fluidAmount));

        return tankTips;
    }

  /*  public void setPotionDescription(FluidStack fluidStack, List<String> lores)
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
    } */

    public void drawScreenFluidBar(MatrixStack matrices)
    {
        RenderUtils.renderScreenTank(matrices, tank, this.startX, this.startY, this.height, this.width);
    }

    public boolean inTank(TravelersBackpackHandledScreen screen, int mouseX, int mouseY)
    {
        mouseX -= screen.getX();
        mouseY -= screen.getY();
        return startX <= mouseX && mouseX <= startX + width && startY <= mouseY && mouseY <= startY + height;
    }
}
