package com.tiviacz.travellersbackpack.fluids.effects;

import java.util.List;

import com.tiviacz.travellersbackpack.api.FluidEffect;
import com.tiviacz.travellersbackpack.util.FluidUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class EffectPotion extends FluidEffect
{
    public EffectPotion()
    {
        super(FluidRegistry.getFluid("potion"));
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
}