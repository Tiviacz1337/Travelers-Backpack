package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.RadialToolsOverlay;
import com.tiviacz.travelersbackpack.client.screens.ToolsScreen;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.commands.BackpackIconCommands;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.network.ServerboundRetrieveBackpackPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT)
public class NeoForgeClientEventHandler {
    @SubscribeEvent
    public static void renderStoreItemIcons(ContainerScreenEvent.Render.Foreground event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;

        GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();

        //Draw + and - for items that can be inserted to backpack
        if(mc.screen instanceof AbstractContainerScreen<?> screen) {
            if(!TravelersBackpackItem.isCreative(player)) {
                var menu = screen.getMenu();
                ItemStack carried = menu.getCarried();
                Slot hoveredSlot = screen.getSlotUnderMouse();
                Optional<TooltipComponent> tooltip = Optional.empty();

                guiGraphics.pose().pushMatrix();

                for(Slot slot : menu.slots) {
                    ItemStack slotStack = slot.getItem();
                    if(carried.getItem() instanceof TravelersBackpackItem) {
                        tooltip = Optional.of(new BackpackTooltipComponent(carried, true));
                        if(!slotStack.isEmpty() && slot.mayPickup(player) && BackpackSlotItemHandler.isItemValid(slotStack)) {
                            guiGraphics.text(mc.font, Component.literal("-").withStyle(ChatFormatting.YELLOW), slot.x + 2, slot.y - 1, -1); //16109090
                            if(slot == hoveredSlot) {
                                guiGraphics.setTooltipForNextFrame(mc.font, List.of(Component.translatable("screen.travelersbackpack.add_to_backpack").withStyle(ChatFormatting.YELLOW)), tooltip, event.getMouseX(), event.getMouseY());
                            }
                        }
                    } else if(!carried.isEmpty() && BackpackSlotItemHandler.isItemValid(carried)) {
                        if(slotStack.getItem() instanceof TravelersBackpackItem && slot.allowModification(player)) {
                            tooltip = Optional.of(new BackpackTooltipComponent(slotStack, true));
                            guiGraphics.text(mc.font, Component.literal("+").withStyle(ChatFormatting.YELLOW), slot.x + 9, slot.y + 8, -1); //16109090
                            if(slot == hoveredSlot) {
                                guiGraphics.setTooltipForNextFrame(mc.font, List.of(Component.translatable("screen.travelersbackpack.add_to_backpack").withStyle(ChatFormatting.YELLOW)), tooltip, event.getMouseX(), event.getMouseY());
                            }
                        }
                    }
                }
                guiGraphics.pose().popMatrix();
            }
        }
    }

