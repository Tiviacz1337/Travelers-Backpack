package com.tiviacz.travelersbackpack.client.screen;


import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String fluidName = !fluidVariant.isBlank() ? FluidVariantRendering.getName(fluidVariant).getString(): I18n.translate("screen.travelersbackpack.none");
        String fluidAmount = !fluidVariant.isBlank() ? tank.getAmount() + "/" + tank.getCapacity() : I18n.translate("screen.travelersbackpack.empty");

        if(!fluidVariant.isBlank())
        {
            if(fluidVariant.hasNbt())
            {
                if(fluidVariant.getNbt().contains("Potion"))
                {
                    fluidName = null;
                    buildTooltip(FluidUtils.getItemStackFromFluidStack(fluidVariant), tankTips);
                }
            }
        }

        if(fluidName != null) tankTips.add(new LiteralText(fluidName));
        tankTips.add(new LiteralText(fluidAmount));

        return tankTips;
    }

    public static void buildTooltip(ItemStack stack, List<Text> list) {
        List<StatusEffectInstance> list2 = PotionUtil.getPotionEffects(stack);
        ArrayList<Pair<EntityAttribute, EntityAttributeModifier>> list3 = Lists.newArrayList();
        if (list2.isEmpty()) {
            list.add(new TranslatableText("effect.none").formatted(Formatting.GRAY));
        } else {
            for (StatusEffectInstance statusEffectInstance : list2) {
                TranslatableText mutableText = new TranslatableText(statusEffectInstance.getTranslationKey());
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
                        EntityAttributeModifier entityAttributeModifier = entry.getValue();
                        EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(entityAttributeModifier.getName(), statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
                        list3.add(new Pair<EntityAttribute, EntityAttributeModifier>(entry.getKey(), entityAttributeModifier2));
                    }
                }
                if (statusEffectInstance.getAmplifier() > 0) {
                    mutableText = new TranslatableText("potion.withAmplifier", mutableText, new TranslatableText("potion.potency." + statusEffectInstance.getAmplifier()));
                }
                if (statusEffectInstance.getDuration() > 20) {
                    mutableText = new TranslatableText("potion.withDuration", mutableText, StatusEffectUtil.durationToString(statusEffectInstance, 1.0F));
                }
                list.add(mutableText.formatted(statusEffect.getCategory().getFormatting()));
            }
        }
        if (!list3.isEmpty()) {
            list.add(LiteralText.EMPTY);
            list.add(new TranslatableText("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
            for (Pair pair : list3) {
                EntityAttributeModifier entityAttributeModifier3 = (EntityAttributeModifier)pair.getSecond();
                double d = entityAttributeModifier3.getValue();
                double e = entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL ? entityAttributeModifier3.getValue() * 100.0 : entityAttributeModifier3.getValue();
                if (d > 0.0) {
                    list.add(new TranslatableText("attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e), new TranslatableText(((EntityAttribute)pair.getFirst()).getTranslationKey())).formatted(Formatting.BLUE));
                    continue;
                }
                if (!(d < 0.0)) continue;
                list.add(new TranslatableText("attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e *= -1.0), new TranslatableText(((EntityAttribute)pair.getFirst()).getTranslationKey())).formatted(Formatting.RED));
            }
        }
    }

    public void drawScreenFluidBar(TravelersBackpackHandledScreen screen, MatrixStack matrices)
    {
        RenderUtils.renderScreenTank(matrices, tank, screen.getX() + this.startX, screen.getY() + this.startY, this.height, this.width);
    }

    public boolean inTank(TravelersBackpackHandledScreen screen, int mouseX, int mouseY)
    {
        return screen.getX() + startX <= mouseX && mouseX <= startX + width + screen.getX() && startY + screen.getY() <= mouseY && mouseY <= startY + height + screen.getY();
    }
}