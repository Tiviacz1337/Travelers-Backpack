package com.tiviacz.travellersbackpack.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class BackpackProvider implements ICapabilitySerializable<NBTBase> 
{
	private final BackpackWearable backpack;
	
	public BackpackProvider(EntityPlayer player)
	{
		this.backpack = new BackpackWearable(player);
	}
	
	@CapabilityInject(IBackpack.class)
	public static Capability<IBackpack> BACKPACK_CAP = null;
	
	private IBackpack instance = BACKPACK_CAP.getDefaultInstance(); 
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) 
	{
		return capability == BACKPACK_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) 
	{
		return capability == BACKPACK_CAP ? BACKPACK_CAP.<T> cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT() 
	{
		return BACKPACK_CAP.getStorage().writeNBT(BACKPACK_CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) 
	{
		BACKPACK_CAP.readNBT(this.instance, null, nbt);
	}
}