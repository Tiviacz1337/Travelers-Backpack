package com.tiviacz.travelersbackpack.handlers.models;

import com.tiviacz.travelersbackpack.TravelersBackpackClient;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public class ModuleModelLoader implements ModelLoadingPlugin {
    public static void initialize() {
        ModelLoadingPlugin.register(new ModuleModelLoader());
    }

    @Override
    public void initialize(ModelLoadingPlugin.Context pluginContext) {
        pluginContext.addModels(TravelersBackpackClient.STAR_MODEL);
    }
}