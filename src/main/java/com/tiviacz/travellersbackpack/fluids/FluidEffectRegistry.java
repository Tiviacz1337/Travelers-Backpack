package com.tiviacz.travellersbackpack.fluids;

import java.util.ArrayList;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.tiviacz.travellersbackpack.api.FluidEffect;
import com.tiviacz.travellersbackpack.fluids.effects.EffectLava;
import com.tiviacz.travellersbackpack.fluids.effects.EffectMilk;
import com.tiviacz.travellersbackpack.fluids.effects.EffectPotion;
import com.tiviacz.travellersbackpack.fluids.effects.EffectWater;
import com.tiviacz.travellersbackpack.util.LogHelper;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidEffectRegistry
{
    public static BiMap<String, FluidEffect> EFFECT_REGISTRY = HashBiMap.create();

    public static FluidEffect WATER_EFFECT;
    public static FluidEffect LAVA_EFFECT;
    public static FluidEffect MILK_EFFECT;
    public static FluidEffect POTION_EFFECT;
//  public static FluidEffect MELON_EFFECT;
//  public static FluidEffect SOUP_EFFECT;

    private static int effectIDCounter = 0;

    public static void initEffects()
    {
        EFFECT_REGISTRY.clear();
        WATER_EFFECT = new EffectWater();
        LAVA_EFFECT = new EffectLava();
        MILK_EFFECT = new EffectMilk();
        POTION_EFFECT = new EffectPotion();
//      MELON_EFFECT = new MelonJuiceEffect();
//      SOUP_EFFECT = new MushroomStewEffect();
    }
    
    public static int registerFluidEffect(FluidEffect effect)
    {
        String className = effect.getClass().getName();
        
        if(!EFFECT_REGISTRY.containsKey(className) && effect.fluid != null)
        {
            EFFECT_REGISTRY.put(className, effect);
            effect.setEffectID(effectIDCounter);
            LogHelper.info(String.valueOf("Registered the class " + className + " as a FluidEffect for " + effect.fluid.getName() + " with the ID " + effectIDCounter));
            effectIDCounter++;
            return effectIDCounter;
        }
        return -1;
    }

    public static Map<String, FluidEffect> getRegisteredFluidEffects()
    {
        return ImmutableMap.copyOf(EFFECT_REGISTRY);
    }

    public static String[] getRegisteredFluids()
    {
        String[] result = new String[EFFECT_REGISTRY.size()];
        int counter = 0;
        
        for(FluidEffect effect : getRegisteredFluidEffects().values())
        {
            result[counter++] = effect.fluid.getName();
        }
        return result;
    }

    public static boolean hasFluidEffect(Fluid fluid)
    {
        for(FluidEffect effect : getRegisteredFluidEffects().values())
        {
            if(fluid == effect.fluid)
            {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<FluidEffect> getEffectsForFluid(Fluid fluid)
    {
        ArrayList<FluidEffect> effectsForFluid = new ArrayList<FluidEffect>();
        
        for(FluidEffect effect : EFFECT_REGISTRY.values())
        {
            if(fluid == effect.fluid)
            {
                effectsForFluid.add(effect);
            }
        }
        return effectsForFluid;
    }

    public static boolean executeFluidEffectsForFluid(FluidStack fluid, Entity entity, World world)
    {
        boolean executed = false;
        
        for(FluidEffect effect : EFFECT_REGISTRY.values())
        {
            if(effect != null)
            {
                if(effect.fluid == fluid.getFluid())
                {
                    effect.affectDrinker(fluid, world, entity);
                    executed = true;
                }
            }
        }
        return executed;
    }
}