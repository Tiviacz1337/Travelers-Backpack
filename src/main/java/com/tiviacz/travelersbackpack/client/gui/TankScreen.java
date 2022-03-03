package com.tiviacz.travelersbackpack.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
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

    public List<Component> getTankTooltip()
    {
        FluidStack fluidStack = tank.getFluid();
        List<Component> tankTips = new ArrayList<>();
        String fluidName = !fluidStack.isEmpty() ? fluidStack.getDisplayName().getString() : I18n.get("screen.travelersbackpack.none");
        String fluidAmount = !fluidStack.isEmpty() ? fluidStack.getAmount() + "/" + tank.getCapacity() : I18n.get("screen.travelersbackpack.empty");

        if(!fluidStack.isEmpty())
        {
            if(fluidStack.getTag() != null)
            {
                if(fluidStack.getTag().contains("Potion"))
                {
                    fluidName = I18n.get(PotionUtils.getPotion(FluidUtils.getItemStackFromFluidStack(fluidStack)).getName("potion.effect."));
                    //setPotionDescription(fluidStack, tankTips);
                }
            }
        }

        tankTips.add(new TextComponent(fluidName));
        tankTips.add(new TextComponent(fluidAmount));

        return tankTips;
    }

    public void setPotionDescription(FluidStack fluidStack, List<String> lores)
    {
        List<MobEffectInstance> list = PotionUtils.getMobEffects(FluidUtils.getItemStackFromFluidStack(fluidStack));

        if(list.isEmpty())
        {
            String s = I18n.get("effect.none").trim();
            lores.add(ChatFormatting.GRAY + s);
        }
        else
        {
            for(MobEffectInstance effectInstance : list)
            {
                String s1 = I18n.get(effectInstance.getDescriptionId()).trim();
           //     Potion potion = new Potion(effect.toString());

                if(effectInstance.getAmplifier() > 0)
                {
                    s1 = s1 + " " + I18n.get("potion.potency." + effectInstance.getAmplifier()).trim();
                }

                if(effectInstance.getDuration() > 20)
                {
                    s1 = s1 + " (" + effectInstance.getDuration() + ")";
                }

                if(!effectInstance.getEffect().isBeneficial())
                {
                    lores.add(ChatFormatting.RED + s1);
                }
                else
                {
                    lores.add(ChatFormatting.BLUE + s1);
                }
            }
        }
    }

    public void drawScreenFluidBar(PoseStack poseStack)
    {
        RenderUtils.renderScreenTank(poseStack, tank, this.startX, this.startY, this.height, this.width);
    }

    public boolean inTank(TravelersBackpackScreen screen, int mouseX, int mouseY)
    {
        mouseX -= screen.getGuiLeft();
        mouseY -= screen.getGuiTop();
        return startX <= mouseX && mouseX <= startX + width && startY <= mouseY && mouseY <= startY + height;
    }
}