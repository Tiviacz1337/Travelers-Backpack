package com.tiviacz.travelersbackpack.tileentity;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModTileEntityTypes;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
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

public class TravelersBackpackTileEntity extends TileEntity implements ITravelersBackpackInventory, INamedContainerProvider, INameable, ITickableTileEntity
{
    private final ItemStackHandler inventory = createHandler(Reference.INVENTORY_SIZE);
    private final ItemStackHandler craftingInventory = createHandler(Reference.CRAFTING_GRID_SIZE);
    private final FluidTank leftTank = createFluidHandler(TravelersBackpackConfig.SERVER.tanksCapacity.get());
    private final FluidTank rightTank = createFluidHandler(TravelersBackpackConfig.SERVER.tanksCapacity.get());
    private boolean isSleepingBagDeployed = false;
    private int color = 0;
    private boolean ability = true;
    private int lastTime = 0;
    private ITextComponent customName = null;

    private final LazyOptional<IItemHandlerModifiable> inventoryCapability = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 39));
    private final LazyOptional<ItemStackHandler> craftingInventoryCapability = LazyOptional.of(() -> this.craftingInventory);
    private final LazyOptional<IFluidHandler> leftFluidTankCapability = LazyOptional.of(() -> this.leftTank);
    private final LazyOptional<IFluidHandler> rightFluidTankCapability = LazyOptional.of(() -> this.rightTank);

    private final String INVENTORY = "Inventory";
    private final String CRAFTING_INVENTORY = "CraftingInventory";
    private final String LEFT_TANK = "LeftTank";
    private final String RIGHT_TANK = "RightTank";
    private final String SLEEPING_BAG = "SleepingBag";
    private final String COLOR = "Color";
    private final String LAST_TIME = "LastTime";
    private final String ABILITY = "Ability";
    private final String CUSTOM_NAME = "CustomName";

    public TravelersBackpackTileEntity()
    {
        super(ModTileEntityTypes.TRAVELERS_BACKPACK.get());
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
        super.save(compound);
        this.saveAllData(compound);
        return compound;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
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

    @Override
    public void saveColor(CompoundNBT compound)
    {
        compound.putInt(COLOR, this.color);
    }

    @Override
    public void loadColor(CompoundNBT compound)
    {
        this.color = compound.getInt(COLOR);
    }

    @Override
    public void saveAbility(CompoundNBT compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void loadAbility(CompoundNBT compound)
    {
        this.ability = compound.getBoolean(ABILITY);
    }

    public PlayerEntity getUsingPlayer()
    {
        for(PlayerEntity player : this.level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(getBlockPos()).expandTowards(3.0, 3.0, 3.0)))
        {
            if(player.containerMenu instanceof TravelersBackpackTileContainer)
            {
                return player;
            }
        }
        return null;
    }

    public boolean isUsableByPlayer(PlayerEntity player)
    {
        if(this.level.getBlockEntity(this.getBlockPos()) != this)
        {
            return false;
        }
        else
        {
            return player.distanceToSqr((double)this.getBlockPos().getX() + 0.5D, (double)this.getBlockPos().getY() + 0.5D, (double)this.getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }


    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), Reference.BUCKET_IN_LEFT, getUsingPlayer()) || InventoryActions.transferContainerTank(this, getRightTank(), Reference.BUCKET_IN_RIGHT, getUsingPlayer());
    }

    @Override
    public void markTankDirty() { }

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

    public void saveName(CompoundNBT compound)
    {
        if(this.customName != null)
        {
            compound.putString(CUSTOM_NAME, ITextComponent.Serializer.toJson(this.customName));
        }
    }

    public void loadName(CompoundNBT compound)
    {
        if(compound.contains(CUSTOM_NAME, 8))
        {
            this.customName = ITextComponent.Serializer.fromJson(compound.getString(CUSTOM_NAME));
        }
    }

    @Override
    public void saveAllData(CompoundNBT compound)
    {
        this.saveTanks(compound);
        this.saveItems(compound);
        this.saveSleepingBag(compound);
        this.saveTime(compound);
        this.saveColor(compound);
        this.saveName(compound);
        this.saveAbility(compound);
    }

    @Override
    public void loadAllData(CompoundNBT compound)
    {
        this.loadTanks(compound);
        this.loadItems(compound);
        this.loadSleepingBag(compound);
        this.loadTime(compound);
        this.loadColor(compound);
        this.loadName(compound);
        this.loadAbility(compound);
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
            this.setChanged();
        }
        return stack;
    }

    @Override
    public boolean hasTileEntity()
    {
        return true;
    }

    @Override
    public boolean hasColor()
    {
        return this.color != 0;
    }

    @Override
    public boolean getAbilityValue()
    {
        return this.ability;
    }

    @Override
    public void setAbility(boolean value)
    {
        this.ability = value;
        this.setChanged();
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
        return this.getBlockPos();
    }

    @Override
    public int getColor()
    {
        return this.color;
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
    public void markLastTimeDirty() {}

    @Override
    public byte getScreenID()
    {
        return Reference.TRAVELERS_BACKPACK_TILE_SCREEN_ID;
    }

    @Override
    public ItemStack getItemStack()
    {
        Block block = level.getBlockState(getBlockPos()).getBlock();

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
        Direction direction = this.getBlockDirection(world.getBlockEntity(getBlockPos()));
        this.isThereSleepingBag(direction);

        if(!this.isSleepingBagDeployed)
        {
            BlockPos sleepingBagPos1 = pos.relative(direction);
            BlockPos sleepingBagPos2 = sleepingBagPos1.relative(direction);

            if(world.isEmptyBlock(sleepingBagPos2) && world.isEmptyBlock(sleepingBagPos1))
            {
                if(world.getBlockState(sleepingBagPos1.below()).isFaceSturdy(world, sleepingBagPos1.below(), Direction.UP) && world.getBlockState(sleepingBagPos2.below()).isFaceSturdy(world, sleepingBagPos2.below(), Direction.UP))
                {
                    world.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);

                    if(!world.isClientSide)
                    {
                        world.setBlockAndUpdate(sleepingBagPos1, ModBlocks.SLEEPING_BAG.get().defaultBlockState().setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.FOOT));
                        world.setBlockAndUpdate(sleepingBagPos2, ModBlocks.SLEEPING_BAG.get().defaultBlockState().setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.HEAD));

                        world.updateNeighborsAt(pos, ModBlocks.SLEEPING_BAG.get());
                        world.updateNeighborsAt(sleepingBagPos2, ModBlocks.SLEEPING_BAG.get());
                    }

                    this.isSleepingBagDeployed = true;
                    this.setChanged();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeSleepingBag(World world)
    {
        Direction blockFacing = this.getBlockDirection(world.getBlockEntity(getBlockPos()));

        this.isThereSleepingBag(blockFacing);

        if(this.isSleepingBagDeployed)
        {
            BlockPos sleepingBagPos1 = getBlockPos().relative(blockFacing);
            BlockPos sleepingBagPos2 = sleepingBagPos1.relative(blockFacing);

            if(world.getBlockState(sleepingBagPos1).getBlock() == ModBlocks.SLEEPING_BAG.get() && world.getBlockState(sleepingBagPos2).getBlock() == ModBlocks.SLEEPING_BAG.get())
            {
                world.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);
                world.setBlockAndUpdate(sleepingBagPos2, Blocks.AIR.defaultBlockState());
                world.setBlockAndUpdate(sleepingBagPos1, Blocks.AIR.defaultBlockState());
                this.isSleepingBagDeployed = false;
                this.setChanged();
                return true;
            }
        }
        else
        {
            this.isSleepingBagDeployed = false;
            this.setChanged();
            return true;
        }
        return false;
    }

    public boolean isThereSleepingBag(Direction direction)
    {
        if(level.getBlockState(getBlockPos().relative(direction)).getBlock() == ModBlocks.SLEEPING_BAG.get() && level.getBlockState(getBlockPos().relative(direction).relative(direction)).getBlock() == ModBlocks.SLEEPING_BAG.get())
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
            if(level == null || !(level.getBlockState(getBlockPos()).getBlock() instanceof TravelersBackpackBlock))
            {
                return Direction.NORTH;
            }
            return level.getBlockState(getBlockPos()).getValue(TravelersBackpackBlock.FACING);
        }
        return Direction.NORTH;
    }

    public boolean drop(World world, BlockPos pos, Item item)
    {
        ItemStack stack = new ItemStack(item, 1);
        transferToItemStack(stack);
        if(this.hasCustomName())
        {
            stack.setHoverName(this.getCustomName());
        }
        ItemEntity droppedItem = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);

        return world.addFreshEntity(droppedItem);
    }

    public ItemStack transferToItemStack(ItemStack stack)
    {
        CompoundNBT compound = new CompoundNBT();
        saveTanks(compound);
        saveItems(compound);
        saveTime(compound);
        if(this.hasColor()) this.saveColor(compound);
        saveAbility(compound);
        stack.setTag(compound);
        return stack;
    }

    @Override
    public ITextComponent getName()
    {
        return this.customName != null ? this.customName : this.getDisplayName();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName()
    {
        return this.customName;
    }

    public void setCustomName(ITextComponent customName)
    {
        this.customName = customName;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public void setChanged()
    {
        if(!level.isClientSide)
        {
            super.setChanged();
            notifyBlockUpdate();
        }
    }

    private void notifyBlockUpdate()
    {
        BlockState blockstate = getLevel().getBlockState(getBlockPos());
        level.setBlocksDirty(getBlockPos(), blockstate, blockstate);
        level.sendBlockUpdated(getBlockPos(), blockstate, blockstate, 3);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.getBlockPos(), 3, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        super.onDataPacket(net, pkt);
        this.handleUpdateTag(level.getBlockState(pkt.getPos()), pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.save(new CompoundNBT());
    }

    public void openGUI(PlayerEntity player, INamedContainerProvider containerSupplier, BlockPos pos)
    {
        if(!player.level.isClientSide && getUsingPlayer() == null)
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

    private ItemStackHandler createHandler(int size)
    {
        return new ItemStackHandler(size)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                ResourceLocation blacklistedItems = new ResourceLocation(TravelersBackpack.MODID, "blacklisted_items");

                return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.getItem().is(ItemTags.getAllTags().getTag(blacklistedItems));
            }
        };
    }

    private FluidTank createFluidHandler(int capacity)
    {
        return new FluidTank(capacity)
        {
            @Override
            protected void onContentsChanged()
            {
                setChanged();
            }
        };
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
                switch (side)
                {
                    case WEST: return rightFluidTankCapability.cast();
                    case EAST: return leftFluidTankCapability.cast();
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
    public void invalidateCaps()
    {
        super.invalidateCaps();
        inventoryCapability.invalidate();
        craftingInventoryCapability.invalidate();
        leftFluidTankCapability.invalidate();
        rightFluidTankCapability.invalidate();
    }

    @Override
    public void tick()
    {
        if(getAbilityValue() && BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, getItemStack()))
        {
            if(getLastTime() > 0)
            {
                setLastTime(getLastTime() - 1);
                setChanged();
            }

            BackpackAbilities.ABILITIES.abilityTick(null, null, this);
        }
    }
}