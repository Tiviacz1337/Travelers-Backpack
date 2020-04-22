package com.tiviacz.travellersbackpack.gui.inventory;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.network.client.SyncBackpackCapabilityMP;
import com.tiviacz.travellersbackpack.util.ItemStackUtils;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidTank;

public class InventoryTravellersBackpack implements IInventoryTravellersBackpack
{
	private FluidTank leftTank = new FluidTank(Reference.BASIC_TANK_CAPACITY);
	private FluidTank rightTank = new FluidTank(Reference.BASIC_TANK_CAPACITY);
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(Reference.INVENTORY_SIZE, ItemStack.EMPTY);
	private NonNullList<ItemStack> craftingGrid = NonNullList.<ItemStack>withSize(Reference.CRAFTING_GRID_SIZE, ItemStack.EMPTY);
	private EntityPlayer player;
	private ItemStack stack;
	private int lastTime;
	
	public InventoryTravellersBackpack(ItemStack stack, EntityPlayer player) 
	{
		this.player = player;
		this.stack = stack;
		
		this.loadAllData(this.getTagCompound(stack));
	}
	
	@Override
	public NonNullList<ItemStack> getInventory() 
	{
		return this.inventory;
	}
	
	@Override
	public NonNullList<ItemStack> getCraftingGridInventory()
	{
		return this.craftingGrid;
	}

	@Override
	public FluidTank getLeftTank() 
	{
		return this.leftTank;
	}

	@Override
	public FluidTank getRightTank() 
	{
		return this.rightTank;
	}
	
	@Override
	public void saveAllData(NBTTagCompound compound)
	{
		this.markTankDirty();
		this.saveItems(compound);
		this.saveTime(compound);
	}
	
	@Override
	public void loadAllData(NBTTagCompound compound)
	{
		this.loadTanks(compound);
		this.loadItems(compound);
		this.loadTime(compound);
	}
	
	@Override
    public void markDirty()
    {
		this.saveAllData(this.getTagCompound(this.stack));
    }
	
	@Override
	public void markTankDirty() 
	{
		this.saveTanks(this.getTagCompound(this.stack));
		this.sendPacket();
	}
	
	@Override
	public void markTimeDirty() 
	{
		this.saveTime(this.getTagCompound(this.stack));
	}

	@Override
	public void saveTanks(NBTTagCompound compound) 
	{
		compound.setTag("LeftTank", this.leftTank.writeToNBT(new NBTTagCompound()));
		compound.setTag("RightTank", this.rightTank.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void loadTanks(NBTTagCompound compound) 
	{
		this.leftTank.readFromNBT(compound.getCompoundTag("LeftTank"));
		this.rightTank.readFromNBT(compound.getCompoundTag("RightTank"));
	}
	
	@Override
	public void saveItems(NBTTagCompound compound)
	{
		ItemStackUtils.saveAllItemsBlackList(compound, inventory, craftingGrid);
	}
	
	@Override
	public void loadItems(NBTTagCompound compound)
	{
    	ItemStackUtils.loadAllItems(compound, inventory, craftingGrid);
	}
	
	@Override
	public void saveTime(NBTTagCompound compound)
	{
		compound.setInteger("LastTime", this.lastTime);
	}

	@Override
	public void loadTime(NBTTagCompound compound) 
	{
		this.lastTime = compound.getInteger("LastTime");
	}
	
	@Override
	public int getLastTime() 
	{
		return this.lastTime;
	}

	@Override
	public void setLastTime(int time) 
	{
		this.lastTime = time;
	}

	@Override
	public boolean hasTileEntity() 
	{
		return false;
	}

	@Override
	public boolean isSleepingBagDeployed() 
	{
		return false;
	}

	@Override
	public String getColor() 
	{
		return Reference.BACKPACK_NAMES[stack.getMetadata()];
	}

	@Override
	public int getSizeInventory() 
	{
		return this.inventory.size();
	}

	@Override
	public boolean isEmpty()
    {
        for(ItemStack itemstack : this.inventory)
        {
            if(!itemstack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

	@Override
	public ItemStack getStackInSlot(int index) 
	{
		return index >= 0 && index < this.inventory.size() ? this.inventory.get(index) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventory, index, count);

        if(!itemstack.isEmpty())
        {
            this.markDirty();
        }
        return itemstack;
    }

	@Override
	public ItemStack removeStackFromSlot(int index)
    {
        ItemStack itemstack = this.inventory.get(index);

        if(itemstack.isEmpty())
        {
            return ItemStack.EMPTY;
        }
        else
        {
            this.inventory.set(index, ItemStack.EMPTY);
            return itemstack;
        }
    }

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventory.set(index, stack);

        if(!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

	@Override
	public int getInventoryStackLimit() 
	{
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) 
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) 
	{
	}

	@Override
	public void closeInventory(EntityPlayer player) 
	{	
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) 
	{
		return true;
	}

	@Override
	public int getField(int id) 
	{
		return id == 0 ? leftTank.getFluidAmount() : id == 1 ? rightTank.getFluidAmount() : 0;
	}

	@Override
	public void setField(int id, int value) 
	{
		if(id == 0)
		{
			if(leftTank.getFluid() == null)
			{
				return;
			}
			leftTank.getFluid().amount = value;
		}
			
		if(id == 1)
		{
			if(rightTank.getFluid() == null)
			{
				return;
			}
			rightTank.getFluid().amount = value;
		}
	}

	@Override
	public int getFieldCount() 
	{
		return 2;
	}

	@Override
	public void clear() 
	{
		this.inventory.clear();
	}

	@Override
	public String getName() 
	{
		return "InventoryTravellersBackpack";
	}

	@Override
	public boolean hasCustomName() 
	{
		return false;
	}

	@Override
	public ITextComponent getDisplayName() 
	{
		return (this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
	}

	@Override
	public BlockPos getPosition() 
	{
		return this.player.getPosition();
	}
	
	@Override
	public boolean updateTankSlots()
    {
		return InventoryActions.transferContainerTank(this, getLeftTank(), Reference.BUCKET_IN_LEFT, player) || InventoryActions.transferContainerTank(this, getRightTank(), Reference.BUCKET_IN_RIGHT, player);
    }
	
	public void sendPacket()
	{
		if(!player.world.isRemote)
		{
			TravellersBackpack.NETWORK.sendToAllTracking(new SyncBackpackCapabilityMP(stack.writeToNBT(new NBTTagCompound()), player.getEntityId()), player);
		}
	}
	
	@Override
	public NBTTagCompound getTagCompound(ItemStack stack)
	{
		if(stack.getTagCompound() == null)
		{
			NBTTagCompound tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
	    	
		return stack.getTagCompound();
	}
}