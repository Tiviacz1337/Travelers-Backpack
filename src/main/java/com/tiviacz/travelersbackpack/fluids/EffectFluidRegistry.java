package com.tiviacz.travelersbackpack.fluids;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.fluids.effects.LavaEffect;
import com.tiviacz.travelersbackpack.fluids.effects.PotionEffect;
import com.tiviacz.travelersbackpack.fluids.effects.WaterEffect;
import com.tiviacz.travelersbackpack.util.LogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Map;

public class EffectFluidRegistry
{
    public static BiMap<String, EffectFluid> EFFECT_REGISTRY = HashBiMap.create();

    public static EffectFluid WATER_EFFECT;
    public static EffectFluid LAVA_EFFECT;
    public static EffectFluid POTION_EFFECT;

    private static int effectIDCounter = 0;

    public static void initEffects()
    {
        EFFECT_REGISTRY.clear();

        WATER_EFFECT = new WaterEffect();
        LAVA_EFFECT = new LavaEffect();
        POTION_EFFECT = new PotionEffect();
     //   POTION_EFFECT = new EffectPotion();
    }

    public static int registerFluidEffect(EffectFluid effect)
    {
        String className = effect.getClass().getName();

        if(!EFFECT_REGISTRY.containsKey(className) && effect.fluid != null)
        {
            EFFECT_REGISTRY.put(className, effect);
            effect.setEffectID(effectIDCounter);
            LogHelper.info(("Registered the class " + className + " as a FluidEffect for " + effect.fluid.getAttributes().getDisplayName(new FluidStack(effect.fluid, 1000)).getString() + " with the ID " + effectIDCounter));
            effectIDCounter++;
            return effectIDCounter;
        }
        return -1;
    }

    public static Map<String, EffectFluid> getRegisteredFluidEffects()
    {
        return ImmutableMap.copyOf(EFFECT_REGISTRY);
    }

    public static String[] getRegisteredFluids()
    {
        String[] result = new String[EFFECT_REGISTRY.size()];
        int counter = 0;

        for(EffectFluid effect : getRegisteredFluidEffects().values())
        {
            result[counter++] = effect.fluid.getRegistryType().getName();
        }
        return result;
    }

    public static boolean hasFluidEffect(Fluid fluid)
    {
        for(EffectFluid effect : getRegisteredFluidEffects().values())
        {
            if(fluid == effect.fluid)
            {
                return true;
            }
        }
        return false;
    }

    public static EffectFluid getFluidEffect(Fluid fluid)
    {
        for(EffectFluid effect : getRegisteredFluidEffects().values())
        {
            if(fluid == effect.fluid)
            {
                return effect;
            }
        }
        return null;
    }

    public static ArrayList<EffectFluid> getEffectsForFluid(Fluid fluid)
    {
        ArrayList<EffectFluid> effectsForFluid = new ArrayList<>();

        for(EffectFluid effect : EFFECT_REGISTRY.values())
        {
            if(fluid == effect.fluid)
            {
                effectsForFluid.add(effect);
            }
        }
        return effectsForFluid;
    }

    public static boolean hasFluidEffectAndCanExecute(FluidStack fluid, World world, Entity entity)
    {
        for(EffectFluid effect : getRegisteredFluidEffects().values())
        {
            if(fluid.getFluid() == effect.fluid)
            {
                if(effect.canExecuteEffect(fluid, world, entity))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean executeFluidEffectsForFluid(FluidStack fluid, Entity entity, World world)
    {
        boolean executed = false;

        for(EffectFluid effect : EFFECT_REGISTRY.values())
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
