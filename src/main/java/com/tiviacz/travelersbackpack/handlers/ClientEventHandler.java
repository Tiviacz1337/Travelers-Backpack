package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.container.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.CycleToolPacket;
import com.tiviacz.travelersbackpack.network.ScreenPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{
  /*  @SubscribeEvent
    public static void renderExperienceBar(RenderGameOverlayEvent.Post event)
    {
        if(TravelersBackpackConfig.CLIENT.overlay.enableOverlay.get())
        {
            if(event.getType() != RenderGameOverlayEvent.ElementType.LAYER || event.isCanceled()) return;

            if(CapabilityUtils.isWearingBackpack(Minecraft.getInstance().player))
            {
                OverlayScreen gui = new OverlayScreen();
                gui.renderOverlay(event.getMatrixStack());
            }
        }
    } */

    @SubscribeEvent
    public static void clientTickEvent(final TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END) return;

        LocalPlayer player = Minecraft.getInstance().player;

        if(player != null && CapabilityUtils.isWearingBackpack(player))
        {
            if(ModClientEventHandler.OPEN_INVENTORY.consumeClick())
            {
                TravelersBackpack.NETWORK.sendToServer(new ScreenPacket(Reference.BACKPACK_GUI, Reference.FROM_KEYBIND));
            }

            if(player.getMainHandItem().getItem() instanceof HoseItem && player.getMainHandItem().getTag() != null)
            {
                if(ModClientEventHandler.TOGGLE_TANK.consumeClick())
                {
                    TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(0, Reference.TOGGLE_HOSE_TANK));
                }
            }

            KeyMapping key = ModClientEventHandler.CYCLE_TOOL;

            if(TravelersBackpackConfig.disableScrollWheel && key.consumeClick())
            {
                ItemStack heldItem = player.getMainHandItem();

                if(!heldItem.isEmpty())
                {
                    if(TravelersBackpackConfig.enableToolCycling)
                    {
                        if(ToolSlotItemHandler.isValid(heldItem))
                        {
                            TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(1.0D, Reference.CYCLE_TOOL_ACTION));
                        }
                    }

                    if(heldItem.getItem() instanceof HoseItem)
                    {
                        if(heldItem.getTag() != null)
                        {
                            TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(1.0D, Reference.SWITCH_HOSE_ACTION));
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
        KeyMapping key1 = ModClientEventHandler.CYCLE_TOOL;
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
                                TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(scrollDelta, Reference.CYCLE_TOOL_ACTION));
                                event.setCanceled(true);
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.getTag() != null)
                            {
                                TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(scrollDelta, Reference.SWITCH_HOSE_ACTION));
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
