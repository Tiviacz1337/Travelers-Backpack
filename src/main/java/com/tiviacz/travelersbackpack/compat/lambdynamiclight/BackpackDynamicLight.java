package com.tiviacz.travelersbackpack.compat.lambdynamiclight;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class BackpackDynamicLight implements DynamicLightsInitializer {
    public static final EntityLuminance.Type TYPE = EntityLuminance.Type.registerSimple(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "lantern_upgrade"), BackpackEntityLuminance.INSTANCE);

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        context.entityLightSourceManager().onRegisterEvent().register(registerContext -> registerContext.register(EntityType.PLAYER, BackpackEntityLuminance.INSTANCE));
    }

    @Override
    public void onInitializeDynamicLights(ItemLightSourceManager itemLightSourceManager) {

    }
}