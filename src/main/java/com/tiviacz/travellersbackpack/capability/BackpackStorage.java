package com.tiviacz.travellersbackpack.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class BackpackStorage implements IStorage<IBackpack>
{
    @Override
    public NBTBase writeNBT(Capability<IBackpack> capability, IBackpack instance, EnumFacing side)
    {
    	NBTTagCompound tag = new NBTTagCompound();

    	if(instance.hasWearable())
    	{
    		ItemStack wearable = instance.getWearable();
    		wearable.writeToNBT(tag);
    	}
    	if(!instance.hasWearable())
    	{
    		ItemStack wearable = ItemStack.EMPTY;
    		wearable.writeToNBT(tag);
    	}
    	
        return (NBTBase)tag;
    }

    @Override
    public void readNBT(Capability<IBackpack> capability, IBackpack instance, EnumFacing side, NBTBase nbt)
    {
    	NBTTagCompound stackTag = (NBTTagCompound)nbt;
    	ItemStack wearable = new ItemStack(stackTag);
        instance.setWearable(wearable);
    }
}