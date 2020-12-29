package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.FluidStack;

public class WaterEffect extends EffectFluid
{
    public WaterEffect()
    {
        super(Fluids.WATER, Reference.BUCKET);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, World world, Entity entity)
    {
        if(entity instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity)entity;
            Biome biome = world.getBiome(player.getPosition());
            int duration = 7 * 20;

     /*       if(BiomeDictionary.hasType(biome, Biome.Type.HOT)
                    || BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY)
                    || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)       //#TODO
                    || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WASTELAND)
                    || BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER))
            { */

            if(biome.getTemperature(player.getPosition()) >= 2.0F)
            {
                if(player.isBurning())
                {
                    player.extinguish();
                }
                else
                {
                    player.addPotionEffect(new EffectInstance(Effects.REGENERATION, duration, 0));
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
