package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.util.FluidStackHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class PotionEffect extends EffectFluid {
    public PotionEffect(String uniqueId, Fluid fluid) {
        super(uniqueId, fluid, (int)FluidConstants.BOTTLE);
    }

    public PotionEffect(String uniqueId, String modid, String fluidName) {
        super(uniqueId, modid, fluidName, (int)FluidConstants.BOTTLE);
    }

    @Override
    public void affectDrinker(FluidVariantWrapper stack, Level level, Entity entity) {
        if(!level.isClientSide && entity instanceof Player player) {
            for(MobEffectInstance mobEffectInstance : PotionUtils.getMobEffects(FluidStackHelper.getItemStackFromFluidStack(stack.fluidVariant()))) {
                if(mobEffectInstance.getEffect().isInstantenous()) {
                    mobEffectInstance.getEffect().applyInstantenousEffect(player, player, player, mobEffectInstance.getAmplifier(), 1.0D);
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