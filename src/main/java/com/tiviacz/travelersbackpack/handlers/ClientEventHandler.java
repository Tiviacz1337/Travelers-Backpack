package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{
    @SubscribeEvent
    public static void clientTickEvent(final TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END) return;

        LocalPlayer player = Minecraft.getInstance().player;

        if(player != null && CapabilityUtils.isWearingBackpack(player))
        {
            if(ModClientEventsHandler.OPEN_INVENTORY.consumeClick())
            {
                TravelersBackpack.NETWORK.send(PacketDistributor.SERVER.noArg(), new ServerboundSpecialActionPacket(Reference.NO_SCREEN_ID, Reference.OPEN_SCREEN, 0.0D));
            }

            if(player.getMainHandItem().getItem() instanceof HoseItem && player.getMainHandItem().getTag() != null)
            {
                if(ModClientEventsHandler.TOGGLE_TANK.consumeClick())
                {
                    TravelersBackpack.NETWORK.send(PacketDistributor.SERVER.noArg(), new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.TOGGLE_HOSE_TANK, 0));
                }
            }

            KeyMapping key = ModClientEventsHandler.CYCLE_TOOL;

            if(TravelersBackpackConfig.disableScrollWheel && key.consumeClick())
            {
                ItemStack heldItem = player.getMainHandItem();

                if(!heldItem.isEmpty())
                {
                    if(TravelersBackpackConfig.enableToolCycling)
                    {
                        if(ToolSlotItemHandler.isValid(heldItem))
                        {
                            TravelersBackpack.NETWORK.send(PacketDistributor.SERVER.noArg(), new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, 1.0D));
                        }
                    }

                    if(heldItem.getItem() instanceof HoseItem)
                    {
                        if(heldItem.getTag() != null)
                        {
                            TravelersBackpack.NETWORK.send(PacketDistributor.SERVER.noArg(), new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, 1.0D));
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
        KeyMapping key1 = ModClientEventsHandler.CYCLE_TOOL;
        double scrollDelta = event.getScrollDeltaY();

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

                        if(TravelersBackpackConfig.enableToolCycling)
                        {
                            if(ToolSlotItemHandler.isValid(heldItem))
                            {
                                TravelersBackpack.NETWORK.send(PacketDistributor.SERVER.noArg(), new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, scrollDelta));
                                event.setCanceled(true);
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.getTag() != null)
                            {
                                TravelersBackpack.NETWORK.send(PacketDistributor.SERVER.noArg(), new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, scrollDelta));
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}