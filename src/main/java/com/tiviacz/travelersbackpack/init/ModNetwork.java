package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModNetwork
{
    public static final Identifier EQUIP_BACKPACK_ID = new Identifier(TravelersBackpack.MODID, "equip_backpack");
    public static final Identifier DEPLOY_SLEEPING_BAG_ID = new Identifier(TravelersBackpack.MODID, "deploy_sleeping_bag");
    public static final Identifier SPECIAL_ACTION_ID = new Identifier(TravelersBackpack.MODID, "special_action");
    public static final Identifier ABILITY_SLIDER_ID = new Identifier(TravelersBackpack.MODID, "ability_slider");
    public static final Identifier SORTER_ID = new Identifier(TravelersBackpack.MODID, "sorter");
    public static final Identifier SLOT_ID = new Identifier(TravelersBackpack.MODID, "slot");
    public static final Identifier MEMORY_ID = new Identifier(TravelersBackpack.MODID, "memory");
    public static final Identifier UPDATE_CONFIG_ID = new Identifier(TravelersBackpack.MODID,"update_config");

    public static void initClient()
    {
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_CONFIG_ID, (client, handler, buf, sender) ->
        {
            NbtCompound configNbt = buf.readNbt();
            client.execute(() ->
            {
                if(configNbt != null)
                {
                    TravelersBackpackConfig.fromNbt(configNbt);
                }
            });
        });
    }

    public static void initServer()
    {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeNbt(TravelersBackpackConfig.toNbt());
            sender.sendPacket(ModNetwork.UPDATE_CONFIG_ID, buf);
        });

        ServerPlayNetworking.registerGlobalReceiver(EQUIP_BACKPACK_ID, (server, player, handler, buf, response) ->
        {
            boolean equip = buf.readBoolean();

            server.execute(() -> {
                if(player != null) //&& !TravelersBackpack.enableCurios())
                {
                    if(equip)
                    {
                        if(!ComponentUtils.isWearingBackpack(player))
                        {
                            ServerActions.equipBackpack(player);
                        }
                        else
                        {
                            player.onHandledScreenClosed();
                            player.sendMessage(Text.translatable(Reference.OTHER_BACKPACK), false);
                        }
                    }
                    else
                    {
                        if(ComponentUtils.isWearingBackpack(player))
                        {
                            ServerActions.unequipBackpack(player);
                        }
                        else
                        {
                            player.onHandledScreenClosed();
                            player.sendMessage(Text.translatable(Reference.NO_BACKPACK), false);
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(DEPLOY_SLEEPING_BAG_ID, (server, player, handler, buf, response) ->
        {
            BlockPos pos = buf.readBlockPos();

            server.execute(() -> {
                if(player != null)
                {
                    ServerActions.toggleSleepingBag(player, pos);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SPECIAL_ACTION_ID, (server, player, handler, buf, response) ->
        {
            byte screenID = buf.readByte();
            byte typeOfAction = buf.readByte();
            double scrollDelta = buf.readDouble();

            server.execute(() -> {
                if(player != null)
                {
                    if(typeOfAction == Reference.SWAP_TOOL)
                    {
                        ServerActions.swapTool(player, scrollDelta);
                    }

                    else if(typeOfAction == Reference.SWITCH_HOSE_MODE)
                    {
                        ServerActions.switchHoseMode(player, scrollDelta);
                    }

                    else if(typeOfAction == Reference.TOGGLE_HOSE_TANK)
                    {
                        ServerActions.toggleHoseTank(player);
                    }

                    else if(typeOfAction == Reference.EMPTY_TANK)
                    {
                        ServerActions.emptyTank(scrollDelta, player, player.world, screenID);
                    }

                    else if(typeOfAction == Reference.OPEN_SCREEN)
                    {
                        if(ComponentUtils.isWearingBackpack(player))
                        {
                            TravelersBackpackInventory.openHandledScreen(player, ComponentUtils.getWearingBackpack(player), Reference.WEARABLE_SCREEN_ID);
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ABILITY_SLIDER_ID, (server, player, handler, buf, response) ->
        {
            byte screenID = buf.readByte();
            boolean sliderValue = buf.readBoolean();

            server.execute(() -> {
                if(player != null)
                {
                    if(screenID == Reference.WEARABLE_SCREEN_ID && ComponentUtils.isWearingBackpack(player))
                    {
                        ServerActions.switchAbilitySlider(player, sliderValue);
                    }
                    else if(screenID == Reference.BLOCK_ENTITY_SCREEN_ID && player.currentScreenHandler instanceof TravelersBackpackBlockEntityScreenHandler)
                    {
                        ServerActions.switchAbilitySliderBlockEntity(player, ((TravelersBackpackBlockEntityScreenHandler)player.currentScreenHandler).inventory.getPosition(), sliderValue);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SORTER_ID, (server, player, handler, buf, response) ->
        {
            byte screenID = buf.readByte();
            byte button = buf.readByte();
            boolean shiftPressed = buf.readBoolean();

            server.execute(() -> {
                if(player != null)
                {
                    ServerActions.sortBackpack(player, screenID, button, shiftPressed);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SLOT_ID, (server, player, handler, buf, response) ->
        {
            final byte screenID = buf.readByte();
            final boolean isActive = buf.readBoolean();
            final int[] selectedSlots = buf.readIntArray();

            server.execute(() -> {
                if(player != null)
                {
                    if(screenID == Reference.WEARABLE_SCREEN_ID)
                    {
                        SlotManager manager = ComponentUtils.getBackpackInv(player).getSlotManager();
                        manager.setSelectorActive(SlotManager.UNSORTABLE, isActive);
                        manager.setUnsortableSlots(selectedSlots, true);
                        manager.setSelectorActive(SlotManager.UNSORTABLE, !isActive);
                    }
                    if(screenID == Reference.ITEM_SCREEN_ID)
                    {
                        SlotManager manager = ((TravelersBackpackItemScreenHandler)player.currentScreenHandler).inventory.getSlotManager();
                        manager.setSelectorActive(SlotManager.UNSORTABLE, isActive);
                        manager.setUnsortableSlots(selectedSlots, true);
                        manager.setSelectorActive(SlotManager.UNSORTABLE, !isActive);
                    }
                    if(screenID == Reference.BLOCK_ENTITY_SCREEN_ID)
                    {
                        SlotManager manager = ((TravelersBackpackBlockEntityScreenHandler)player.currentScreenHandler).inventory.getSlotManager();
                        manager.setSelectorActive(SlotManager.UNSORTABLE, isActive);
                        manager.setUnsortableSlots(selectedSlots, true);
                        manager.setSelectorActive(SlotManager.UNSORTABLE, !isActive);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(MEMORY_ID, (server, player, handler, buf, response) ->
        {
            final byte screenID = buf.readByte();
            final boolean isActive = buf.readBoolean();
            final int[] selectedSlots = buf.readIntArray();
            final ItemStack[] stacks = new ItemStack[selectedSlots.length];

            for(int i = 0; i < selectedSlots.length; i++)
            {
                stacks[i] = buf.readItemStack();
            }

            server.execute(() -> {
                if(player != null)
                {
                    if(screenID == Reference.WEARABLE_SCREEN_ID)
                    {
                        SlotManager manager = ComponentUtils.getBackpackInv(player).getSlotManager();
                        manager.setSelectorActive(SlotManager.MEMORY, isActive);
                        manager.setMemorySlots(selectedSlots, stacks, true);
                        manager.setSelectorActive(SlotManager.MEMORY, !isActive);
                    }
                    if(screenID == Reference.ITEM_SCREEN_ID)
                    {
                        SlotManager manager = ((TravelersBackpackItemScreenHandler)player.currentScreenHandler).inventory.getSlotManager();
                        manager.setSelectorActive(SlotManager.MEMORY, isActive);
                        manager.setMemorySlots(selectedSlots, stacks, true);
                        manager.setSelectorActive(SlotManager.MEMORY, !isActive);
                    }
                    if(screenID == Reference.BLOCK_ENTITY_SCREEN_ID)
                    {
                        SlotManager manager = ((TravelersBackpackBlockEntityScreenHandler)player.currentScreenHandler).inventory.getSlotManager();
                        manager.setSelectorActive(SlotManager.MEMORY, isActive);
                        manager.setMemorySlots(selectedSlots, stacks, true);
                        manager.setSelectorActive(SlotManager.MEMORY, !isActive);
                    }
                }
            });
        });
    }
}