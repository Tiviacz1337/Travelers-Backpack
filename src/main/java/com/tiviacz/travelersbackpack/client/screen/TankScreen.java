package com.tiviacz.travelersbackpack.client.screen;


import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
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
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Iterator;
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
        String fluidName = !fluidVariant.isBlank() ? FluidVariantAttributes.getName(fluidVariant).getString(): I18n.translate("screen.travelersbackpack.none");
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

        if(fluidName != null) tankTips.add(Text.literal(fluidName));
        tankTips.add(Text.literal(fluidAmount));

        return tankTips;
    }

    public static void buildTooltip(ItemStack stack, List<Text> list) {
        List<StatusEffectInstance> list2 = PotionUtil.getPotionEffects(stack);
        List<Pair<EntityAttribute, EntityAttributeModifier>> list3 = Lists.newArrayList();
        Iterator var5;
        MutableText mutableText;
        StatusEffect statusEffect;
        if (list2.isEmpty()) {
            list.add(Text.translatable("effect.none").formatted(Formatting.GRAY));
        } else {
            for(var5 = list2.iterator(); var5.hasNext(); list.add(mutableText.formatted(statusEffect.getCategory().getFormatting()))) {
                StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var5.next();
                mutableText = Text.translatable(statusEffectInstance.getTranslationKey());
                statusEffect = statusEffectInstance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    Iterator var10 = map.entrySet().iterator();

                    while(var10.hasNext()) {
                        Map.Entry<EntityAttribute, EntityAttributeModifier> entry = (Map.Entry)var10.next();
                        EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
                        EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(entityAttributeModifier.getName(), statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
                        list3.add(new Pair((EntityAttribute)entry.getKey(), entityAttributeModifier2));
                    }
                }

                if (statusEffectInstance.getAmplifier() > 0) {
                    mutableText = Text.translatable("potion.withAmplifier", new Object[]{mutableText, Text.translatable("potion.potency." + statusEffectInstance.getAmplifier())});
                }

                if (statusEffectInstance.getDuration() > 20) {
                    mutableText = Text.translatable("potion.withDuration", new Object[]{mutableText, StatusEffectUtil.durationToString(statusEffectInstance, 1.0F)});
                }
            }
        }

        if (!list3.isEmpty()) {
            list.add(ScreenTexts.EMPTY);
            list.add(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
            var5 = list3.iterator();

            while(var5.hasNext()) {
                Pair<EntityAttribute, EntityAttributeModifier> pair = (Pair)var5.next();
                EntityAttributeModifier entityAttributeModifier3 = (EntityAttributeModifier)pair.getSecond();
                double d = entityAttributeModifier3.getValue();
                double e;
                if (entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                    e = entityAttributeModifier3.getValue();
                } else {
                    e = entityAttributeModifier3.getValue() * 100.0;
                }

                if (d > 0.0) {
                    list.add(Text.translatable("attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(), new Object[]{ItemStack.MODIFIER_FORMAT.format(e), Text.translatable(((EntityAttribute)pair.getFirst()).getTranslationKey())}).formatted(Formatting.BLUE));
                } else if (d < 0.0) {
                    e *= -1.0;
                    list.add(Text.translatable("attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(), new Object[]{ItemStack.MODIFIER_FORMAT.format(e), Text.translatable(((EntityAttribute)pair.getFirst()).getTranslationKey())}).formatted(Formatting.RED));
                }
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