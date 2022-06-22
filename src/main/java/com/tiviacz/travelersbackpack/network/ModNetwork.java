package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.MessageType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModNetwork
{
    public static final Identifier EQUIP_BACKPACK_ID = new Identifier(TravelersBackpack.MODID, "equip_backpack");
    public static final Identifier UNEQUIP_BACKPACK_ID = new Identifier(TravelersBackpack.MODID, "unequip_backpack");
    public static final Identifier OPEN_SCREEN_ID = new Identifier(TravelersBackpack.MODID, "open_screen");
    public static final Identifier DEPLOY_SLEEPING_BAG_ID = new Identifier(TravelersBackpack.MODID, "deploy_sleeping_bag");
    public static final Identifier CYCLE_TOOL_ID = new Identifier(TravelersBackpack.MODID, "cycle_tool");

    public static void initServer()
    {
        ServerPlayNetworking.registerGlobalReceiver(EQUIP_BACKPACK_ID, (server, player, handler, buf, response) ->
        {
            server.execute(() -> {
                if(player != null)
                {
                    if(!ComponentUtils.isWearingBackpack(player))
                    {
                        ServerActions.equipBackpack(player);
                    }
                    else
                    {
                        player.closeScreenHandler();
                        player.sendMessage(new TranslatableText(Reference.OTHER_BACKPACK), MessageType.CHAT, player.getUuid());
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(UNEQUIP_BACKPACK_ID, (server, player, handler, buf, response) ->
        {
            server.execute(() -> {
                if(player != null)
                {
                    if(ComponentUtils.isWearingBackpack(player))
                    {
                        ServerActions.unequipBackpack(player);
                    }
                    else
                    {
                        player.closeScreenHandler();
                        player.sendMessage(new TranslatableText(Reference.NO_BACKPACK), MessageType.CHAT, player.getUuid());
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_ID, (server, player, handler, buf, response) ->
        {
            server.execute(() -> {
                if(player != null)
                {
                    if(ComponentUtils.isWearingBackpack(player))
                    {
                        TravelersBackpackInventory.openGUI(player, ComponentUtils.getWearingBackpack(player), Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID);
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

        ServerPlayNetworking.registerGlobalReceiver(CYCLE_TOOL_ID, (server, player, handler, buf, response) ->
        {
            double scrollDelta = buf.readDouble();
            byte actionId = buf.readByte();

            server.execute(() -> {
                if(player != null)
                {
                    if(ComponentUtils.isWearingBackpack(player))
                    {
                        if(actionId == Reference.CYCLE_TOOL_ACTION)
                        {
                            ServerActions.cycleTool(player, scrollDelta);
                        }

                        else if(actionId == Reference.SWITCH_HOSE_ACTION)
                        {
                            //ServerActions.switchHoseMode(player, scrollDelta);
                        }

                        else if(actionId == Reference.TOGGLE_HOSE_TANK)
                        {
                           // ServerActions.toggleHoseTank(serverPlayerEntity);
                        }

                        else if(actionId == Reference.EMPTY_TANK)
                        {
                            ServerActions.emptyTank(scrollDelta, player, player.world);
                        }
                    }
                }
            });
        });
    }
}