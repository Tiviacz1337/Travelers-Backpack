package com.tiviacz.travellersbackpack.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class NBTUtils 
{
	public static boolean hasWearingTag(EntityPlayer player)
	{
		return player.getEntityData().hasKey("Wearable");
	}
	
	public static NBTTagCompound getWearingTag(EntityPlayer player)
	{
		return player.getEntityData().getCompoundTag("Wearable");
	}
}