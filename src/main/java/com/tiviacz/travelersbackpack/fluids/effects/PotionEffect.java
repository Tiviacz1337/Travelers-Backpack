package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class PotionEffect extends EffectFluid {
    public PotionEffect(String uniqueId, Fluid fluid) {
        super(uniqueId, fluid, FluidConstants.BOTTLE);
    }

    public PotionEffect(String uniqueId, String modid, String fluidName) {
        super(uniqueId, modid, fluidName, FluidConstants.BOTTLE);
    }

    @Override
    public void affectDrinker(FluidVariantWrapper stack, Level level, Entity entity) {
        if(!level.isClientSide && entity instanceof Player player) {
            for(MobEffectInstance mobEffectInstance : stack.fluidVariant().getComponents().get(DataComponents.POTION_CONTENTS).get().getAllEffects()) {
                if(mobEffectInstance.getEffect().value().isInstantenous()) {
                    mobEffectInstance.getEffect().value().applyInstantenousEffect(player, player, player, mobEffectInstance.getAmplifier(), 1.0D);
                } else {
                    player.addEffect(new MobEffectInstance(mobEffectInstance));
                }
            }
        }
    }

    @Override
    public boolean canExecuteEffect(FluidVariantWrapper stack, Level level, Entity entity) {
        return stack.getAmount() >= amountRequired;
    }
}