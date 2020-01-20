package com.tiviacz.travellersbackpack.tileentity;

import com.tiviacz.travellersbackpack.blocks.BlockSleepingBag;
import com.tiviacz.travellersbackpack.blocks.BlockTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.container.ContainerTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.inventory.IInventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.gui.inventory.InventoryActions;
import com.tiviacz.travellersbackpack.init.ModBlocks;
import com.tiviacz.travellersbackpack.init.ModItems;
import com.tiviacz.travellersbackpack.util.BackpackUtils;
import com.tiviacz.travellersbackpack.util.ItemStackUtils;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;

public class TileEntityTravellersBackpack extends TileEntity implements IInventoryTravellersBackpack
{
	private final NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(Reference.INVENTORY_SIZE, ItemStack.EMPTY);
	private final NonNullList<ItemStack> craftingGrid = NonNullList.<ItemStack>withSize(Reference.CRAFTING_GRID_SIZE, ItemStack.EMPTY);
	private final FluidTank leftTank = new FluidTank(Reference.BASIC_TANK_CAPACITY);
	private final FluidTank rightTank = new FluidTank(Reference.BASIC_TANK_CAPACITY);
	private boolean isSleepingBagDeployed = false;
	private String color = "null";

	public TileEntityTravellersBackpack() {}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
		super.writeToNBT(compound);
		this.saveAllData(compound);
        return compound;
    }
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
    {
		super.readFromNBT(compound);
		this.loadAllData(compound);
    }
	
	public boolean isSleepingBagDeployed()
	{
		return this.isSleepingBagDeployed;
	}
	
	public void setSleepingBagDeployed(boolean deployed)
	{
		this.isSleepingBagDeployed = deployed;
	}
	
	public boolean deploySleepingBag(World world, BlockPos pos)
    {
		EnumFacing blockFacing = this.getBlockFacing(world.getTileEntity(getPos()));
		this.isThereSleepingBag(blockFacing);
		
		if(!this.isSleepingBagDeployed)
		{
	        BlockPos sleepingBagPos1 = pos.offset(blockFacing);
	        BlockPos sleepingBagPos2 = sleepingBagPos1.offset(blockFacing);
	        
	        if(world.isAirBlock(sleepingBagPos2) && world.isAirBlock(sleepingBagPos1))
	        {
	        	if(world.getBlockState(sleepingBagPos1.down()).isSideSolid(world, sleepingBagPos1.down(), EnumFacing.UP) && world.getBlockState(sleepingBagPos2.down()).isSideSolid(world, sleepingBagPos2.down(), EnumFacing.UP))
	        	{
	        		world.playSound(null, sleepingBagPos2, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);
	        		
	        		if(!world.isRemote)
	        		{
	        			world.setBlockState(sleepingBagPos1, ModBlocks.SLEEPING_BAG_BOTTOM.getDefaultState().withProperty(BlockSleepingBag.FACING, blockFacing));
	            		world.setBlockState(sleepingBagPos2, ModBlocks.SLEEPING_BAG_TOP.getDefaultState().withProperty(BlockSleepingBag.FACING, blockFacing));
	
	            		world.notifyNeighborsRespectDebug(pos, ModBlocks.SLEEPING_BAG_BOTTOM, false);
	            		world.notifyNeighborsRespectDebug(sleepingBagPos2, ModBlocks.SLEEPING_BAG_TOP, false);
	        		}
	        		
	        		this.isSleepingBagDeployed = true;
	        		this.markDirty();
	        		return true;
	        	}
	        }
		}
		return false;
    }
	
	public boolean removeSleepingBag(World world)
	{
		EnumFacing blockFacing = this.getBlockFacing(world.getTileEntity(getPos()));
		
		this.isThereSleepingBag(blockFacing);
		
		if(this.isSleepingBagDeployed)
		{
			BlockPos sleepingBagPos1 = pos.offset(blockFacing);
			BlockPos sleepingBagPos2 = sleepingBagPos1.offset(blockFacing);
					
			if(world.getBlockState(sleepingBagPos1).getBlock() == ModBlocks.SLEEPING_BAG_BOTTOM && world.getBlockState(sleepingBagPos2).getBlock() == ModBlocks.SLEEPING_BAG_TOP)
			{
				world.playSound(null, sleepingBagPos2, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);
				world.setBlockToAir(sleepingBagPos1);
				world.setBlockToAir(sleepingBagPos2);
				this.isSleepingBagDeployed = false;
				this.markDirty();
				return true;
			}
		}
		else
		{
			this.isSleepingBagDeployed = false;
			this.markDirty();
			return true;
		}
		return false;
	}
	
	public boolean isThereSleepingBag(EnumFacing facing)
	{
		if(world.getBlockState(pos.offset(facing)).getBlock() == ModBlocks.SLEEPING_BAG_BOTTOM && world.getBlockState(pos.offset(facing).offset(facing)).getBlock() == ModBlocks.SLEEPING_BAG_TOP)
		{
			return true;
		}
		else
		{
			this.isSleepingBagDeployed = false;
			return false;
		}
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
		this.saveTanks(compound);
		this.saveItems(compound);
		this.saveSleepingBag(compound);
		this.saveColor(compound);
	}
	
	@Override
	public void loadAllData(NBTTagCompound compound)
	{
		this.loadTanks(compound);
		this.loadItems(compound);
		this.loadSleepingBag(compound);
		this.loadColor(compound);
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
	
	public void saveSleepingBag(NBTTagCompound compound)
	{
		compound.setBoolean("isSleepingBagDeployed", this.isSleepingBagDeployed);
	}
	
	public void loadSleepingBag(NBTTagCompound compound)
	{
		if(compound.hasKey("isSleepingBagDeployed"))
		{
			this.isSleepingBagDeployed = compound.getBoolean("isSleepingBagDeployed");
		}
	}
	
	public String getColorFromMeta(int meta)
	{
		return Reference.BACKPACK_NAMES[meta];
	}
	
	public void setColorFromMeta(int meta)
	{
		this.color = Reference.BACKPACK_NAMES[meta];
	}
	
	@Override
	public String getColor()
	{
		if(this.color != null)
		{
			return this.color;
		}
		return "Standard";
	}
	
	public void saveColor(NBTTagCompound compound)
	{
		compound.setString("Color", color);
	}
	
	public void loadColor(NBTTagCompound compound)
	{
		if(compound.hasKey("Color"))
		{
			this.color = compound.getString("Color");
		}
	}
	
	@Override
	public void saveItems(NBTTagCompound compound) 
	{
		ItemStackUtils.saveAllItems(compound, inventory, craftingGrid);
	}

	@Override
	public void loadItems(NBTTagCompound compound)
	{
		ItemStackUtils.loadAllItems(compound, inventory, craftingGrid);
	}
	
	public boolean drop(World world, EntityPlayer player, int x, int y, int z)
    {
        if(player.capabilities.isCreativeMode)
        {
        	return true;
        }
        
        ItemStack stack = new ItemStack(ModItems.TRAVELLERS_BACKPACK, 1, BackpackUtils.convertNameToMeta(this.color));
        transferToItemStack(stack);
        EntityItem droppedItem = new EntityItem(world, x, y, z, stack);

        return world.spawnEntity(droppedItem);
    }
	
	public ItemStack transferToItemStack(ItemStack stack)
    {
        NBTTagCompound compound = new NBTTagCompound();
        saveTanks(compound);
        saveItems(compound);
        stack.setTagCompound(compound);
        return stack;
    }
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
	    return new SPacketUpdateTileEntity(this.getPos(), 3, this.getUpdateTag());
	}
	
	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		super.onDataPacket(net, pkt);
		this.handleUpdateTag(pkt.getNbtCompound());
	}
	
	private void notifyBlockUpdate() 
	{
		IBlockState state = getWorld().getBlockState(pos);
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyBlockUpdate(pos, state, state, 3);
	} 

	@Override
	public void markDirty() 
	{
		super.markDirty();
		notifyBlockUpdate();
	} 

	@Override
	public boolean updateTankSlots() 
	{
		return InventoryActions.transferContainerTank(this, getLeftTank(), Reference.BUCKET_IN_LEFT, getUsingPlayer()) || InventoryActions.transferContainerTank(this, getRightTank(), Reference.BUCKET_IN_RIGHT, getUsingPlayer());
    }
	
	private EntityPlayer getUsingPlayer()
	{
		int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
       
        if(!world.isRemote)
        {
        	for(EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)i - 5.0F), (double)((float)j - 5.0F), (double)((float)k - 5.0F), (double)((float)(i + 1) + 5.0F), (double)((float)(j + 1) + 5.0F), (double)((float)(k + 1) + 5.0F))))
            {
        		if(entityplayer.openContainer instanceof ContainerTravellersBackpack)
        		{
        			return entityplayer;
        		}
            }
        }
		return null;
	}
	
	public EnumFacing getBlockFacing(TileEntity tile)
	{
		if(tile instanceof TileEntityTravellersBackpack)
		{
			return world.getBlockState(getPos()).getValue(BlockTravellersBackpack.FACING);
		}
		return EnumFacing.NORTH;
	}

	@Override
	public void markTankDirty() {}
	
	public NonNullList<ItemStack> getInventory()
	{
		return this.inventory;
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
		if(this.world.getTileEntity(this.pos) != this)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
	}

	@Override
	public void openInventory(EntityPlayer player) 
	{
		
	}

	@Override
	public void closeInventory(EntityPlayer player) 
	{
		this.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) 
	{
		return true;
	}

	@Override
	public int getField(int id) 
	{
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() 
	{
		return 0;
	}

	@Override
	public void clear() 
	{
		this.inventory.clear();
	}

	@Override
	public String getName() 
	{
		return "TileInventoryTravellersBackpack";
	}

	@Override
	public boolean hasCustomName() 
	{
		return false;
	}

	@Override
	public NBTTagCompound getTagCompound(ItemStack stack) 
	{
		return null;
	}

	@Override
	public boolean hasTileEntity() 
	{
		return true;
	}

	@Override
	public NonNullList<ItemStack> getCraftingGridInventory() 
	{
		return this.craftingGrid;
	}

	@Override
	public BlockPos getPosition() 
	{
		return this.getPos();
	}
}