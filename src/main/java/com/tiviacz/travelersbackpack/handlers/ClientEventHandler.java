package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screen.OverlayScreen;
import com.tiviacz.travelersbackpack.client.screen.WarningScreen;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.container.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.SSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{
    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event)
    {
        Screen gui = event.getGui();

        if(TravelersBackpackConfig.displayWarning && gui instanceof MainMenuScreen)
        {
            event.setGui(new WarningScreen((MainMenuScreen)gui));
            TravelersBackpackConfig.CLIENT.displayWarning.set(false);
        }
    }

    @SubscribeEvent
    public static void renderExperienceBar(RenderGameOverlayEvent.Post event)
    {
        if(TravelersBackpackConfig.enableOverlay)
        {
            if(event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR || event.isCanceled()) return;

            if(CapabilityUtils.isWearingBackpack(Minecraft.getInstance().player))
            {
                OverlayScreen gui = new OverlayScreen();
                gui.renderOverlay(event.getMatrixStack());
            }
        }
    }

    @SubscribeEvent
    public static void clientTickEvent(final TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END) return;

        ClientPlayerEntity player = Minecraft.getInstance().player;

        if(player != null && CapabilityUtils.isWearingBackpack(player))
        {
            if(ModClientEventHandler.OPEN_INVENTORY.consumeClick())
            {
                TravelersBackpack.NETWORK.sendToServer(new SSpecialActionPacket(Reference.NO_SCREEN_ID, Reference.OPEN_SCREEN, 0.0D));
            }

            if(player.getMainHandItem().getItem() instanceof HoseItem && player.getMainHandItem().getTag() != null)
            {
                if(ModClientEventHandler.TOGGLE_TANK.consumeClick())
                {
                    TravelersBackpack.NETWORK.sendToServer(new SSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.TOGGLE_HOSE_TANK, 0));
                }
            }

            KeyBinding key = ModClientEventHandler.CYCLE_TOOL;

            if(TravelersBackpackConfig.disableScrollWheel && key.consumeClick())
            {
                ItemStack heldItem = player.getMainHandItem();

                if(!heldItem.isEmpty())
                {
                    if(TravelersBackpackConfig.enableToolCycling)
                    {
                        if(ToolSlotItemHandler.isValid(heldItem))
                        {
                            TravelersBackpack.NETWORK.sendToServer(new SSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, 1.0D));
                        }
                    }

                    if(heldItem.getItem() instanceof HoseItem)
                    {
                        if(heldItem.getTag() != null)
                        {
                            TravelersBackpack.NETWORK.sendToServer(new SSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, 1.0D));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void mouseWheelDetect(InputEvent.MouseScrollEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        KeyBinding key1 = ModClientEventHandler.CYCLE_TOOL;
        double scrollDelta = event.getScrollDelta();

        if(!TravelersBackpackConfig.disableScrollWheel && scrollDelta != 0.0)
        {
            ClientPlayerEntity player = mc.player;

            if(player != null && player.isAlive() && key1.isDown())
            {
                ItemStack backpack = CapabilityUtils.getWearingBackpack(player);

                if(backpack != null && backpack.getItem() instanceof TravelersBackpackItem)
                {
                    if(!player.getMainHandItem().isEmpty())
                    {
                        ItemStack heldItem = player.getMainHandItem();

                        if(TravelersBackpackConfig.enableToolCycling)
                        {
                            if(ToolSlotItemHandler.isValid(heldItem))
                            {
                                TravelersBackpack.NETWORK.sendToServer(new SSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, scrollDelta));
                                event.setCanceled(true);
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.getTag() != null)
                            {
                                TravelersBackpack.NETWORK.sendToServer(new SSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, scrollDelta));
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}