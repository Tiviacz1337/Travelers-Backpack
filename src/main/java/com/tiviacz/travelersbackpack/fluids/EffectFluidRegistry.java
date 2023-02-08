package com.tiviacz.travelersbackpack.fluids;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.tiviacz.travelersbackpack.fluids.effects.LavaEffect;
import com.tiviacz.travelersbackpack.fluids.effects.MilkEffect;
import com.tiviacz.travelersbackpack.fluids.effects.PotionEffect;
import com.tiviacz.travelersbackpack.fluids.effects.WaterEffect;
import com.tiviacz.travelersbackpack.util.LogHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Map;

public class EffectFluidRegistry
{
    public static BiMap<String, EffectFluid> EFFECT_REGISTRY = HashBiMap.create();

    public static EffectFluid WATER_EFFECT;
    public static EffectFluid LAVA_EFFECT;
    public static EffectFluid POTION_EFFECT;
    public static EffectFluid MILK_EFFECT;

    private static int effectIDCounter = 0;

    public static void initEffects()
    {
        EFFECT_REGISTRY.clear();

        WATER_EFFECT = new WaterEffect();
        LAVA_EFFECT = new LavaEffect();
        POTION_EFFECT = new PotionEffect();
        MILK_EFFECT = new MilkEffect();
    }

    public static int registerFluidEffect(EffectFluid effect)
    {
        String className = effect.getClass().getName();

        if(!EFFECT_REGISTRY.containsKey(className) && effect.fluid != null)
        {
            EFFECT_REGISTRY.put(className, effect);
            effect.setEffectID(effectIDCounter);
            LogHelper.info(("Registered the class " + className + " as a FluidEffect for " + Registry.FLUID.getId(effect.fluid) + " " + effect.amountRequired + " with the ID " + effectIDCounter));
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
            result[counter++] = Registry.FLUID.getId(effect.fluid).toString();
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

    public static boolean hasFluidEffectAndCanExecute(SingleVariantStorage<FluidVariant> storage, World world, Entity entity)
    {
        for(EffectFluid effect : getRegisteredFluidEffects().values())
        {
            if(storage.getResource().getFluid() == effect.fluid)
            {
                if(effect.canExecuteEffect(storage, world, entity))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean executeFluidEffectsForFluid(SingleVariantStorage<FluidVariant> storage, Entity entity, World world)
    {
        boolean executed = false;

        for(EffectFluid effect : EFFECT_REGISTRY.values())
        {
            if(effect != null)
            {
                if(effect.fluid == storage.getResource().getFluid())
                {
                    effect.affectDrinker(storage, world, entity);
                    executed = true;
                }
            }
        }
        return executed;
    }
}