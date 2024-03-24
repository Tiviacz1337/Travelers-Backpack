package com.tiviacz.travelersbackpack.mixin.abilities;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class GhastEntityMixin extends LivingEntity
{
    protected GhastEntityMixin(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(value = "HEAD"), method =  "setTarget", cancellable = true)
    public void setTarget(LivingEntity target, CallbackInfo ci)
    {
        if(TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities)
        {
            if(this instanceof Object)
            {
                if((Object)this instanceof GhastEntity ghast)
                {
                    BackpackAbilities.ghastAbility(ghast, target, ci);
                }
            }
        }
    }
}
