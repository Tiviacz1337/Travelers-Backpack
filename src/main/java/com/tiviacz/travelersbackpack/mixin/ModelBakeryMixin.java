package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.TravelersBackpackClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> unbakedCache;
    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> topLevelModels;

    @Shadow
    abstract UnbakedModel getModel(ResourceLocation modelLocation);

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"), method = "<init>")
    private void init(BlockColors blockColors, ProfilerFiller profilerFiller, Map modelResources, Map blockStateResources, CallbackInfo ci) {
        UnbakedModel unbakedmodel = this.getModel(TravelersBackpackClient.STAR_MODEL); // loadTopLevel(...), but w/o ModelResourceLocation limitation
        this.unbakedCache.put(TravelersBackpackClient.STAR_MODEL, unbakedmodel);
        this.topLevelModels.put(TravelersBackpackClient.STAR_MODEL, unbakedmodel);
    }
}
