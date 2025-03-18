package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.common.BackpackManager;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 0)
public class LivingEntityPriorityMixin {
    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void drop(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
        LivingEntity e = (LivingEntity)(Object)this;
        if(!(e instanceof ServerPlayer player)) return;

        //Mixin responsible only for creating backup of equipped backpack before any compatibility actions are taken, does not make any changes to backpacks or anything
        if(ComponentUtils.isWearingBackpack(player)) {
            BackpackManager.addBackpack(player, ComponentUtils.getWearingBackpack(player));
        }
    }
}
