package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.commands.BackpackIconCommands;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.network.ServerboundRetrieveBackpackPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT)
public class NeoForgeClientEventHandler {
    @SubscribeEvent
    public static void renderBackpackIcon(ScreenEvent.Render.Post event) {
        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        //Render Backpack Icon if Backpack is equipped in Capability but Integration is enabled to easily retrieve the backpack
        if(Minecraft.getInstance().screen instanceof InventoryScreen screen && AttachmentUtils.getAttachment(player).isPresent()) {
            if(AttachmentUtils.getAttachment(player).get().hasBackpack() && TravelersBackpack.enableIntegration()) {
                ItemStack backpack = AttachmentUtils.getAttachment(player).get().getBackpack();
                GuiGraphics guiGraphics = event.getGuiGraphics();
                guiGraphics.renderItem(backpack, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);

                if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                    AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18, -1000);
                    List<Component> components = new ArrayList<>();
                    components.add(Component.translatable("screen.travelersbackpack.retrieve_backpack"));
                    guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.of(new BackpackTooltipComponent(backpack)), event.getMouseX(), event.getMouseY());
                }
            }
        }

        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        if(Minecraft.getInstance().screen instanceof InventoryScreen screen && AttachmentUtils.isWearingBackpack(player)) {
            if(TravelersBackpack.enableIntegration()) return;

            ItemStack backpack = AttachmentUtils.getWearingBackpack(player);
            GuiGraphics guiGraphics = event.getGuiGraphics();
            guiGraphics.renderItem(backpack, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);

            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18, -1000);
                String button = ModClientEventHandler.OPEN_BACKPACK.getKey().getDisplayName().getString();
                List<Component> components = new ArrayList<>();
                components.add(Component.translatable("screen.travelersbackpack.open_inventory", button));
                components.add(Component.translatable("screen.travelersbackpack.unequip_tip"));
                components.add(Component.translatable("screen.travelersbackpack.hide_icon"));
                TooltipFlag.Default tooltipflag$default = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
                backpack.getItem().appendHoverText(backpack, Item.TooltipContext.of(player.level()), components, tooltipflag$default);
                guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.of(new BackpackTooltipComponent(backpack)), event.getMouseX(), event.getMouseY());
            }
        }
    }

    @SubscribeEvent
    public static void hideBackpackIcon(ScreenEvent.MouseButtonPressed.Post event) {
        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        //Render Backpack Icon if Backpack is equipped in Capability but Integration is enabled to easily retrieve the backpack
        if(Minecraft.getInstance().screen instanceof InventoryScreen screen && AttachmentUtils.getAttachment(player).isPresent()) {
            if(AttachmentUtils.getAttachment(player).get().hasBackpack() && TravelersBackpack.enableIntegration()) {
                if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                    if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
                        PacketDistributor.sendToServer(new ServerboundRetrieveBackpackPacket(AttachmentUtils.getAttachment(player).get().getBackpack().getItem().getDefaultInstance()));
                    }
                }
            }
        }

        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        if(AttachmentUtils.isWearingBackpack(player) && Minecraft.getInstance().screen instanceof InventoryScreen screen) {
            if(TravelersBackpack.enableIntegration()) return;

            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
                    if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("screen.travelersbackpack.hide_icon_info"));
                    } else {
                        ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SCREEN);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void screenTickEvent(ScreenEvent.KeyPressed.Pre event) {
        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        if(!TravelersBackpackConfig.SERVER.backpackSettings.allowOpeningFromSlot.get()) {
            return;
        }
        if(event.getScreen() instanceof AbstractContainerScreen<?> screen && event.getScreen().getMinecraft().player != null) {
            if(ModClientEventHandler.OPEN_BACKPACK.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
                Slot slot = screen.getSlotUnderMouse();
                if(slot != null && slot.getItem().getItem() instanceof TravelersBackpackItem && slot.allowModification(event.getScreen().getMinecraft().player) && slot.container instanceof Inventory) {
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_BACKPACK, slot.getContainerSlot(), true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void clientTickEvent(final ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        //Change Hose Tank Assignment
        if(player.getMainHandItem().getItem() instanceof HoseItem && player.getMainHandItem().has(ModDataComponents.HOSE_MODES)) {
            while(ModClientEventHandler.TOGGLE_TANK.consumeClick()) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.SWITCH_HOSE_TANK);
            }
        }
        //Change Hose modes
        if(TravelersBackpackConfig.CLIENT.disableScrollWheel.get()) {
            ItemStack heldItem = player.getMainHandItem();
            if(!ToolSlotItemHandler.isValid(heldItem)) {
                while(ModClientEventHandler.SWAP_TOOL.consumeClick()) {
                    if(!heldItem.isEmpty()) {
                        if(heldItem.getItem() instanceof HoseItem && heldItem.has(ModDataComponents.HOSE_MODES)) {
                            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SWITCH_HOSE_MODE, 1.0D);
                        }
                    }
                }
            }
        }
        if(AttachmentUtils.isWearingBackpack(player)) {
            while(ModClientEventHandler.OPEN_BACKPACK.consumeClick()) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SCREEN);
            }
            while(ModClientEventHandler.ABILITY.consumeClick()) {
                if(BackpackAbilities.ALLOWED_ABILITIES.contains(AttachmentUtils.getWearingBackpack(player).getItem())) {
                    boolean ability = AttachmentUtils.getBackpackWrapperArtificial(player).isAbilityEnabled();
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.ABILITY_SLIDER, !ability);
                    player.displayClientMessage(Component.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"), true);
                }
            }
            if(TravelersBackpackConfig.CLIENT.disableScrollWheel.get()) {
                ItemStack heldItem = player.getMainHandItem();
                while(ModClientEventHandler.SWAP_TOOL.consumeClick()) {
                    if(!heldItem.isEmpty()) {
                        if(TravelersBackpackConfig.CLIENT.enableToolCycling.get()) {
                            if(ToolSlotItemHandler.isValid(heldItem)) {
                                ServerboundActionTagPacket.create(ServerboundActionTagPacket.SWAP_TOOL, 1.0D);
                            }
                        }
                    }
                }
            }
        } else {
            while(ModClientEventHandler.OPEN_BACKPACK.consumeClick()) {
                for(int i = 0; i < player.getInventory().items.size(); i++) {
                    ItemStack stack = player.getInventory().items.get(i);
                    if(stack.getItem() instanceof TravelersBackpackItem) {
                        ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_BACKPACK, i, false);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void mouseWheelDetect(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        double scrollDelta = event.getScrollDeltaY();
        if(!TravelersBackpackConfig.CLIENT.disableScrollWheel.get() && scrollDelta != 0.0) {
            Player player = mc.player;
            if(player != null && player.isAlive() && ModClientEventHandler.SWAP_TOOL.isDown()) {
                ItemStack heldItem = player.getMainHandItem();
                if(!heldItem.isEmpty()) {
                    if(heldItem.getItem() instanceof HoseItem && heldItem.has(ModDataComponents.HOSE_MODES)) {
                        ServerboundActionTagPacket.create(ServerboundActionTagPacket.SWITCH_HOSE_MODE, scrollDelta);
                        event.setCanceled(true);
                    }
                    if(AttachmentUtils.isWearingBackpack(player) && TravelersBackpackConfig.CLIENT.enableToolCycling.get()) {
                        if(ToolSlotItemHandler.isValid(heldItem)) {
                            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SWAP_TOOL, scrollDelta);
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerCommands(final RegisterClientCommandsEvent event) {
        new BackpackIconCommands(event.getDispatcher());
    }
}