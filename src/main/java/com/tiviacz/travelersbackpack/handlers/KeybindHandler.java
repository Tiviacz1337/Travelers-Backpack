package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.init.ModNetwork;
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
    public static final KeyBinding TOGGLE_TANK = new KeyBinding(Reference.TOGGLE_TANK, InputUtil.Type.KEYSYM, InputUtil.fromTranslationKey("key.keyboard.n").getCode(), Reference.CATEGORY);
    public static final KeyBinding CYCLE_TOOL = new KeyBinding(Reference.CYCLE_TOOL, InputUtil.Type.KEYSYM, InputUtil.fromTranslationKey("key.keyboard.z").getCode(), Reference.CATEGORY);

    public static void initKeybinds()
    {
        KeyBindingHelper.registerKeyBinding(OPEN_INVENTORY);
        KeyBindingHelper.registerKeyBinding(TOGGLE_TANK);
        KeyBindingHelper.registerKeyBinding(CYCLE_TOOL);
    }

    public static void registerListeners()
    {
        ClientTickEvents.END_CLIENT_TICK.register(evt ->
        {
            ClientPlayerEntity player = evt.player;

            if(player == null) return;

            if(ComponentUtils.isWearingBackpack(player))
            {
                if(OPEN_INVENTORY.wasPressed())
                {
                    ClientPlayNetworking.send(ModNetwork.OPEN_SCREEN_ID, PacketByteBufs.empty());
                }

                if(player.getMainHandStack().getItem() instanceof HoseItem && player.getMainHandStack().getNbt() != null)
                {
                    if(TOGGLE_TANK.wasPressed())
                    {
                        ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, PacketByteBufs.copy(PacketByteBufs.create().writeDouble(0.0D).writeByte(Reference.TOGGLE_HOSE_TANK)));
                    }
                }

                if(CYCLE_TOOL.wasPressed()) //disableScrollWheel
                {
                    ItemStack heldItem = player.getMainHandStack();

                    if(!heldItem.isEmpty())
                    {
                        if(TravelersBackpackConfig.enableToolCycling)
                        {
                            if(ToolSlot.isValid(heldItem))
                            {
                                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, PacketByteBufs.copy(PacketByteBufs.create().writeDouble(1.0D).writeByte(Reference.SWAP_TOOL)));
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.getNbt() != null)
                            {
                                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, PacketByteBufs.copy(PacketByteBufs.create().writeDouble(1.0D).writeByte(Reference.SWITCH_HOSE_MODE)));
                            }
                        }
                    }
                }
            }
        });
    }
}
