package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.effects.FluidEffect;

import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class EffectWater extends FluidEffect
{
    public EffectWater()
    {
        super(FluidRegistry.WATER, Reference.BUCKET);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, World world, Entity entity)
    {
        if(entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entity;
            Biome biome = world.getBiome(player.getPosition());
            int duration = 7 * 20;

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
                    player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, 0));
                }
            }
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, World world, Entity entity)
    {
        return stack.amount >= amountRequired;
    }
}