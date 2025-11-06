package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

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