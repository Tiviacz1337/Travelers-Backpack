package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.item.HoseItem;
import com.tiviacz.travelersbackpack.network.ServerboundAbilitySliderPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
    private static final String CATEGORY = "key.travelersbackpack.category";
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.travelersbackpack.inventory", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping SORT_BACKPACK = new KeyMapping("key.travelersbackpack.sort", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY = new KeyMapping("key.travelersbackpack.ability", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping SWITCH_TOOL = new KeyMapping("key.travelersbackpack.cycle_tool", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping TOGGLE_TANK = new KeyMapping("key.travelersbackpack.toggle_tank", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);

    public static void initKeybinds() {
        KeyBindingHelper.registerKeyBinding(OPEN_BACKPACK);
        KeyBindingHelper.registerKeyBinding(SORT_BACKPACK);
        KeyBindingHelper.registerKeyBinding(ABILITY);
        KeyBindingHelper.registerKeyBinding(SWITCH_TOOL);
        KeyBindingHelper.registerKeyBinding(TOGGLE_TANK);
    }

    public static void registerListener() {
        ClientTickEvents.START_CLIENT_TICK.register(evt -> {
            Player player = Minecraft.getInstance().player;
            if(player == null) return;
            //Change Hose Tank Assignment
            if(player.getMainHandItem().getItem() instanceof com.tiviacz.travelersbackpack.item.HoseItem && player.getMainHandItem().has(ModDataComponents.HOSE_MODES)) {
                while(KeybindHandler.TOGGLE_TANK.consumeClick()) {
                    PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(com.tiviacz.travelersbackpack.util.Reference.WEARABLE_SCREEN_ID, com.tiviacz.travelersbackpack.util.Reference.TOGGLE_HOSE_TANK, 0));
                }
            }
            //Change Hose modes
            if(TravelersBackpackConfig.getConfig().client.disableScrollWheel) {
                ItemStack heldItem = player.getMainHandItem();
                if(!ToolSlotItemHandler.isValid(heldItem)) {
                    while(KeybindHandler.SWITCH_TOOL.consumeClick()) {
                        if(!heldItem.isEmpty()) {
                            if(heldItem.getItem() instanceof HoseItem && heldItem.has(ModDataComponents.HOSE_MODES)) {
                                PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, com.tiviacz.travelersbackpack.util.Reference.SWITCH_HOSE_MODE, 1.0D));
                            }
                        }
                    }
                }
            }
            if(ComponentUtils.isWearingBackpack(player)) {
                while(KeybindHandler.OPEN_BACKPACK.consumeClick()) {
                    PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(com.tiviacz.travelersbackpack.util.Reference.NO_SCREEN_ID, com.tiviacz.travelersbackpack.util.Reference.OPEN_SCREEN, 0.0D));
                }
                while(KeybindHandler.ABILITY.consumeClick()) {
                    if(BackpackAbilities.ALLOWED_ABILITIES.contains(ComponentUtils.getWearingBackpack(player).getItem())) {
                        boolean ability = ComponentUtils.getBackpackWrapper(player).isAbilityEnabled();
                        PacketDistributor.sendToServer(new ServerboundAbilitySliderPacket(com.tiviacz.travelersbackpack.util.Reference.WEARABLE_SCREEN_ID, !ability));
                        player.displayClientMessage(Component.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"), true);
                    }
                }
                if(TravelersBackpackConfig.getConfig().client.disableScrollWheel) {
                    ItemStack heldItem = player.getMainHandItem();
                    while(KeybindHandler.SWITCH_TOOL.consumeClick()) {
                        if(!heldItem.isEmpty()) {
                            if(TravelersBackpackConfig.getConfig().client.enableToolCycling) {
                                if(ToolSlotItemHandler.isValid(heldItem)) {
                                    PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, 1.0D));
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public static boolean mouseWheelDetect(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        double scrollDelta = mouseY;
        if(!TravelersBackpackConfig.getConfig().client.disableScrollWheel && scrollDelta != 0.0) {
            Player player = mc.player;
            if(player != null && player.isAlive() && KeybindHandler.SWITCH_TOOL.isDown()) {
                ItemStack heldItem = player.getMainHandItem();
                if(!heldItem.isEmpty()) {
                    if(heldItem.getItem() instanceof HoseItem && heldItem.has(ModDataComponents.HOSE_MODES)) {
                        PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, scrollDelta));
                        return true;
                        // event.setCanceled(true);
                    }
                    if(ComponentUtils.isWearingBackpack(player) && TravelersBackpackConfig.getConfig().client.enableToolCycling) {
                        if(ToolSlotItemHandler.isValid(heldItem)) {
                            PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, scrollDelta));
                            return true;
                            // event.setCanceled(true);
                        }
                    }
                }
            }
        }
        return false;
    }
}