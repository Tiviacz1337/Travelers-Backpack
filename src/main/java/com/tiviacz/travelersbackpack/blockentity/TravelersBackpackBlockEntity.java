package com.tiviacz.travelersbackpack.blockentity;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.ContainerUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelersBackpackBlockEntity extends BlockEntity implements ITravelersBackpackContainer, MenuProvider, Nameable
{
    private final ItemStackHandler inventory = createHandler(Tiers.LEATHER.getStorageSlots(), true);
    private final ItemStackHandler craftingInventory = createHandler(Reference.CRAFTING_GRID_SIZE, false);
    private final ItemStackHandler toolSlots = createToolsHandler(Tiers.LEATHER.getToolSlots());
    private final ItemStackHandler fluidSlots = createTemporaryHandler();
    private final FluidTank leftTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    private final FluidTank rightTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    private final SlotManager slotManager = new SlotManager(this);
    private final SettingsManager settingsManager = new SettingsManager(this);
    private Player player = null;
    private boolean isSleepingBagDeployed = false;
    private int color = 0;
    private int sleepingBagColor = DyeColor.RED.getId();
    private Tiers.Tier tier = Tiers.LEATHER;
    private boolean ability = true;
    private int lastTime = 0;
    private Component customName = null;

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
    public ItemStackHandler getToolSlotsHandler()
    {
        return this.toolSlots;
    }

    @Override
    public ItemStackHandler getCraftingGridHandler()
    {
        return this.craftingInventory;
    }

    @Override
    public ItemStackHandler getFluidSlotsHandler()
    {
        return this.fluidSlots;
    }

    @Override
    public IItemHandlerModifiable getCombinedHandler()
    {
        return new CombinedInvWrapper(getHandler(), getToolSlotsHandler(), getFluidSlotsHandler(), getCraftingGridHandler());
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

    public void saveTier(CompoundTag compound)
    {
        compound.putInt(TIER, this.tier.getOrdinal());
    }

    public void loadTier(CompoundTag compound)
    {
        if(compound.contains(TIER, Tag.TAG_STRING))
        {
            Tiers.Tier tier = Tiers.of(compound.getString(TIER));
            compound.remove(TIER);
            compound.putInt(TIER, tier.getOrdinal());
        }
        this.tier = compound.contains(TIER) ? Tiers.of(compound.getInt(TIER)) : TravelersBackpackConfig.enableTierUpgrades ? Tiers.LEATHER : Tiers.DIAMOND;
    }

    @Override
    public void saveItems(CompoundTag compound)
    {
        compound.put(INVENTORY, this.inventory.serializeNBT());
        compound.put(TOOLS_INVENTORY, this.toolSlots.serializeNBT());
        compound.put(CRAFTING_INVENTORY, this.craftingInventory.serializeNBT());
    }

    @Override
    public void loadItems(CompoundTag compound)
    {
        this.inventory.deserializeNBT(compound.getCompound(INVENTORY));
        this.toolSlots.deserializeNBT(compound.getCompound(TOOLS_INVENTORY));
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
    public void saveSleepingBagColor(CompoundTag compound)
    {
        compound.putInt(SLEEPING_BAG_COLOR, this.sleepingBagColor);
    }

    @Override
    public void loadSleepingBagColor(CompoundTag compound)
    {
        this.sleepingBagColor = compound.contains(SLEEPING_BAG_COLOR) ? compound.getInt(SLEEPING_BAG_COLOR) : DyeColor.RED.getId();
    }

    @Override
    public void saveAbility(CompoundTag compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void loadAbility(CompoundTag compound)
    {
        this.ability = !compound.contains(ABILITY) && TravelersBackpackConfig.forceAbilityEnabled || compound.getBoolean(ABILITY);
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
        this.saveTier(compound);
        this.saveTanks(compound);
        this.saveItems(compound);
        this.saveSleepingBag(compound);
        this.saveColor(compound);
        this.saveSleepingBagColor(compound);
        this.saveAbility(compound);
        this.saveTime(compound);
        this.saveName(compound);
        this.slotManager.saveUnsortableSlots(compound);
        this.slotManager.saveMemorySlots(compound);
        this.settingsManager.saveSettings(compound);
    }

    @Override
    public void loadAllData(CompoundTag compound)
    {
        this.loadTier(compound);
        this.loadTanks(compound);
        this.loadItems(compound);
        this.loadSleepingBag(compound);
        this.loadColor(compound);
        this.loadSleepingBagColor(compound);
        this.loadAbility(compound);
        this.loadTime(compound);
        this.loadName(compound);
        this.slotManager.loadUnsortableSlots(compound);
        this.slotManager.loadMemorySlots(compound);
        this.settingsManager.loadSettings(compound);
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), 0, this.player) || InventoryActions.transferContainerTank(this, getRightTank(), 2, this.player);
    }

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
    public boolean hasSleepingBagColor()
    {
        return this.sleepingBagColor != DyeColor.RED.getId();
    }

    @Override
    public int getSleepingBagColor()
    {
        if(hasSleepingBagColor())
        {
            return this.sleepingBagColor;
        }
        return DyeColor.RED.getId();
    }

    public void setSleepingBagColor(int colorId)
    {
        this.sleepingBagColor = colorId;
    }

    @Override
    public boolean getAbilityValue()
    {
        return TravelersBackpackConfig.enableBackpackAbilities ? (BackpackAbilities.ALLOWED_ABILITIES.contains(getItemStack().getItem()) ? this.ability : false) : false;
    }

    @Override
    public void setAbility(boolean value)
    {
        this.ability = value;
        this.setDataChanged();
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
    public int getRows()
    {
        return (int)Math.ceil((double)getHandler().getSlots() / 9);
    }

    @Override
    public int getYOffset()
    {
        return 18 * Math.max(0, getRows() - 3);
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
    public SlotManager getSlotManager()
    {
        return slotManager;
    }

    @Override
    public SettingsManager getSettingsManager()
    {
        return settingsManager;
    }

    @Override
    public Tiers.Tier getTier()
    {
        return this.tier;
    }

    public void resetTier()
    {
        this.tier = Tiers.LEATHER;
        this.setDataChanged();
    }

    @Override
    public Level getLevel()
    {
        return super.getLevel();
    }

    @Override
    public BlockPos getPosition()
    {
        return this.getBlockPos();
    }

    @Override
    public byte getScreenID()
    {
        return Reference.BLOCK_ENTITY_SCREEN_ID;
    }

    @Override
    public ItemStack getItemStack()
    {
        if(level.getBlockState(getBlockPos()).getBlock() instanceof TravelersBackpackBlock block)
        {
            return new ItemStack(block);
        }
        return new ItemStack(ModBlocks.STANDARD_TRAVELERS_BACKPACK.get());
    }

    @Override
    public void setUsingPlayer(@Nullable Player player)
    {
        this.player = player;
    }

    @Override
    public void setDataChanged(byte... dataIds) {}

    @Override
    public void setDataChanged()
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
                        BlockState sleepingBagState = getProperSleepingBag(getSleepingBagColor());
                        level.setBlock(sleepingBagPos1, sleepingBagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.FOOT).setValue(SleepingBagBlock.CAN_DROP, false), 3);
                        level.setBlock(sleepingBagPos2, sleepingBagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.HEAD).setValue(SleepingBagBlock.CAN_DROP, false), 3);

                        level.updateNeighborsAt(pos, sleepingBagState.getBlock());
                        level.updateNeighborsAt(sleepingBagPos2, sleepingBagState.getBlock());
                    }

                    this.isSleepingBagDeployed = true;
                    this.setDataChanged();
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

            if(level.getBlockState(sleepingBagPos1).getBlock() instanceof SleepingBagBlock && level.getBlockState(sleepingBagPos2).getBlock() instanceof SleepingBagBlock)
            {
                level.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
                level.setBlock(sleepingBagPos2, Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(sleepingBagPos1, Blocks.AIR.defaultBlockState(), 3);
                this.isSleepingBagDeployed = false;
                this.setDataChanged();
                return true;
            }
        }
        else
        {
            this.isSleepingBagDeployed = false;
            this.setDataChanged();
            return true;
        }
        return false;
    }

    public boolean isThereSleepingBag(Direction direction)
    {
        if(level.getBlockState(getBlockPos().relative(direction)).getBlock() instanceof SleepingBagBlock && level.getBlockState(getBlockPos().relative(direction).relative(direction)).getBlock() instanceof SleepingBagBlock)
        {
            return true;
        }
        else
        {
            this.isSleepingBagDeployed = false;
            return false;
        }
    }

    public BlockState getProperSleepingBag(int colorId)
    {
        return switch(colorId)
        {
                    case 0 -> ModBlocks.WHITE_SLEEPING_BAG.get().defaultBlockState();
            case 1 -> ModBlocks.ORANGE_SLEEPING_BAG.get().defaultBlockState();
            case 2 -> ModBlocks.MAGENTA_SLEEPING_BAG.get().defaultBlockState();
            case 3 -> ModBlocks.LIGHT_BLUE_SLEEPING_BAG.get().defaultBlockState();
            case 4 -> ModBlocks.YELLOW_SLEEPING_BAG.get().defaultBlockState();
            case 5 -> ModBlocks.LIME_SLEEPING_BAG.get().defaultBlockState();
            case 6 -> ModBlocks.PINK_SLEEPING_BAG.get().defaultBlockState();
            case 7 -> ModBlocks.GRAY_SLEEPING_BAG.get().defaultBlockState();
            case 8 -> ModBlocks.LIGHT_GRAY_SLEEPING_BAG.get().defaultBlockState();
            case 9 -> ModBlocks.CYAN_SLEEPING_BAG.get().defaultBlockState();
            case 10 -> ModBlocks.PURPLE_SLEEPING_BAG.get().defaultBlockState();
            case 11 -> ModBlocks.BLUE_SLEEPING_BAG.get().defaultBlockState();
            case 12 -> ModBlocks.BROWN_SLEEPING_BAG.get().defaultBlockState();
            case 13 -> ModBlocks.GREEN_SLEEPING_BAG.get().defaultBlockState();
            case 14 -> ModBlocks.RED_SLEEPING_BAG.get().defaultBlockState();
            case 15 -> ModBlocks.BLACK_SLEEPING_BAG.get().defaultBlockState();
            default -> ModBlocks.RED_SLEEPING_BAG.get().defaultBlockState();
        };
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

    public boolean hasData()
    {
        boolean isDefaultTier = getTier() == Tiers.LEATHER;
        boolean isInvEmpty = ContainerUtils.isEmpty(getHandler());
        boolean isToolsEmpty = ContainerUtils.isEmpty(getToolSlotsHandler());
        boolean isCraftingGridEmpty = ContainerUtils.isEmpty(getCraftingGridHandler());
        boolean leftTankEmpty = getLeftTank().isEmpty();
        boolean rightTankEmpty = getRightTank().isEmpty();
        boolean hasColor = hasColor();
        boolean hasSleepingBagColor = hasSleepingBagColor();
        boolean hasTime = getLastTime() != 0;
        boolean hasUnsortableSlots = !slotManager.getUnsortableSlots().isEmpty();
        boolean hasMemorySlots = !slotManager.getMemorySlots().isEmpty();
        boolean hasCustomName = hasCustomName();
        return !isDefaultTier || !isInvEmpty || !isToolsEmpty || !isCraftingGridEmpty || !leftTankEmpty || !rightTankEmpty || hasColor || hasSleepingBagColor || hasTime || hasUnsortableSlots || hasMemorySlots || hasCustomName;
    }

    public ItemStack transferToItemStack(ItemStack stack)
    {
        CompoundTag compound = new CompoundTag();
        saveTier(compound);
        saveTanks(compound);
        saveItems(compound);
        if(this.hasColor()) this.saveColor(compound);
        if(this.hasSleepingBagColor()) this.saveSleepingBagColor(compound);
        saveAbility(compound);
        saveTime(compound);
        slotManager.saveUnsortableSlots(compound);
        slotManager.saveMemorySlots(compound);
        settingsManager.saveSettings(compound);
        stack.setTag(compound);
        if(hasCustomName()) stack.setHoverName(getCustomName());
        return stack;
    }

    @Override
    public Component getName()
    {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Nullable
    @Override
    public Component getCustomName()
    {
        return this.customName;
    }

    @Override
    public Component getDisplayName()
    {
        return this.getName();
    }

    public Component getDefaultName()
    {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    public void setCustomName(Component customName)
    {
        this.customName = customName;
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
                blockEntity.setDataChanged();
            }

            BackpackAbilities.ABILITIES.abilityTick(null, null, blockEntity);
        }
    }

    public void openGUI(Player player, MenuProvider containerSupplier, BlockPos pos)
    {
        if(!player.level().isClientSide && this.player == null)
        {
            ((ServerPlayer)player).openMenu(containerSupplier, pos);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
    {
        return new TravelersBackpackBlockEntityMenu(id, inventory, this);
    }

    private ItemStackHandler createHandler(int size, boolean isInventory)
    {
        return new ItemStackHandler(size)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                setDataChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return BackpackSlotItemHandler.isItemValid(stack);
            }

            @Override
            public void deserializeNBT(CompoundTag nbt)
            {
                if(isInventory)
                {
                    setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : TravelersBackpackBlockEntity.this.tier.getStorageSlots());
                    ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
                    for (int i = 0; i < tagList.size(); i++)
                    {
                        CompoundTag itemTags = tagList.getCompound(i);
                        int slot = itemTags.getInt("Slot");

                        if (slot >= 0 && slot < stacks.size())
                        {
                            stacks.set(slot, ItemStack.of(itemTags));
                        }
                    }
                    onLoad();
                }
                else
                {
                    super.deserializeNBT(nbt);
                }
            }
        };
    }

    private ItemStackHandler createToolsHandler(int size)
    {
        return new ItemStackHandler(size)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                setDataChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return ToolSlotItemHandler.isValid(stack);
            }

            @Override
            public void deserializeNBT(CompoundTag nbt)
            {
                setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : TravelersBackpackBlockEntity.this.tier.getToolSlots());
                ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
                for(int i = 0; i < tagList.size(); i++)
                {
                    CompoundTag itemTags = tagList.getCompound(i);
                    int slot = itemTags.getInt("Slot");

                    if(slot >= 0 && slot < stacks.size())
                    {
                        stacks.set(slot, ItemStack.of(itemTags));
                    }
                }
                onLoad();
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
                setDataChanged();
            }

            @Override
            public FluidTank readFromNBT(CompoundTag nbt)
            {
                setCapacity(nbt.contains("Capacity", 3) ? nbt.getInt("Capacity") : TravelersBackpackBlockEntity.this.tier.getTankCapacity());
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                setFluid(fluid);
                return this;
            }

            @Override
            public CompoundTag writeToNBT(CompoundTag nbt)
            {
                if(!nbt.contains("Capacity", 3)) nbt.putInt("Capacity", TravelersBackpackBlockEntity.this.tier.getTankCapacity());
                fluid.writeToNBT(nbt);
                return nbt;
            }
        };
    }
}