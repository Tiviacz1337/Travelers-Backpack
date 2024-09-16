package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModComponentTypes;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.network.AbilitySliderPacket;
import com.tiviacz.travelersbackpack.network.SpecialActionPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class KeybindHandler
{
    private static final String CATEGORY = "key.travelersbackpack.category";
    public static final KeyBinding OPEN_BACKPACK = new KeyBinding("key.travelersbackpack.inventory", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_B, CATEGORY);
    public static final KeyBinding SORT_BACKPACK = new KeyBinding("key.travelersbackpack.sort", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), CATEGORY);
    public static final KeyBinding ABILITY = new KeyBinding("key.travelersbackpack.ability", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), CATEGORY);
    public static final KeyBinding SWITCH_TOOL = new KeyBinding("key.travelersbackpack.cycle_tool", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_Z, CATEGORY);
    public static final KeyBinding TOGGLE_TANK = new KeyBinding("key.travelersbackpack.toggle_tank", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_N, CATEGORY);

    public static void initKeybinds()
    {
        KeyBindingHelper.registerKeyBinding(OPEN_BACKPACK);
        KeyBindingHelper.registerKeyBinding(SORT_BACKPACK);
        KeyBindingHelper.registerKeyBinding(ABILITY);
        KeyBindingHelper.registerKeyBinding(SWITCH_TOOL);
        KeyBindingHelper.registerKeyBinding(TOGGLE_TANK);
    }

    public static void registerListeners()
    {
        ClientTickEvents.START_CLIENT_TICK.register(evt ->
        {
            PlayerEntity player = evt.player;
            if(player == null) return;

            if(ComponentUtils.isWearingBackpack(player))
            {
                while(OPEN_BACKPACK.wasPressed())
                {
                    ClientPlayNetworking.send(new SpecialActionPacket(Reference.NO_SCREEN_ID, Reference.OPEN_SCREEN, 0.0D));
                }

                while(ABILITY.wasPressed())
                {
                    if(TravelersBackpackConfig.isAbilityAllowed(ComponentUtils.getWearingBackpack(player)))
                    {
                        boolean ability = ComponentUtils.getBackpackInv(player).getAbilityValue();

                        ClientPlayNetworking.send(new AbilitySliderPacket(Reference.WEARABLE_SCREEN_ID, !ability));

                        player.sendMessage(Text.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"), true);
                    }
                }

                if(player.getMainHandStack().getItem() instanceof HoseItem && player.getMainHandStack().contains(ModComponentTypes.HOSE_MODES))
                {
                    while(TOGGLE_TANK.wasPressed())
                    {
                        ClientPlayNetworking.send(new SpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.TOGGLE_HOSE_TANK, 0.0D));
                    }
                }

                if(TravelersBackpackConfig.getConfig().client.disableScrollWheel)
                {
                    ItemStack heldItem = player.getMainHandStack();

                    while(SWITCH_TOOL.wasPressed())
                    {
                        if(!heldItem.isEmpty())
                        {
                            if(heldItem.getItem() instanceof HoseItem)
                            {
                                if(heldItem.contains(ModComponentTypes.HOSE_MODES))
                                {
                                    ClientPlayNetworking.send(new SpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, 1.0D));
                                }
                            }

                            if(TravelersBackpackConfig.getConfig().client.enableToolCycling)
                            {
                                if(ToolSlot.isValid(heldItem))
                                {
                                    ClientPlayNetworking.send(new SpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, 1.0D));
                                }
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

        if(!TravelersBackpackConfig.getConfig().client.disableScrollWheel && deltaY != 0.0)
        {
            PlayerEntity player = mc.player;

            if(player != null && player.isAlive() && KeybindHandler.SWITCH_TOOL.isPressed())
            {
                if(ComponentUtils.isWearingBackpack(player))
                {
                    if(!player.getMainHandStack().isEmpty())
                    {
                        ItemStack heldItem = player.getMainHandStack();

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.contains(ModComponentTypes.HOSE_MODES))
                            {
                                ClientPlayNetworking.send(new SpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, deltaY));
                                return true;
                            }
                        }

                        if(TravelersBackpackConfig.getConfig().client.enableToolCycling)
                        {
                            if(ToolSlot.isValid(heldItem))
                            {
                                ClientPlayNetworking.send(new SpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, deltaY));
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