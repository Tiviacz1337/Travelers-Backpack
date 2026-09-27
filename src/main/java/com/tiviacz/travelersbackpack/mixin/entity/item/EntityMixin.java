package com.tiviacz.travelersbackpack.mixin.entity.item;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Unique
    private boolean travelersbackpack$isBackpack() {
        if((Object)this instanceof ItemEntity itemEntity) {
            return itemEntity.getItem().getItem() instanceof TravelersBackpackItem;
        }
        return false;
    }

    @Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
    private void travelersbackpack$isInWater(CallbackInfoReturnable<Boolean> cir) {
        if(this.travelersbackpack$isBackpack() && TravelersBackpackConfig.SERVER.backpackSettings.voidProtection.get()) {
            Entity entity = (Entity)(Object)this;
            if(entity.getY() < entity.level().getMinY() + 1) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "isInvulnerable", at = @At("HEAD"), cancellable = true)
    private void travelersbackpack$isInvulnerable(CallbackInfoReturnable<Boolean> cir) {
        if(this.travelersbackpack$isBackpack() && TravelersBackpackConfig.SERVER.backpackSettings.invulnerableBackpack.get()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onBelowWorld", at = @At("HEAD"), cancellable = true)
    private void travelersbackpack$onBelowWorld(CallbackInfo ci) {
        if(this.travelersbackpack$isBackpack() && TravelersBackpackConfig.SERVER.backpackSettings.voidProtection.get()) {
            ci.cancel();
        }
    }
}