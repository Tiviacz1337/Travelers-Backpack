package com.tiviacz.travelersbackpack.handlers;

import com.google.common.collect.Sets;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.client.model.BackpackUnbakedModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class BackpackModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        // We want to add our model when the models are loaded
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            // This is called for every model that is loaded, so make sure we only target ours
            Set<ModelResourceLocation> backpackModels = Sets.newHashSet();
            Set<ModelResourceLocation> backpackItemModels = Sets.newHashSet();
            for(Block block : BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof TravelersBackpackBlock).toList()) {
                for(BlockState state : block.getStateDefinition().getPossibleStates()) {
                    ModelResourceLocation modelId = BlockModelShaper.stateToModelLocation(BuiltInRegistries.BLOCK.getKey(block), state);
                    backpackModels.add(modelId);
                }
                backpackItemModels.add(new ModelResourceLocation(BuiltInRegistries.BLOCK.getKey(block), "inventory"));
            }
            final ModelResourceLocation id = context.topLevelId();
            if(id != null && (backpackModels.contains(id) || backpackItemModels.contains(id))) {
                return new BackpackUnbakedModel(original);
            } else {
                // If we don't modify the model we just return the original as-is
                return original;
            }
        });
    }
}