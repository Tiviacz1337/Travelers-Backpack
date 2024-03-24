package com.tiviacz.travelersbackpack.mixin.abilities;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin extends HostileEntity
{
    public EndermanEntityMixin(EntityType<? extends EndermanEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(value = "HEAD"), method =  "isPlayerStaring", cancellable = true)
    public void isPlayerStaring(PlayerEntity player, CallbackInfoReturnable<Boolean> cir)
    {
        if(TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities)
        {
            BackpackAbilities.pumpkinAbility(player, cir);
        }
    }
}