package com.tiviacz.travelersbackpack.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemStackUtils
{
    public static ItemStack getAndSplit(IItemHandler inventory, int index, int amount)
    {
        return index >= 0 && index < inventory.getSlots() && !inventory.getStackInSlot(index).isEmpty() && amount > 0 ? inventory.getStackInSlot(index).split(amount) : ItemStack.EMPTY;
    }

 /*   public static CompoundNBT saveAllItems(CompoundNBT compound, NonNullList<ItemStack> inventory, NonNullList<ItemStack> craftingGrid)
    {
        ListNBT listNBT = new ListNBT();

        for(int i = 0; i < inventory.size(); ++i)
        {
            ItemStack itemstack = inventory.get(i);

            if(i <= 8)
            {
                ItemStack gridStack = craftingGrid.get(i);

                if(!gridStack.isEmpty())
                {
                    CompoundNBT slotCompound = new CompoundNBT();
                    slotCompound.putByte("Slot", (byte)i);
                    gridStack.write(slotCompound);
                    listNBT.add(slotCompound);
                }
            }

            else if(!itemstack.isEmpty())
            {
                CompoundNBT slotCompound = new CompoundNBT();
                slotCompound.putByte("Slot", (byte)i);
                itemstack.write(slotCompound);
                listNBT.add(slotCompound);
            }
        }

        compound.put("Items", listNBT);

        return compound;
    }

    public static CompoundNBT saveAllItemsBlackList(CompoundNBT compound, NonNullList<ItemStack> inventory, NonNullList<ItemStack> craftingGrid)
    {
        List<Integer> intList = new ArrayList<>();
        intList.add(Reference.BUCKET_IN_LEFT);
        intList.add(Reference.BUCKET_IN_RIGHT);
        intList.add(Reference.BUCKET_OUT_LEFT);
        intList.add(Reference.BUCKET_OUT_RIGHT);

        ListNBT listNBT = new ListNBT();

        for(int i = 0; i < inventory.size(); ++i)
        {
            ItemStack itemstack = inventory.get(i);

            if(i <= 8)
            {
                ItemStack gridStack = craftingGrid.get(i);

                if(!gridStack.isEmpty())
                {
                    CompoundNBT slotCompound = new CompoundNBT();
                    slotCompound.putByte("Slot", (byte)i);
                    gridStack.write(slotCompound);
                    listNBT.add(slotCompound);
                }
            }

            else if((!itemstack.isEmpty() && !intList.contains(i)))
            {
                CompoundNBT slotCompound = new CompoundNBT();
                slotCompound.putByte("Slot", (byte)i);
                itemstack.write(slotCompound);
                listNBT.add(slotCompound);
            }
        }

        compound.put("Items", listNBT);

        return compound;
    }

    public static void loadAllItems(CompoundNBT compound, NonNullList<ItemStack> inventory, NonNullList<ItemStack> craftingGrid) {
        ListNBT listNBT = compound.getList("Items", 10);

        for (int i = 0; i < listNBT.size(); ++i) {
            CompoundNBT entry = listNBT.getCompound(i);
            int index = entry.getByte("Slot") & 255;

            if(index < inventory.size())
            {
                if(index <= 8)
                {
                    craftingGrid.set(index, ItemStack.read(entry));
                } else {
                    inventory.set(index, ItemStack.read(entry));
                }
            }
        }
    } */
}

