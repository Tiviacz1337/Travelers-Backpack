package com.tiviacz.travelersbackpack.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.*;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String fluidName = !fluidStack.isEmpty() ? fluidStack.getDisplayName().getString(): I18n.get("screen.travelersbackpack.none");
        String fluidAmount = !fluidStack.isEmpty() ? fluidStack.getAmount() + "/" + tank.getCapacity() : I18n.get("screen.travelersbackpack.empty");

        if(!fluidStack.isEmpty())
        {
            if(fluidStack.getTag() != null)
            {
                if(fluidStack.getTag().contains("Potion"))
                {
                    fluidName = null;
                    setPotionDescription(FluidUtils.getItemStackFromFluidStack(fluidStack), tankTips);
                }
            }
        }

        if(fluidName != null) tankTips.add(new StringTextComponent(fluidName));
        tankTips.add(new StringTextComponent(fluidAmount));

        return tankTips;
    }

    public static void setPotionDescription(ItemStack p_185182_0_, List<ITextComponent> p_185182_1_) {
        List<EffectInstance> list = PotionUtils.getMobEffects(p_185182_0_);
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        if (list.isEmpty()) {
            p_185182_1_.add((new TranslationTextComponent("effect.none")).withStyle(TextFormatting.GRAY));
        } else {
            for(EffectInstance effectinstance : list) {
                IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(effectinstance.getDescriptionId());
                Effect effect = effectinstance.getEffect();
                Map<Attribute, AttributeModifier> map = effect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierValue(effectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }

                if (effectinstance.getAmplifier() > 0) {
                    iformattabletextcomponent = new TranslationTextComponent("potion.withAmplifier", iformattabletextcomponent, new TranslationTextComponent("potion.potency." + effectinstance.getAmplifier()));
                }

                if (effectinstance.getDuration() > 20) {
                    iformattabletextcomponent = new TranslationTextComponent("potion.withDuration", iformattabletextcomponent, EffectUtils.formatDuration(effectinstance, 1.0F));
                }

                p_185182_1_.add(iformattabletextcomponent.withStyle(effect.getCategory().getTooltipFormatting()));
            }
        }

        if (!list1.isEmpty()) {
            p_185182_1_.add(StringTextComponent.EMPTY);
            p_185182_1_.add((new TranslationTextComponent("potion.whenDrank")).withStyle(TextFormatting.DARK_PURPLE));

            for(Pair<Attribute, AttributeModifier> pair : list1) {
                AttributeModifier attributemodifier2 = pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;
                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    p_185182_1_.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getDescriptionId()))).withStyle(TextFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 = d1 * -1.0D;
                    p_185182_1_.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getDescriptionId()))).withStyle(TextFormatting.RED));
                }
            }
        }
    }

    public void drawScreenFluidBar(TravelersBackpackScreen screen, MatrixStack matrixStackIn)
    {
        RenderUtils.renderScreenTank(matrixStackIn, tank, screen.getGuiLeft() + this.startX, screen.getGuiTop() + this.startY, this.height, this.width);
    }

    public boolean inTank(TravelersBackpackScreen screen, int mouseX, int mouseY)
    {
        return screen.getGuiLeft() + startX <= mouseX && mouseX <= startX + width + screen.getGuiLeft() && startY + screen.getGuiTop() <= mouseY && mouseY <= startY + height + screen.getGuiTop();
    }
}