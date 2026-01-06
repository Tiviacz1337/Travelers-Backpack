package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.network.ServerboundRetrieveBackpackPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScreenRenderHandler {
    public static void renderAboveContents(AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;

        //Draw + and - for items that can be inserted to backpack
        if(!TravelersBackpackItem.isCreative(player)) {
            var menu = screen.getMenu();
            ItemStack carried = menu.getCarried();
            Slot hoveredSlot = screen.hoveredSlot;
            Optional<TooltipComponent> tooltip = Optional.empty();

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate((float)screen.leftPos, (float)screen.topPos);

            for(Slot slot : menu.slots) {
                ItemStack slotStack = slot.getItem();
                if(carried.getItem() instanceof TravelersBackpackItem) {
                    tooltip = Optional.of(new BackpackTooltipComponent(carried, true));
                    if(!slotStack.isEmpty() && slot.mayPickup(player) && BackpackSlotItemHandler.isItemValid(slotStack)) {
                        guiGraphics.drawString(mc.font, Component.literal("-").withStyle(ChatFormatting.YELLOW), slot.x + 2, slot.y - 1, -1); //16109090
                        if(slot == hoveredSlot) {
                            guiGraphics.setTooltipForNextFrame(mc.font, List.of(Component.translatable("screen.travelersbackpack.add_to_backpack").withStyle(ChatFormatting.YELLOW)), tooltip, mouseX, mouseY);
                        }
                    }
                } else if(!carried.isEmpty() && BackpackSlotItemHandler.isItemValid(carried)) {
                    if(slotStack.getItem() instanceof TravelersBackpackItem && slot.allowModification(player)) {
                        tooltip = Optional.of(new BackpackTooltipComponent(slotStack, true));
                        guiGraphics.drawString(mc.font, Component.literal("+").withStyle(ChatFormatting.YELLOW), slot.x + 9, slot.y + 8, -1); //16109090
                        if(slot == hoveredSlot) {
                            guiGraphics.setTooltipForNextFrame(mc.font, List.of(Component.translatable("screen.travelersbackpack.add_to_backpack").withStyle(ChatFormatting.YELLOW)), tooltip, mouseX, mouseY);
                        }
                    }
                }
            }
            guiGraphics.pose().popMatrix();
        }

        //Render Backpack Icon if Backpack is equipped in Capability but Integration is enabled to easily retrieve the backpack
        if(mc.screen instanceof InventoryScreen) {
            boolean highlighted = mouseX >= screen.leftPos + 77 && mouseX < screen.leftPos + 77 + 16 && mouseY >= screen.topPos + 62 - 18 && mouseY < screen.topPos + 62 - 18 + 16;
            if(ComponentUtils.getComponent(player).isPresent()) {
                if(ComponentUtils.getComponent(player).get().hasBackpack() && TravelersBackpack.enableIntegration()) {
                    ItemStack backpack = ComponentUtils.getComponent(player).get().getBackpack();
                    if(highlighted) {
                        RenderHelper.renderSlotHighlightBack(guiGraphics, screen.leftPos + 77, screen.topPos + 62 - 18);
                    }
                    guiGraphics.renderItem(backpack, screen.leftPos + 77, screen.topPos + 62 - 18);

                    if(highlighted) {
                        List<Component> components = new ArrayList<>();
                        components.add(Component.translatable("screen.travelersbackpack.retrieve_backpack"));
                        guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, components, Optional.of(new BackpackTooltipComponent(backpack)), mouseX, mouseY);
                        RenderHelper.renderSlotHighlightFront(guiGraphics, screen.leftPos + 77, screen.topPos + 62 - 18);
                    }
                }
            }

            if(!TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory) return;

            if(ComponentUtils.isWearingBackpack(player)) {
                if(TravelersBackpack.enableIntegration()) return;

                ItemStack backpack = ComponentUtils.getWearingBackpack(player);
                if(highlighted) {
                    RenderHelper.renderSlotHighlightBack(guiGraphics, screen.leftPos + 77, screen.topPos + 62 - 18);
                }
                guiGraphics.renderItem(backpack, screen.leftPos + 77, screen.topPos + 62 - 18);

                if(highlighted) {
                    String button = KeybindHandler.OPEN_BACKPACK.getTranslatedKeyMessage().getString();
                    List<Component> components = new ArrayList<>();
                    components.add(Component.translatable("screen.travelersbackpack.open_inventory", button));
                    components.add(Component.translatable("screen.travelersbackpack.unequip_tip"));
                    components.add(Component.translatable("screen.travelersbackpack.hide_icon"));
                    TooltipFlag.Default tooltipflag$default = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
                    backpack.getItem().appendHoverText(backpack, Item.TooltipContext.of(player.level()), TooltipDisplay.DEFAULT, components::add, tooltipflag$default);
                    guiGraphics.setTooltipForNextFrame(mc.font, components, Optional.of(new BackpackTooltipComponent(backpack)), mouseX, mouseY);
                    RenderHelper.renderSlotHighlightFront(guiGraphics, screen.leftPos + 77, screen.topPos + 62 - 18);
                }
            }
        }
    }

    public static void registerScreenEvents() {
        ScreenEvents.AFTER_INIT.register(((client, screen2, scaledWidth, scaledHeight) -> {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if(player == null) return;

            if(screen2 instanceof InventoryScreen screen) {
                ScreenMouseEvents.afterMouseClick(screen).register((screen1, mouseButtonEvent, doubleClick) -> {
                    //Render Backpack Icon if Backpack is equipped in Capability but Integration is enabled to easily retrieve the backpack
                    if(Minecraft.getInstance().screen instanceof InventoryScreen && ComponentUtils.getComponent(player).isPresent()) {
                        if(ComponentUtils.getComponent(player).get().hasBackpack() && TravelersBackpack.enableIntegration()) {
                            if(mouseButtonEvent.x() >= screen.leftPos + 77 && mouseButtonEvent.x() < screen.leftPos + 77 + 16 && mouseButtonEvent.y() >= screen.topPos + 62 - 18 && mouseButtonEvent.y() < screen.topPos + 62 - 18 + 16) {
                                if(mouseButtonEvent.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
                                    PacketDistributor.sendToServer(new ServerboundRetrieveBackpackPacket(ComponentUtils.getComponent(player).get().getBackpack().getItem().getDefaultInstance()));
                                    return true;
                                }
                            }
                        }
                    }

                    if(!TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory) return false;

                    if(ComponentUtils.isWearingBackpack(player)) {
                        if(TravelersBackpack.enableIntegration()) return false;

                        if(mouseButtonEvent.x() >= screen.leftPos + 77 && mouseButtonEvent.x() < screen.leftPos + 77 + 16 && mouseButtonEvent.y() >= screen.topPos + 62 - 18 && mouseButtonEvent.y() < screen.topPos + 62 - 18 + 16) {
                            if(mouseButtonEvent.button() == GLFW.GLFW_MOUSE_BUTTON_2) {
                                if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                                    Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("screen.travelersbackpack.hide_icon_info"));
                                } else {
                                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SCREEN);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                });
            }
        }));
    }
}