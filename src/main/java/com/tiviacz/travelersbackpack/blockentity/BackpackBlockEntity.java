package com.tiviacz.travelersbackpack.blockentity;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BackpackBlockEntity extends BlockEntity implements MenuProvider, Nameable {
    private BackpackWrapper wrapper = BackpackWrapper.DUMMY;
    private boolean isSleepingBagDeployed = false;
    public ArrayList<Integer> infiniteAccessUsers = new ArrayList<>();
    public int settingsUser = -1;

    public Component customName = null;
    @Nullable
    public Player player;

    public String BACKPACK = "Backpack";
    public String SLEEPING_BAG = "SleepingBag";
    public String SETTINGS_USER = "SettingsUser";
    public String CUSTOM_NAME = "CustomName";

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BACKPACK, pos, state);
    }

    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        writeBackpack(compound);
        compound.putBoolean(SLEEPING_BAG, this.isSleepingBagDeployed);
        if(this.customName != null) {
            compound.putString(CUSTOM_NAME, Component.Serializer.toJson(this.customName));
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        setBackpackFromNbt(compound);
        if(compound.contains(TIER)) {
            setBackpack(getOldDataBackpack(compound));
            compound.remove(TIER);
        }
        this.isSleepingBagDeployed = compound.getBoolean(SLEEPING_BAG);
        if(compound.contains(SETTINGS_USER)) {
            this.settingsUser = compound.getInt(SETTINGS_USER);
        }
        if(compound.contains(CUSTOM_NAME, CompoundTag.TAG_STRING)) {
            this.customName = Component.Serializer.fromJson(compound.getString(CUSTOM_NAME));
        }
    }

    public void setBackpack(ItemStack backpack) {
        if(backpack.getItem() instanceof TravelersBackpackItem) {
            if(this.wrapper == BackpackWrapper.DUMMY) {
                this.wrapper = new BackpackWrapper(backpack.copy(), Reference.BLOCK_ENTITY_SCREEN_ID, null, getLevel());
                wrapper.setBackpackPos(getBlockPos());
                wrapper.saveHandler = () -> {
                    this.setChanged();
                    this.notifyBlockUpdate();
                };
                wrapper.abilityHandler = () -> {
                    if(getLevel() != null) {
                        getLevel().updateNeighborsAt(getBlockPos(), getBlockState().getBlock());

                        if(getBlockState().getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK) {
                            ((TravelersBackpackBlock)getBlockState().getBlock()).tryAbsorbWater(getLevel(), getBlockPos());
                        }
                    }
                };
            } else {
                this.wrapper.setBackpackStack(backpack.copy());
            }
        }
    }

    private void setBackpackFromNbt(CompoundTag nbt) {
        setBackpack(ItemStack.of(nbt.getCompound(BACKPACK)));
    }

    private void writeBackpack(CompoundTag ret) {
        ItemStack backpackCopy = wrapper.getBackpackStack().copy();
        if(backpackCopy.getItem() instanceof TravelersBackpackItem) {
            ret.put(BACKPACK, backpackCopy.save(new CompoundTag()));
        }
    }

    public void removeWrapper() {
        this.wrapper = BackpackWrapper.DUMMY;
    }

    public Direction getBlockDirection() {
        if(level == null || !(level.getBlockState(getBlockPos()).getBlock() instanceof TravelersBackpackBlock) || !level.getBlockState(getBlockPos()).hasProperty(TravelersBackpackBlock.FACING))
            return Direction.NORTH;
        return level.getBlockState(getBlockPos()).getValue(TravelersBackpackBlock.FACING);
    }

    public boolean isSleepingBagDeployed() {
        return this.isSleepingBagDeployed;
    }

    public void setSleepingBagDeployed(boolean isSleepingBagDeployed) {
        this.isSleepingBagDeployed = isSleepingBagDeployed;
        setChanged();
        notifyBlockUpdate();
    }

    public static boolean canPlaceSleepingBag(BlockPos relative, Level level) {
        return level.getBlockState(relative).canBeReplaced() && level.getWorldBorder().isWithinBounds(relative);
    }

    public boolean deploySleepingBag(Level level, BlockPos pos) {
        Direction direction = this.getBlockDirection();
        this.isThereSleepingBag(direction);

        if(!isSleepingBagDeployed()) {
            BlockPos sleepingBagPos1 = pos.relative(direction);
            BlockPos sleepingBagPos2 = sleepingBagPos1.relative(direction);

            if(canPlaceSleepingBag(sleepingBagPos1, level) && canPlaceSleepingBag(sleepingBagPos2, level)) {
                if(level.getBlockState(sleepingBagPos1.below()).isAir() || level.getBlockState(sleepingBagPos1.below()).getBlock() instanceof LiquidBlock) {
                    return false;
                }

                level.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);

                if(!level.isClientSide) {
                    BlockState sleepingBagState = getProperSleepingBag(getWrapper().getSleepingBagColor());
                    level.setBlock(sleepingBagPos1, sleepingBagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.FOOT).setValue(SleepingBagBlock.CAN_DROP, false), 3);
                    level.setBlock(sleepingBagPos2, sleepingBagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.HEAD).setValue(SleepingBagBlock.CAN_DROP, false), 3);

                    level.updateNeighborsAt(pos, sleepingBagState.getBlock());
                    level.updateNeighborsAt(sleepingBagPos2, sleepingBagState.getBlock());
                }

                setSleepingBagDeployed(true);
                getWrapper().saveHandler.run();
                return true;
            }
        }
        return false;
    }

    public boolean removeSleepingBag(Level level, Direction direction) {
        this.isThereSleepingBag(direction);

        if(isSleepingBagDeployed()) {
            BlockPos sleepingBagPos1 = getBlockPos().relative(direction);
            BlockPos sleepingBagPos2 = sleepingBagPos1.relative(direction);

            if(level.getBlockState(sleepingBagPos1).getBlock() instanceof SleepingBagBlock && level.getBlockState(sleepingBagPos2).getBlock() instanceof SleepingBagBlock) {
                level.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
                level.setBlock(sleepingBagPos2, Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(sleepingBagPos1, Blocks.AIR.defaultBlockState(), 3);
                setSleepingBagDeployed(false);
                getWrapper().saveHandler.run();
                return true;
            }
        } else {
            setSleepingBagDeployed(false);
            getWrapper().saveHandler.run();
            return true;
        }
        return false;
    }

    public boolean isThereSleepingBag(Direction direction) {
        if(level.getBlockState(getBlockPos().relative(direction)).getBlock() instanceof SleepingBagBlock && level.getBlockState(getBlockPos().relative(direction).relative(direction)).getBlock() instanceof SleepingBagBlock) {
            return true;
        } else {
            setSleepingBagDeployed(false);
            return false;
        }
    }

    public static BlockState getProperSleepingBag(int sleepingBagColor) {
        return switch(sleepingBagColor) {
            case 0 -> ModBlocks.WHITE_SLEEPING_BAG.defaultBlockState();
            case 1 -> ModBlocks.ORANGE_SLEEPING_BAG.defaultBlockState();
            case 2 -> ModBlocks.MAGENTA_SLEEPING_BAG.defaultBlockState();
            case 3 -> ModBlocks.LIGHT_BLUE_SLEEPING_BAG.defaultBlockState();
            case 4 -> ModBlocks.YELLOW_SLEEPING_BAG.defaultBlockState();
            case 5 -> ModBlocks.LIME_SLEEPING_BAG.defaultBlockState();
            case 6 -> ModBlocks.PINK_SLEEPING_BAG.defaultBlockState();
            case 7 -> ModBlocks.GRAY_SLEEPING_BAG.defaultBlockState();
            case 8 -> ModBlocks.LIGHT_GRAY_SLEEPING_BAG.defaultBlockState();
            case 9 -> ModBlocks.CYAN_SLEEPING_BAG.defaultBlockState();
            case 10 -> ModBlocks.PURPLE_SLEEPING_BAG.defaultBlockState();
            case 11 -> ModBlocks.BLUE_SLEEPING_BAG.defaultBlockState();
            case 12 -> ModBlocks.BROWN_SLEEPING_BAG.defaultBlockState();
            case 13 -> ModBlocks.GREEN_SLEEPING_BAG.defaultBlockState();
            case 14 -> ModBlocks.RED_SLEEPING_BAG.defaultBlockState();
            case 15 -> ModBlocks.BLACK_SLEEPING_BAG.defaultBlockState();
            default -> ModBlocks.RED_SLEEPING_BAG.defaultBlockState();
        };
    }

    public ItemStack toItemStack(ItemStack stack) {
        stack.setTag(this.wrapper.getBackpackStack().copy().getOrCreateTag());
        return stack;
    }

    private void notifyBlockUpdate() {
        if(getLevel() == null) {
            return;
        }
        getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BackpackBlockEntity backpackBlockEntity) {
        BackpackWrapper.tickForBlockEntity(backpackBlockEntity);
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.customName;
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    public Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    public void setCustomName(Component customName) {
        this.customName = customName;
    }

    public void setSettingsUser(Player player) {
        this.settingsUser = player.getId();
        notifyBlockUpdate();
    }

    public int getSettingsUser() {
        return this.settingsUser;
    }

    public void removeSettingsUser() {
        this.settingsUser = -1;
        notifyBlockUpdate();
    }

    public boolean canOpenSettings(Player player) {
        if(!player.level().isClientSide) {
            return this.settingsUser == player.getId();
        } else {
            if(this.settingsUser == -1) {
                return true;
            } else {
                return this.settingsUser == player.getId();
            }
        }
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = this.saveWithoutMetadata();
        tag.putInt(SETTINGS_USER, this.settingsUser);
        return tag;
    }

    public void openBackpack(Player player, MenuProvider containerSupplier, BlockPos pos) {
        if(!player.level().isClientSide) {
            if(this.infiniteAccessUsers.contains(player.getId())) {
                this.infiniteAccessUsers.remove((Object)player.getId());
            }
            player.openMenu(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    buf.writeInt(-1);
                    buf.writeBlockPos(pos);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(wrapper == BackpackWrapper.DUMMY) {
                        throw new IllegalStateException("BackpackWrapper is not initialized!");
                    }
                    if(canOpenSettings(player)) {
                        return new BackpackSettingsMenu(i, inventory, wrapper);
                    } else {
                        return new BackpackBlockEntityMenu(i, inventory, infiniteAccessUsers.contains(player.getId()) ? player.getId() : -1, wrapper);
                    }
                }
            });
        }
    }

    public static FriendlyByteBuf saveSettingsExtraData(FriendlyByteBuf buf, BlockPos pos) {
        buf.writeBoolean(true);
        buf.writeBlockPos(pos);
        return buf;
    }

    public void openSettings(Player player, MenuProvider containerSupplier, BlockPos pos) {
        if(!player.level().isClientSide) {
            //Set settings user
            setSettingsUser(player);
            player.openMenu(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    saveSettingsExtraData(buf, pos);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(wrapper == BackpackWrapper.DUMMY) {
                        throw new IllegalStateException("BackpackWrapper is not initialized!");
                    }
                    if(canOpenSettings(player)) {
                        return new BackpackSettingsMenu(i, inventory, wrapper);
                    } else {
                        return new BackpackBlockEntityMenu(i, inventory, infiniteAccessUsers.contains(player.getId()) ? player.getId() : -1, wrapper);
                    }
                }
            });
        }
    }

    public void openBackpackFromCommand(Player player, MenuProvider containerSupplier, BlockPos pos) {
        if(!player.level().isClientSide) {
            //Set user access to infinite if accessing from command
            if(!this.infiniteAccessUsers.contains(player.getId())) this.infiniteAccessUsers.add(player.getId());

            player.openMenu(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    buf.writeInt(player.getId());
                    buf.writeBlockPos(pos);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.travelersbackpack.item");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    if(wrapper == BackpackWrapper.DUMMY) {
                        throw new IllegalStateException("BackpackWrapper is not initialized!");
                    }
                    if(canOpenSettings(player)) {
                        return new BackpackSettingsMenu(i, inventory, wrapper);
                    } else {
                        return new BackpackBlockEntityMenu(i, inventory, infiniteAccessUsers.contains(player.getId()) ? player.getId() : -1, wrapper);
                    }
                }
            });
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        if(this.wrapper == BackpackWrapper.DUMMY) {
            throw new IllegalStateException("BackpackWrapper is not initialized!");
        }
        if(canOpenSettings(player)) {
            return new BackpackSettingsMenu(id, inventory, this.wrapper);
        } else {
            return new BackpackBlockEntityMenu(id, inventory, this.infiniteAccessUsers.contains(player.getId()) ? player.getId() : -1, this.wrapper);
        }
    }

    //Old data helper #TODO for removal
    public ItemStack getOldDataBackpack(CompoundTag compound) {
        ItemStack backpack;
        if(level != null) {
            backpack = new ItemStack(level.getBlockState(getBlockPos()).getBlock().asItem());
        } else {
            backpack = ModItems.STANDARD_TRAVELERS_BACKPACK.getDefaultInstance();
        }
        int tier = Tiers.LEATHER.getOrdinal();

        if(compound.contains(TIER)) {
            tier = compound.getInt(TIER);
            NbtHelper.set(backpack, ModDataHelper.TIER, tier);
        }

        BackpackWrapper.initializeSize(backpack);

        int storageSlots = NbtHelper.get(backpack, ModDataHelper.STORAGE_SLOTS); //backpack.get(ModDataComponents.STORAGE_SLOTS.get());
        int toolSlots = NbtHelper.get(backpack, ModDataHelper.TOOL_SLOTS); //backpack.get(ModDataComponents.TOOL_SLOTS.get());
        int upgradeSlots = NbtHelper.get(backpack, ModDataHelper.UPGRADE_SLOTS); //backpack.get(ModDataComponents.UPGRADE_SLOTS.get());
        if(compound.contains(INVENTORY)) {
            ItemStackHandler inventory = new ItemStackHandler(99);
            inventory.deserializeNBT(compound.getCompound(INVENTORY));
            NbtHelper.set(backpack, ModDataHelper.BACKPACK_CONTAINER, inventory);
            // backpack.set(ModDataComponents.BACKPACK_CONTAINER.get(), InventoryHelper.itemsToList(storageSlots, inventory));
        }
        if(compound.contains(TOOLS_INVENTORY)) {
            ItemStackHandler tools = new ItemStackHandler(12);
            tools.deserializeNBT(compound.getCompound(TOOLS_INVENTORY));
            NbtHelper.set(backpack, ModDataHelper.TOOLS_CONTAINER, tools);
            //backpack.set(ModDataComponents.TOOLS_CONTAINER.get(), InventoryHelper.itemsToList(toolSlots, tools));
        }
        FluidVariantWrapper leftFluidStack = FluidVariantWrapper.blank();
        FluidVariantWrapper rightFluidStack = FluidVariantWrapper.blank();
        if(compound.contains(LEFT_TANK)) {
            FluidVariant variant = FluidVariant.fromNbt(compound.getCompound(LEFT_TANK).getCompound("variant"));
            long amount = compound.getCompound(LEFT_TANK).getLong("amount");
            FluidVariantWrapper fluidStack = new FluidVariantWrapper(variant, amount);
            if(!fluidStack.isEmpty()) {
                leftFluidStack = fluidStack;
            }
            //FluidTank tank = new FluidTank(20000);
            //tank.readFromNBT(compound.getCompound(LEFT_TANK));
            //leftFluidStack = tank.getFluid();
        }
        if(compound.contains(RIGHT_TANK)) {
            FluidVariant variant = FluidVariant.fromNbt(compound.getCompound(RIGHT_TANK).getCompound("variant"));
            long amount = compound.getCompound(RIGHT_TANK).getLong("amount");
            FluidVariantWrapper fluidStack = new FluidVariantWrapper(variant, amount);
            if(!fluidStack.isEmpty()) {
                rightFluidStack = fluidStack;
            }
        }

        ItemStack tanksUpgrade = ModItems.TANKS_UPGRADE.getDefaultInstance();
        //tanksUpgrade.set(ModDataComponents.FLUIDS.get(), new Fluids(leftFluidStack, rightFluidStack));
        NbtHelper.set(tanksUpgrade, ModDataHelper.FLUIDS, new Fluids(leftFluidStack, rightFluidStack));

        ItemStackHandler upgrades = new ItemStackHandler(upgradeSlots);
        upgrades.setStackInSlot(0, tanksUpgrade);
        NbtHelper.set(backpack, ModDataHelper.UPGRADES, upgrades);
        //backpack.set(ModDataComponents.UPGRADES.get(), InventoryHelper.itemsToList(upgradeSlots, upgrades));

        return backpack;
    }

    String TIER = "Tier";
    String INVENTORY = "Inventory";
    String TOOLS_INVENTORY = "ToolsInventory";
    String LEFT_TANK = "LeftTank";
    String RIGHT_TANK = "RightTank";
}