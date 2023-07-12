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
import java.util.List;
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
        POTION_EFFECT = new PotionEffect("travelersbackpack:potion", ModFluids.POTION_FLUID.get());
        MILK_EFFECT = new MilkEffect();

        if(canInitialize("create")) CREATE_POTION_EFFECT = new PotionEffect("create:potion", "create", "potion");
        if(canInitialize("tconstruct")) TINKERS_CONSTRUCT_POTION_EFFECT = new PotionEffect("tconstruct:potion", "tconstruct", "potion");
    }

    public static int registerFluidEffect(EffectFluid effect)
    {
        //String className = effect.getClass().getName();
        String uniqueId = effect.getUniqueId();

        if(!EFFECT_REGISTRY.containsKey(uniqueId) && effect.fluid != null)
        {
            EFFECT_REGISTRY.put(uniqueId, effect);
            effect.setEffectID(effectIDCounter);
            LogHelper.info(("Registered the FluidEffect with Unique ID of " + uniqueId + " for " + effect.fluid.getAttributes().getDisplayName(new FluidStack(effect.fluid, 1000)).getString() + " (Fluid Amount Required: " + effect.amountRequired + ")" + " with the ID " + effectIDCounter));
            effectIDCounter++;
            return effectIDCounter;
        }
        return -1;
    }

    public static Map<String, EffectFluid> getRegisteredFluidEffects()
    {
        return ImmutableMap.copyOf(EFFECT_REGISTRY);
    }

    public static int getHighestFluidEffectAmount(Fluid fluid)
    {
        int amount = 0;

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

    public static boolean hasEffects(FluidStack fluid)
    {
        List<EffectFluid> effects = getEffectsForFluid(fluid.getFluid());
        return !effects.isEmpty();
    }

    public static boolean hasExecutableEffects(FluidStack fluid, Level level, Entity entity)
    {
        List<EffectFluid> executableEffects = getExecutableEffects(fluid, level, entity);
        return !executableEffects.isEmpty();
    }

    public static List<EffectFluid> getExecutableEffects(FluidStack fluid, Level level, Entity entity)
    {
        List<EffectFluid> executableEffects = new ArrayList<>();

        for(EffectFluid effect : getEffectsForFluid(fluid.getFluid()))
        {
            if(effect.canExecuteEffect(fluid, level, entity))
            {
                executableEffects.add(effect);
            }
        }
        return executableEffects;
    }

    public static boolean executeEffects(FluidStack fluid, Entity entity, Level level)
    {
        for(EffectFluid effect : getExecutableEffects(fluid, level, entity))
        {
            effect.affectDrinker(fluid, level, entity);
        }
        return true;
    }

    public static boolean canInitialize(String modid)
    {
        return ModList.get().isLoaded(modid);
    }
}