package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.network.ServerboundRetrieveBackpackPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScreenRenderHandler {
    public static void registerScreenEvents() {
        ScreenEvents.AFTER_INIT.register(((client, screen2, scaledWidth, scaledHeight) -> {
            Player player = client.player;
            if(player == null) return;

            //Draw + and - for items that can be inserted to backpack
            if(screen2 instanceof AbstractContainerScreen<?> screen) {
                ScreenEvents.afterRender(screen).register((screen1, guiGraphics, mouseX, mouseY, tickDelta) -> {
                    if(!TravelersBackpackItem.isCreative(player)) {
                        var menu = screen.getMenu();
                        ItemStack carried = menu.getCarried();
                        Slot hoveredSlot = screen.hoveredSlot;
                        Optional<TooltipComponent> tooltip = Optional.empty();

                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate((float)screen.leftPos, (float)screen.topPos, 350.0F);

                        for(Slot slot : menu.slots) {
                            ItemStack slotStack = slot.getItem();
                            if(carried.getItem() instanceof TravelersBackpackItem) {
                                tooltip = Optional.of(new BackpackTooltipComponent(carried, true));
                                if(!slotStack.isEmpty() && slot.mayPickup(player) && BackpackSlotItemHandler.isItemValid(slotStack)) {
                                    guiGraphics.drawString(client.font, "-", slot.x + 2, slot.y - 1, ChatFormatting.YELLOW.getColor().intValue()); //16109090
                                    if(slot == hoveredSlot) {
                                        renderBackpackTooltipOnHover(guiGraphics, mouseX, mouseY, client, tooltip, (float)screen.leftPos, (float)screen.topPos, 350.0F);
                                    }
                                }
                            } else if(!carried.isEmpty() && BackpackSlotItemHandler.isItemValid(carried)) {
                                if(slotStack.getItem() instanceof TravelersBackpackItem && slot.allowModification(player)) {
                                    tooltip = Optional.of(new BackpackTooltipComponent(slotStack, true));
                                    guiGraphics.drawString(client.font, "+", slot.x + 9, slot.y + 8, ChatFormatting.YELLOW.getColor().intValue()); //16109090
                                    if(slot == hoveredSlot) {
                                        renderBackpackTooltipOnHover(guiGraphics, mouseX, mouseY, client, tooltip, (float)screen.leftPos, (float)screen.topPos, 350.0F);
                                    }
                                }
                            }
                        }
                        guiGraphics.pose().popPose();
                    }
                });
            }

            if(screen2 instanceof InventoryScreen screen) {
                ScreenEvents.afterRender(screen).register((screen1, guiGraphics, mouseX, mouseY, tickDelta) -> {
                    //Render Backpack Icon if Backpack is equipped in Capability but Integration is enabled to easily retrieve the backpack
                    if(Minecraft.getInstance().screen instanceof InventoryScreen && ComponentUtils.getComponent(player).isPresent()) {
                        if(ComponentUtils.getComponent(player).get().hasBackpack() && TravelersBackpack.enableIntegration()) {
                            ItemStack backpack = ComponentUtils.getComponent(player).get().getBackpack();
                            guiGraphics.renderItem(backpack, screen.leftPos + 77, screen.topPos + 62 - 18);

                            if(mouseX >= screen.leftPos + 77 && mouseX < screen.leftPos + 77 + 16 && mouseY >= screen.topPos + 62 - 18 && mouseY < screen.topPos + 62 - 18 + 16) {
                                AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.leftPos + 77, screen.topPos + 62 - 18, -1000);
                                List<Component> components = new ArrayList<>();
                                components.add(Component.translatable("screen.travelersbackpack.retrieve_backpack"));
                                guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.of(new BackpackTooltipComponent(backpack)), mouseX, mouseY);
                            }
                        }
                    }

                    if(!TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory) return;

                    if(ComponentUtils.isWearingBackpack(player)) {
                        if(TravelersBackpack.enableIntegration()) return;

                        ItemStack backpack = ComponentUtils.getWearingBackpack(player);
                        guiGraphics.renderItem(backpack, screen.leftPos + 77, screen.topPos + 62 - 18);

                        if(mouseX >= screen.leftPos + 77 && mouseX < screen.leftPos + 77 + 16 && mouseY >= screen.topPos + 62 - 18 && mouseY < screen.topPos + 62 - 18 + 16) {
                            EffectRenderingInventoryScreen.renderSlotHighlight(guiGraphics, screen.leftPos + 77, screen.topPos + 62 - 18, -1000);
                            String button = KeybindHandler.OPEN_BACKPACK.getTranslatedKeyMessage().getString();
                            List<Component> components = new ArrayList<>();
                            components.add(Component.translatable("screen.travelersbackpack.open_inventory", button));
                            components.add(Component.translatable("screen.travelersbackpack.unequip_tip"));
                            components.add(Component.translatable("screen.travelersbackpack.hide_icon"));
                            TooltipFlag.Default tooltipflag$default = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
                            backpack.getItem().appendHoverText(backpack, Item.TooltipContext.of(player.level()), components, tooltipflag$default);
                            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.of(new BackpackTooltipComponent(backpack)), mouseX, mouseY);
                        }
                    }
                });

                ScreenMouseEvents.afterMouseClick(screen).register((screen1, mouseX, mouseY, button) -> {
                    //Render Backpack Icon if Backpack is equipped in Capability but Integration is enabled to easily retrieve the backpack
                    if(Minecraft.getInstance().screen instanceof InventoryScreen && ComponentUtils.getComponent(player).isPresent()) {
                        if(ComponentUtils.getComponent(player).get().hasBackpack() && TravelersBackpack.enableIntegration()) {
                            if(mouseX >= screen.leftPos + 77 && mouseX < screen.leftPos + 77 + 16 && mouseY >= screen.topPos + 62 - 18 && mouseY < screen.topPos + 62 - 18 + 16) {
                                if(button == GLFW.GLFW_MOUSE_BUTTON_1) {
                                    PacketDistributor.sendToServer(new ServerboundRetrieveBackpackPacket(ComponentUtils.getComponent(player).get().getBackpack().getItem().getDefaultInstance()));
                                }
                            }
                        }
                    }

                    if(!TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory) return;

                    if(ComponentUtils.isWearingBackpack(player)) {
                        if(TravelersBackpack.enableIntegration()) return;

                        if(mouseX >= screen.leftPos + 77 && mouseX < screen.leftPos + 77 + 16 && mouseY >= screen.topPos + 62 - 18 && mouseY < screen.topPos + 62 - 18 + 16) {
                            if(button == GLFW.GLFW_MOUSE_BUTTON_2) {
                                if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                                    player.sendSystemMessage(Component.translatable("screen.travelersbackpack.hide_icon_info"));
                                } else {
                                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SCREEN);
                                }
                            }
                        }
                    }
                });
            }
        }));
    }

    private static void renderBackpackTooltipOnHover(GuiGraphics guiGraphics, int mouseX, int mouseY, Minecraft mc, Optional<TooltipComponent> component, float fx, float fy, float fz) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(-fx, -fy, -fz);
        poseStack.translate(0, 0, 100);
        guiGraphics.renderTooltip(mc.font, List.of(Component.translatable("screen.travelersbackpack.add_to_backpack").withStyle(ChatFormatting.YELLOW)), component, mouseX, mouseY);
        poseStack.popPose();
    }
}