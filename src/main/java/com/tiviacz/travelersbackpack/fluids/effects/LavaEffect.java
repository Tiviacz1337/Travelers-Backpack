package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.World;

public class LavaEffect extends EffectFluid
{
    public LavaEffect()
    {
        super(Fluids.LAVA.getStill(), Reference.BUCKET);
    }

    @Override
    public void affectDrinker(StorageView<FluidVariant> fluidStack, World world, Entity entity)
    {
        if(entity instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity)entity;
            int duration = 15;

            player.setOnFireFor(duration);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration * 20 * 6, 2));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, duration * 20 * 6, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, duration * 20 * 6, 3));
        }
    }

    @Override
    public boolean canExecuteEffect(StorageView<FluidVariant> stack, World world, Entity entity)
    {
        return stack.getAmount() >= amountRequired;
    }
}