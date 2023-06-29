package com.tiviacz.travelersbackpack.fluids;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.fluids.effects.LavaEffect;
import com.tiviacz.travelersbackpack.fluids.effects.MilkEffect;
import com.tiviacz.travelersbackpack.fluids.effects.PotionEffect;
import com.tiviacz.travelersbackpack.fluids.effects.WaterEffect;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.util.LogHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.Map;

public class EffectFluidRegistry
{
    public static BiMap<String, EffectFluid> EFFECT_REGISTRY = HashBiMap.create();

    public static EffectFluid WATER_EFFECT;
    public static EffectFluid LAVA_EFFECT;
    public static EffectFluid POTION_EFFECT;
    public static EffectFluid MILK_EFFECT;

    //Create
    public static EffectFluid CREATE_POTION_EFFECT;

    //Tinkers Construct
    public static EffectFluid TINKERS_CONSTRUCT_POTION_EFFECT;

    private static int effectIDCounter = 0;

    public static void initEffects()
    {
        EFFECT_REGISTRY.clear();

        WATER_EFFECT = new WaterEffect();
        LAVA_EFFECT = new LavaEffect();
        POTION_EFFECT = new PotionEffect(ModFluids.POTION_FLUID.get());
        MILK_EFFECT = new MilkEffect();

        if(canInitialize("create")) CREATE_POTION_EFFECT = new PotionEffect("create", "potion");
        if(canInitialize("tconstruct")) TINKERS_CONSTRUCT_POTION_EFFECT = new PotionEffect("tconstruct", "potion");
    }

    public static int registerFluidEffect(EffectFluid effect)
    {
        //String className = effect.getClass().getName();
        String fluidName = effect.fluid.getRegistryName().toString();

        if(!EFFECT_REGISTRY.containsKey(fluidName) && effect.fluid != null)
        {
            EFFECT_REGISTRY.put(fluidName, effect);
            effect.setEffectID(effectIDCounter);
            LogHelper.info(("Registered the fluid " + fluidName + " as a FluidEffect for " + effect.fluid.getAttributes().getDisplayName(new FluidStack(effect.fluid, 1000)).getString() + " with the ID " + effectIDCounter));
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

    public static boolean hasFluidEffectAndCanExecute(FluidStack fluid, Level level, Entity entity)
    {
        for(EffectFluid effect : getRegisteredFluidEffects().values())
        {
            if(fluid.getFluid() == effect.fluid)
            {
                if(effect.canExecuteEffect(fluid, level, entity))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean executeFluidEffectsForFluid(FluidStack fluid, Entity entity, Level level)
    {
        boolean executed = false;

        for(EffectFluid effect : EFFECT_REGISTRY.values())
        {
            if(effect != null)
            {
                if(effect.fluid == fluid.getFluid())
                {
                    effect.affectDrinker(fluid, level, entity);
                    executed = true;
                }
            }
        }
        return executed;
    }

    public static boolean canInitialize(String modid)
    {
        return ModList.get().isLoaded(modid);
    }
}