package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.network.ServerboundAbilitySliderPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
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
        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        if(Minecraft.getInstance().screen instanceof InventoryScreen screen && CapabilityUtils.isWearingBackpack(player)) {
            if(TravelersBackpack.enableIntegration()) return;

            ItemStack backpack = CapabilityUtils.getWearingBackpack(player);
            GuiGraphics guiGraphics = event.getGuiGraphics();
            guiGraphics.renderItem(backpack, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18);

            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, screen.getGuiLeft() + 77, screen.getGuiTop() + 62 - 18, -1000);
                String button = ModClientEventHandler.OPEN_BACKPACK.getKey().getDisplayName().getString();
                List<Component> components = new ArrayList<>();
                components.add(Component.translatable("screen.travelersbackpack.open_inventory", button));
                components.add(Component.translatable("screen.travelersbackpack.hide_icon"));
                TooltipFlag.Default tooltipflag$default = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
                backpack.getItem().appendHoverText(backpack, player.level(), components, tooltipflag$default);
                guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.of(new BackpackTooltipComponent(backpack)), event.getMouseX(), event.getMouseY());
            }
        }
    }

    @SubscribeEvent
    public static void hideBackpackIcon(ScreenEvent.MouseButtonPressed.Post event) {
        if(!TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.get()) return;

        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        if(CapabilityUtils.isWearingBackpack(player) && Minecraft.getInstance().screen instanceof InventoryScreen screen) {
            if(TravelersBackpack.enableIntegration()) return;

            if(event.getMouseX() >= screen.getGuiLeft() + 77 && event.getMouseX() < screen.getGuiLeft() + 77 + 16 && event.getMouseY() >= screen.getGuiTop() + 62 - 18 && event.getMouseY() < screen.getGuiTop() + 62 - 18 + 16) {
                if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
                    if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("screen.travelersbackpack.hidden_icon_info"));
                        TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.set(false);
                        TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.save();
                    } else {
                        PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.NO_SCREEN_ID, Reference.OPEN_SCREEN, 0.0D));
                    }
                }
            }
        }
    }

   /* @SubscribeEvent
    public static void screenTickEvent(ScreenEvent.KeyPressed.Pre event) {
        if(event.getScreen() instanceof InventoryScreen screen && event.getScreen().getMinecraft().player != null) {
            if(ModClientEventHandler.OPEN_BACKPACK.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
                Slot slot = screen.getSlotUnderMouse();
                if(slot != null && slot.getItem().getItem() instanceof TravelersBackpackItem && slot.allowModification(event.getScreen().getMinecraft().player) && slot.container instanceof Inventory) {
                    //slot.getContainerSlot()
                    PacketDistributor.sendToServer(new ServerboundOpenBackpackPacket(screen.getSlotUnderMouse().index));
                }
            }
        }
    } */

    @SubscribeEvent
    public static void clientTickEvent(final TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.START) return;

        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        //Change Hose Tank Assignment
        if(player.getMainHandItem().getItem() instanceof HoseItem && NbtHelper.has(player.getMainHandItem(), ModDataHelper.HOSE_MODES)) { //player.getMainHandItem().has(ModDataComponents.HOSE_MODES.get())) {
            while(ModClientEventHandler.TOGGLE_TANK.consumeClick()) {
                PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.TOGGLE_HOSE_TANK, 0));
            }
        }
        //Change Hose modes
        if(TravelersBackpackConfig.CLIENT.disableScrollWheel.get()) {
            ItemStack heldItem = player.getMainHandItem();
            if(!ToolSlotItemHandler.isValid(heldItem)) {
                while(ModClientEventHandler.SWAP_TOOL.consumeClick()) {
                    if(!heldItem.isEmpty()) {
                        if(heldItem.getItem() instanceof HoseItem && NbtHelper.has(heldItem, ModDataHelper.HOSE_MODES)) { //heldItem.has(ModDataComponents.HOSE_MODES.get())) {
                            PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, 1.0D));
                        }
                    }
                }
            }
        }
        if(CapabilityUtils.isWearingBackpack(player)) {
            while(ModClientEventHandler.OPEN_BACKPACK.consumeClick()) {
                PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.NO_SCREEN_ID, Reference.OPEN_SCREEN, 0.0D));
            }
            while(ModClientEventHandler.ABILITY.consumeClick()) {
                if(BackpackAbilities.ALLOWED_ABILITIES.contains(CapabilityUtils.getWearingBackpack(player).getItem())) {
                    boolean ability = CapabilityUtils.getBackpackWrapper(player).isAbilityEnabled();
                    PacketDistributorHelper.sendToServer(new ServerboundAbilitySliderPacket(Reference.WEARABLE_SCREEN_ID, !ability));
                    player.displayClientMessage(Component.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"), true);
                }
            }
            if(TravelersBackpackConfig.CLIENT.disableScrollWheel.get()) {
                ItemStack heldItem = player.getMainHandItem();
                while(ModClientEventHandler.SWAP_TOOL.consumeClick()) {
                    if(!heldItem.isEmpty()) {
                        if(TravelersBackpackConfig.CLIENT.enableToolCycling.get()) {
                            if(ToolSlotItemHandler.isValid(heldItem)) {
                                PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, 1.0D));
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
        if(!TravelersBackpackConfig.CLIENT.disableScrollWheel.get() && scrollDelta != 0.0) {
            Player player = mc.player;
            if(player != null && player.isAlive() && ModClientEventHandler.SWAP_TOOL.isDown()) {
                ItemStack heldItem = player.getMainHandItem();
                if(!heldItem.isEmpty()) {
                    if(heldItem.getItem() instanceof HoseItem && NbtHelper.has(heldItem, ModDataHelper.HOSE_MODES)) { //heldItem.has(ModDataComponents.HOSE_MODES.get())) {
                        PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, scrollDelta));
                        event.setCanceled(true);
                    }
                    if(CapabilityUtils.isWearingBackpack(player) && TravelersBackpackConfig.CLIENT.enableToolCycling.get()) {
                        if(ToolSlotItemHandler.isValid(heldItem)) {
                            PacketDistributorHelper.sendToServer(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, scrollDelta));
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }
}