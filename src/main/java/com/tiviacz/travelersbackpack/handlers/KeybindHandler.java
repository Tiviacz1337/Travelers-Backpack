package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

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
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeByte(Reference.NO_SCREEN_ID).writeByte(Reference.OPEN_SCREEN).writeDouble(0.0D);

                    ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);
                }

                if(player.getMainHandStack().getItem() instanceof HoseItem && player.getMainHandStack().getNbt() != null)
                {
                    if(TOGGLE_TANK.wasPressed())
                    {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeByte(Reference.WEARABLE_SCREEN_ID).writeByte(Reference.TOGGLE_HOSE_TANK).writeDouble(0.0D);

                        ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);
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
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeByte(Reference.WEARABLE_SCREEN_ID).writeByte(Reference.SWAP_TOOL).writeDouble(1.0D);

                                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.getNbt() != null)
                            {
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeByte(Reference.WEARABLE_SCREEN_ID).writeByte(Reference.SWITCH_HOSE_MODE).writeDouble(1.0D);

                                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);
                            }
                        }
                    }
                }
            }
        });
    }

    public static boolean onMouseScroll(double deltaX, double deltaY)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        KeyBinding key1 = KeybindHandler.CYCLE_TOOL;

        if(!TravelersBackpackConfig.disableScrollWheel && deltaY != 0.0)
        {
            ClientPlayerEntity player = mc.player;

            if(player != null && player.isAlive() && key1.isPressed())
            {
                ItemStack backpack = ComponentUtils.getWearingBackpack(player);

                if(backpack != null && backpack.getItem() instanceof TravelersBackpackItem)
                {
                    if(!player.getMainHandStack().isEmpty())
                    {
                        ItemStack heldItem = player.getMainHandStack();

                        if(TravelersBackpackConfig.enableToolCycling)
                        {
                            if(ToolSlot.isValid(heldItem))
                            {
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeByte(Reference.WEARABLE_SCREEN_ID).writeByte(Reference.SWAP_TOOL).writeDouble(deltaY);

                                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);
                                return true;
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.getNbt() != null)
                            {
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeByte(Reference.WEARABLE_SCREEN_ID).writeByte(Reference.SWITCH_HOSE_MODE).writeDouble(deltaY);

                                ClientPlayNetworking.send(ModNetwork.SPECIAL_ACTION_ID, buf);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}