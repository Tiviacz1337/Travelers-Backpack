package com.tiviacz.travellersbackpack.fluids.effects;

import com.tiviacz.travellersbackpack.api.FluidEffect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

public class EffectLava extends FluidEffect
{
    public EffectLava()
    {
        super(FluidRegistry.LAVA, 15);
    }

    @Override
    public void affectDrinker(World world, Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            player.setFire(timeInSeconds);
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, timeInSeconds * 20 * 6, 2));
            player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, timeInSeconds * 20 * 6, 0));
            player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, timeInSeconds * 20 * 6, 3));
        }
    }

}