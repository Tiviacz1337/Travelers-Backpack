package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    public LivingEntityMixin(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Inject(at = @At(value = "HEAD"), method = "tryUseTotem", cancellable = true)
    private void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir)
    {
        if(this instanceof Object)
        {
            if((Object)this instanceof PlayerEntity player)
            {
                if(TravelersBackpackConfig.enableBackpackAbilities && BackpackAbilities.creeperAbility(player))
                {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "dropInventory")
    private void onDeath(CallbackInfo info)
    {
        if(this instanceof Object)
        {
            if((Object)this instanceof PlayerEntity player)
            {
                if(ComponentUtils.isWearingBackpack(player))
                {
                    if(!player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY))
                    {
                        BackpackUtils.onPlayerDeath(player.world, player, ComponentUtils.getWearingBackpack(player));
                    }
                }
                ComponentUtils.sync(player);
            }

            if((Object)this instanceof LivingEntity livingEntity && Reference.ALLOWED_TYPE_ENTRIES.contains(livingEntity.getType()))
            {
                if(ComponentUtils.isWearingBackpack(livingEntity))
                {
                    livingEntity.dropStack(ComponentUtils.getWearingBackpack(livingEntity));
                }
            }
        }
    }
}