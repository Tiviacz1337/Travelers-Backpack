package com.tiviacz.travelersbackpack.compat.effects.dehydration;

import com.tiviacz.travelersbackpack.fluids.EffectFluid;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.FluidInit;
import net.dehydration.thirst.ThirstManager;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class PurifiedWaterEffect extends EffectFluid
{
    public PurifiedWaterEffect()
    {
        super("dehydration:purified_water", FluidInit.PURIFIED_WATER.getStill(), FluidConstants.BOTTLE);
    }

    @Override
    public void affectDrinker(StorageView<FluidVariant> variant, World world, Entity entity)
    {
        if(entity instanceof PlayerEntity player)
        {
            ThirstManager thirstManager = ((ThirstManagerAccess)player).getThirstManager();
            thirstManager.add(3);
        }
    }

    @Override
    public boolean canExecuteEffect(StorageView<FluidVariant> variant, World world, Entity entity)
    {
        return variant.getAmount() >= amountRequired;
    }
}