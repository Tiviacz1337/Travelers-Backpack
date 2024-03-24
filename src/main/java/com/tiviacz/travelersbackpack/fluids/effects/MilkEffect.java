package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.fluids.FluidStack;

public class MilkEffect extends EffectFluid
{
    public MilkEffect()
    {
        super("minecraft:milk", "minecraft", "milk", Reference.BUCKET);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, Level level, Entity entity)
    {
        if(entity instanceof Player player)
        {
            player.removeEffectsCuredBy(EffectCures.MILK);
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, Level level, Entity entity)
    {
        return stack.getAmount() >= amountRequired;
    }
}