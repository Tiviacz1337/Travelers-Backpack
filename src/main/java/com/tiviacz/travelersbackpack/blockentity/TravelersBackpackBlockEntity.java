package com.tiviacz.travelersbackpack.blockentity;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelersBackpackBlockEntity extends BlockEntity implements ITravelersBackpackContainer, MenuProvider, Nameable
{
    private final ItemStackHandler inventory = createHandler(Reference.INVENTORY_SIZE);
    private final ItemStackHandler craftingInventory = createHandler(Reference.CRAFTING_GRID_SIZE);
    private final FluidTank leftTank = createFluidHandler(TravelersBackpackConfig.tanksCapacity);
    private final FluidTank rightTank = createFluidHandler(TravelersBackpackConfig.tanksCapacity);
    private boolean isSleepingBagDeployed = false;
    private int color = 0;
    private boolean ability = true;
    private int lastTime = 0;
    private Component customName = null;

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
    private final String ABILITY = "Ability";
    private final String LAST_TIME = "LastTime";
    private final String CUSTOM_NAME = "CustomName";

    public TravelersBackpackBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntityTypes.TRAVELERS_BACKPACK.get(), pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag compound)
    {
        super.saveAdditional(compound);
        this.saveAllData(compound);
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        this.loadAllData(compound);
    }

    @Override
    public ItemStackHandler getHandler()
    {
        return this.inventory;
    }

    @Override
    public ItemStackHandler getCraftingGridHandler()
    {
        return this.craftingInventory;
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
    public void saveItems(CompoundTag compound)
    {
        compound.put(INVENTORY, this.inventory.serializeNBT());
        compound.put(CRAFTING_INVENTORY, this.craftingInventory.serializeNBT());
    }

    @Override
    public void loadItems(CompoundTag compound)
    {
        this.inventory.deserializeNBT(compound.getCompound(INVENTORY));
        this.craftingInventory.deserializeNBT(compound.getCompound(CRAFTING_INVENTORY));
    }

    @Override
    public void saveTanks(CompoundTag compound)
    {
        compound.put(LEFT_TANK, this.leftTank.writeToNBT(new CompoundTag()));
        compound.put(RIGHT_TANK, this.rightTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void loadTanks(CompoundTag compound)
    {
        this.leftTank.readFromNBT(compound.getCompound(LEFT_TANK));
        this.rightTank.readFromNBT(compound.getCompound(RIGHT_TANK));
    }

    @Override
    public void saveColor(CompoundTag compound)
    {
        compound.putInt(COLOR, this.color);
    }

    @Override
    public void loadColor(CompoundTag compound)
    {
        this.color = compound.getInt(COLOR);
    }

    @Override
    public void saveAbility(CompoundTag compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void loadAbility(CompoundTag compound)
    {
        this.ability = compound.getBoolean(ABILITY);
    }

    @Override
    public void saveTime(CompoundTag compound)
    {
        compound.putInt(LAST_TIME, this.lastTime);
    }

    @Override
    public void loadTime(CompoundTag compound)
    {
        this.lastTime = compound.getInt(LAST_TIME);
    }

    public void saveSleepingBag(CompoundTag compound)
    {
        compound.putBoolean(SLEEPING_BAG, this.isSleepingBagDeployed);
    }

    public void loadSleepingBag(CompoundTag compound)
    {
        this.isSleepingBagDeployed = compound.getBoolean(SLEEPING_BAG);
    }

    public void saveName(CompoundTag compound)
    {
        if(this.customName != null)
        {
            compound.putString(CUSTOM_NAME, Component.Serializer.toJson(this.customName));
        }
    }

    public void loadName(CompoundTag compound)
    {
        if(compound.contains(CUSTOM_NAME, 8))
        {
            this.customName = Component.Serializer.fromJson(compound.getString(CUSTOM_NAME));
        }
    }

    @Override
    public void saveAllData(CompoundTag compound)
    {
        this.saveTanks(compound);
        this.saveItems(compound);
        this.saveSleepingBag(compound);
        this.saveColor(compound);
        this.saveAbility(compound);
        this.saveTime(compound);
        this.saveName(compound);
    }

    @Override
    public void loadAllData(CompoundTag compound)
    {
        this.loadTanks(compound);
        this.loadItems(compound);
        this.loadSleepingBag(compound);
        this.loadColor(compound);
        this.loadAbility(compound);
        this.loadTime(compound);
        this.loadName(compound);
    }

    public Player getUsingPlayer()
    {
        for(Player player : this.level.getEntitiesOfClass(Player.class, new AABB(getBlockPos()).inflate(5.0D)))
        {
            if(player.containerMenu instanceof TravelersBackpackBlockEntityMenu)
            {
                return player;
            }
        }
        return null;
    }

    public boolean isUsableByPlayer(Player player)
    {
        if(this.level.getBlockEntity(getBlockPos()) != this)
        {
            return false;
        }
        else
        {
            return player.distanceToSqr((double)getBlockPos().getX() + 0.5D, (double)getBlockPos().getY() + 0.5D, (double)getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), Reference.BUCKET_IN_LEFT, getUsingPlayer()) || InventoryActions.transferContainerTank(this, getRightTank(), Reference.BUCKET_IN_RIGHT, getUsingPlayer());
    }

    @Override
    public void setTankChanged() {}

    @Override
    public boolean hasColor()
    {
        return this.color != 0;
    }

    @Override
    public int getColor()
    {
        return this.color;
    }

    @Override
    public boolean getAbilityValue()
    {
        return TravelersBackpackConfig.enableBackpackAbilities ? this.ability : false;
    }

    @Override
    public void setAbility(boolean value)
    {
        this.ability = value;
        this.setChanged();
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
    public CompoundTag getTagCompound(ItemStack stack)
    {
        return null;
    }

    @Override
    public boolean hasBlockEntity()
    {
        return true;
    }

    @Override
    public boolean isSleepingBagDeployed()
    {
        return this.isSleepingBagDeployed;
    }

    @Override
    public BlockPos getPosition()
    {
        return this.getBlockPos();
    }

    @Override
    public byte getScreenID()
    {
        return Reference.TRAVELERS_BACKPACK_BLOCK_ENTITY_SCREEN_ID;
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
        getLevel().setBlocksDirty(getBlockPos(), blockstate, blockstate);
        getLevel().sendBlockUpdated(getBlockPos(), blockstate, blockstate, 3);
    }

    public void setSleepingBagDeployed(boolean isSleepingBagDeployed)
    {
        this.isSleepingBagDeployed = isSleepingBagDeployed;
    }

    public boolean deploySleepingBag(Level level, BlockPos pos)
    {
        Direction direction = this.getBlockDirection(level.getBlockEntity(getBlockPos()));
        this.isThereSleepingBag(direction);

        if(!this.isSleepingBagDeployed)
        {
            BlockPos sleepingBagPos1 = pos.relative(direction);
            BlockPos sleepingBagPos2 = sleepingBagPos1.relative(direction);

            if(level.getBlockState(sleepingBagPos2).isAir() && level.getBlockState(sleepingBagPos1).isAir())
            {
                if(level.getBlockState(sleepingBagPos1.below()).isCollisionShapeFullBlock(level, sleepingBagPos1.below()) && level.getBlockState(sleepingBagPos2.below()).isCollisionShapeFullBlock(level, sleepingBagPos2.below()))
                {
                    level.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);

                    if(!level.isClientSide)
                    {
                        level.setBlock(sleepingBagPos1, ModBlocks.SLEEPING_BAG.get().defaultBlockState().setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.FOOT), 3);
                        level.setBlock(sleepingBagPos2, ModBlocks.SLEEPING_BAG.get().defaultBlockState().setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.HEAD), 3);

                        level.updateNeighborsAt(pos, ModBlocks.SLEEPING_BAG.get());
                        level.updateNeighborsAt(sleepingBagPos2, ModBlocks.SLEEPING_BAG.get());
                    }

                    this.isSleepingBagDeployed = true;
                    this.setChanged();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeSleepingBag(Level level)
    {
        Direction blockFacing = this.getBlockDirection(level.getBlockEntity(getBlockPos()));

        this.isThereSleepingBag(blockFacing);

        if(this.isSleepingBagDeployed)
        {
            BlockPos sleepingBagPos1 = getBlockPos().relative(blockFacing);
            BlockPos sleepingBagPos2 = sleepingBagPos1.relative(blockFacing);

            if(level.getBlockState(sleepingBagPos1).getBlock() == ModBlocks.SLEEPING_BAG.get() && level.getBlockState(sleepingBagPos2).getBlock() == ModBlocks.SLEEPING_BAG.get())
            {
                level.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
                level.setBlock(sleepingBagPos2, Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(sleepingBagPos1, Blocks.AIR.defaultBlockState(), 3);
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

    public Direction getBlockDirection(BlockEntity blockEntity)
    {
        if(blockEntity instanceof TravelersBackpackBlockEntity)
        {
            if(level == null || !(level.getBlockState(getBlockPos()).getBlock() instanceof TravelersBackpackBlock))
            {
                return Direction.NORTH;
            }
            return level.getBlockState(getBlockPos()).getValue(TravelersBackpackBlock.FACING);
        }
        return Direction.NORTH;
    }

    public boolean drop(Level level, BlockPos pos, Item item)
    {
        ItemStack stack = new ItemStack(item, 1);
        transferToItemStack(stack);
        if(this.hasCustomName())
        {
            stack.setHoverName(this.getCustomName());
        }
        ItemEntity droppedItem = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);

        return level.addFreshEntity(droppedItem);
    }

    public ItemStack transferToItemStack(ItemStack stack)
    {
        CompoundTag compound = new CompoundTag();
        saveTanks(compound);
        saveItems(compound);
        if(this.hasColor()) this.saveColor(compound);
        saveAbility(compound);
        saveTime(compound);
        stack.setTag(compound);
        return stack;
    }

    @Override
    public Component getName()
    {
        return this.customName != null ? this.customName : this.getDisplayName();
    }

    @Nullable
    @Override
    public Component getCustomName()
    {
        return this.customName;
    }

    public void setCustomName(Component customName)
    {
        this.customName = customName;
    }

    @Override
    public Component getDisplayName()
    {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        super.onDataPacket(net, pkt);
        this.handleUpdateTag(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveWithoutMetadata();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TravelersBackpackBlockEntity blockEntity)
    {
        if(blockEntity.getAbilityValue() && BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, blockEntity.getItemStack()))
        {
            if(blockEntity.getLastTime() > 0)
            {
                blockEntity.setLastTime(blockEntity.getLastTime() - 1);
                blockEntity.setChanged();
            }

            BackpackAbilities.ABILITIES.abilityTick(null, null, blockEntity);
        }
    }

    public void openGUI(Player player, MenuProvider containerSupplier, BlockPos pos)
    {
        if(!player.level.isClientSide && getUsingPlayer() == null)
        {
            NetworkHooks.openScreen((ServerPlayer)player, containerSupplier, pos);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
    {
        return new TravelersBackpackBlockEntityMenu(id, inventory, this);
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
                return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.is(ModTags.BLACKLISTED_ITEMS);
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
        Direction direction = getBlockDirection(this);
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
}