    @SubscribeEvent
    public static void renderBackpackIcon(ScreenEvent.Render.Background event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;

        GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();

        //Render Backpack Icon if Backpack is equipped in Capability but Integration is enabled to easily retrieve the backpack
        if(mc.screen instanceof InventoryScreen screen && AttachmentUtils.getAttachment(player).isPresent()) {
            if(AttachmentUtils.getAttachment(player).get().hasBackpack() && TravelersBackpack.enableIntegration()) {
                ItemStack backpack = AttachmentUtils.getAttachment(player).get().getBackpack();
                if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                    renderSlotHighlightBack(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);
                }
                guiGraphics.item(backpack, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);

                if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                    List<Component> components = new ArrayList<>();
                    components.add(Component.translatable("screen.travelersbackpack.retrieve_backpack"));
                    guiGraphics.setTooltipForNextFrame(mc.font, components, Optional.of(new BackpackTooltipComponent(backpack)), event.getMouseX(), event.getMouseY());
                    renderSlotHighlightFront(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);
                }
            }
        }

        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        if(mc.screen instanceof InventoryScreen screen && AttachmentUtils.isWearingBackpack(player)) {
            if(TravelersBackpack.enableIntegration()) return;

            ItemStack backpack = AttachmentUtils.getWearingBackpack(player);
            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                renderSlotHighlightBack(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);
            }
            guiGraphics.item(backpack, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);

            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                String button = ModClientEventHandler.OPEN_BACKPACK.getKey().getDisplayName().getString();
                List<Component> components = new ArrayList<>();
                components.add(Component.translatable("screen.travelersbackpack.open_inventory", button));
                components.add(Component.translatable("screen.travelersbackpack.unequip_tip"));
                components.add(Component.translatable("screen.travelersbackpack.hide_icon"));
                TooltipFlag.Default tooltipflag$default = mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
                backpack.getItem().appendHoverText(backpack, Item.TooltipContext.of(player.level()), TooltipDisplay.DEFAULT, components::add, tooltipflag$default);
                guiGraphics.setTooltipForNextFrame(mc.font, components, Optional.of(new BackpackTooltipComponent(backpack)), event.getMouseX(), event.getMouseY());
                renderSlotHighlightFront(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);
            }
        }
    }

    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");

    private static void renderSlotHighlightBack(GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, x - 4, y - 4, 24, 24);
    }

    private static void renderSlotHighlightFront(GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, x - 4, y - 4, 24, 24);
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
                        ClientPacketDistributor.sendToServer(new ServerboundRetrieveBackpackPacket(AttachmentUtils.getAttachment(player).get().getBackpack().getItem().getDefaultInstance()));
                    }
                }
            }
        }

        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        if(AttachmentUtils.isWearingBackpack(player) && Minecraft.getInstance().screen instanceof InventoryScreen screen) {
            if(TravelersBackpack.enableIntegration()) return;

            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SCREEN);
                }
                if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_2) {
                    if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                        Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.translatable("screen.travelersbackpack.hide_icon_info"));
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
            if(ModClientEventHandler.OPEN_BACKPACK.isActiveAndMatches(InputConstants.getKey(event.getKeyEvent()))) {
                Slot slot = screen.getSlotUnderMouse();
                if(slot != null && slot.getItem().getItem() instanceof TravelersBackpackItem && slot.allowModification(event.getScreen().getMinecraft().player) && slot.container instanceof Inventory) {
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_BACKPACK, slot.getContainerSlot(), true);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void clientTickEvent(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;

        if(AttachmentUtils.isWearingBackpack(player)) {
            while(ModClientEventHandler.OPEN_BACKPACK.consumeClick()) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SCREEN);
            }
            while(ModClientEventHandler.ABILITY.consumeClick()) {
                if(BackpackAbilities.ALLOWED_ABILITIES.contains(AttachmentUtils.getWearingBackpack(player).getItem())) {
                    boolean ability = AttachmentUtils.getBackpackWrapperArtificial(player).isAbilityEnabled();
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.ABILITY_SLIDER, !ability);
                    player.sendOverlayMessage(Component.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"));
                }
            }
            while(ModClientEventHandler.SWAP_TOOL.consumeClick()) {
                if(mc.screen == null && !mc.options.hideGui && mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
                    if(!TravelersBackpackConfig.SERVER.backpackSettings.allowToolSwapping.get() && mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() != ModItems.HOSE.get()) {
                        return;
                    }
                    mc.setScreen(new ToolsScreen());
                }
            }
            for(int i = 0; i < ModClientEventHandler.TOGGLE_UPGRADE_KEYS.size(); i++) {
                KeyMapping key = ModClientEventHandler.TOGGLE_UPGRADE_KEYS.get(i);
                while(key.consumeClick()) {
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, i, false, ServerActions.UPGRADE_ENABLED, false); //Upgrade status read on server
                }
            }
        } else {
            while(ModClientEventHandler.OPEN_BACKPACK.consumeClick()) {
                for(int i = 0; i < player.getInventory().getNonEquipmentItems().size(); i++) {
                    ItemStack stack = player.getInventory().getNonEquipmentItems().get(i);
                    if(stack.getItem() instanceof TravelersBackpackItem) {
                        ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_BACKPACK, i, false);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void renderGuiOverlay(RenderGuiLayerEvent.Pre event) {
        if(event.getName() == VanillaGuiLayers.CROSSHAIR) {
            if(Minecraft.getInstance().screen instanceof ToolsScreen && !RadialToolsOverlay.drawCrosshair) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void updateInputEvent(MovementInputUpdateEvent event) {
        if(Minecraft.getInstance().screen instanceof ToolsScreen) {
            Options settings = Minecraft.getInstance().options;
            ClientInput eInput = event.getInput();
            eInput.keyPresses = new Input(isKeyDown(settings.keyUp), isKeyDown(settings.keyDown), isKeyDown(settings.keyLeft), isKeyDown(settings.keyRight), isKeyDown(settings.keyJump), isKeyDown(settings.keyShift), isKeyDown(settings.keySprint));
            eInput.moveVector = new Vec2(eInput.keyPresses.left() == eInput.keyPresses.right() ? 0.0F : (eInput.keyPresses.left() ? 1.0F : -1.0F), eInput.keyPresses.forward() == eInput.keyPresses.backward() ? 0.0F : (eInput.keyPresses.forward() ? 1.0F : -1.0F));
            if(Minecraft.getInstance().player.isMovingSlowly()) {
                eInput.moveVector = new Vec2((float)((double)eInput.moveVector.x * 0.3), (float)((double)eInput.moveVector.y * 0.3));
            }
        }
    }

    public static boolean isKeyDown(KeyMapping keybind) {
        if(keybind.isUnbound()) {
            return false;
        }
        return switch(keybind.getKey().getType()) {
            case KEYSYM -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), keybind.getKey().getValue());
            case MOUSE ->
                    GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().handle(), keybind.getKey().getValue()) == GLFW.GLFW_PRESS;
            default -> keybind.isDown();
        };
    }

    @SubscribeEvent
    public static void registerCommands(final RegisterClientCommandsEvent event) {
        new BackpackIconCommands(event.getDispatcher());
    }
}