package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screens.ToolsScreen;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.commands.BackpackIconCommands;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.network.ServerboundRetrieveBackpackPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.Input;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT)
public class NeoForgeClientEventHandler {
    @SubscribeEvent
    public static void renderBackpackIcon(ScreenEvent.Render.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();

        //Draw + and - for items that can be inserted to backpack
        if(mc.screen instanceof AbstractContainerScreen<?> screen) {
            if(!TravelersBackpackItem.isCreative(player)) {
                var menu = screen.getMenu();
                ItemStack carried = menu.getCarried();
                Slot hoveredSlot = screen.getSlotUnderMouse();
                Optional<TooltipComponent> tooltip = Optional.empty();

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate((float)screen.getGuiLeft(), (float)screen.getGuiTop(), 350.0F);

                for(Slot slot : menu.slots) {
                    ItemStack slotStack = slot.getItem();
                    if(carried.getItem() instanceof TravelersBackpackItem) {
                        tooltip = Optional.of(new BackpackTooltipComponent(carried, true));
                        if(!slotStack.isEmpty() && slot.mayPickup(player) && BackpackSlotItemHandler.isItemValid(slotStack)) {
                            guiGraphics.drawString(mc.font, "-", slot.x + 2, slot.y - 1, ChatFormatting.YELLOW.getColor().intValue()); //16109090
                            if(slot == hoveredSlot) {
                                renderBackpackTooltipOnHover(event, mc, tooltip, (float)screen.getGuiLeft(), (float)screen.getGuiTop(), 350.0F);
                            }
                        }
                    } else if(!carried.isEmpty() && BackpackSlotItemHandler.isItemValid(carried)) {
                        if(slotStack.getItem() instanceof TravelersBackpackItem && slot.allowModification(player)) {
                            tooltip = Optional.of(new BackpackTooltipComponent(slotStack, true));
                            guiGraphics.drawString(mc.font, "+", slot.x + 9, slot.y + 8, ChatFormatting.YELLOW.getColor().intValue()); //16109090
                            if(slot == hoveredSlot) {
                                renderBackpackTooltipOnHover(event, mc, tooltip, (float)screen.getGuiLeft(), (float)screen.getGuiTop(), 350.0F);
                            }
                        }
                    }
                }
                guiGraphics.pose().popPose();
            }
        }

        //Render Backpack Icon if Backpack is equipped in Capability but Integration is enabled to easily retrieve the backpack
        if(mc.screen instanceof InventoryScreen screen && CapabilityUtils.getCapability(player).isPresent()) {
            if(CapabilityUtils.getCapability(player).resolve().get().hasBackpack() && TravelersBackpack.enableIntegration()) {
                ItemStack backpack = CapabilityUtils.getCapability(player).resolve().get().getBackpack();
                guiGraphics.renderItem(backpack, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);

                if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                    AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18, -1000);
                    List<Component> components = new ArrayList<>();
                    components.add(Component.translatable("screen.travelersbackpack.retrieve_backpack"));
                    guiGraphics.renderTooltip(mc.font, components, Optional.of(new BackpackTooltipComponent(backpack)), event.getMouseX(), event.getMouseY());
                }
            }
        }

        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        if(mc.screen instanceof InventoryScreen screen && CapabilityUtils.isWearingBackpack(player)) {
            if(TravelersBackpack.enableIntegration()) return;

            ItemStack backpack = CapabilityUtils.getWearingBackpack(player);
            guiGraphics.renderItem(backpack, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);

            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18, -1000);
                String button = ModClientEventHandler.OPEN_BACKPACK.getKey().getDisplayName().getString();
                List<Component> components = new ArrayList<>();
                components.add(Component.translatable("screen.travelersbackpack.open_inventory", button));
                components.add(Component.translatable("screen.travelersbackpack.unequip_tip"));
                components.add(Component.translatable("screen.travelersbackpack.hide_icon"));
                TooltipFlag.Default tooltipflag$default = mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
                backpack.getItem().appendHoverText(backpack, player.level(), components, tooltipflag$default);
                guiGraphics.renderTooltip(mc.font, components, Optional.of(new BackpackTooltipComponent(backpack)), event.getMouseX(), event.getMouseY());
            }
        }
    }

    private static void renderBackpackTooltipOnHover(ScreenEvent.Render.Post event, Minecraft mc, Optional<TooltipComponent> component, float fx, float fy, float fz) {
        PoseStack poseStack = event.getGuiGraphics().pose();
        poseStack.pushPose();
        poseStack.translate(-fx, -fy, -fz);
        poseStack.translate(0, 0, 100);
        event.getGuiGraphics().renderTooltip(mc.font, List.of(Component.translatable("screen.travelersbackpack.add_to_backpack").withStyle(ChatFormatting.YELLOW)), component, event.getMouseX(), event.getMouseY());
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void hideBackpackIcon(ScreenEvent.MouseButtonPressed.Post event) {
        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        //Render Backpack Icon if Backpack is equipped in Capability but Integration is enabled to easily retrieve the backpack
        if(Minecraft.getInstance().screen instanceof InventoryScreen screen && CapabilityUtils.getCapability(player).isPresent()) {
            if(CapabilityUtils.getCapability(player).resolve().get().hasBackpack() && TravelersBackpack.enableIntegration()) {
                if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                    if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
                        PacketDistributorHelper.sendToServer(new ServerboundRetrieveBackpackPacket(CapabilityUtils.getCapability(player).resolve().get().getBackpack().getItem().getDefaultInstance()));
                    }
                }
            }
        }

        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        if(CapabilityUtils.isWearingBackpack(player) && Minecraft.getInstance().screen instanceof InventoryScreen screen) {
            if(TravelersBackpack.enableIntegration()) return;

            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SCREEN);
                }
                if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_2) {
                    if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                        player.sendSystemMessage(Component.translatable("screen.travelersbackpack.hide_icon_info"));
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
    public static void clientTickEvent(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;

        if(CapabilityUtils.isWearingBackpack(player)) {
            while(ModClientEventHandler.OPEN_BACKPACK.consumeClick()) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SCREEN);
            }
            while(ModClientEventHandler.ABILITY.consumeClick()) {
                if(BackpackAbilities.ALLOWED_ABILITIES.contains(CapabilityUtils.getWearingBackpack(player).getItem())) {
                    boolean ability = CapabilityUtils.getBackpackWrapperArtificial(player).isAbilityEnabled();
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.ABILITY_SLIDER, !ability);
                    player.displayClientMessage(Component.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"), true);
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

    @SubscribeEvent
    public static void updateInputEvent(MovementInputUpdateEvent event) {
        if(Minecraft.getInstance().screen instanceof ToolsScreen) {
            Options settings = Minecraft.getInstance().options;
            Input eInput = event.getInput();
            eInput.up = isKeyDown(settings.keyUp);
            eInput.down = isKeyDown(settings.keyDown);
            eInput.left = isKeyDown(settings.keyLeft);
            eInput.right = isKeyDown(settings.keyRight);

            eInput.forwardImpulse = eInput.up == eInput.down ? 0.0F : (eInput.up ? 1.0F : -1.0F);
            eInput.leftImpulse = eInput.left == eInput.right ? 0.0F : (eInput.left ? 1.0F : -1.0F);
            eInput.jumping = isKeyDown(settings.keyJump);
            eInput.shiftKeyDown = isKeyDown(settings.keyShift);
            if(Minecraft.getInstance().player.isMovingSlowly()) {
                eInput.leftImpulse = (float)((double)eInput.leftImpulse * 0.3D);
                eInput.forwardImpulse = (float)((double)eInput.forwardImpulse * 0.3D);
            }
        }
    }

    public static boolean isKeyDown(KeyMapping keybind) {
        if(keybind.isUnbound()) {
            return false;
        }
        return switch(keybind.getKey().getType()) {
            case KEYSYM ->
                    InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keybind.getKey().getValue());
            case MOUSE ->
                    GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), keybind.getKey().getValue()) == GLFW.GLFW_PRESS;
            default -> keybind.isDown();
        };
    }

    @SubscribeEvent
    public static void registerCommands(final RegisterClientCommandsEvent event) {
        new BackpackIconCommands(event.getDispatcher());
    }
}