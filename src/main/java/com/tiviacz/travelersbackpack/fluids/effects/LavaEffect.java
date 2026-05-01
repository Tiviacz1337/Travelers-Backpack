package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

public class LavaEffect extends EffectFluid {
    public LavaEffect() {
        super("minecraft:lava", Fluids.LAVA, (int)FluidConstants.BUCKET);
    }

    @Override
    public void affectDrinker(FluidVariantWrapper fluidStack, Level level, Entity entity) {
        if(entity instanceof Player player) {
            int duration = 15;

            player.setRemainingFireTicks(duration * 20);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration * 20 * 4, 2));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, duration * 20 * 4, 0));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration * 20 * 4, 3));
        }
    }

    @Override
    public boolean canExecuteEffect(FluidVariantWrapper stack, Level level, Entity entity) {
        return stack.getAmount() >= amountRequired;
    }
}