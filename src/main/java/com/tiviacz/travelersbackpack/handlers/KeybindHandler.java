package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ServerboundAbilitySliderPacket;
import com.tiviacz.travelersbackpack.network.ServerboundOpenBackpackPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
    public static final ResourceLocation TRAVELERS_BACKPACK_PHASE = new ResourceLocation("travelersbackpack", "phase");
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
        ClientTickEvents.START_CLIENT_TICK.addPhaseOrdering(TRAVELERS_BACKPACK_PHASE, Event.DEFAULT_PHASE);
        ClientTickEvents.START_CLIENT_TICK.register(TRAVELERS_BACKPACK_PHASE, evt -> {
            Player player = Minecraft.getInstance().player;
            if(player == null) return;
            //Change Hose Tank Assignment
            if(player.getMainHandItem().getItem() instanceof HoseItem && NbtHelper.has(player.getMainHandItem(), ModDataHelper.HOSE_MODES)) {
                while(KeybindHandler.TOGGLE_TANK.consumeClick()) {
                    PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.TOGGLE_HOSE_TANK, 0));
                }
            }
            //Change Hose modes
            if(TravelersBackpackConfig.getConfig().client.disableScrollWheel) {
                ItemStack heldItem = player.getMainHandItem();
                if(!ToolSlotItemHandler.isValid(heldItem)) {
                    while(KeybindHandler.SWITCH_TOOL.consumeClick()) {
                        if(!heldItem.isEmpty()) {
                            if(heldItem.getItem() instanceof HoseItem && NbtHelper.has(heldItem, ModDataHelper.HOSE_MODES)) {
                                PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, 1.0D));
                            }
                        }
                    }
                }
            }
            if(ComponentUtils.isWearingBackpack(player)) {
                while(KeybindHandler.OPEN_BACKPACK.consumeClick()) {
                    PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.NO_SCREEN_ID, Reference.OPEN_SCREEN, 0.0D));
                }
                while(KeybindHandler.ABILITY.consumeClick()) {
                    if(BackpackAbilities.ALLOWED_ABILITIES.contains(ComponentUtils.getWearingBackpack(player).getItem())) {
                        boolean ability = ComponentUtils.getBackpackWrapper(player).isAbilityEnabled();
                        PacketDistributorHelper.sendToServer(new ServerboundAbilitySliderPacket(Reference.WEARABLE_SCREEN_ID, !ability));
                        player.displayClientMessage(Component.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"), true);
                    }
                }
                if(TravelersBackpackConfig.getConfig().client.disableScrollWheel) {
                    ItemStack heldItem = player.getMainHandItem();
                    while(KeybindHandler.SWITCH_TOOL.consumeClick()) {
                        if(!heldItem.isEmpty()) {
                            if(TravelersBackpackConfig.getConfig().client.enableToolCycling) {
                                if(ToolSlotItemHandler.isValid(heldItem)) {
                                    PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, 1.0D));
                                }
                            }
                        }
                    }
                }
            } else {
                while(KeybindHandler.OPEN_BACKPACK.consumeClick()) {
                    for(int i = 0; i < player.getInventory().items.size(); i++) {
                        ItemStack stack = player.getInventory().items.get(i);
                        if(stack.getItem() instanceof TravelersBackpackItem) {
                            PacketDistributorHelper.sendToServer(new ServerboundOpenBackpackPacket(i));
                            break;
                        }
                    }
                }
            }
        });

        ScreenEvents.BEFORE_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
            ScreenKeyboardEvents.beforeKeyPress(screen).register((gui, keyCode, scanCode, modifiers) -> {
                if(!TravelersBackpackConfig.getConfig().backpackSettings.allowOpeningFromSlot) {
                    return;
                }
                if(screen instanceof AbstractContainerScreen<?> containerScreen && client.player != null) {
                    if(KeybindHandler.OPEN_BACKPACK.matches(keyCode, scanCode)) {
                        Slot slot = containerScreen.hoveredSlot;
                        if(slot != null && slot.getItem().getItem() instanceof TravelersBackpackItem && slot.allowModification(client.player) && slot.container instanceof Inventory) {
                            PacketDistributorHelper.sendToServer(new ServerboundOpenBackpackPacket(slot.getContainerSlot(), true));
                            return true;
                        }
                    }
                }
            });
        }));
    }

    public static boolean mouseWheelDetect(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        double scrollDelta = mouseY;
        if(!TravelersBackpackConfig.getConfig().client.disableScrollWheel && scrollDelta != 0.0) {
            Player player = mc.player;
            if(player != null && player.isAlive() && KeybindHandler.SWITCH_TOOL.isDown()) {
                ItemStack heldItem = player.getMainHandItem();
                if(!heldItem.isEmpty()) {
                    if(heldItem.getItem() instanceof HoseItem && NbtHelper.has(heldItem, ModDataHelper.HOSE_MODES)) {
                        PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, scrollDelta));
                        return true;
                        // event.setCanceled(true);
                    }
                    if(ComponentUtils.isWearingBackpack(player) && TravelersBackpackConfig.getConfig().client.enableToolCycling) {
                        if(ToolSlotItemHandler.isValid(heldItem)) {
                            PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, scrollDelta));
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