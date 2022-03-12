package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class LavaEffect extends EffectFluid
{
    public LavaEffect()
    {
        super(Fluids.LAVA, Reference.BUCKET);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, World world, Entity entity)
    {
        if(entity instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity)entity;
            int duration = 15;

            player.setSecondsOnFire(duration);
            player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, duration * 20 * 6, 2));
            player.addEffect(new EffectInstance(Effects.JUMP, duration * 20 * 6, 0));
            player.addEffect(new EffectInstance(Effects.DIG_SLOWDOWN, duration * 20 * 6, 3));
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, World world, Entity entity)
    {
        return stack.getAmount() >= amountRequired;
    }
}