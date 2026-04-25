package com.tiviacz.travelersbackpack.mixin.abilities;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class GhastEntityMixin extends LivingEntity {
    protected GhastEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At(value = "HEAD"), method = "setTarget", cancellable = true)
    public void setTarget(LivingEntity target, CallbackInfo ci) {
        if(TravelersBackpackConfig.isBackpackAbilitiesEnabled()) {
            if(this instanceof Object) {
                if((Object)this instanceof Ghast ghast) {
                    BackpackAbilities.ghastAbility(ghast, target, ci);
                }
            }
        }
    }
}
