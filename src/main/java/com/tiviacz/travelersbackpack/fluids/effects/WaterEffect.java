package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public class WaterEffect extends EffectFluid
{
    public WaterEffect()
    {
        super(Fluids.WATER, Reference.BUCKET);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, Level level, Entity entity)
    {
        if(entity instanceof Player)
        {
            Player player = (Player)entity;
            Holder<Biome> biome = level.getBiome(player.blockPosition());
            int duration = 7 * 20;

     /*       if(BiomeDictionary.hasType(biome, Biome.Type.HOT)
                    || BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY)
                    || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)       //#TODO
                    || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WASTELAND)
                    || BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER))
            { */

            if(biome.value().getBaseTemperature() >= 2.0F)
            {
                if(player.isOnFire())
                {
                    player.clearFire();
                }
                else
                {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, 0));
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