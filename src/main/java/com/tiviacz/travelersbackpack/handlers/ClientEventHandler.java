package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.gui.OverlayScreen;
import com.tiviacz.travelersbackpack.client.gui.WarningScreen;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.container.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.CycleToolPacket;
import com.tiviacz.travelersbackpack.network.ScreenPacket;
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

        if(TravelersBackpackConfig.CLIENT.displayWarning.get() && gui instanceof MainMenuScreen)
        {
            event.setGui(new WarningScreen((MainMenuScreen)gui));
            TravelersBackpackConfig.CLIENT.displayWarning.set(false);
        }
    }

    @SubscribeEvent
    public static void renderExperienceBar(RenderGameOverlayEvent.Post event)
    {
        if(TravelersBackpackConfig.CLIENT.overlay.enableOverlay.get())
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
            if(ModClientEventHandler.OPEN_INVENTORY.isPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new ScreenPacket(Reference.BACKPACK_GUI, Reference.FROM_KEYBIND));
            }

            if(player.getHeldItemMainhand().getItem() instanceof HoseItem && player.getHeldItemMainhand().getTag() != null)
            {
                if(ModClientEventHandler.TOGGLE_TANK.isPressed())
                {
                    TravelersBackpack.NETWORK.sendToServer(new CycleToolPacket(0, Reference.TOGGLE_HOSE_TANK));
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

        if(scrollDelta != 0.0)
        {
            ClientPlayerEntity player = mc.player;

            if(player != null && player.isAlive() && key1.isKeyDown())
            {
                ItemStack backpack = CapabilityUtils.getWearingBackpack(player);

                if(backpack != null && backpack.getItem() instanceof TravelersBackpackItem)
                {
                    if(!player.getHeldItemMainhand().isEmpty())
                    {
                        ItemStack heldItem = player.getHeldItemMainhand();

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
