package com.tiviacz.travelersbackpack.client.screens;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
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
                    fluidName = null;
                    setPotionDescription(FluidUtils.getItemStackFromFluidStack(fluidStack), tankTips);
                }
            }
        }

        if(fluidName != null) tankTips.add(Component.literal(fluidName));
        tankTips.add(Component.literal(fluidAmount));

        return tankTips;
    }

    public static void setPotionDescription(ItemStack p_43556_, List<Component> p_43557_) {
        List<MobEffectInstance> list = PotionUtils.getMobEffects(p_43556_);
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        if (list.isEmpty()) {
            p_43557_.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            for(MobEffectInstance mobeffectinstance : list) {
                MutableComponent mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
                MobEffect mobeffect = mobeffectinstance.getEffect();
                Map<Attribute, AttributeModifier> map = mobeffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffect.getAttributeModifierValue(mobeffectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }

                if (mobeffectinstance.getAmplifier() > 0) {
                    mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobeffectinstance.getAmplifier()));
                }

                if (mobeffectinstance.getDuration() > 20) {
                    mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, 1.0F));
                }

                p_43557_.add(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));
            }
        }

        if (!list1.isEmpty()) {
            p_43557_.add(CommonComponents.EMPTY);
            p_43557_.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

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
                    p_43557_.add(Component.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    p_43557_.add(Component.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.RED));
                }
            }
        }

    }

    public void drawScreenFluidBar(TravelersBackpackScreen screen, GuiGraphics guiGraphics)
    {
        RenderUtils.renderScreenTank(guiGraphics, tank, screen.getGuiLeft() + this.startX, screen.getGuiTop() + this.startY, 0, this.height, this.width);
    }

    public boolean inTank(TravelersBackpackScreen screen, int mouseX, int mouseY)
    {
        //mouseX -= screen.getGuiLeft();
        //mouseY -= screen.getGuiTop();
        return screen.getGuiLeft() + startX <= mouseX && mouseX <= startX + width + screen.getGuiLeft() && startY + screen.getGuiTop() <= mouseY && mouseY <= startY + height + screen.getGuiTop();
    }
}