package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class MilkEffect extends EffectFluid {
    public MilkEffect() {
        super("minecraft:milk", "minecraft", "milk", FluidType.BUCKET_VOLUME);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, Level level, Entity entity) {
        if(entity instanceof Player player) {
            player.removeAllEffects();
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, Level level, Entity entity) {
        return stack.getAmount() >= amountRequired;
    }
}