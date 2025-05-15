package com.tiviacz.travelersbackpack.handlers;

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

import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class BackpackModelLoadingPlugin implements ModelLoadingPlugin {

    private static final Set<ModelResourceLocation> BACKPACK_MODELS = new HashSet<>();
    private static final Set<ModelResourceLocation> BACKPACK_ITEM_MODELS = new HashSet<>();

    static {
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof TravelersBackpackBlock) {
                ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
                for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                    ModelResourceLocation modelId = BlockModelShaper.stateToModelLocation(blockId, state);
                    BACKPACK_MODELS.add(modelId);
                }
                BACKPACK_ITEM_MODELS.add(new ModelResourceLocation(blockId, "inventory"));
            }
        }
    }

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        // We want to add our model when the models are loaded
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            // This is called for every model that is loaded, so make sure we only target ours
            ModelResourceLocation id = context.topLevelId();
            if (id != null && (BACKPACK_MODELS.contains(id) || BACKPACK_ITEM_MODELS.contains(id))) {
                return new BackpackUnbakedModel(original);
            } else {
                // If we don't modify the model we just return the original as-is
                return original;
            }
        });
    }
}