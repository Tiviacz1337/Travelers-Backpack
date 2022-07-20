package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class MilkEffect extends EffectFluid
{
    public MilkEffect()
    {
        super("minecraft", "milk", Reference.BUCKET);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, World world, Entity entity)
    {
        if(entity instanceof PlayerEntity)
        {
            ((PlayerEntity)entity).curePotionEffects(new ItemStack(Items.MILK_BUCKET));
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, World world, Entity entity)
    {
        return stack.getAmount() >= amountRequired;
    }
}