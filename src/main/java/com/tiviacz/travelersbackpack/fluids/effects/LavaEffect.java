package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class LavaEffect extends EffectFluid {
    public LavaEffect() {
        super("minecraft:lava", Fluids.LAVA, FluidType.BUCKET_VOLUME);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, Level level, Entity entity) {
        if(entity instanceof Player player) {
            int duration = 15;

            player.setRemainingFireTicks(duration * 20);
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, duration * 20 * 4, 2));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, duration * 20 * 4, 0));
            player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, duration * 20 * 4, 3));
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, Level level, Entity entity) {
        return stack.getAmount() >= amountRequired;
    }
}