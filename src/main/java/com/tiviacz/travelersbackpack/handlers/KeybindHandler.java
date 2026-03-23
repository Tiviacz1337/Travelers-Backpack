package com.tiviacz.travelersbackpack.handlers;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.client.screens.ToolsScreen;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class KeybindHandler {
    public static final ResourceLocation TRAVELERS_BACKPACK_PHASE = new ResourceLocation("travelersbackpack", "phase");
    private static final String CATEGORY = "key.travelersbackpack.category";
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.travelersbackpack.inventory", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping SORT_BACKPACK = new KeyMapping("key.travelersbackpack.sort", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY = new KeyMapping("key.travelersbackpack.ability", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping SWAP_TOOL = new KeyMapping("key.travelersbackpack.cycle_tool", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping TOGGLE_UPGRADE_0 = new KeyMapping("key.travelersbackpack.toggle_upgrade_0", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping TOGGLE_UPGRADE_1 = new KeyMapping("key.travelersbackpack.toggle_upgrade_1", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping TOGGLE_UPGRADE_2 = new KeyMapping("key.travelersbackpack.toggle_upgrade_2", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping TOGGLE_UPGRADE_3 = new KeyMapping("key.travelersbackpack.toggle_upgrade_3", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final List<KeyMapping> TOGGLE_UPGRADE_KEYS = ImmutableList.of(TOGGLE_UPGRADE_0, TOGGLE_UPGRADE_1, TOGGLE_UPGRADE_2, TOGGLE_UPGRADE_3);

    public static void initKeybinds() {
        KeyBindingHelper.registerKeyBinding(OPEN_BACKPACK);
        KeyBindingHelper.registerKeyBinding(SORT_BACKPACK);
        KeyBindingHelper.registerKeyBinding(ABILITY);
        KeyBindingHelper.registerKeyBinding(SWAP_TOOL);
        KeyBindingHelper.registerKeyBinding(TOGGLE_UPGRADE_0);
        KeyBindingHelper.registerKeyBinding(TOGGLE_UPGRADE_1);
        KeyBindingHelper.registerKeyBinding(TOGGLE_UPGRADE_2);
        KeyBindingHelper.registerKeyBinding(TOGGLE_UPGRADE_3);
    }

    public static void registerListener() {
        ClientTickEvents.START_CLIENT_TICK.addPhaseOrdering(TRAVELERS_BACKPACK_PHASE, Event.DEFAULT_PHASE);
        ClientTickEvents.START_CLIENT_TICK.register(TRAVELERS_BACKPACK_PHASE, evt -> {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if(player == null) return;

            if(ComponentUtils.isWearingBackpack(player)) {
                while(KeybindHandler.OPEN_BACKPACK.consumeClick()) {
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SCREEN);
                }
                while(KeybindHandler.ABILITY.consumeClick()) {
                    if(BackpackAbilities.ALLOWED_ABILITIES.contains(ComponentUtils.getWearingBackpack(player).getItem())) {
                        boolean ability = ComponentUtils.getBackpackWrapperArtificial(player).isAbilityEnabled();
                        ServerboundActionTagPacket.create(ServerboundActionTagPacket.ABILITY_SLIDER, !ability);
                        player.displayClientMessage(Component.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"), true);
                    }
                }
                while(KeybindHandler.SWAP_TOOL.consumeClick()) {
                    if(mc.screen == null && !mc.options.hideGui && mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
                        if(!TravelersBackpackConfig.getConfig().backpackSettings.allowToolSwapping && mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() != ModItems.HOSE) {
                            return;
                        }
                        mc.setScreen(new ToolsScreen());
                    }
                }
                for(int i = 0; i < KeybindHandler.TOGGLE_UPGRADE_KEYS.size(); i++) {
                    KeyMapping key = KeybindHandler.TOGGLE_UPGRADE_KEYS.get(i);
                    while(key.consumeClick()) {
                        ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, i, false, ServerActions.UPGRADE_ENABLED, false); //Upgrade status read on server
                    }
                }
            } else {
                while(KeybindHandler.OPEN_BACKPACK.consumeClick()) {
                    for(int i = 0; i < player.getInventory().items.size(); i++) {
                        ItemStack stack = player.getInventory().items.get(i);
                        if(stack.getItem() instanceof TravelersBackpackItem) {
                            ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_BACKPACK, i, false);
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
                            ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_BACKPACK, slot.getContainerSlot(), true);
                        }
                    }
                }
            });
        }));
    }

    public static boolean isKeyDown(KeyMapping keybind) {
        if(keybind.isUnbound()) {
            return false;
        }
        return switch(keybind.key.getType()) {
            case KEYSYM ->
                    InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keybind.key.getValue());
            case MOUSE ->
                    GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), keybind.key.getValue()) == GLFW.GLFW_PRESS;
            default -> keybind.isDown();
        };
    }
}