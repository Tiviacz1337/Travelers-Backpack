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
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelersBackpackTileEntity extends TileEntity implements ITravelersBackpackInventory, INamedContainerProvider, INameable, ITickableTileEntity
{
    private final ItemStackHandler inventory = createHandler(Tiers.LEATHER.getAllSlots(), true);
    private final ItemStackHandler craftingInventory = createHandler(Reference.CRAFTING_GRID_SIZE, false);
    private final ItemStackHandler fluidSlots = createTemporaryHandler();
    private final FluidTank leftTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    private final FluidTank rightTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    private PlayerEntity player = null;
    private boolean isSleepingBagDeployed = false;
    private int color = 0;
    private int sleepingBagColor = DyeColor.RED.getId();
    private Tiers.Tier tier = Tiers.LEATHER;
    private boolean ability = true;
    private int lastTime = 0;
    private ITextComponent customName = null;

    private final LazyOptional<IItemHandlerModifiable> inventoryCapability = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, this.tier.getStorageSlots()));
    private final LazyOptional<ItemStackHandler> craftingInventoryCapability = LazyOptional.of(() -> this.craftingInventory);
    private final LazyOptional<IFluidHandler> leftFluidTankCapability = LazyOptional.of(() -> this.leftTank);
    private final LazyOptional<IFluidHandler> rightFluidTankCapability = LazyOptional.of(() -> this.rightTank);
    private final SlotManager slotManager = new SlotManager(this);
    private final SettingsManager settingsManager = new SettingsManager(this);

    private final String INVENTORY = "Inventory";
    private final String CRAFTING_INVENTORY = "CraftingInventory";
    private final String LEFT_TANK = "LeftTank";
    private final String RIGHT_TANK = "RightTank";
    private final String SLEEPING_BAG = "SleepingBag";
    private final String COLOR = "Color";
    private final String SLEEPING_BAG_COLOR = "SleepingBagColor";
    private final String ABILITY = "Ability";
    private final String LAST_TIME = "LastTime";
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
    public ItemStackHandler getFluidSlotsInventory()
    {
        return this.fluidSlots;
    }

    @Override
    public IItemHandlerModifiable getCombinedInventory()
    {
        RangedWrapper additional = null;
        if(this.tier != Tiers.LEATHER)
        {
            additional = new RangedWrapper(getInventory(), 0, this.tier.getStorageSlots() - 18);
        }
        if(additional != null)
        {
            return new CombinedInvWrapper(
                    additional,
                    new RangedWrapper(getInventory(), additional.getSlots(), additional.getSlots() + 6),
                    new RangedWrapper(getCraftingGridInventory(), 0, 3),
                    new RangedWrapper(getInventory(), additional.getSlots() + 6, additional.getSlots() + 12),
                    new RangedWrapper(getCraftingGridInventory(), 3, 6),
                    new RangedWrapper(getInventory(), additional.getSlots() + 12, additional.getSlots() + 18),
                    new RangedWrapper(getCraftingGridInventory(), 6, 9));
        }
        else
        {
            return new CombinedInvWrapper(
                    new RangedWrapper(getInventory(), 0, 6),
                    new RangedWrapper(getCraftingGridInventory(), 0, 3),
                    new RangedWrapper(getInventory(), 6, 12),
                    new RangedWrapper(getCraftingGridInventory(), 3, 6),
                    new RangedWrapper(getInventory(), 12, 18),
                    new RangedWrapper(getCraftingGridInventory(), 6, 9));
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

    public void saveTier(CompoundNBT compound)
    {
        compound.putInt(Tiers.TIER, this.tier.getOrdinal());
    }

    public void loadTier(CompoundNBT compound)
    {
        if(compound.contains(Tiers.TIER, Constants.NBT.TAG_STRING))
        {
            Tiers.Tier tier = Tiers.of(compound.getString(Tiers.TIER));
            compound.remove(Tiers.TIER);
            compound.putInt(Tiers.TIER, tier.getOrdinal());
        }
        this.tier = compound.contains(Tiers.TIER) ? Tiers.of(compound.getInt(Tiers.TIER)) : TravelersBackpackConfig.enableTierUpgrades ? Tiers.LEATHER : Tiers.DIAMOND;
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
    public void saveSleepingBagColor(CompoundNBT compound)
    {
        compound.putInt(SLEEPING_BAG_COLOR, this.sleepingBagColor);
    }

    @Override
    public void loadSleepingBagColor(CompoundNBT compound)
    {
        this.sleepingBagColor = compound.contains(SLEEPING_BAG_COLOR) ? compound.getInt(SLEEPING_BAG_COLOR) : DyeColor.RED.getId();
    }

    @Override
    public void saveAbility(CompoundNBT compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void loadAbility(CompoundNBT compound)
    {
        this.ability = !compound.contains(ABILITY) && TravelersBackpackConfig.forceAbilityEnabled || compound.getBoolean(ABILITY);
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
    public void loadAllData(CompoundNBT compound)
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
        return InventoryActions.transferContainerTank(this, getLeftTank(), this.tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), this.player) || InventoryActions.transferContainerTank(this, getRightTank(), this.tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT), this.player);
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
    public World getLevel()
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
        return Reference.TILE_SCREEN_ID;
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
    public void setUsingPlayer(@Nullable PlayerEntity player)
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
        level.setBlocksDirty(getBlockPos(), blockstate, blockstate);
        level.sendBlockUpdated(getBlockPos(), blockstate, blockstate, 3);
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
                        BlockState sleepingBagState = getProperSleepingBag(getSleepingBagColor());
                        level.setBlock(sleepingBagPos1, sleepingBagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.FOOT).setValue(SleepingBagBlock.CAN_DROP, false), 3);
                        level.setBlock(sleepingBagPos2, sleepingBagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.HEAD).setValue(SleepingBagBlock.CAN_DROP, false), 3);

                        world.updateNeighborsAt(pos, sleepingBagState.getBlock());
                        world.updateNeighborsAt(sleepingBagPos2, sleepingBagState.getBlock());
                    }

                    this.isSleepingBagDeployed = true;
                    this.setDataChanged();
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

            if(world.getBlockState(sleepingBagPos1).getBlock() instanceof SleepingBagBlock && world.getBlockState(sleepingBagPos2).getBlock() instanceof SleepingBagBlock)
            {
                world.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);
                world.setBlockAndUpdate(sleepingBagPos2, Blocks.AIR.defaultBlockState());
                world.setBlockAndUpdate(sleepingBagPos1, Blocks.AIR.defaultBlockState());
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
        switch(colorId)
                {
                    case 0: return ModBlocks.WHITE_SLEEPING_BAG.get().defaultBlockState();
                    case 1: return ModBlocks.ORANGE_SLEEPING_BAG.get().defaultBlockState();
                    case 2: return ModBlocks.MAGENTA_SLEEPING_BAG.get().defaultBlockState();
                    case 3: return ModBlocks.LIGHT_BLUE_SLEEPING_BAG.get().defaultBlockState();
                    case 4: return ModBlocks.YELLOW_SLEEPING_BAG.get().defaultBlockState();
                    case 5: return ModBlocks.LIME_SLEEPING_BAG.get().defaultBlockState();
                    case 6: return ModBlocks.PINK_SLEEPING_BAG.get().defaultBlockState();
                    case 7: return ModBlocks.GRAY_SLEEPING_BAG.get().defaultBlockState();
                    case 8: return ModBlocks.LIGHT_GRAY_SLEEPING_BAG.get().defaultBlockState();
                    case 9: return ModBlocks.CYAN_SLEEPING_BAG.get().defaultBlockState();
                    case 10: return ModBlocks.PURPLE_SLEEPING_BAG.get().defaultBlockState();
                    case 11: return ModBlocks.BLUE_SLEEPING_BAG.get().defaultBlockState();
                    case 12: return ModBlocks.BROWN_SLEEPING_BAG.get().defaultBlockState();
                    case 13: return ModBlocks.GREEN_SLEEPING_BAG.get().defaultBlockState();
                    case 14: return ModBlocks.RED_SLEEPING_BAG.get().defaultBlockState();
                    case 15: return ModBlocks.BLACK_SLEEPING_BAG.get().defaultBlockState();
                    default: return ModBlocks.RED_SLEEPING_BAG.get().defaultBlockState();
                }
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

    public boolean hasData()
    {
        boolean isDefaultTier = getTier() == Tiers.LEATHER;
        boolean isInvEmpty = ItemStackUtils.isEmpty(getInventory());
        boolean isCraftingGridEmpty = ItemStackUtils.isEmpty(getCraftingGridInventory());
        boolean leftTankEmpty = getLeftTank().isEmpty();
        boolean rightTankEmpty = getRightTank().isEmpty();
        boolean hasColor = hasColor();
        boolean hasSleepingBagColor = hasSleepingBagColor();
        boolean hasTime = getLastTime() != 0;
        boolean hasUnsortableSlots = !slotManager.getUnsortableSlots().isEmpty();
        boolean hasMemorySlots = !slotManager.getMemorySlots().isEmpty();
        boolean hasCustomName = hasCustomName();
        return !isDefaultTier || !isInvEmpty || !isCraftingGridEmpty || !leftTankEmpty || !rightTankEmpty || hasColor || hasSleepingBagColor || hasTime || hasUnsortableSlots || hasMemorySlots || hasCustomName;
    }

    public ItemStack transferToItemStack(ItemStack stack)
    {
        CompoundNBT compound = new CompoundNBT();
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
    public ITextComponent getName()
    {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName()
    {
        return this.customName;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return this.getName();
    }

    public ITextComponent getDefaultName()
    {
        return new TranslationTextComponent(getBlockState().getBlock().getDescriptionId());
    }

    public void setCustomName(ITextComponent customName)
    {
        this.customName = customName;
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

    @Override
    public void tick()
    {
        if(getAbilityValue() && BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, getItemStack()))
        {
            if(getLastTime() > 0)
            {
                setLastTime(getLastTime() - 1);
                setDataChanged();
            }

            BackpackAbilities.ABILITIES.abilityTick(null, null, this);
        }
    }

    public void openGUI(PlayerEntity player, INamedContainerProvider containerSupplier, BlockPos pos)
    {
        if(!player.level.isClientSide && this.player == null)
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
                ResourceLocation blacklistedItems = new ResourceLocation(TravelersBackpack.MODID, "blacklisted_items");

                return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.getItem().is(ItemTags.getAllTags().getTag(blacklistedItems));
            }

            @Override
            public void deserializeNBT(CompoundNBT nbt)
            {
                if(isInventory)
                {
                    //Prevents losing items if updated from previous version
                    if(TravelersBackpackTileEntity.this.getTier() == Tiers.LEATHER)
                    {
                        int size = nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size();

                        if(size == Reference.INVENTORY_SIZE)
                        {
                            TravelersBackpackTileEntity.this.tier = Tiers.DIAMOND;
                        }
                    }

                    setSize(TravelersBackpackTileEntity.this.tier.getAllSlots());
                    ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
                    for (int i = 0; i < tagList.size(); i++)
                    {
                        CompoundNBT itemTags = tagList.getCompound(i);
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
            public FluidTank readFromNBT(CompoundNBT nbt)
            {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                setCapacity(TravelersBackpackTileEntity.this.tier.getTankCapacity());
                setFluid(fluid);
                return this;
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
}