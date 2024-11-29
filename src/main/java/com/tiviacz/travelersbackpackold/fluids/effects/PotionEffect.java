package com.tiviacz.travelersbackpackold.fluids.effects;

import com.tiviacz.travelersbackpackold.fluids.EffectFluid;
import com.tiviacz.travelersbackpackneo.init.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class PotionEffect extends EffectFluid
{
    public PotionEffect()
    {
        super("travelersbackpack:potion", ModFluids.POTION_STILL, FluidConstants.BOTTLE);
    }

    @Override
    public void affectDrinker(StorageView<FluidVariant> variant, World world, Entity entity)
    {
        if(!world.isClient && entity instanceof PlayerEntity player)
        {
            if(variant.getResource().getComponents() == null || !variant.getResource().getComponents().get(DataComponentTypes.POTION_CONTENTS).isPresent()) return;

            for(StatusEffectInstance mobEffectInstance : variant.getResource().getComponents().get(DataComponentTypes.POTION_CONTENTS).get().getEffects())
            {
                if(mobEffectInstance.getEffectType().value().isInstant())
                {
                    mobEffectInstance.getEffectType().value().applyInstantEffect(player, player, player, mobEffectInstance.getAmplifier(), 1.0D);
                }
                else
                {
                    player.addStatusEffect(new StatusEffectInstance(mobEffectInstance));
                }
            }

         /*   for(StatusEffectInstance effectinstance : PotionUtil.getPotionEffects(FluidUtils.getItemStackFromFluidStack(variant.getResource())))
            {
                if(effectinstance.getEffectType().isInstant())
                {
                    effectinstance.getEffectType().applyInstantEffect(player, player, player, effectinstance.getAmplifier(), 1.0D);
                }
                else
                {
                    player.addStatusEffect(new StatusEffectInstance(effectinstance));
                }
            } */
        }
    }

    @Override
    public boolean canExecuteEffect(StorageView<FluidVariant> variant, World world, Entity entity)
    {
        return variant.getAmount() >= amountRequired;
    }
}