package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.TravelersBackpackClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
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
    abstract UnbakedModel getModel(ResourceLocation modelLocation);

    @Shadow
    protected abstract void registerModelAndLoadDependencies(ModelResourceLocation modelLocation, UnbakedModel model);

    @Inject(at = @At(value = "TAIL"), method = "<init>")
    private void init(BlockColors blockColors, ProfilerFiller profilerFiller, Map modelResources, Map blockStateResources, CallbackInfo ci) {
        UnbakedModel unbakedmodel = this.getModel(TravelersBackpackClient.STAR_MODEL.id());
        this.registerModelAndLoadDependencies(TravelersBackpackClient.STAR_MODEL, unbakedmodel);
    }
}
