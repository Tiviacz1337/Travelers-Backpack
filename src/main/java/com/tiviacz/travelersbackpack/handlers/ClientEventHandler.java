package com.tiviacz.travelersbackpack.handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.gui.OverlayScreen;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.container.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.SpecialActionPacket;
import com.tiviacz.travelersbackpack.network.ScreenPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{

    public static final KeyMapping OPEN_INVENTORY = new KeyMapping(Reference.INVENTORY, GLFW.GLFW_KEY_B, Reference.CATEGORY);
    public static final KeyMapping TOGGLE_TANK = new KeyMapping(Reference.TOGGLE_TANK, GLFW.GLFW_KEY_N, Reference.CATEGORY);
    public static final KeyMapping CYCLE_TOOL = new KeyMapping(Reference.CYCLE_TOOL, GLFW.GLFW_KEY_Z, Reference.CATEGORY);

    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent evt)
    {
        evt.register(OPEN_INVENTORY);
        evt.register(TOGGLE_TANK);
        evt.register(CYCLE_TOOL);
    }

    @SubscribeEvent
    public static void registerKeys(final RegisterGuiOverlaysEvent evt)
    {
        evt.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "Traveler's Backpack HUD", (gui, poseStack, partialTick, width, height) -> {
            Minecraft mc = Minecraft.getInstance();

            if(!mc.options.hideGui && CapabilityUtils.isWearingBackpack(mc.player) && mc.gameMode.getPlayerMode() != GameType.SPECTATOR)
            {
                OverlayScreen.renderOverlay(gui, mc, poseStack);
            }
        });
    }

    @SubscribeEvent
    public static void clientTickEvent(final TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END) return;

        LocalPlayer player = Minecraft.getInstance().player;

        if(player != null && CapabilityUtils.isWearingBackpack(player))
        {
            if(OPEN_INVENTORY.consumeClick())
            {
                TravelersBackpack.NETWORK.sendToServer(new ScreenPacket(Reference.BACKPACK_GUI, Reference.FROM_KEYBIND));
            }

            if(player.getMainHandItem().getItem() instanceof HoseItem && player.getMainHandItem().getTag() != null)
            {
                if(TOGGLE_TANK.consumeClick())
                {
                    TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(0, Reference.TOGGLE_HOSE_TANK));
                }
            }

            KeyMapping key = CYCLE_TOOL;

            if(TravelersBackpackConfig.disableScrollWheel && key.consumeClick())
            {
                ItemStack heldItem = player.getMainHandItem();

                if(!heldItem.isEmpty())
                {
                    if(TravelersBackpackConfig.enableToolCycling)
                    {
                        if(ToolSlotItemHandler.isValid(heldItem))
                        {
                            TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(1.0D, Reference.SWAP_TOOL));
                        }
                    }

                    if(heldItem.getItem() instanceof HoseItem)
                    {
                        if(heldItem.getTag() != null)
                        {
                            TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(1.0D, Reference.SWITCH_HOSE_MODE));
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
        KeyMapping key1 = CYCLE_TOOL;
        double scrollDelta = event.getScrollDelta();

        if(!TravelersBackpackConfig.disableScrollWheel && scrollDelta != 0.0)
        {
            LocalPlayer player = mc.player;

            if(player != null && player.isAlive() && key1.isDown())
            {
                ItemStack backpack = CapabilityUtils.getWearingBackpack(player);

                if(backpack != null && backpack.getItem() instanceof TravelersBackpackItem)
                {
                    if(!player.getMainHandItem().isEmpty())
                    {
                        ItemStack heldItem = player.getMainHandItem();

                        if(TravelersBackpackConfig.CLIENT.enableToolCycling.get())
                        {
                            if(ToolSlotItemHandler.isValid(heldItem))
                            {
                                TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(scrollDelta, Reference.SWAP_TOOL));
                                event.setCanceled(true);
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.getTag() != null)
                            {
                                TravelersBackpack.NETWORK.sendToServer(new SpecialActionPacket(scrollDelta, Reference.SWITCH_HOSE_MODE));
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
