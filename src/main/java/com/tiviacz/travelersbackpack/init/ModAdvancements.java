package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.advancements.ActionTypeTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAdvancements {
    public static final DeferredRegister<CriterionTrigger<?>> CRITERION_TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, TravelersBackpack.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, ActionTypeTrigger> ACTION_TRIGGER = CRITERION_TRIGGERS.register("action_type", ActionTypeTrigger::new);
}