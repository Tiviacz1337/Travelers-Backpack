package com.tiviacz.travelersbackpack.client.model;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.TravelersBackpackClient;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;
import java.util.List;

public class StarModelReloadListener implements SimpleSynchronousResourceReloadListener {
    public static final StarModelReloadListener INSTANCE = new StarModelReloadListener();
    public static final Identifier ID = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "star_model");

    private BlockStateModel starModel;

    public BlockStateModel getStarModel() {
        return starModel;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        starModel = Minecraft.getInstance().getModelManager().getModel(TravelersBackpackClient.STAR_MODEL_KEY);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(ResourceReloadListenerKeys.MODELS);
    }
}