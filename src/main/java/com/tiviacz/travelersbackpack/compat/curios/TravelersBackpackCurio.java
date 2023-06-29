package com.tiviacz.travelersbackpack.compat.curios;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.GameRules;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;

public class TravelersBackpackCurio implements ICurio
{
    public TravelersBackpackCurio() {}

    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity)
    {
        return TravelersBackpackConfig.curiosIntegration;
    }

    @Nonnull
    @Override
    public ICurio.DropRule getDropRule(LivingEntity livingEntity)
    {
        return livingEntity.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) ? DropRule.ALWAYS_KEEP : TravelersBackpack.isAnyGraveModInstalled() ? DropRule.DEFAULT : DropRule.DESTROY;
    }
}