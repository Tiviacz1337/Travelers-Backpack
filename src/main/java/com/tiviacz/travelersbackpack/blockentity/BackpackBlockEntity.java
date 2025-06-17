package com.tiviacz.travelersbackpack.blockentity;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
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
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BackpackBlockEntity extends BlockEntity implements MenuProvider {
    private BackpackWrapper wrapper = BackpackWrapper.DUMMY;
    private boolean isSleepingBagDeployed = false;
    public List<Integer> infiniteAccessUsers = new ArrayList<>();
    public int settingsUser = -1;

    @Nullable
    public Player player;

    public static final String BACKPACK = "Backpack";
    public static final String SLEEPING_BAG = "SleepingBag";
    public static final String SETTINGS_USER = "SettingsUser";

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BACKPACK.get(), pos, state);
    }

    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    public void removeWrapper() {
        this.wrapper = BackpackWrapper.DUMMY;
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.saveAdditional(compound, pRegistries);
        writeBackpack(compound, pRegistries);
        compound.putBoolean(SLEEPING_BAG, this.isSleepingBagDeployed);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        setBackpackFromNbt(compound, pRegistries);
        if(compound.contains(TIER)) {
            setBackpack(getOldDataBackpack(compound, pRegistries), pRegistries);
            compound.remove(TIER);
        }
        this.isSleepingBagDeployed = compound.getBoolean(SLEEPING_BAG);
        if(compound.contains(SETTINGS_USER)) {
            this.settingsUser = compound.getInt(SETTINGS_USER);
        }
        requestModelDataUpdate();
    }

    public void setBackpack(ItemStack backpack, HolderLookup.Provider registryAccess) {
        if(backpack.getItem() instanceof TravelersBackpackItem) {
            if(this.wrapper == BackpackWrapper.DUMMY) {
                this.wrapper = new BackpackWrapper(backpack.copy(), Reference.BLOCK_ENTITY_SCREEN_ID, registryAccess, null, getLevel());
                wrapper.setBackpackPos(getBlockPos());
                wrapper.saveHandler = () -> {
                    this.setChanged();
                    this.notifyBlockUpdate();
                    requestModelDataUpdate();
                };
                wrapper.abilityHandler = () -> {
                    if(getLevel() != null) {
                        getLevel().updateNeighborsAt(getBlockPos(), getBlockState().getBlock());

                        if(getBlockState().getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get()) {
                            ((TravelersBackpackBlock)getBlockState().getBlock()).tryAbsorbWater(getLevel(), getBlockPos());
                        }
                    }
                };
            } else {
                this.wrapper.setBackpackStack(backpack.copy());
            }
        }
    }

    private void setBackpackFromNbt(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        setBackpack(ItemStack.parseOptional(pRegistries, nbt.getCompound(BACKPACK)), pRegistries);
    }

    private void writeBackpack(CompoundTag ret, HolderLookup.Provider registries) {
        ItemStack backpackCopy = wrapper.getBackpackStack().copy();
        if(backpackCopy.getItem() instanceof TravelersBackpackItem) {
            ret.put(BACKPACK, backpackCopy.save(registries));
        }
    }

    public Direction getBlockDirection() {
        if(level == null || !(level.getBlockState(getBlockPos()).getBlock() instanceof TravelersBackpackBlock) || !level.getBlockState(getBlockPos()).hasProperty(TravelersBackpackBlock.FACING))
            return Direction.NORTH;
        return level.getBlockState(getBlockPos()).getValue(TravelersBackpackBlock.FACING);
    }

    public boolean isSleepingBagDeployed() {
        if(getWrapper().hasSleepingBag()) {
            return this.isSleepingBagDeployed;
        }
        return true;
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

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder pComponents) {
        super.collectImplicitComponents(pComponents);
        pComponents.addAll(this.wrapper.getBackpackStack().copy().getComponents());
    }

    public ItemStack toItemStack(ItemStack stack) {
        stack.applyComponents(this.wrapper.getBackpackStack().copy().getComponents());
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
    public Component getDisplayName() {
        return this.getDefaultName();
    }

    public Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
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

    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = this.saveWithoutMetadata(pRegistries);
        tag.putInt(SETTINGS_USER, this.settingsUser);
        return tag;
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

    public static RegistryFriendlyByteBuf saveSettingsExtraData(RegistryFriendlyByteBuf buf, BlockPos pos) {
        buf.writeBoolean(true);
        buf.writeBlockPos(pos);
        return buf;
    }

    //Forge

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries) {
        super.onDataPacket(net, pkt, pRegistries);
        this.handleUpdateTag(pkt.getTag(), pRegistries);
    }

    public void openBackpack(Player player, MenuProvider containerSupplier, BlockPos pos) {
        if(!player.level().isClientSide) {
            if(TravelersBackpackConfig.SERVER.backpackSettings.preventMultiplePlayersAccess.get()) {
                if(getWrapper() != BackpackWrapper.DUMMY && !getWrapper().getPlayersUsing().isEmpty()) {
                    return;
                }
            }
            if(this.infiniteAccessUsers.contains(player.getId())) {
                this.infiniteAccessUsers.remove((Object)player.getId());
            }
            player.openMenu(containerSupplier, buf -> buf.writeInt(-1).writeBlockPos(pos));
        }
    }

    public void openSettings(Player player, MenuProvider containerSupplier, BlockPos pos) {
        if(!player.level().isClientSide) {
            //Set settings user
            setSettingsUser(player);
            player.openMenu(containerSupplier, buf -> saveSettingsExtraData(buf, pos));
        }
    }

    public void openBackpackFromCommand(Player player, MenuProvider containerSupplier, BlockPos pos) {
        if(!player.level().isClientSide) {
            //Set user access to infinite if accessing from command
            if(!this.infiniteAccessUsers.contains(player.getId())) this.infiniteAccessUsers.add(player.getId());
            player.openMenu(containerSupplier, buf -> buf.writeInt(player.getId()).writeBlockPos(pos));
        }
    }

    public static ModelProperty<RenderInfo> RENDER_INFO = new ModelProperty<>();
    public static ModelProperty<Integer> DYE_COLOR = new ModelProperty<>();
    public static ModelProperty<Boolean> SLEEPING_BAG_DEPLOYED = new ModelProperty<>();
    public static ModelProperty<Integer> SLEEPING_BAG_COLOR = new ModelProperty<>();

    @Override
    public ModelData getModelData() {
        ModelData.Builder modelData = ModelData.builder();
        if(getWrapper().isDyed()) {
            modelData.with(DYE_COLOR, getWrapper().getDyeColor());
        }
        modelData.with(RENDER_INFO, getWrapper().getRenderInfo());
        modelData.with(SLEEPING_BAG_DEPLOYED, isSleepingBagDeployed());
        modelData.with(SLEEPING_BAG_COLOR, getWrapper().getSleepingBagColor());
        return modelData.build();
    }

    //Old data helper #TODO for removal
    public ItemStack getOldDataBackpack(CompoundTag compound, HolderLookup.Provider registries) {
        ItemStack backpack;
        if(level != null) {
            backpack = new ItemStack(level.getBlockState(getBlockPos()).getBlock().asItem());
        } else {
            backpack = ModItems.STANDARD_TRAVELERS_BACKPACK.toStack();
        }
        int tier = Tiers.LEATHER.getOrdinal();

        if(compound.contains(TIER)) {
            tier = compound.getInt(TIER);
            backpack.set(ModDataComponents.TIER, tier);
        }

        BackpackWrapper.initializeSize(backpack);

        int storageSlots = backpack.get(ModDataComponents.STORAGE_SLOTS);
        int toolSlots = backpack.get(ModDataComponents.TOOL_SLOTS);
        int upgradeSlots = backpack.get(ModDataComponents.UPGRADE_SLOTS);
        if(compound.contains(INVENTORY)) {
            ItemStackHandler inventory = new ItemStackHandler(99);
            inventory.deserializeNBT(registries, compound.getCompound(INVENTORY));
            backpack.set(ModDataComponents.BACKPACK_CONTAINER, InventoryHelper.itemsToList(storageSlots, inventory));
        }
        if(compound.contains(TOOLS_INVENTORY)) {
            ItemStackHandler tools = new ItemStackHandler(12);
            tools.deserializeNBT(registries, compound.getCompound(TOOLS_INVENTORY));
            backpack.set(ModDataComponents.TOOLS_CONTAINER, InventoryHelper.itemsToList(toolSlots, tools));
        }
        FluidStack leftFluidStack = FluidStack.EMPTY;
        FluidStack rightFluidStack = FluidStack.EMPTY;
        if(compound.contains(LEFT_TANK)) {
            FluidTank tank = new FluidTank(20000);
            tank.readFromNBT(registries, compound.getCompound(LEFT_TANK));
            leftFluidStack = tank.getFluid();
        }
        if(compound.contains(RIGHT_TANK)) {
            FluidTank tank = new FluidTank(20000);
            tank.readFromNBT(registries, compound.getCompound(RIGHT_TANK));
            rightFluidStack = tank.getFluid();
        }

        ItemStack tanksUpgrade = ModItems.TANKS_UPGRADE.toStack();
        tanksUpgrade.set(ModDataComponents.FLUIDS, new Fluids(leftFluidStack, rightFluidStack));

        ItemStackHandler upgrades = new ItemStackHandler(6);
        upgrades.setStackInSlot(0, tanksUpgrade);
        backpack.set(ModDataComponents.UPGRADES, InventoryHelper.itemsToList(upgradeSlots, upgrades));

        return backpack;
    }

    private static final String TIER = "Tier";
    private static final String INVENTORY = "Inventory";
    private static final String TOOLS_INVENTORY = "ToolsInventory";
    private static final String LEFT_TANK = "LeftTank";
    private static final String RIGHT_TANK = "RightTank";
}