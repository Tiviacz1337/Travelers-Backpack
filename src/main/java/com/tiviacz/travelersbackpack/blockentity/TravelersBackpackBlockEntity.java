package com.tiviacz.travelersbackpack.blockentity;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.inventory.*;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.inventory.sorter.wrappers.CombinedInvWrapper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TravelersBackpackBlockEntity extends BlockEntity implements ITravelersBackpackInventory, Nameable
{
    public final InventoryImproved inventory = createInventory(Tiers.LEATHER.getStorageSlots(), true);
    public final InventoryImproved craftingInventory = createInventory(9, false);
    private final InventoryImproved toolSlots = createToolsInventory(Tiers.LEATHER.getToolSlots());
    private final InventoryImproved fluidSlots = createTemporaryInventory();
    private final FluidTank leftTank = createFluidTank(Tiers.LEATHER.getTankCapacity());
    private final FluidTank rightTank = createFluidTank(Tiers.LEATHER.getTankCapacity());
    private final SlotManager slotManager = new SlotManager(this);
    private final SettingsManager settingsManager = new SettingsManager(this);
    private PlayerEntity player = null;
    private boolean isSleepingBagDeployed = false;
    private int color = 0;
    private int sleepingBagColor = DyeColor.RED.getId();
    private Tiers.Tier tier = Tiers.LEATHER;
    private boolean ability = TravelersBackpackConfig.getConfig().backpackAbilities.forceAbilityEnabled;
    private int lastTime = 0;
    private Text customName = null;

    public TravelersBackpackBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntityTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.readAllData(nbt);
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        this.writeAllData(tag);
    }

    @Override
    public InventoryImproved getInventory()
    {
        return this.inventory;
    }

    @Override
    public InventoryImproved getToolSlotsInventory()
    {
        return this.toolSlots;
    }

    @Override
    public InventoryImproved getCraftingGridInventory()
    {
        return this.craftingInventory;
    }

    @Override
    public InventoryImproved getFluidSlotsInventory()
    {
        return this.fluidSlots;
    }

    @Override
    public Inventory getCombinedInventory()
    {
        return new CombinedInvWrapper(this, getInventory(), getToolSlotsInventory(), getFluidSlotsInventory(), getCraftingGridInventory());
    }

    @Override
    public FluidTank getLeftTank() {
        return this.leftTank;
    }

    @Override
    public FluidTank getRightTank() {
        return this.rightTank;
    }

    public void writeTier(NbtCompound compound)
    {
        compound.putInt(TIER, this.tier.getOrdinal());
    }

    public void readTier(NbtCompound compound)
    {
        if(compound.contains(TIER, NbtElement.STRING_TYPE))
        {
            Tiers.Tier tier = Tiers.of(compound.getString(TIER));
            compound.remove(TIER);
            compound.putInt(TIER, tier.getOrdinal());
        }
        this.tier = compound.contains(TIER) ? Tiers.of(compound.getInt(TIER)) : TravelersBackpackConfig.getConfig().backpackSettings.enableTierUpgrades ? Tiers.LEATHER : Tiers.DIAMOND;
    }

    @Override
    public void writeItems(NbtCompound compound)
    {
        compound.put(INVENTORY, this.inventory.writeNbt());
        compound.put(TOOLS_INVENTORY, this.toolSlots.writeNbt());
        compound.put(CRAFTING_INVENTORY, this.craftingInventory.writeNbt());
    }

    @Override
    public void readItems(NbtCompound compound)
    {
        this.inventory.readNbt(compound.getCompound(INVENTORY));
        this.toolSlots.readNbt(compound.getCompound(TOOLS_INVENTORY));
        this.craftingInventory.readNbt(compound.getCompound(CRAFTING_INVENTORY));

        //Read from old NBT
        if(compound.contains(INVENTORY, NbtElement.LIST_TYPE))
        {
            this.inventory.readNbtOld(compound, true);
        }

        //Read from old NBT
        if(compound.contains(CRAFTING_INVENTORY, NbtElement.LIST_TYPE))
        {
            this.craftingInventory.readNbtOld(compound, false);
        }
    }

    @Override
    public void writeTanks(NbtCompound compound)
    {
        compound.put(LEFT_TANK, this.leftTank.writeToNbt(new NbtCompound()));
        compound.put(RIGHT_TANK, this.rightTank.writeToNbt(new NbtCompound()));
    }

    @Override
    public void readTanks(NbtCompound compound)
    {
        this.leftTank.readNbt(compound.getCompound(LEFT_TANK));
        this.rightTank.readNbt(compound.getCompound(RIGHT_TANK));

        //Read from old NBT
        if(compound.contains(LEFT_TANK_AMOUNT, NbtElement.LONG_TYPE))
        {
            this.leftTank.readOldNbt(compound, true);
        }

        //Read from old NBT
        if(compound.contains(RIGHT_TANK_AMOUNT, NbtElement.LONG_TYPE))
        {
            this.rightTank.readOldNbt(compound, false);
        }
    }

    @Override
    public void writeColor(NbtCompound compound)
    {
        compound.putInt(COLOR, this.color);
    }

    @Override
    public void readColor(NbtCompound compound)
    {
        this.color = compound.getInt(COLOR);
    }

    @Override
    public void writeSleepingBagColor(NbtCompound compound)
    {
        compound.putInt(SLEEPING_BAG_COLOR, this.sleepingBagColor);
    }

    @Override
    public void readSleepingBagColor(NbtCompound compound)
    {
        this.sleepingBagColor = compound.contains(SLEEPING_BAG_COLOR) ? compound.getInt(SLEEPING_BAG_COLOR) : DyeColor.RED.getId();
    }

    @Override
    public void writeAbility(NbtCompound compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void readAbility(NbtCompound compound)
    {
        this.ability = !compound.contains(ABILITY) && TravelersBackpackConfig.getConfig().backpackAbilities.forceAbilityEnabled || compound.getBoolean(ABILITY);
    }

    @Override
    public void writeTime(NbtCompound compound)
    {
        compound.putInt(LAST_TIME, this.lastTime);
    }

    @Override
    public void readTime(NbtCompound compound)
    {
        this.lastTime = compound.getInt(LAST_TIME);
    }

    public void writeSleepingBag(NbtCompound compound)
    {
        compound.putBoolean(SLEEPING_BAG, this.isSleepingBagDeployed);
    }

    public void readSleepingBag(NbtCompound compound)
    {
        this.isSleepingBagDeployed = compound.getBoolean(SLEEPING_BAG);
    }

    public void writeName(NbtCompound compound)
    {
        if(this.customName != null)
        {
            compound.putString(CUSTOM_NAME, Text.Serializer.toJson(this.customName));
        }
    }

    public void readName(NbtCompound compound)
    {
        if(compound.contains(CUSTOM_NAME, 8))
        {
            this.customName = Text.Serializer.fromJson(compound.getString(CUSTOM_NAME));
        }
    }

    @Override
    public void writeAllData(NbtCompound compound)
    {
        writeTier(compound);
        writeItems(compound);
        writeTanks(compound);
        writeSleepingBag(compound);
        writeColor(compound);
        writeSleepingBagColor(compound);
        writeAbility(compound);
        writeTime(compound);
        writeName(compound);
        this.slotManager.writeUnsortableSlots(compound);
        this.slotManager.writeMemorySlots(compound);
        this.settingsManager.writeSettings(compound);
    }

    @Override
    public void readAllData(NbtCompound compound)
    {
        readTier(compound);
        readItems(compound);
        readTanks(compound);
        readSleepingBag(compound);
        readColor(compound);
        readSleepingBagColor(compound);
        readAbility(compound);
        readTime(compound);
        readName(compound);
        this.slotManager.readUnsortableSlots(compound);
        this.slotManager.readMemorySlots(compound);
        this.settingsManager.readSettings(compound);
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
        return TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities ? (TravelersBackpackConfig.isAbilityAllowed(getItemStack()) ? this.ability : false) : false;
    }

    @Override
    public void setAbility(boolean value)
    {
        this.ability = value;
        this.markDirty();
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
        return (int)Math.ceil((double)getInventory().size() / 9);
    }

    @Override
    public int getYOffset()
    {
        return 18 * Math.max(0, getRows() - 3);
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
        this.markDirty();
    }

    @Override
    public World getWorld()
    {
        return super.getWorld();
    }

    @Override
    public BlockPos getPosition()
    {
        return this.pos;
    }

    @Override
    public byte getScreenID() {
        return Reference.BLOCK_ENTITY_SCREEN_ID;
    }

    @Override
    public ItemStack getItemStack()
    {
        if(world.getBlockState(getPos()).getBlock() instanceof TravelersBackpackBlock block)
        {
            return new ItemStack(block);
        }
        return new ItemStack(ModBlocks.STANDARD_TRAVELERS_BACKPACK);
    }

    @Override
    public void setUsingPlayer(@Nullable PlayerEntity player)
    {
        this.player = player;
    }

    @Override
    public void markDataDirty(byte... dataIds) {}

    @Override
    public void markDirty()
    {
        if(!world.isClient)
        {
            super.markDirty();
            world.updateListeners(pos, getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), Block.NOTIFY_LISTENERS);
        }
    }

    public void setSleepingBagDeployed(boolean isSleepingBagDeployed)
    {
        this.isSleepingBagDeployed = isSleepingBagDeployed;
    }

    public boolean deploySleepingBag(World world, BlockPos pos)
    {
        Direction direction = this.getBlockDirection(world.getBlockEntity(getPos()));
        this.isThereSleepingBag(direction);

        if(!this.isSleepingBagDeployed)
        {
            BlockPos sleepingBagPos1 = pos.offset(direction);
            BlockPos sleepingBagPos2 = sleepingBagPos1.offset(direction);

            if(world.isAir(sleepingBagPos2) && world.isAir(sleepingBagPos1))
            {
                if(world.getBlockState(sleepingBagPos1.down()).isSideSolid(world, sleepingBagPos1.down(), Direction.UP, SideShapeType.FULL) && world.getBlockState(sleepingBagPos2.down()).isSideSolid(world, sleepingBagPos2.down(), Direction.UP, SideShapeType.FULL))
                {
                    world.playSound(null, sleepingBagPos2, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);

                    if(!world.isClient)
                    {
                        BlockState sleepingBagState = getProperSleepingBag(getSleepingBagColor());
                        world.setBlockState(sleepingBagPos1, sleepingBagState.with(SleepingBagBlock.FACING, direction).with(SleepingBagBlock.PART, BedPart.FOOT).with(SleepingBagBlock.CAN_DROP, false), 3);
                        world.setBlockState(sleepingBagPos2, sleepingBagState.with(SleepingBagBlock.FACING, direction).with(SleepingBagBlock.PART, BedPart.HEAD).with(SleepingBagBlock.CAN_DROP, false), 3);

                        world.updateNeighborsAlways(pos, sleepingBagState.getBlock());
                        world.updateNeighborsAlways(sleepingBagPos2, sleepingBagState.getBlock());
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
        Direction blockFacing = this.getBlockDirection(world.getBlockEntity(getPos()));

        this.isThereSleepingBag(blockFacing);

        if(this.isSleepingBagDeployed)
        {
            BlockPos sleepingBagPos1 = pos.offset(blockFacing);
            BlockPos sleepingBagPos2 = sleepingBagPos1.offset(blockFacing);

            if(world.getBlockState(sleepingBagPos1).getBlock() instanceof SleepingBagBlock && world.getBlockState(sleepingBagPos2).getBlock() instanceof SleepingBagBlock)
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
        if(world.getBlockState(pos.offset(direction)).getBlock() instanceof SleepingBagBlock && world.getBlockState(pos.offset(direction).offset(direction)).getBlock() instanceof SleepingBagBlock)
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
        return switch (colorId) {
            case 0 -> ModBlocks.WHITE_SLEEPING_BAG.getDefaultState();
            case 1 -> ModBlocks.ORANGE_SLEEPING_BAG.getDefaultState();
            case 2 -> ModBlocks.MAGENTA_SLEEPING_BAG.getDefaultState();
            case 3 -> ModBlocks.LIGHT_BLUE_SLEEPING_BAG.getDefaultState();
            case 4 -> ModBlocks.YELLOW_SLEEPING_BAG.getDefaultState();
            case 5 -> ModBlocks.LIME_SLEEPING_BAG.getDefaultState();
            case 6 -> ModBlocks.PINK_SLEEPING_BAG.getDefaultState();
            case 7 -> ModBlocks.GRAY_SLEEPING_BAG.getDefaultState();
            case 8 -> ModBlocks.LIGHT_GRAY_SLEEPING_BAG.getDefaultState();
            case 9 -> ModBlocks.CYAN_SLEEPING_BAG.getDefaultState();
            case 10 -> ModBlocks.PURPLE_SLEEPING_BAG.getDefaultState();
            case 11 -> ModBlocks.BLUE_SLEEPING_BAG.getDefaultState();
            case 12 -> ModBlocks.BROWN_SLEEPING_BAG.getDefaultState();
            case 13 -> ModBlocks.GREEN_SLEEPING_BAG.getDefaultState();
            case 14 -> ModBlocks.RED_SLEEPING_BAG.getDefaultState();
            case 15 -> ModBlocks.BLACK_SLEEPING_BAG.getDefaultState();
            default -> ModBlocks.RED_SLEEPING_BAG.getDefaultState();
        };
    }

    public Direction getBlockDirection(BlockEntity blockEntity)
    {
        if(blockEntity instanceof TravelersBackpackBlockEntity)
        {
            if(world == null || !(world.getBlockState(getPos()).getBlock() instanceof TravelersBackpackBlock))
            {
                return Direction.NORTH;
            }
            return world.getBlockState(getPos()).get(TravelersBackpackBlock.FACING);
        }
        return Direction.NORTH;
    }

    public boolean hasData()
    {
        boolean isDefaultTier = getTier() == Tiers.LEATHER;
        boolean isInvEmpty = getInventory().isEmpty();
        boolean isToolsEmpty = getToolSlotsInventory().isEmpty();
        boolean isCraftingInvEmpty = getCraftingGridInventory().isEmpty();
        boolean leftTankEmpty = getLeftTank().isResourceBlank();
        boolean rightTankEmpty = getRightTank().isResourceBlank();
        boolean hasColor = hasColor();
        boolean hasSleepingBagColor = hasSleepingBagColor();
        boolean hasTime = getLastTime() != 0;
        boolean hasUnsortableSlots = !slotManager.getUnsortableSlots().isEmpty();
        boolean hasMemorySlots = !slotManager.getMemorySlots().isEmpty();
        boolean hasCustomName = hasCustomName();
        return !isDefaultTier || !isInvEmpty || !isToolsEmpty || !isCraftingInvEmpty || !leftTankEmpty || !rightTankEmpty || hasColor || hasSleepingBagColor || hasTime || hasUnsortableSlots || hasMemorySlots || hasCustomName;
    }

    public ItemStack transferToItemStack(ItemStack stack)
    {
        NbtCompound compound = new NbtCompound();
        writeTier(compound);
        writeTanks(compound);
        writeItems(compound);
        if(this.hasColor()) this.writeColor(compound);
        if(this.hasSleepingBagColor()) this.writeSleepingBagColor(compound);
        writeAbility(compound);
        writeTime(compound);
        slotManager.writeUnsortableSlots(compound);
        slotManager.writeMemorySlots(compound);
        settingsManager.writeSettings(compound);
        stack.setNbt(compound);
        if(hasCustomName()) stack.setCustomName(getCustomName());
        return stack;
    }

    public boolean isUsableByPlayer(PlayerEntity player)
    {
        if(this.world.getBlockEntity(this.pos) != this)
        {
            return false;
        }
        else
        {
            return this.player == player && player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        return createNbt();
    }

    @Override
    public Text getName()
    {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Override
    public Text getDisplayName()
    {
        return this.getName();
    }

    @Nullable
    @Override
    public Text getCustomName()
    {
        return this.customName;
    }

    public Text getDefaultName()
    {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    public void setCustomName(Text customName)
    {
        this.customName = customName;
    }

    public static void tick(World world, BlockPos pos, BlockState state, TravelersBackpackBlockEntity blockEntity)
    {
        if(blockEntity.getAbilityValue() && BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, blockEntity.getItemStack()))
        {
            if(blockEntity.getLastTime() > 0)
            {
                blockEntity.setLastTime(blockEntity.getLastTime() - 1);
                blockEntity.markDirty();
            }

            BackpackAbilities.ABILITIES.abilityTick(null, null, blockEntity);
        }
    }

    public void openHandledScreen(PlayerEntity player)
    {
        if(!player.world.isClient)
        {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
                {
                    buf.writeBlockPos(TravelersBackpackBlockEntity.this.pos);
                }

                @Override
                public Text getDisplayName()
                {
                    return new TranslatableText(getCachedState().getBlock().getTranslationKey());
                }

                @Nullable
                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
                {
                    return new TravelersBackpackBlockEntityScreenHandler(syncId, inv, TravelersBackpackBlockEntity.this);
                }
            });
        }
    }

    public InventoryImproved createInventory(int size, boolean isInventory)
    {
        return new InventoryImproved(size)
        {
            @Override
            public void markDirty()
            {
                TravelersBackpackBlockEntity.this.markDirty();
            }

            @Override
            public void readNbt(NbtCompound nbt)
            {
                if(isInventory)
                {
                    this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : TravelersBackpackBlockEntity.this.tier.getStorageSlots());
                    NbtList tagList = nbt.getList("Items", 10);

                    for(int i = 0; i < tagList.size(); ++i)
                    {
                        NbtCompound itemTags = tagList.getCompound(i);
                        int slot = itemTags.getInt("Slot");
                        if(slot >= 0 && slot < this.stacks.size())
                        {
                            this.stacks.set(slot, ItemStack.fromNbt(itemTags));
                        }
                    }
                }
                else
                {
                    super.readNbt(nbt);
                }
            }
        };
    }

    private InventoryImproved createToolsInventory(int size)
    {
        return new InventoryImproved(size)
        {
            @Override
            public void markDirty()
            {
                TravelersBackpackBlockEntity.this.markDirty();
            }

            @Override
            public boolean isValid(int slot, ItemStack stack)
            {
                return ToolSlot.isValid(stack);
            }

            @Override
            public void readNbt(NbtCompound nbt)
            {
                this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : TravelersBackpackBlockEntity.this.tier.getToolSlots());
                NbtList tagList = nbt.getList("Items", 10);

                for(int i = 0; i < tagList.size(); ++i)
                {
                    NbtCompound itemTags = tagList.getCompound(i);
                    int slot = itemTags.getInt("Slot");
                    if(slot >= 0 && slot < this.stacks.size())
                    {
                        this.stacks.set(slot, ItemStack.fromNbt(itemTags));
                    }
                }
            }
        };
    }

    public FluidTank createFluidTank(long tankCapacity)
    {
        return new FluidTank(tankCapacity)
        {
            @Override
            protected void onFinalCommit()
            {
                TravelersBackpackBlockEntity.this.markDirty();
            }

            @Override
            public FluidTank readNbt(NbtCompound nbt)
            {
                setCapacity(nbt.contains("capacity") ? nbt.getLong("capacity") : TravelersBackpackBlockEntity.this.tier.getTankCapacity());
                this.variant = FluidVariantImpl.fromNbt(nbt.getCompound("variant"));
                this.amount = nbt.getLong("amount");
                return this;
            }
        };
    }
}