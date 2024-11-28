package com.tiviacz.travelersbackpack.inventory.menu;

import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class BackpackBlockEntityMenu extends BackpackBaseMenu {
    private final ContainerLevelAccess access;
    private final Block backpackBlock;
    private final int infniteAccessUser;

    public BackpackBlockEntityMenu(int windowID, Inventory inventory, FriendlyByteBuf data) {
        this(windowID, inventory, isInfiniteAccess(data), getBlockEntity(inventory, data));
    }

    public BackpackBlockEntityMenu(int windowID, Inventory inventory, int entityId, BackpackWrapper wrapper) {
        super(ModMenuTypes.BACKPACK_BLOCK_MENU.get(), windowID, inventory, wrapper);
        this.access = ContainerLevelAccess.create(player.level(), getWrapper().getBackpackPos());
        this.backpackBlock = player.level().getBlockState(getWrapper().getBackpackPos()).getBlock();
        this.infniteAccessUser = entityId;
        this.wrapper.addUser(inventory.player);
    }

    private static int isInfiniteAccess(FriendlyByteBuf data) {
        return data.readInt();
    }

    private static BackpackWrapper getBlockEntity(Inventory inventory, FriendlyByteBuf data) {
        Objects.requireNonNull(inventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        BlockPos pos = data.readBlockPos();
        BlockEntity blockEntityAtPos = inventory.player.level().getBlockEntity(pos);

        if(blockEntityAtPos instanceof BackpackBlockEntity backpackBlockEntity) {
            backpackBlockEntity.getWrapper().addUser(inventory.player);
            backpackBlockEntity.getWrapper().setBackpackPos(pos);
            return backpackBlockEntity.getWrapper();
        }
        throw new IllegalStateException("Block Entity is not correct! " + blockEntityAtPos);
    }

    @Override
    public boolean stillValid(Player player) { //stillValid
        return this.access.evaluate((level, blockPos) -> !level.getBlockState(blockPos).is(this.backpackBlock) ? false : player.getId() == this.infniteAccessUser || player.canInteractWithBlock(blockPos, 4.0), true);
    }

    @Override
    public void removed(Player player) {
        if(player.containerMenu instanceof BackpackBaseMenu && player.level().isClientSide) {
            return;
        }
        this.wrapper.playersUsing.remove(player);
        super.removed(player);
    }
}
