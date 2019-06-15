package com.tiviacz.travellersbackpack.util;

import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.capability.IBackpack;
import com.tiviacz.travellersbackpack.handlers.ConfigHandler;
import com.tiviacz.travellersbackpack.init.ModBlocks;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BackpackUtils 
{
	public static int convertNameToMeta(String name)
	{
		for(int x = 0; x < Reference.BACKPACK_NAMES.length; x++)
		{
			String string = Reference.BACKPACK_NAMES[x];
			
			if(string.toLowerCase().equals(name.toLowerCase()))
			{
				return x;
			}
		}
		return 0;
	}
	
	public static void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
	{
		if(!world.isRemote)
		{
			if(ConfigHandler.backpackDeathPlace)
			{
				if(!tryPlace(world, player, stack))
				{
					player.entityDropItem(stack, 1);
				}
			}
			else
			{
				player.entityDropItem(stack, 1);
				IBackpack cap = CapabilityUtils.getCapability(player);
				cap.setWearable(ItemStack.EMPTY);
			}
		}
	}
	
	private static boolean tryPlace(World world, EntityPlayer player, ItemStack stack)
	{
		if(stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		
		int x = player.getPosition().getX();
		int y = player.getPosition().getY();
		int z = player.getPosition().getZ();
		BlockPos targetPos = new BlockPos(x,y,z);
		
		if(world.isAirBlock(targetPos.offset(EnumFacing.DOWN)))
		{
			return false;
		}
		
		if(world.getBlockState(targetPos).getMaterial().isReplaceable())
		{
			if(!world.getBlockState(targetPos).getMaterial().isSolid())
			{
				world.setBlockState(targetPos, ModBlocks.TRAVELLERS_BACKPACK.getDefaultState());
				world.playSound(player, x, y, z, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);
				((TileEntityTravellersBackpack)world.getTileEntity(targetPos)).loadAllData(stack.getTagCompound());
				((TileEntityTravellersBackpack)world.getTileEntity(targetPos)).setColorFromMeta(stack.getMetadata());
				
				if(CapabilityUtils.isWearingBackpack(player))
				{
					IBackpack cap = CapabilityUtils.getCapability(player);
					cap.setWearable(ItemStack.EMPTY);
				}
				
				return true;
			}
		}
		else
		{
			return false;
		}
		return false;
	}
}