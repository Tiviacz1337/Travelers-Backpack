package com.tiviacz.travellersbackpack.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ItemStackUtils 
{
	public static NBTTagCompound saveAllItems(NBTTagCompound tag, NonNullList<ItemStack> inventory, NonNullList<ItemStack> craftingGrid)
    {
        NBTTagList nbttaglist = new NBTTagList();

        for(int i = 0; i < inventory.size(); ++i)
        {
            ItemStack itemstack = inventory.get(i);

            if(i >= 0 && i <= 8)
    		{
    			ItemStack gridStack = craftingGrid.get(i);
    			
    			if(!gridStack.isEmpty())
    			{
    				NBTTagCompound slotTag = new NBTTagCompound();
        			slotTag.setByte("Slot", (byte)i);
        			gridStack.writeToNBT(slotTag);
        			nbttaglist.appendTag(slotTag);
    			}
    		}
            
            else if(!itemstack.isEmpty() && i >= 9)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }
        
        tag.setTag("Items", nbttaglist);

        return tag;
    }
	
	public static NBTTagCompound saveAllItemsBlackList(NBTTagCompound tag, NonNullList<ItemStack> inventory, NonNullList<ItemStack> craftingGrid)
    {
		List<Integer> intList = new ArrayList<Integer>();
		intList.add(Reference.BUCKET_IN_LEFT);
		intList.add(Reference.BUCKET_IN_RIGHT);
		intList.add(Reference.BUCKET_OUT_LEFT);
		intList.add(Reference.BUCKET_OUT_RIGHT);
		
        NBTTagList nbttaglist = new NBTTagList();

        for(int i = 0; i < inventory.size(); ++i)
        {
            ItemStack itemstack = inventory.get(i);

            if(i >= 0 && i <= 8)
    		{
    			ItemStack gridStack = craftingGrid.get(i);
    			
    			if(!gridStack.isEmpty())
    			{
    				NBTTagCompound slotTag = new NBTTagCompound();
        			slotTag.setByte("Slot", (byte)i);
        			gridStack.writeToNBT(slotTag);
        			nbttaglist.appendTag(slotTag);
    			}
    		}
            
            else if((!itemstack.isEmpty() && !intList.contains(i)) && i >= 9)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }
        
        tag.setTag("Items", nbttaglist);

        return tag;
    }

    public static void loadAllItems(NBTTagCompound tag, NonNullList<ItemStack> inventory, NonNullList<ItemStack> craftingGrid)
    {
        NBTTagList nbttaglist = tag.getTagList("Items", 10);

        for(int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound entry = nbttaglist.getCompoundTagAt(i);
            int index = entry.getByte("Slot") & 255;

            if(index >= 0 && index < inventory.size())
            {
            	if(index >= 0 && index <= 8)
    			{
    				craftingGrid.set(index, new ItemStack(entry));
    			}
            	else
            	{
            		inventory.set(index, new ItemStack(entry));
            	}
            }
        }
    }
}