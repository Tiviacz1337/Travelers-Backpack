package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.advancements.ActionTypeTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModAdvancements {
    public static ActionTypeTrigger ACTION_TRIGGER;

    public static void init() {
        ACTION_TRIGGER = Registry.register(BuiltInRegistries.TRIGGER_TYPES, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "action_type"), new ActionTypeTrigger());
    }
}