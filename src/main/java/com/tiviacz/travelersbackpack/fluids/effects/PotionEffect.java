package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class PotionEffect extends EffectFluid
{
    public PotionEffect(String uniqueId, Fluid fluid)
    {
        super(uniqueId, fluid, Reference.POTION);
    }

    public PotionEffect(String uniqueId, String modid, String fluidName)
    {
        super(uniqueId, modid, fluidName, Reference.POTION);
    }

    @Override
    public void affectDrinker(FluidStack stack, Level level, Entity entity)
    {
        if(!level.isClientSide && entity instanceof Player player)
        {
            for(MobEffectInstance mobEffectInstance : PotionUtils.getMobEffects(FluidUtils.getItemStackFromFluidStack(stack)))
            {
                if(mobEffectInstance.getEffect().isInstantenous())
                {
                    mobEffectInstance.getEffect().applyInstantenousEffect(player, player, player, mobEffectInstance.getAmplifier(), 1.0D);
                }
                else
                {
                    player.addEffect(new MobEffectInstance(mobEffectInstance));
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