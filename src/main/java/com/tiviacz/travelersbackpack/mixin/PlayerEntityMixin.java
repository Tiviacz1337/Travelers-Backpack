package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity
{
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(at = @At(value = "HEAD"), method = "dropInventory")
    private void onDeath(CallbackInfo info)
    {
        if(this instanceof Object)
        {
            if((Object)this instanceof PlayerEntity)
            {
                PlayerEntity player = (PlayerEntity)(Object)this;

                if(ComponentUtils.isWearingBackpack(player))
                {
                    if(!player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY))
                    {
                        BackpackUtils.onPlayerDeath(player.world, player, ComponentUtils.getWearingBackpack(player));
                    }
                }
                ComponentUtils.sync(player);
            }
        }
    }
}