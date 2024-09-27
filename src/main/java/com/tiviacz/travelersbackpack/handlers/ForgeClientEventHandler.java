package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.network.ServerboundAbilitySliderPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT)
public class ForgeClientEventHandler
{
    @SubscribeEvent
    public static void renderBackpackIcon(ScreenEvent.Render.Post event)
    {
        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        if(CapabilityUtils.isWearingBackpack(player) && Minecraft.getInstance().screen instanceof InventoryScreen screen)
        {
            if(TravelersBackpack.enableCurios()) return;

            GuiGraphics guiGraphics = event.getGuiGraphics();
            guiGraphics.renderItem(CapabilityUtils.getWearingBackpack(player), screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);
            //guiGraphics.renderItem(AttachmentUtils.getWearingBackpack(player), screen.getGuiLeft() + 59, screen.getGuiTop() + 7);
            //guiGraphics.renderItem(AttachmentUtils.getWearingBackpack(player), screen.getGuiLeft() - 8 - 9, screen.getGuiTop() + 8 + 18);
            //guiGraphics.blit(TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, screen.getGuiLeft() - 8 - 10, screen.getGuiTop() + 7 + 18, 213, 0, 18, 18);

            //if(event.getMouseX() >= screen.getGuiLeft() - 17 && event.getMouseX() < screen.getGuiLeft() - 1 && event.getMouseY() >= screen.getGuiTop() + 8 + 18 && event.getMouseY() < screen.getGuiTop() + 8 + 18 + 16)
            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16)
            {
                //AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.getGuiLeft() - 8 - 9, screen.getGuiTop() + 8 + 18, -1000);
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18, -1000);
                String button = ModClientEventHandler.OPEN_BACKPACK.getKey().getDisplayName().getString();
                List<Component> components = Arrays.asList(Component.translatable("screen.travelersbackpack.open_inventory", button), Component.translatable("screen.travelersbackpack.hide_icon"));
                guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), event.getMouseX(), event.getMouseY());
            }
        }
    }

    @SubscribeEvent
    public static void hideBackpackIcon(ScreenEvent.MouseButtonPressed.Post event)
    {
        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        if(CapabilityUtils.isWearingBackpack(player) && Minecraft.getInstance().screen instanceof InventoryScreen screen)
        {
            if(TravelersBackpack.enableCurios()) return;

            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16)
            {
                if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) && event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1)
                {
                    player.sendSystemMessage(Component.translatable("screen.travelersbackpack.hidden_icon_info"));
                    TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.set(false);
                    TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.save();
                }
            }
        }
    }

    @SubscribeEvent
    public static void clientTickEvent(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if(CapabilityUtils.isWearingBackpack(player)) {
            while (ModClientEventHandler.OPEN_BACKPACK.consumeClick()) {
                TravelersBackpack.NETWORK.sendToServer(new ServerboundSpecialActionPacket(Reference.NO_SCREEN_ID, Reference.OPEN_SCREEN, 0.0D));
            }

            while (ModClientEventHandler.ABILITY.consumeClick()) {
                if (BackpackAbilities.ALLOWED_ABILITIES.contains(CapabilityUtils.getWearingBackpack(player).getItem())) {
                    boolean ability = CapabilityUtils.getBackpackInv(player).getAbilityValue();
                    TravelersBackpack.NETWORK.send(PacketDistributor.SERVER.noArg(), new ServerboundAbilitySliderPacket(Reference.WEARABLE_SCREEN_ID, !ability));

                    player.displayClientMessage(Component.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"), true);
                }
            }

            if (player.getMainHandItem().getItem() instanceof HoseItem && player.getMainHandItem().getTag() != null) {
                while (ModClientEventHandler.TOGGLE_TANK.consumeClick()) {
                    TravelersBackpack.NETWORK.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.TOGGLE_HOSE_TANK, 0));
                }
            }

            if (TravelersBackpackConfig.CLIENT.disableScrollWheel.get()) {
                ItemStack heldItem = player.getMainHandItem();

                while (ModClientEventHandler.SWAP_TOOL.consumeClick()) {
                    if (!heldItem.isEmpty()) {
                        if (heldItem.getItem() instanceof HoseItem && heldItem.getTag() != null) {
                            TravelersBackpack.NETWORK.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, 1.0D));
                        }
                        if (TravelersBackpackConfig.enableToolCycling) {
                            if (ToolSlotItemHandler.isValid(heldItem)) {
                                TravelersBackpack.NETWORK.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, 1.0D));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void mouseWheelDetect(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        double scrollDelta = event.getScrollDelta();

        if (!TravelersBackpackConfig.CLIENT.disableScrollWheel.get() && scrollDelta != 0.0) {
            Player player = mc.player;

            if (player != null && player.isAlive() && ModClientEventHandler.SWAP_TOOL.isDown()) {
                if (CapabilityUtils.isWearingBackpack(player)) {
                    ItemStack heldItem = player.getMainHandItem();
                    if(!heldItem.isEmpty())
                    {
                        if(heldItem.getItem() instanceof HoseItem && heldItem.getTag() != null) {
                            TravelersBackpack.NETWORK.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, scrollDelta));
                            event.setCanceled(true);
                        }

                        if (TravelersBackpackConfig.CLIENT.enableToolCycling.get()) {
                            if (ToolSlotItemHandler.isValid(heldItem)) {
                                TravelersBackpack.NETWORK.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, scrollDelta));
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}