package com.tiviacz.travelersbackpack.compat.curios;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.GameRules;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;

public class TravelersBackpackCurio implements ICurio
{
    public TravelersBackpackCurio() {}

    @Nonnull
    @Override
    public ICurio.DropRule getDropRule(LivingEntity livingEntity)
    {
        return livingEntity.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) ? DropRule.ALWAYS_KEEP : DropRule.DESTROY;
    }
}