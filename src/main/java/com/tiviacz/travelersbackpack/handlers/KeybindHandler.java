package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.network.ModNetwork;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;

public class KeybindHandler
{
    public static final KeyBinding OPEN_INVENTORY = new KeyBinding(Reference.INVENTORY, InputUtil.Type.KEYSYM, InputUtil.fromTranslationKey("key.keyboard.b").getCode(), Reference.CATEGORY);
    //public static final KeyBinding POTION_BAG_MODE = new KeyBinding("key.extraalchemy.potion_bag_mode", InputUtil.Type.KEYSYM, InputUtil.fromTranslationKey("key.keyboard.k").getCode(), Reference.CATEGORY);
    public static final KeyBinding CYCLE_TOOL = new KeyBinding(Reference.CYCLE_TOOL, InputUtil.Type.KEYSYM, InputUtil.fromTranslationKey("key.keyboard.z").getCode(), Reference.CATEGORY);

    public static void initKeybinds()
    {
        KeyBindingHelper.registerKeyBinding(OPEN_INVENTORY);
        KeyBindingHelper.registerKeyBinding(CYCLE_TOOL);
    }

    public static void registerListeners()
    {
        ClientTickEvents.END_CLIENT_TICK.register(evt ->
        {
            ClientPlayerEntity player = evt.player;

            if(player != null && ComponentUtils.isWearingBackpack(player))
            {
                if(OPEN_INVENTORY.wasPressed())
                {
                    ClientPlayNetworking.send(ModNetwork.OPEN_SCREEN_ID, PacketByteBufs.empty());
                }
            }

            if(TravelersBackpackConfig.disableScrollWheel && CYCLE_TOOL.wasPressed())
            {
                ItemStack heldItem = player.getMainHandStack();

                if(!heldItem.isEmpty())
                {
                    if(TravelersBackpackConfig.enableToolCycling)
                    {
                        if(ToolSlot.isValid(heldItem))
                        {
                            ClientPlayNetworking.send(ModNetwork.CYCLE_TOOL_ID, PacketByteBufs.copy(PacketByteBufs.create().writeDouble(1.0D).writeByte(Reference.CYCLE_TOOL_ACTION)));
                        }
                    }
                }
            }

         /*   if(player.getHeldItemMainhand().getItem() instanceof HoseItem && player.getHeldItemMainhand().getTag() != null)
            {
                if(ModClientEventHandler.TOGGLE_TANK.isPressed())
                {
                    TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(0, Reference.TOGGLE_HOSE_TANK));
                }
            }

            KeyBinding key = ModClientEventHandler.CYCLE_TOOL;

            if(TravelersBackpackConfig.disableScrollWheel && key.isPressed())
            {
                ItemStack heldItem = player.getHeldItemMainhand();

                if(!heldItem.isEmpty())
                {
                    if(TravelersBackpackConfig.enableToolCycling)
                    {
                        if(ToolSlotItemHandler.isValid(heldItem))
                        {
                            TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(1.0D, Reference.CYCLE_TOOL_ACTION));
                        }
                    }

                    if(heldItem.getItem() instanceof HoseItem)
                    {
                        if(heldItem.getTag() != null)
                        {
                            TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(1.0D, Reference.SWITCH_HOSE_ACTION));
                        }
                    }
                }
            } */
        });
    }
}
