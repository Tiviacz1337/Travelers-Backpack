package com.tiviacz.travelersbackpack.mixin.abilities;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public class EndermanEntityMixin extends Monster {
    protected EndermanEntityMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At(value = "HEAD"), method = "isBeingStaredBy", cancellable = true)
    public void isPlayerStaring(Player player, CallbackInfoReturnable<Boolean> cir) {
        BackpackAbilities.pumpkinAbility(player, cir);
    }
}