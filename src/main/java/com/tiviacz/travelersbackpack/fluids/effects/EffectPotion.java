package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.api.fluids.effects.FluidEffect;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class EffectPotion extends FluidEffect
{
    public EffectPotion()
    {
        super(FluidRegistry.getFluid("potion"), Reference.POTION);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, World world, Entity entity)
    {
        if(fluidStack.tag != null)
        {
        	ItemStack stack = FluidUtils.getItemStackFromFluidStack(fluidStack);
        	List<PotionEffect> effects = PotionUtils.getEffectsFromStack(stack);
        	
        	for(PotionEffect effect : effects)
        	{
        		if(entity instanceof EntityLivingBase)
        		{
        			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(effect));
        		}
        	}
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, World world, Entity entity)
    {
        return stack.amount >= amountRequired;
    }
}