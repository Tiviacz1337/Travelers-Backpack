package com.tiviacz.travelersbackpack.inventory.container;

import com.tiviacz.travelersbackpack.init.ModContainerTypes;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpack;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

import java.util.Objects;

public class TravelersBackpackTileContainer extends TravelersBackpackBaseContainer
{
    public TravelersBackpackTileContainer(int windowID, PlayerInventory playerInventory, PacketBuffer data)
    {
        this(windowID, playerInventory, getTileEntity(playerInventory, data));
    }

    public TravelersBackpackTileContainer(int windowID, PlayerInventory playerInventory, ITravelersBackpack inventory)
    {
        super(ModContainerTypes.TRAVELERS_BACKPACK_TILE.get(), windowID, playerInventory, inventory);
    }

    private static TravelersBackpackTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data)
    {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());

        if(tileAtPos instanceof TravelersBackpackTileEntity)
        {
            return (TravelersBackpackTileEntity)tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        TileEntity tile = playerIn.world.getTileEntity(inventory.getPosition());

        if(tile instanceof TravelersBackpackTileEntity)
        {
           return ((TravelersBackpackTileEntity)tile).isUsableByPlayer(playerIn);
        }
        return false;
    }
}
