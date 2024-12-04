package com.tiviacz.travelersbackpack.fluids;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.fluids.effects.LavaEffect;
import com.tiviacz.travelersbackpack.fluids.effects.MilkEffect;
import com.tiviacz.travelersbackpack.fluids.effects.PotionEffect;
import com.tiviacz.travelersbackpack.fluids.effects.WaterEffect;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.util.FluidTypeHelper;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpackneo.compat.toughasnails.ToughAsNailsPotionEffect;
import com.tiviacz.travelersbackpackneo.compat.toughasnails.ToughAsNailsWaterCanteenEffect;
import com.tiviacz.travelersbackpackneo.compat.toughasnails.ToughAsNailsWaterEffect;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EffectFluidRegistry {
    public static BiMap<String, EffectFluid> EFFECT_REGISTRY = HashBiMap.create();

    public static EffectFluid WATER_EFFECT;
    public static EffectFluid LAVA_EFFECT;
    public static EffectFluid POTION_EFFECT;
    public static EffectFluid MILK_EFFECT;

    //Tough as Nails
    public static EffectFluid TAN_POTION_EFFECT;
    public static EffectFluid TAN_WATER_CANTEEN_EFFECT;
    public static EffectFluid TAN_WATER_EFFECT;

    private static int effectIDCounter = 0;

    public static void initEffects() {
        EFFECT_REGISTRY.clear();

        WATER_EFFECT = new WaterEffect();
        LAVA_EFFECT = new LavaEffect();
        POTION_EFFECT = new PotionEffect("travelersbackpack:potion", ModFluids.POTION_STILL);
        MILK_EFFECT = new MilkEffect();

        if (TravelersBackpack.toughasnailsLoaded) {
            TAN_POTION_EFFECT = new ToughAsNailsPotionEffect();
            TAN_WATER_CANTEEN_EFFECT = new ToughAsNailsWaterCanteenEffect();
            TAN_WATER_EFFECT = new ToughAsNailsWaterEffect();
        }
    }

    public static int registerFluidEffect(EffectFluid effect) {
        String uniqueId = effect.getUniqueId();

        if (!EFFECT_REGISTRY.containsKey(uniqueId) && effect.fluid != null) {
            EFFECT_REGISTRY.put(uniqueId, effect);
            effect.setEffectID(effectIDCounter);
            LogHelper.info(("Registered the FluidEffect with Unique ID of " + uniqueId + " for " + FluidTypeHelper.getFluidVariantName(FluidVariant.of(effect.fluid)).getString() + " (Fluid Amount Required: " + effect.amountRequired + ")" + " with the ID " + effectIDCounter));
            effectIDCounter++;
            return effectIDCounter;
        }
        return -1;
    }

    public static Map<String, EffectFluid> getRegisteredFluidEffects() {
        return ImmutableMap.copyOf(EFFECT_REGISTRY);
    }

    public static long getHighestFluidEffectAmount(Fluid fluid) {
        long amount = 0;

        for (EffectFluid effect : getEffectsForFluid(fluid)) {
            if (effect.amountRequired > amount) {
                amount = effect.amountRequired;
            }
        }
        return amount;
    }

    public static ArrayList<EffectFluid> getEffectsForFluid(Fluid fluid) {
        ArrayList<EffectFluid> effectsForFluid = new ArrayList<>();

        for (EffectFluid effect : getRegisteredFluidEffects().values()) {
            if (fluid == effect.fluid) {
                effectsForFluid.add(effect);
            }
        }
        return effectsForFluid;
    }

    public static boolean hasEffects(FluidVariantWrapper fluid) {
        List<EffectFluid> effects = getEffectsForFluid(fluid.fluidVariant().getFluid());
        return !effects.isEmpty();
    }

    public static boolean hasExecutableEffects(FluidVariantWrapper fluid, Level level, Entity entity) {
        List<EffectFluid> executableEffects = getExecutableEffects(fluid, level, entity);
        return !executableEffects.isEmpty();
    }

    public static List<EffectFluid> getExecutableEffects(FluidVariantWrapper fluid, Level level, Entity entity) {
        List<EffectFluid> executableEffects = new ArrayList<>();

        for (EffectFluid effect : getEffectsForFluid(fluid.fluidVariant().getFluid())) {
            if (effect.canExecuteEffect(fluid, level, entity)) {
                executableEffects.add(effect);
            }
        }
        return executableEffects;
    }

    public static boolean executeEffects(FluidVariantWrapper fluid, Entity entity, Level level) {
        for (EffectFluid effect : getExecutableEffects(fluid, level, entity)) {
            effect.affectDrinker(fluid, level, entity);
        }
        return true;
    }

    public static boolean canInitialize(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }
}