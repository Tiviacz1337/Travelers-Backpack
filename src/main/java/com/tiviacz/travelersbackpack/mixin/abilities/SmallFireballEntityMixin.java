package com.tiviacz.travelersbackpack.mixin.abilities;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmallFireballEntity.class)
public class SmallFireballEntityMixin extends AbstractFireballEntity
{
    public SmallFireballEntityMixin(EntityType<? extends FireballEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(value = "HEAD"), method =  "onEntityHit", cancellable = true)
    public void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci)
    {
        if(TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities)
        {
            if(!this.getEntityWorld().isClient)
            {
                if(this instanceof Object)
                {
                    if((Object)this instanceof SmallFireballEntity smallFireball)
                    {
                        BackpackAbilities.blazeAbility(entityHitResult, smallFireball, ci);
                    }
                }
            }
        }
    }
}