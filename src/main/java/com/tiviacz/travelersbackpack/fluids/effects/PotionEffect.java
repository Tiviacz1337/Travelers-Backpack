package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.potion.PotionUtil;
import net.minecraft.world.World;

public class PotionEffect extends EffectFluid
{
    public PotionEffect(Fluid fluid)
    {
        super(fluid, FluidConstants.BOTTLE);
    }

    public PotionEffect(String modid, String fluidName)
    {
        super(modid, fluidName, FluidConstants.BOTTLE);
    }

    @Override
    public void affectDrinker(StorageView<FluidVariant> variant, World world, Entity entity)
    {
        if(!world.isClient && entity instanceof PlayerEntity player)
        {
            for(StatusEffectInstance effectinstance : PotionUtil.getPotionEffects(FluidUtils.getItemStackFromFluidStack(variant.getResource())))
            {
                if(effectinstance.getEffectType().isInstant())
                {
                    effectinstance.getEffectType().applyInstantEffect(player, player, player, effectinstance.getAmplifier(), 1.0D);
                }
                else
                {
                    player.addStatusEffect(new StatusEffectInstance(effectinstance));
                }
            }
        }
    }

    @Override
    public boolean canExecuteEffect(StorageView<FluidVariant> variant, World world, Entity entity)
    {
        return variant.getAmount() >= amountRequired;
    }
}