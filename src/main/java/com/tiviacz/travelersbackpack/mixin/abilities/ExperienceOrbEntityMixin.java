package com.tiviacz.travelersbackpack.mixin.abilities;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin
{
    @Inject(at = @At("HEAD"), method = "onPlayerCollision")
    public void onPlayerCollision(PlayerEntity player, CallbackInfo ci)
    {
        if(TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities)
        {
            BackpackAbilities.ABILITIES.lapisAbility(player);
        }
    }
}