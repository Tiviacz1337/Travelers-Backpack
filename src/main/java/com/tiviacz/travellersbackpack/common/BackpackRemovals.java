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
	
	public void itemDragon(EntityPlayer player, World world, ItemStack stack)
	{
		itemPigman(player, world, stack);
		itemSquid(player, world, stack);
		
		PotionEffect potion;
        
        if(player.isPotionActive(MobEffects.REGENERATION)) 
        {
            potion = player.getActivePotionEffect(MobEffects.REGENERATION);

            if(potion != null && potion.getAmplifier() == 0) 
            {
                player.removePotionEffect(MobEffects.REGENERATION);
            }
        }
        
        if(player.isPotionActive(MobEffects.STRENGTH)) 
        {
            potion = player.getActivePotionEffect(MobEffects.STRENGTH);

            if(potion != null && potion.getAmplifier() == 0) 
            {
                player.removePotionEffect(MobEffects.STRENGTH);
            }
        }
	}
	
	public void itemPigman(EntityPlayer player, World world, ItemStack stack)
	{
		PotionEffect potion;
        
        if(player.isPotionActive(MobEffects.FIRE_RESISTANCE)) 
        {
            potion = player.getActivePotionEffect(MobEffects.FIRE_RESISTANCE);

            if(potion != null && potion.getAmplifier() == 0) 
            {
                player.removePotionEffect(MobEffects.FIRE_RESISTANCE);
            }
        }
	}
	
	public void itemRainbow(EntityPlayer player, World world, ItemStack stack)
	{
		PotionEffect potionSpeed;
		PotionEffect potionJumpBoost;
        
        if(player.isPotionActive(MobEffects.SPEED)) 
        {
            potionSpeed = player.getActivePotionEffect(MobEffects.SPEED);

            if(potionSpeed != null && potionSpeed.getAmplifier() == 1) 
            {
                player.removePotionEffect(MobEffects.SPEED);
            }
        }
        
        if(player.isPotionActive(MobEffects.JUMP_BOOST)) 
        {
            potionJumpBoost = player.getActivePotionEffect(MobEffects.JUMP_BOOST);

            if(potionJumpBoost != null && potionJumpBoost.getAmplifier() == 1) 
            {
                player.removePotionEffect(MobEffects.JUMP_BOOST);
            }
        }
	}
	
	public void itemSquid(EntityPlayer player, World world, ItemStack stack)
    {
      //itemBat(player, world, stack);
        
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
	
	public void itemWolf(EntityPlayer player, World world, ItemStack stack)
	{
		PotionEffect potion;
        
        if(player.isPotionActive(MobEffects.STRENGTH)) 
        {
            potion = player.getActivePotionEffect(MobEffects.STRENGTH);

            if(potion != null && potion.getAmplifier() == 2) 
            {
                player.removePotionEffect(MobEffects.STRENGTH);
            }
        }
	}
}