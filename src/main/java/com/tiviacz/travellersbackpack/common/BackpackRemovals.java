package com.tiviacz.travellersbackpack.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class BackpackRemovals 
{
	public void itemBat(EntityPlayer player, World world, ItemStack stack)
    {
        PotionEffect potion;
        
        if(player.isPotionActive(MobEffects.NIGHT_VISION)) 
        {
            potion = player.getActivePotionEffect(MobEffects.NIGHT_VISION);

            if(potion != null && potion.getAmplifier() == 0) 
            {
                player.removePotionEffect(MobEffects.NIGHT_VISION);
            }
        }
    }
	
	public void itemSquid(EntityPlayer player, World world, ItemStack stack)
    {
        itemBat(player, world, stack);
        
        PotionEffect potion;
        
        if(player.isPotionActive(MobEffects.WATER_BREATHING)) 
        {
            potion = player.getActivePotionEffect(MobEffects.WATER_BREATHING);

            if(potion != null && potion.getAmplifier() == 0) 
            {
                player.removePotionEffect(MobEffects.WATER_BREATHING);
            }
        }
    }
}