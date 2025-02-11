package com.tiviacz.travelersbackpack.inventory.menu;

import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.SlotPositioner;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class BackpackSettingsMenu extends AbstractContainerMenu {
    protected final Inventory inventory;
    protected final BackpackWrapper wrapper;
    public int extendedScreenOffset = 0;
    public final Player player;

    //BackpackBlockEntity
    private ContainerLevelAccess access;
    private Block backpackBlock;

    public BackpackSettingsMenu(int windowID, Inventory playerInventory, FriendlyByteBuf data) {
        this(windowID, playerInventory, createWrapper(playerInventory, data));
    }

    public BackpackSettingsMenu(int windowID, Inventory playerInventory, BackpackWrapper wrapper) {
        this(ModMenuTypes.BACKPACK_SETTINGS_MENU, windowID, playerInventory, wrapper);

        if(this.wrapper.getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
            this.access = ContainerLevelAccess.create(player.level(), getWrapper().getBackpackPos());
            this.backpackBlock = player.level().getBlockState(getWrapper().getBackpackPos()).getBlock();
            this.wrapper.addUser(inventory.player);
        } else {
            this.wrapper.addUser(playerInventory.player);
        }
    }

    public BackpackSettingsMenu(final MenuType<?> type, final int windowID, final Inventory inventory, final BackpackWrapper wrapper) {
        super(type, windowID);
        this.inventory = inventory;
        this.player = inventory.player;
        this.wrapper = wrapper;
        this.addSlots();
    }

    public BackpackWrapper getWrapper() {
        return this.wrapper;
    }

    public Inventory getPlayerInventory() {
        return this.inventory;
    }

    public void updateSlots() {
        this.lastSlots.clear();
        this.slots.clear();
        this.remoteSlots.clear();

        this.addSlots();
    }

    public void addSlots() {
        //Storage Slots
        this.addBackpackStorageSlots(wrapper);

        //Player Inventory
        this.addPlayerInventoryAndHotbar(inventory, inventory.selected);
    }

    public void addBackpackStorageSlots(BackpackWrapper wrapper) {
        SlotPositioner pos = wrapper.getSlotPositioner();
        int slot = 0;

        for(int i = 0; i < pos.getRows(); i++) {
            for(int j = 0; j < pos.getSlotsInRow(); j++) {
                if(slot >= wrapper.getStorage().getSlots()) break;
                this.addSlot(new BackpackSlotItemHandler(wrapper.getStorage(), slot, this.extendedScreenOffset + 8 + j * 18, 18 + i * 18));
                slot++;
            }
        }
    }

    public void addPlayerInventoryAndHotbar(Inventory inventory, int currentItemIndex) {
        int modifiedOffset = this.extendedScreenOffset;
        SlotPositioner pos = wrapper.getSlotPositioner();
        if(pos.isExtended()) {
            modifiedOffset += 18;
        }
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, modifiedOffset + 8 + x * 18, (18 + pos.getRows() * 18 + 14) + y * 18));
            }
        }

        if(wrapper.getScreenID() == Reference.ITEM_SCREEN_ID) {
            for(int x = 0; x < 9; x++) {
                if(x == currentItemIndex) {
                    this.addSlot(new DisabledSlot(inventory, x, modifiedOffset + 8 + x * 18, pos.getRows() * 18 + 10 + 80));
                } else {
                    this.addSlot(new Slot(inventory, x, modifiedOffset + 8 + x * 18, pos.getRows() * 18 + 10 + 80));
                }
            }
        } else {
            for(int x = 0; x < 9; x++) {
                this.addSlot(new Slot(inventory, x, modifiedOffset + 8 + x * 18, pos.getRows() * 18 + 10 + 80));
            }
        }
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {

    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        if(!player.level().isClientSide) {
            if(getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
                BlockPos pos = getWrapper().getBackpackPos();
                if(pos != null && player.level().getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                    backpackBlockEntity.removeSettingsUser();
                }
            }
        }
        if(player.containerMenu instanceof BackpackSettingsMenu && player.level().isClientSide) {
            return;
        }
        this.wrapper.playersUsing.remove(player);
        super.removed(player);
    }

    @Override
    public boolean stillValid(Player player) {
        if(getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
            return this.access.evaluate((level, blockPos) -> !level.getBlockState(blockPos).is(this.backpackBlock) ? false : player.distanceToSqr((double)blockPos.getX() + (double)0.5F, (double)blockPos.getY() + (double)0.5F, (double)blockPos.getZ() + (double)0.5F) <= (double)64.0F, true);
        } else {
            if(getWrapper().getBackpackOwner() != null) {
                return getWrapper().getBackpackOwner().isAlive() && ComponentUtils.isWearingBackpack(getWrapper().getBackpackOwner());
            }
            return true;
        }
    }

    private static BackpackWrapper createWrapper(Inventory inventory, FriendlyByteBuf data) {
        Objects.requireNonNull(inventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        boolean isBlockEntity = data.readBoolean();
        if(isBlockEntity) {
            return getBlockEntity(inventory, data);
        } else {
            return getWrapper(inventory, data);
        }
    }

    private static BackpackWrapper getWrapper(Inventory inventory, FriendlyByteBuf data) {
        //Read all data with correct order
        byte screenID = data.readByte();
        BlockPos pos = data.readBlockPos(); //Not used here
        if(screenID == Reference.WEARABLE_SCREEN_ID) {
            return ComponentUtils.getBackpackWrapper(inventory.player);
        } else {
            return new BackpackWrapper(inventory.player.getItemInHand(InteractionHand.MAIN_HAND), screenID, inventory.player, inventory.player.level());
        }
    }

    private static BackpackWrapper getBlockEntity(Inventory inventory, FriendlyByteBuf data) {
        //Read data
        BlockPos pos = data.readBlockPos();
        BlockEntity blockEntityAtPos = inventory.player.level().getBlockEntity(pos);
        if(blockEntityAtPos instanceof BackpackBlockEntity backpackBlockEntity) {
            backpackBlockEntity.getWrapper().addUser(inventory.player);
            backpackBlockEntity.getWrapper().setBackpackPos(pos);
            backpackBlockEntity.setSettingsUser(inventory.player);
            return backpackBlockEntity.getWrapper();
        }
        throw new IllegalStateException("Block Entity is not correct! " + blockEntityAtPos);
    }
}