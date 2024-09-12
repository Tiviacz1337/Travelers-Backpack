package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ServerboundAbilitySliderPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT)
public class NeoForgeClientEventHandler
{
    @SubscribeEvent
    public static void renderBackpackIcon(ScreenEvent.Render.Post event)
    {
        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        if(AttachmentUtils.isWearingBackpack(player) && Minecraft.getInstance().screen instanceof InventoryScreen screen)
        {
            if(TravelersBackpack.enableIntegration()) return;

            GuiGraphics guiGraphics = event.getGuiGraphics();
            guiGraphics.renderItem(AttachmentUtils.getWearingBackpack(player), screen.getGuiLeft() + 77, screen.getGuiTop() + 61 - 18);
            //guiGraphics.renderItem(AttachmentUtils.getWearingBackpack(player), screen.getGuiLeft() + 59, screen.getGuiTop() + 7);
            //guiGraphics.renderItem(AttachmentUtils.getWearingBackpack(player), screen.getGuiLeft() - 8 - 9, screen.getGuiTop() + 8 + 18);
            //guiGraphics.blit(TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, screen.getGuiLeft() - 8 - 10, screen.getGuiTop() + 7 + 18, 213, 0, 18, 18);

            //if(event.getMouseX() >= screen.getGuiLeft() - 17 && event.getMouseX() < screen.getGuiLeft() - 1 && event.getMouseY() >= screen.getGuiTop() + 8 + 18 && event.getMouseY() < screen.getGuiTop() + 8 + 18 + 16)
            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 61 - 18 && event.getMouseY() < screen.getGuiTop() + 61 - 18 + 16)
            {
                //AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.getGuiLeft() - 8 - 9, screen.getGuiTop() + 8 + 18, -1000);
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 61 - 18, -1000);
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

        if(AttachmentUtils.isWearingBackpack(player) && Minecraft.getInstance().screen instanceof InventoryScreen screen)
        {
            if(TravelersBackpack.enableIntegration()) return;

            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 61 - 18 && event.getMouseY() < screen.getGuiTop() + 61 - 18 + 16)
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

/*    @SubscribeEvent
    public static void openBackpackFromInventory(ScreenEvent.KeyReleased.Post event)
    {
        //if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        if(Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> containerScreen)
        {
            Slot slot = containerScreen.getSlotUnderMouse();
            if(slot == null) return;

            if(slot.hasItem() && slot.getItem().getItem() instanceof TravelersBackpackItem)
            {
                if(ModClientEventHandler.OPEN_BACKPACK.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode())))
                {
                    PacketDistributor.sendToServer(new ServerboundOpenBackpackPacket(slot.index));
                }
            }
        }
    } */

    @SubscribeEvent
    public static void clientTickEvent(final ClientTickEvent.Post event)
    {
        LocalPlayer player = Minecraft.getInstance().player;

        if(player != null && AttachmentUtils.isWearingBackpack(player))
        {
            while(ModClientEventHandler.OPEN_BACKPACK.consumeClick())
            {
                PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.NO_SCREEN_ID, Reference.OPEN_SCREEN, 0.0D));
            }

            while(ModClientEventHandler.ABILITY.consumeClick())
            {
                if(BackpackAbilities.ALLOWED_ABILITIES.contains(AttachmentUtils.getWearingBackpack(player).getItem()))
                {
                    boolean ability = AttachmentUtils.getBackpackInv(player).getAbilityValue();
                    PacketDistributor.sendToServer(new ServerboundAbilitySliderPacket(Reference.WEARABLE_SCREEN_ID, !ability));

                    player.displayClientMessage(Component.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"), true);
                }
            }

            if(player.getMainHandItem().getItem() instanceof HoseItem && player.getMainHandItem().has(ModDataComponents.HOSE_MODES))
            {
                while(ModClientEventHandler.TOGGLE_TANK.consumeClick())
                {
                    PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.TOGGLE_HOSE_TANK, 0));
                }
            }

            if(TravelersBackpackConfig.CLIENT.disableScrollWheel.get())
            {
                ItemStack heldItem = player.getMainHandItem();

                while(ModClientEventHandler.SWAP_TOOL.consumeClick())
                {
                    if(!heldItem.isEmpty())
                    {
                        if(TravelersBackpackConfig.CLIENT.enableToolCycling.get())
                        {
                            if(ToolSlotItemHandler.isValid(heldItem))
                            {
                                PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, 1.0D));
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.has(ModDataComponents.HOSE_MODES))
                            {
                                PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, 1.0D));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void mouseWheelDetect(InputEvent.MouseScrollingEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        double scrollDelta = event.getScrollDeltaY();

        if(!TravelersBackpackConfig.CLIENT.disableScrollWheel.get() && scrollDelta != 0.0)
        {
            LocalPlayer player = mc.player;

            if(player != null && player.isAlive() && ModClientEventHandler.SWAP_TOOL.isDown())
            {
                ItemStack backpack = AttachmentUtils.getWearingBackpack(player);

                if(backpack != null && backpack.getItem() instanceof TravelersBackpackItem)
                {
                    if(!player.getMainHandItem().isEmpty())
                    {
                        ItemStack heldItem = player.getMainHandItem();

                        if(TravelersBackpackConfig.CLIENT.enableToolCycling.get())
                        {
                            if(ToolSlotItemHandler.isValid(heldItem))
                            {
                                PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, scrollDelta));
                                event.setCanceled(true);
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.has(ModDataComponents.HOSE_MODES))
                            {
                                PacketDistributor.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, scrollDelta));
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}