package com.tiviacz.travelersbackpack.inventory.menu;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class TravelersBackpackItemMenu extends TravelersBackpackBaseMenu
{
    public TravelersBackpackItemMenu(int windowID, Inventory playerInventory, FriendlyByteBuf data)
    {
        this(windowID, playerInventory, createInventory(playerInventory, data));
    }

    public TravelersBackpackItemMenu(int windowID, Inventory playerInventory, ITravelersBackpackContainer container)
    {
        super(ModMenuTypes.TRAVELERS_BACKPACK_ITEM.get(), windowID, playerInventory, container);
    }

    private static TravelersBackpackContainer createInventory(final Inventory inventory, final FriendlyByteBuf data)
    {
        Objects.requireNonNull(inventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        final ItemStack stack; //Get ItemStack from hand or capability to avoid sending a lot of information by packetBuffer
        final byte screenID = data.readByte();

        if(screenID == Reference.ITEM_SCREEN_ID)
        {
            stack = inventory.player.getItemBySlot(EquipmentSlot.MAINHAND);
        }
        else
        {
            if(data.writerIndex() == 5)
            {
                final int entityId = data.readInt();
                stack = CapabilityUtils.getWearingBackpack((Player)inventory.player.level.getEntity(entityId));

                if(stack.getItem() instanceof TravelersBackpackItem)
                {
                    return CapabilityUtils.getBackpackInv((Player)inventory.player.level.getEntity(entityId));
                }
            }
            else
            {
                stack = CapabilityUtils.getWearingBackpack(inventory.player);
            }
        }

        if(stack.getItem() instanceof TravelersBackpackItem)
        {
            if(screenID == Reference.WEARABLE_SCREEN_ID)
            {
                return CapabilityUtils.getBackpackInv(inventory.player);
            }
            else if(screenID == Reference.ITEM_SCREEN_ID)
            {
                return new TravelersBackpackContainer(stack, inventory.player, screenID);
            }
        }
        throw new IllegalStateException("ItemStack is not correct! " + stack);
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player)
    {
        if(clickType == ClickType.SWAP)
        {
            final ItemStack stack = player.getInventory().getItem(dragType);
            final ItemStack currentItem = player.getInventory().getSelected();

            if(!currentItem.isEmpty() && stack == currentItem)
            {
                return;
            }
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public void addPlayerInventoryAndHotbar(Inventory inventory, int currentItemIndex)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, 44 + x*18, (71 + this.container.getTier().getMenuSlotPlacementFactor()) + y*18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            if(x == currentItemIndex && this.container.getScreenID() == Reference.ITEM_SCREEN_ID)
            {
                this.addSlot(new DisabledSlot(inventory, x, 44 + x*18, 129 + this.container.getTier().getMenuSlotPlacementFactor()));
            }
            else
            {
                this.addSlot(new Slot(inventory, x, 44 + x*18, 129 + this.container.getTier().getMenuSlotPlacementFactor()));
            }
        }
    }
}