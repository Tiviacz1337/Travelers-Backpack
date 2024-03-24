package com.tiviacz.travelersbackpack.client.screen;


import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.attribute.AttributeModifierCreator;
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
import net.minecraft.world.World;

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

    public List<Text> getTankTooltip(World world)
    {
        FluidVariant fluidVariant = tank.getResource();
        List<Text> tankTips = new ArrayList<>();
        String fluidName = !fluidVariant.isBlank() ? FluidVariantAttributes.getName(fluidVariant).getString() : I18n.translate("screen.travelersbackpack.none");
        String fluidAmount = I18n.translate("screen.travelersbackpack.empty");

        if(!fluidVariant.isBlank())
        {
            float amount = (float)tank.getAmount() / 81;
            float capacity = (float)tank.getCapacity() / 81;

            fluidAmount = (int)amount + "/" + (int)capacity;
        }

        if(!fluidVariant.isBlank())
        {
            if(fluidVariant.hasNbt())
            {
                if(fluidVariant.getNbt().contains("Potion"))
                {
                    fluidName = null;
                    buildTooltip(FluidUtils.getItemStackFromFluidStack(fluidVariant), tankTips, world);
                }
            }
        }

        if(fluidName != null) tankTips.add(Text.literal(fluidName));
        tankTips.add(Text.literal(fluidAmount));

        return tankTips;
    }

    public static void buildTooltip(ItemStack stack, List<Text> list, World world) {
        List<StatusEffectInstance> statusEffects = PotionUtil.getPotionEffects(stack);
        List<Pair<EntityAttribute, EntityAttributeModifier>> list2 = Lists.newArrayList();
        Iterator var4;
        MutableText mutableText;
        StatusEffect statusEffect;
        if (statusEffects.isEmpty()) {
            list.add(Text.translatable("effect.none").formatted(Formatting.GRAY));
        } else {
            for(var4 = statusEffects.iterator(); var4.hasNext(); list.add(mutableText.formatted(statusEffect.getCategory().getFormatting()))) {
                StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var4.next();
                mutableText = Text.translatable(statusEffectInstance.getTranslationKey());
                statusEffect = statusEffectInstance.getEffectType();
                Map<EntityAttribute, AttributeModifierCreator> map = statusEffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    Iterator var9 = map.entrySet().iterator();

                    while(var9.hasNext()) {
                        Map.Entry<EntityAttribute, AttributeModifierCreator> entry = (Map.Entry)var9.next();
                        list2.add(new Pair((EntityAttribute)entry.getKey(), ((AttributeModifierCreator)entry.getValue()).createAttributeModifier(statusEffectInstance.getAmplifier())));
                    }
                }

                if (statusEffectInstance.getAmplifier() > 0) {
                    mutableText = Text.translatable("potion.withAmplifier", new Object[]{mutableText, Text.translatable("potion.potency." + statusEffectInstance.getAmplifier())});
                }

                if (!statusEffectInstance.isDurationBelow(20)) {
                    mutableText = Text.translatable("potion.withDuration", new Object[]{mutableText, StatusEffectUtil.getDurationText(statusEffectInstance, 1.0F, world == null ? 20.0f : world.getTickManager().getTickRate())});
                }
            }
        }

        if (!list2.isEmpty()) {
            list.add(ScreenTexts.EMPTY);
            list.add(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
            var4 = list2.iterator();

            while(var4.hasNext()) {
                Pair<EntityAttribute, EntityAttributeModifier> pair = (Pair)var4.next();
                EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)pair.getSecond();
                double d = entityAttributeModifier.getValue();
                double e;
                if (entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                    e = entityAttributeModifier.getValue();
                } else {
                    e = entityAttributeModifier.getValue() * 100.0;
                }

                if (d > 0.0) {
                    list.add(Text.translatable("attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(), new Object[]{ItemStack.MODIFIER_FORMAT.format(e), Text.translatable(((EntityAttribute)pair.getFirst()).getTranslationKey())}).formatted(Formatting.BLUE));
                } else if (d < 0.0) {
                    e *= -1.0;
                    list.add(Text.translatable("attribute.modifier.take." + entityAttributeModifier.getOperation().getId(), new Object[]{ItemStack.MODIFIER_FORMAT.format(e), Text.translatable(((EntityAttribute)pair.getFirst()).getTranslationKey())}).formatted(Formatting.RED));
                }
            }
        }
    }

    public void drawScreenFluidBar(TravelersBackpackHandledScreen screen, DrawContext context)
    {
        RenderUtils.renderScreenTank(context, tank, screen.getX() + this.startX, screen.getY() + this.startY, 0, this.height, this.width);
    }

    public boolean inTank(TravelersBackpackHandledScreen screen, int mouseX, int mouseY)
    {
        return screen.getX() + startX <= mouseX && mouseX <= startX + width + screen.getX() && startY + screen.getY() <= mouseY && mouseY <= startY + height + screen.getY();
    }
}