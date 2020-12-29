package com.tiviacz.travelersbackpack.tileentity;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModTileEntityTypes;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelersBackpackTileEntity extends TileEntity implements ITravelersBackpack, INamedContainerProvider
{
    private final ItemStackHandler inventory = new ItemStackHandler(Reference.INVENTORY_SIZE)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
            return !(stack.getItem() instanceof TravelersBackpackItem);
        }
    };
    private final ItemStackHandler craftingInventory = new ItemStackHandler(Reference.CRAFTING_GRID_SIZE)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
            return !(stack.getItem() instanceof TravelersBackpackItem);
        }
    };
    private final FluidTank leftTank = new FluidTank(TravelersBackpackConfig.COMMON.tanksCapacity.get());
    private final FluidTank rightTank = new FluidTank(TravelersBackpackConfig.COMMON.tanksCapacity.get());
    private boolean isSleepingBagDeployed = false;
    private int lastTime = 0;

    private final LazyOptional<IItemHandlerModifiable> inventoryCapability = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 39));
    private final LazyOptional<ItemStackHandler> craftingInventoryCapability = LazyOptional.of(() -> this.craftingInventory);
    private final LazyOptional<IFluidHandler> leftFluidTankCapability = LazyOptional.of(() -> this.leftTank);
    private final LazyOptional<IFluidHandler> rightFluidTankCapability = LazyOptional.of(() -> this.rightTank);

    private final String INVENTORY = "Inventory";
    private final String CRAFTING_INVENTORY = "CraftingInventory";
    private final String LEFT_TANK = "LeftTank";
    private final String RIGHT_TANK = "RightTank";
    private final String SLEEPING_BAG = "SleepingBag";
    private final String LAST_TIME = "LastTime";

    public TravelersBackpackTileEntity()
    {
        super(ModTileEntityTypes.TRAVELERS_BACKPACK.get());
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        this.saveAllData(compound);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        this.loadAllData(nbt);
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
    public void saveTanks(CompoundNBT compound)
    {
        compound.put(LEFT_TANK, this.leftTank.writeToNBT(new CompoundNBT()));
        compound.put(RIGHT_TANK, this.rightTank.writeToNBT(new CompoundNBT()));
    }

    @Override
    public void loadTanks(CompoundNBT compound)
    {
        this.leftTank.readFromNBT(compound.getCompound(LEFT_TANK));
        this.rightTank.readFromNBT(compound.getCompound(RIGHT_TANK));
    }

    public PlayerEntity getUsingPlayer()
    {
        for(PlayerEntity player : this.world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(getPos()).grow(3.0, 3.0, 3.0)))
        {
            if(player.openContainer instanceof TravelersBackpackTileContainer)
            {
                return player;
            }
        }
        return null;
    }

    public boolean isUsableByPlayer(PlayerEntity player)
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
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), Reference.BUCKET_IN_LEFT, getUsingPlayer()) || InventoryActions.transferContainerTank(this, getRightTank(), Reference.BUCKET_IN_RIGHT, getUsingPlayer());
    }

    @Override
    public void markTankDirty()
    {

    }

    @Override
    public void saveItems(CompoundNBT compound)
    {
        compound.put(INVENTORY, this.inventory.serializeNBT());
        compound.put(CRAFTING_INVENTORY, this.craftingInventory.serializeNBT());
    }

    @Override
    public void loadItems(CompoundNBT compound)
    {
        this.inventory.deserializeNBT(compound.getCompound(INVENTORY));
        this.craftingInventory.deserializeNBT(compound.getCompound(CRAFTING_INVENTORY));
    }

    @Override
    public void saveTime(CompoundNBT compound)
    {
        compound.putInt(LAST_TIME, this.lastTime);
    }

    @Override
    public void loadTime(CompoundNBT compound)
    {
        this.lastTime = compound.getInt(LAST_TIME);
    }

    public void saveSleepingBag(CompoundNBT compound)
    {
        compound.putBoolean(SLEEPING_BAG, this.isSleepingBagDeployed);
    }

    public void loadSleepingBag(CompoundNBT compound)
    {
        this.isSleepingBagDeployed = compound.getBoolean(SLEEPING_BAG);
    }

    @Override
    public void saveAllData(CompoundNBT compound)
    {
        this.saveTanks(compound);
        this.saveItems(compound);
        this.saveSleepingBag(compound);
        this.saveTime(compound);
    }

    @Override
    public void loadAllData(CompoundNBT compound)
    {
        this.loadTanks(compound);
        this.loadItems(compound);
        this.loadSleepingBag(compound);
        this.loadTime(compound);
    }

    @Override
    public CompoundNBT getTagCompound(ItemStack stack)
    {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack stack = ItemStackUtils.getAndSplit(this.inventory, index, count);

        if(!stack.isEmpty())
        {
            this.markDirty();
        }
        return stack;
    }

    @Override
    public boolean hasTileEntity()
    {
        return true;
    }

    @Override
    public boolean isSleepingBagDeployed()
    {
        return this.isSleepingBagDeployed;
    }

    @Override
    public ItemStackHandler getInventory()
    {
        return this.inventory;
    }

    @Override
    public ItemStackHandler getCraftingGridInventory()
    {
        return this.craftingInventory;
    }

    @Override
    public BlockPos getPosition()
    {
        return this.getPos();
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
    public byte getScreenID()
    {
        return Reference.TRAVELERS_BACKPACK_TILE_SCREEN_ID;
    }

    @Override
    public ItemStack getItemStack()
    {
        Block block = world.getBlockState(getPos()).getBlock();

        if(block instanceof TravelersBackpackBlock)
        {
            return new ItemStack(block);
        }
        return new ItemStack(ModBlocks.STANDARD_TRAVELERS_BACKPACK.get());
    }

    public void setSleepingBagDeployed(boolean isSleepingBagDeployed)
    {
        this.isSleepingBagDeployed = isSleepingBagDeployed;
    }

    public boolean deploySleepingBag(World world, BlockPos pos)
    {
        Direction direction = this.getBlockDirection(world.getTileEntity(getPos()));
        this.isThereSleepingBag(direction);

        if(!this.isSleepingBagDeployed)
        {
            BlockPos sleepingBagPos1 = pos.offset(direction);
            BlockPos sleepingBagPos2 = sleepingBagPos1.offset(direction);

            if(world.isAirBlock(sleepingBagPos2) && world.isAirBlock(sleepingBagPos1))
            {
                if(world.getBlockState(sleepingBagPos1.down()).isSolidSide(world, sleepingBagPos1.down(), Direction.UP) && world.getBlockState(sleepingBagPos2.down()).isSolidSide(world, sleepingBagPos2.down(), Direction.UP))
                {
                    world.playSound(null, sleepingBagPos2, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);

                    if(!world.isRemote)
                    {
                        world.setBlockState(sleepingBagPos1, ModBlocks.SLEEPING_BAG.get().getDefaultState().with(SleepingBagBlock.HORIZONTAL_FACING, direction).with(SleepingBagBlock.PART, BedPart.FOOT));
                        world.setBlockState(sleepingBagPos2, ModBlocks.SLEEPING_BAG.get().getDefaultState().with(SleepingBagBlock.HORIZONTAL_FACING, direction).with(SleepingBagBlock.PART, BedPart.HEAD));

                        world.notifyNeighborsOfStateChange(pos, ModBlocks.SLEEPING_BAG.get());
                        world.notifyNeighborsOfStateChange(sleepingBagPos2, ModBlocks.SLEEPING_BAG.get());
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
        Direction blockFacing = this.getBlockDirection(world.getTileEntity(getPos()));

        this.isThereSleepingBag(blockFacing);

        if(this.isSleepingBagDeployed)
        {
            BlockPos sleepingBagPos1 = pos.offset(blockFacing);
            BlockPos sleepingBagPos2 = sleepingBagPos1.offset(blockFacing);

            if(world.getBlockState(sleepingBagPos1).getBlock() == ModBlocks.SLEEPING_BAG.get() && world.getBlockState(sleepingBagPos2).getBlock() == ModBlocks.SLEEPING_BAG.get())
            {
                world.playSound(null, sleepingBagPos2, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);
                world.setBlockState(sleepingBagPos2, Blocks.AIR.getDefaultState());
                world.setBlockState(sleepingBagPos1, Blocks.AIR.getDefaultState());
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

    public boolean isThereSleepingBag(Direction direction)
    {
        if(world.getBlockState(pos.offset(direction)).getBlock() == ModBlocks.SLEEPING_BAG.get() && world.getBlockState(pos.offset(direction).offset(direction)).getBlock() == ModBlocks.SLEEPING_BAG.get())
        {
            return true;
        }
        else
        {
            this.isSleepingBagDeployed = false;
            return false;
        }
    }

    public Direction getBlockDirection(TileEntity tile)
    {
        if(tile instanceof TravelersBackpackTileEntity)
        {
            if(world == null || !(world.getBlockState(getPos()).getBlock() instanceof TravelersBackpackBlock))
            {
                return Direction.NORTH;
            }
            return world.getBlockState(getPos()).get(TravelersBackpackBlock.FACING);
        }
        return Direction.NORTH;
    }

    public boolean drop(World world, BlockPos pos, Item item)
    {
      //  if(player.isCreative())
      //  {
      //      return true;
      //  }

        ItemStack stack = new ItemStack(item, 1);
        transferToItemStack(stack);
        ItemEntity droppedItem = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        droppedItem.setNoDespawn();

        return world.addEntity(droppedItem);
    }

    public ItemStack transferToItemStack(ItemStack stack)
    {
        CompoundNBT compound = new CompoundNBT();
        saveTanks(compound);
        saveItems(compound);
        saveTime(compound);
        stack.setTag(compound);
        return stack;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("screen.travelersbackpack.tile");
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        notifyBlockUpdate();
    }

    private void notifyBlockUpdate()
    {
        BlockState blockstate = getWorld().getBlockState(pos);
        world.markBlockRangeForRenderUpdate(pos, blockstate, blockstate);
        world.notifyBlockUpdate(pos, blockstate, blockstate, 3);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.getPos(), 3, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        super.onDataPacket(net, pkt);
        this.handleUpdateTag(world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }

    public void openGUI(PlayerEntity player, INamedContainerProvider containerSupplier, BlockPos pos)
    {
        if(!player.world.isRemote)
        {
            NetworkHooks.openGui((ServerPlayerEntity)player, containerSupplier, pos);
        }
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity playerEntity)
    {
        return new TravelersBackpackTileContainer(id, inventory, this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, @Nullable final Direction side)
    {
        Direction direction = getBlockDirection(getTileEntity());
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if(side == null)
            {
                return inventoryCapability.cast();
            }
            switch(side)
            {
                case DOWN:
                case UP:
                    return inventoryCapability.cast();
                case NORTH:
                case SOUTH:
                case WEST:
                case EAST:
                    if(side == direction || side == direction.getOpposite()) return craftingInventoryCapability.cast();
            }
        }
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if(side == null)
            {
                return leftFluidTankCapability.cast();
            }
            if(direction == Direction.NORTH)
            {
                switch(side)
                {
                    case WEST:
                        return rightFluidTankCapability.cast();
                    case EAST:
                        return leftFluidTankCapability.cast();
                }
            }
            if(direction == Direction.SOUTH)
            {
                switch(side)
                {
                    case EAST: return rightFluidTankCapability.cast();
                    case WEST: return leftFluidTankCapability.cast();
                }
            }

            if(direction == Direction.EAST)
            {
                switch(side)
                {
                    case NORTH: return rightFluidTankCapability.cast();
                    case SOUTH: return leftFluidTankCapability.cast();
                }
            }

            if(direction == Direction.WEST)
            {
                switch(side)
                {
                    case SOUTH: return rightFluidTankCapability.cast();
                    case NORTH: return leftFluidTankCapability.cast();
                }
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void remove()
    {
        super.remove();
        inventoryCapability.invalidate();
        craftingInventoryCapability.invalidate();
        leftFluidTankCapability.invalidate();
        rightFluidTankCapability.invalidate();
    }

  /*  @Override
    public void tick()
    {

    } */
}