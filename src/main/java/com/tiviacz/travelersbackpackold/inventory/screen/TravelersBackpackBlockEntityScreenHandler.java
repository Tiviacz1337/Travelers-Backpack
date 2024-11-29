package com.tiviacz.travelersbackpackold.inventory.screen;

import com.tiviacz.travelersbackpackold.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpackneo.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpackold.inventory.ITravelersBackpackInventory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import java.util.Objects;

public class TravelersBackpackBlockEntityScreenHandler extends TravelersBackpackBaseScreenHandler
{
    public TravelersBackpackBlockEntityScreenHandler(int windowID, PlayerInventory playerInventory, ModScreenHandlerTypes.BlockEntityScreenData data)
    {
        this(windowID, playerInventory, getBlockEntity(playerInventory, data));
    }

    public TravelersBackpackBlockEntityScreenHandler(int windowID, PlayerInventory playerInventory, ITravelersBackpackInventory inventory)
    {
        super(ModScreenHandlerTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY, windowID, playerInventory, inventory);

        inventory.setUsingPlayer(playerInventory.player);
    }

    private static TravelersBackpackBlockEntity getBlockEntity(final PlayerInventory playerInventory, final ModScreenHandlerTypes.BlockEntityScreenData data)
    {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        final BlockEntity blockEntityAtPos = playerInventory.player.getWorld().getBlockEntity(data.pos());

        if(blockEntityAtPos instanceof TravelersBackpackBlockEntity blockEntity)
        {
            return blockEntity;
        }
        throw new IllegalStateException("Block entity is not correct! " + blockEntityAtPos);
    }

    @Override
    public boolean canUse(PlayerEntity playerIn)
    {
        if(playerIn.getWorld().getBlockEntity(inventory.getPosition()) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            return blockEntity.isUsableByPlayer(playerIn);
        }
        return false;
    }
}