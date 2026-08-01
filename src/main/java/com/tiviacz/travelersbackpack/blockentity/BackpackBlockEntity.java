package com.tiviacz.travelersbackpack.blockentity;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BackpackBlockEntity extends BlockEntity implements MenuProvider, RenderDataBlockEntity {
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
        super(ModBlockEntityTypes.BACKPACK, pos, state);
    }

    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    public void removeWrapper() {
        this.wrapper = BackpackWrapper.DUMMY;
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        writeBackpack(output);
        output.putBoolean(SLEEPING_BAG, this.isSleepingBagDeployed);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        setBackpackFromNbt(valueInput);
        this.isSleepingBagDeployed = valueInput.getBooleanOr(SLEEPING_BAG, false);
        this.settingsUser = valueInput.getIntOr(SETTINGS_USER, -1);
    }

    public void setBackpack(ItemStack backpack, HolderLookup.Provider registryAccess) {
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

    private void setBackpackFromNbt(ValueInput valueInput) {
        setBackpack(valueInput.read(BACKPACK, ItemStack.OPTIONAL_CODEC).orElse(new ItemStack(Items.AIR, 0)), valueInput.lookup());
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if(this.wrapper != BackpackWrapper.DUMMY) {
            this.wrapper.setLevel(level);
        }
    }

    private void writeBackpack(ValueOutput valueOutput) {
        ItemStack backpackCopy = wrapper.getBackpackStack().copy();
        if(backpackCopy.getItem() instanceof TravelersBackpackItem) {
            valueOutput.store(BACKPACK, ItemStack.OPTIONAL_CODEC, backpackCopy);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if(this.level != null) {
            this.level.updateNeighbourForOutputSignal(pos, state.getBlock());
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
                if(!level.isClientSide()) {
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
        if(!player.level().isClientSide()) {
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

    @Override
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

    //Fabric

    public void openBackpack(Player player, MenuProvider containerSupplier, BlockPos pos) {
        if(!player.level().isClientSide()) {
            if(TravelersBackpackConfig.SERVER.backpackSettings.preventMultiplePlayersAccess.get()) {
                if(getWrapper() != BackpackWrapper.DUMMY && (!getWrapper().getPlayersUsing().isEmpty() && !getWrapper().getPlayersUsing().contains(player))) {
                    return;
                }
            }
            if(this.infiniteAccessUsers.contains(player.getId())) {
                this.infiniteAccessUsers.remove((Object)player.getId());
            }
            player.openMenu(new ExtendedScreen<>(containerSupplier, saveExtraData(-1, pos)));
        }
    }

    public void openSettings(Player player, MenuProvider containerSupplier, BlockPos pos) {
        if(!player.level().isClientSide()) {
            //Set settings user
            setSettingsUser(player);
            player.openMenu(new ExtendedScreen<>(containerSupplier, saveSettingsExtraData(pos)));
        }
    }

    public void openBackpackFromCommand(Player player, MenuProvider containerSupplier, BlockPos pos) {
        if(!player.level().isClientSide()) {
            //Set user access to infinite if accessing from command
            if(!this.infiniteAccessUsers.contains(player.getId())) this.infiniteAccessUsers.add(player.getId());
            player.openMenu(new ExtendedScreen<>(containerSupplier, saveExtraData(player.getId(), pos)));
        }
    }

    @Override
    public boolean shouldCloseCurrentScreen() {
        if(this.wrapper == BackpackWrapper.DUMMY) {
            return true;
        } else {
            if(!getWrapper().getPlayersUsing().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static ModScreenHandlerTypes.SettingsScreenData saveSettingsExtraData(BlockPos pos) {
        return new ModScreenHandlerTypes.SettingsScreenData(true, Reference.BLOCK_ENTITY_SCREEN_ID, pos, -1);
    }

    public static ModScreenHandlerTypes.BlockEntityScreenData saveExtraData(int entityId, BlockPos pos) {
        return new ModScreenHandlerTypes.BlockEntityScreenData(entityId, pos);
    }

    private record ExtendedScreen<D>(MenuProvider containerSupplier,
                                     D screenOpeningData) implements ExtendedScreenHandlerFactory<D> {
        @Override
        public boolean shouldCloseCurrentScreen() {
            return containerSupplier.shouldCloseCurrentScreen();
        }

        @Override
        public D getScreenOpeningData(ServerPlayer player) {
            return screenOpeningData;
        }

        @Override
        public Component getDisplayName() {
            return containerSupplier.getDisplayName();
        }

        @Override
        public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return containerSupplier.createMenu(i, inventory, player);
        }
    }

    @Nullable
    @Override
    public Object getRenderData() {
        return new BackpackRenderData(getWrapper().getRenderInfo(), getWrapper().getDyeColor(), isSleepingBagDeployed(), getWrapper().getSleepingBagColor());
    }

    public record BackpackRenderData(RenderInfo info, int dyeColor, boolean isSleepingBagDeployed,
                                     int sleepingBagColor) {
    }
}