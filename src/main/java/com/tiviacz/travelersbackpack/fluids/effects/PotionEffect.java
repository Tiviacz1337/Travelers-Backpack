package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class PotionEffect extends EffectFluid
{
    public PotionEffect()
    {
        super(ModFluids.POTION_FLUID.get(), Reference.POTION);
    }

    @Override
    public void affectDrinker(FluidStack stack, World world, Entity entity)
    {
        if(!world.isClientSide && entity instanceof LivingEntity)
        {
            PlayerEntity playerentity = entity instanceof PlayerEntity ? (PlayerEntity)entity : null;

            for(EffectInstance effectinstance : PotionUtils.getMobEffects(FluidUtils.getItemStackFromFluidStack(stack)))
            {
                if(effectinstance.getEffect().isInstantenous())
                {
                    effectinstance.getEffect().applyInstantenousEffect(playerentity, playerentity, (LivingEntity)entity, effectinstance.getAmplifier(), 1.0D);
                }
                else
                {
                    ((LivingEntity)entity).addEffect(new EffectInstance(effectinstance));
                }
            }
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, World world, Entity entity)
    {
        return stack.getAmount() >= amountRequired;
    }
}
