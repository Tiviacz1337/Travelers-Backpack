package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class PotionEffect extends EffectFluid
{
    public PotionEffect()
    {
        super(ModFluids.POTION_FLUID.get(), Reference.POTION);
    }

    @Override
    public void affectDrinker(FluidStack stack, Level level, Entity entity)
    {
        if(!level.isClientSide && entity instanceof LivingEntity)
        {
            Player playerentity = entity instanceof Player ? (Player)entity : null;

            for(MobEffectInstance mobEffectInstance : PotionUtils.getMobEffects(FluidUtils.getItemStackFromFluidStack(stack)))
            {
                if(mobEffectInstance.getEffect().isInstantenous())
                {
                    mobEffectInstance.getEffect().applyInstantenousEffect(playerentity, playerentity, (LivingEntity)entity, mobEffectInstance.getAmplifier(), 1.0D);
                }
                else
                {
                    ((LivingEntity)entity).addEffect(new MobEffectInstance(mobEffectInstance));
                }
            }
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, Level level, Entity entity)
    {
        return stack.getAmount() >= amountRequired;
    }
}