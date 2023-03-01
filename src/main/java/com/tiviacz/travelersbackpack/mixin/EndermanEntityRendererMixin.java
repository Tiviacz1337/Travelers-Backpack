package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackEntityFeature;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value= EnvType.CLIENT)
@Mixin(EndermanEntityRenderer.class)
public abstract class EndermanEntityRendererMixin extends LivingEntityRenderer<LivingEntity, BipedEntityModel<LivingEntity>>
{
    public EndermanEntityRendererMixin(EntityRenderDispatcher dispatcher, BipedEntityModel<LivingEntity> model, float shadowRadius)
    {
        super(dispatcher, model, shadowRadius);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(EntityRenderDispatcher entityRenderDispatcher, CallbackInfo ci) {
        this.addFeature(new TravelersBackpackEntityFeature(this));
    }
}
