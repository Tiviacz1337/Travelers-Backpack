package com.tiviacz.travellersbackpack.fluids.effects;

import com.tiviacz.travellersbackpack.api.FluidEffect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidRegistry;

public class EffectWater extends FluidEffect
{
    public EffectWater()
    {
        super(FluidRegistry.WATER, 7);
    }

    @Override
    public void affectDrinker(World world, Entity entity)
    {
        if(entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entity;
            Biome biome = world.getBiome(player.getPosition());

            if(biome == null)
            {
            	biome = Biomes.PLAINS;
            }
            
            if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.HOT) 
            || BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY) 
            || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY) 
            || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WASTELAND) 
            || BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER))
            {
                if(player.isBurning())
                {
                    player.extinguish();
                }
                else
                {
                    player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, timeInTicks, 0));
                }
            }
        }
    }
}