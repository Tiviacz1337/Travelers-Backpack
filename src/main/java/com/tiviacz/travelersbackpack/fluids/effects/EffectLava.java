package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.effects.FluidEffect;

import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class EffectLava extends FluidEffect
{
    public EffectLava()
    {
        super(FluidRegistry.LAVA, Reference.BUCKET);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, World world, Entity entity)
    {
        if(entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entity;
            int duration = 15;

            player.setFire(duration);
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration * 20 * 6, 2));
            player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration * 20 * 6, 0));
            player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, duration * 20 * 6, 3));
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, World world, Entity entity)
    {
        return stack.amount >= amountRequired;
    }
}