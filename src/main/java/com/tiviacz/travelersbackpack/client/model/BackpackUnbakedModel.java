package com.tiviacz.travelersbackpack.client.model;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public class BackpackUnbakedModel implements UnbakedModel {
    private static final ResourceLocation DYED_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "block/backpack_dyed");
    private final UnbakedModel unbaked;

    public BackpackUnbakedModel(UnbakedModel unbaked) {
        this.unbaked = unbaked;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return this.unbaked.getDependencies();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {
        this.unbaked.resolveParents(resolver);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation location) {
        BakedModel original = this.unbaked.bake(baker, spriteGetter, state, location);
        BakedModel dyedBackpack = baker.bake(DYED_BACKPACK, state);
        return new BackpackBakedModel(original, dyedBackpack);
    }
}