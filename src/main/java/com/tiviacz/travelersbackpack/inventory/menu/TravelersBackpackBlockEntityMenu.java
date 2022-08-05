package com.tiviacz.travelersbackpack.inventory.menu;

import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class TravelersBackpackBlockEntityMenu extends TravelersBackpackBaseMenu
{
    public TravelersBackpackBlockEntityMenu(int windowID, Inventory inventory, FriendlyByteBuf data)
    {
        this(windowID, inventory, getBlockEntity(inventory, data));
    }

    public TravelersBackpackBlockEntityMenu(int windowID, Inventory inventory, ITravelersBackpackContainer container)
    {
        super(ModMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY.get(), windowID, inventory, container);

        container.setUsingPlayer(inventory.player);
    }

    private static TravelersBackpackBlockEntity getBlockEntity(final Inventory inventory, final FriendlyByteBuf data)
    {
        Objects.requireNonNull(inventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        final BlockEntity blockEntityAtPos = inventory.player.level.getBlockEntity(data.readBlockPos());

        if(blockEntityAtPos instanceof TravelersBackpackBlockEntity blockEntity)
        {
            return blockEntity;
        }
        throw new IllegalStateException("Block Entity is not correct! " + blockEntityAtPos);
    }

    @Override
    public boolean stillValid(Player player)
    {
        if(player.level.getBlockEntity(container.getPosition()) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            return blockEntity.isUsableByPlayer(player);
        }
        return false;
    }
}