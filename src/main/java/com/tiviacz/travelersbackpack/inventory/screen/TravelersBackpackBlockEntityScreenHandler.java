package com.tiviacz.travelersbackpack.inventory.screen;

import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;

import java.util.Objects;

public class TravelersBackpackBlockEntityScreenHandler extends TravelersBackpackBaseScreenHandler
{
    public TravelersBackpackBlockEntityScreenHandler(int windowID, PlayerInventory playerInventory, PacketByteBuf data)
    {
        this(windowID, playerInventory, getBlockEntity(playerInventory, data));
    }

    public TravelersBackpackBlockEntityScreenHandler(int windowID, PlayerInventory playerInventory, ITravelersBackpackInventory inventory)
    {
        super(ModScreenHandlerTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY, windowID, playerInventory, inventory);

        inventory.setUsingPlayer(playerInventory.player);
    }

    private static TravelersBackpackBlockEntity getBlockEntity(final PlayerInventory playerInventory, final PacketByteBuf data)
    {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        final BlockEntity blockEntityAtPos = playerInventory.player.world.getBlockEntity(data.readBlockPos());

        if(blockEntityAtPos instanceof TravelersBackpackBlockEntity)
        {
            return (TravelersBackpackBlockEntity)blockEntityAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + blockEntityAtPos);
    }

    @Override
    public boolean canUse(PlayerEntity playerIn)
    {
        BlockEntity blockEntity = playerIn.world.getBlockEntity(inventory.getPosition());

        if(blockEntity instanceof TravelersBackpackBlockEntity)
        {
            return ((TravelersBackpackBlockEntity)blockEntity).isUsableByPlayer(playerIn);
        }
        return false;
    }
}