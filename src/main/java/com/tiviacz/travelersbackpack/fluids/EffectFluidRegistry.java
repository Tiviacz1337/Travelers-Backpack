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
import net.minecraft.registry.Registries;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
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
        String uniqueId = effect.getUniqueId();

        if(!EFFECT_REGISTRY.containsKey(uniqueId) && effect.fluid != null)
        {
            EFFECT_REGISTRY.put(uniqueId, effect);
            effect.setEffectID(effectIDCounter);
            LogHelper.info(("Registered the FluidEffect with Unique ID of " + uniqueId + " for " + Registries.FLUID.getId(effect.fluid) + " " + effect.amountRequired + " with the ID " + effectIDCounter));
            effectIDCounter++;
            return effectIDCounter;
        }
        return -1;
    }

    public static Map<String, EffectFluid> getRegisteredFluidEffects()
    {
        return ImmutableMap.copyOf(EFFECT_REGISTRY);
    }

    public static long getHighestFluidEffectAmount(Fluid fluid)
    {
        long amount = 0;
        for(EffectFluid effect : getEffectsForFluid(fluid))
        {
            if(effect.amountRequired > amount)
            {
                amount = effect.amountRequired;
            }
        }
        return amount;
    }

    public static ArrayList<EffectFluid> getEffectsForFluid(Fluid fluid)
    {
        ArrayList<EffectFluid> effectsForFluid = new ArrayList<>();

        for(EffectFluid effect : getRegisteredFluidEffects().values())
        {
            if(fluid == effect.fluid)
            {
                effectsForFluid.add(effect);
            }
        }
        return effectsForFluid;
    }

    public static boolean hasEffects(Fluid fluid)
    {
        List<EffectFluid> effects = getEffectsForFluid(fluid);
        return !effects.isEmpty();
    }

    public static boolean hasExecutableEffects(SingleVariantStorage<FluidVariant> storage, World world, Entity entity)
    {
        List<EffectFluid> executableEffects = getExecutableEffects(storage, world, entity);
        return !executableEffects.isEmpty();
    }

    public static List<EffectFluid> getExecutableEffects(SingleVariantStorage<FluidVariant> storage, World world, Entity entity)
    {
        List<EffectFluid> executableEffects = new ArrayList<>();

        for(EffectFluid effect : getEffectsForFluid(storage.getResource().getFluid()))
        {
            if(effect.canExecuteEffect(storage, world, entity))
            {
                executableEffects.add(effect);
            }
        }
        return executableEffects;
    }

    public static boolean executeEffects(SingleVariantStorage<FluidVariant> storage, Entity entity, World world)
    {
        for(EffectFluid effect : getExecutableEffects(storage, world, entity))
        {
            effect.affectDrinker(storage, world, entity);
        }
        return true;
    }
}