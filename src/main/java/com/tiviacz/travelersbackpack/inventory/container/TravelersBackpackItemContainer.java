package com.tiviacz.travelersbackpack.inventory.container;

import com.tiviacz.travelersbackpack.init.ModContainerTypes;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.container.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import java.util.Objects;

public class TravelersBackpackItemContainer extends TravelersBackpackBaseContainer
{
    public TravelersBackpackItemContainer(int windowID, PlayerInventory playerInventory, PacketBuffer data)
    {
        this(windowID, playerInventory, createInventory(playerInventory, data));
    }

    public TravelersBackpackItemContainer(int windowID, PlayerInventory playerInventory, ITravelersBackpack inventory)
    {
        super(ModContainerTypes.TRAVELERS_BACKPACK_ITEM.get(), windowID, playerInventory, inventory);
    }

    private static TravelersBackpackInventory createInventory(final PlayerInventory playerInventory, final PacketBuffer data)
    {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        final ItemStack stack = data.readItemStack();
        final byte screenID = data.readByte();

        if(stack.getItem() instanceof TravelersBackpackItem)
        {
            return new TravelersBackpackInventory(stack, playerInventory.player, screenID);
        }
        throw new IllegalStateException("ItemStack is not correct! " + stack);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player)
    {
        if(clickTypeIn == ClickType.SWAP)
        {
            final ItemStack stack = player.inventory.getStackInSlot(dragType);
            final ItemStack currentItem = player.inventory.getCurrentItem();

            if(!currentItem.isEmpty() && stack == currentItem)
            {
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void addPlayerInventoryAndHotbar(PlayerInventory playerInv, int currentItemIndex)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 44 + x*18, 125 + y*18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            if(x == currentItemIndex && this.inventory.getScreenID() == Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID)
            {
                this.addSlot(new DisabledSlot(playerInv, x, 44 + x*18, 183));
            }
            else
            {
                this.addSlot(new Slot(playerInv, x, 44 + x*18, 183));
            }
        }
    }
}
