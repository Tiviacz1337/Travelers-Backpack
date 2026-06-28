package com.tiviacz.travelersbackpack.mixin.abilities;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmallFireball.class)
public class SmallFireballEntityMixin extends Fireball {
    public SmallFireballEntityMixin(EntityType<? extends Fireball> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At(value = "HEAD"), method = "onHitEntity", cancellable = true)
    public void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        if(TravelersBackpackConfig.serverSpec.isLoaded() && TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()) {
            if(!this.level().isClientSide()) {
                if((Object)this instanceof SmallFireball smallFireball) {
                    BackpackAbilities.blazeAbility(entityHitResult, smallFireball, ci);
                }
            }
        }
    }
}