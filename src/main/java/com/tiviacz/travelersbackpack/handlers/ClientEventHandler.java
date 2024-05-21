package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ServerboundAbilitySliderPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
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

        if(player != null && AttachmentUtils.isWearingBackpack(player))
        {
            while(ModClientEventsHandler.OPEN_BACKPACK.consumeClick())
            {
                PacketDistributor.SERVER.noArg().send(new ServerboundSpecialActionPacket(Reference.NO_SCREEN_ID, Reference.OPEN_SCREEN, 0.0D));
            }

            while(ModClientEventsHandler.ABILITY.consumeClick())
            {
                if(BackpackAbilities.ALLOWED_ABILITIES.contains(AttachmentUtils.getWearingBackpack(player).getItem()))
                {
                    boolean ability = AttachmentUtils.getBackpackInv(player).getAbilityValue();
                    PacketDistributor.SERVER.noArg().send(new ServerboundAbilitySliderPacket(Reference.WEARABLE_SCREEN_ID, !ability));

                    player.displayClientMessage(Component.translatable(ability ? "screen.travelersbackpack.ability_disabled" : "screen.travelersbackpack.ability_enabled"), true);
                }
            }

            if(player.getMainHandItem().getItem() instanceof HoseItem && player.getMainHandItem().getTag() != null)
            {
                while(ModClientEventsHandler.TOGGLE_TANK.consumeClick())
                {
                    PacketDistributor.SERVER.noArg().send(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.TOGGLE_HOSE_TANK, 0));
                }
            }

            if(TravelersBackpackConfig.disableScrollWheel)
            {
                ItemStack heldItem = player.getMainHandItem();

                while(ModClientEventsHandler.SWAP_TOOL.consumeClick())
                {
                    if(!heldItem.isEmpty())
                    {
                        if(TravelersBackpackConfig.enableToolCycling)
                        {
                            if(ToolSlotItemHandler.isValid(heldItem))
                            {
                                PacketDistributor.SERVER.noArg().send(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, 1.0D));
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.getTag() != null)
                            {
                                PacketDistributor.SERVER.noArg().send(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, 1.0D));
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

        if(!TravelersBackpackConfig.disableScrollWheel && scrollDelta != 0.0)
        {
            LocalPlayer player = mc.player;

            if(player != null && player.isAlive() && ModClientEventsHandler.SWAP_TOOL.isDown())
            {
                ItemStack backpack = AttachmentUtils.getWearingBackpack(player);

                if(backpack != null && backpack.getItem() instanceof TravelersBackpackItem)
                {
                    if(!player.getMainHandItem().isEmpty())
                    {
                        ItemStack heldItem = player.getMainHandItem();

                        if(TravelersBackpackConfig.enableToolCycling)
                        {
                            if(ToolSlotItemHandler.isValid(heldItem))
                            {
                                PacketDistributor.SERVER.noArg().send(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWAP_TOOL, scrollDelta));
                                event.setCanceled(true);
                            }
                        }

                        if(heldItem.getItem() instanceof HoseItem)
                        {
                            if(heldItem.getTag() != null)
                            {
                                PacketDistributor.SERVER.noArg().send(new ServerboundSpecialActionPacket(Reference.WEARABLE_SCREEN_ID, Reference.SWITCH_HOSE_MODE, scrollDelta));
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}