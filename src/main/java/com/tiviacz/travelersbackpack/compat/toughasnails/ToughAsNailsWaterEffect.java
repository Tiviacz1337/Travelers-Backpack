package com.tiviacz.travelersbackpack.compat.toughasnails;

import com.tiviacz.travelersbackpack.fluids.EffectFluid;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.World;
import toughasnails.api.potion.TANEffects;
import toughasnails.api.thirst.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.init.ModConfig;

public class ToughAsNailsWaterEffect extends EffectFluid
{
    public ToughAsNailsWaterEffect()
    {
        super("toughasnails:water", Fluids.WATER, FluidConstants.BUCKET);
    }

    @Override
    public void affectDrinker(StorageView<FluidVariant> variant, World world, Entity entity)
    {
        if(entity instanceof PlayerEntity player)
        {
            if(ModConfig.thirst.enableThirst && !world.isClient)
            {
                //Data for potion
                IThirst thirst = ThirstHelper.getThirst(player);
                int drink_thirst = 3 * 5;
                float drink_hydration = 0.4F;
                float drink_poison_chance = 0.25F;
                thirst.drink(drink_thirst, drink_hydration);

                if(world.random.nextFloat() < drink_poison_chance) {
                    player.addStatusEffect(new StatusEffectInstance(TANEffects.THIRST, 600));
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
