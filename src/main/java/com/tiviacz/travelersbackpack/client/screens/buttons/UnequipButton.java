package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundEquipBackpackPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class UnequipButton extends Button
{
    public UnequipButton(TravelersBackpackScreen screen)
    {
        super(screen, 5, 42 + screen.container.getYOffset(), 18, 18);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        if(!screen.container.hasBlockEntity())
        {
            if(AttachmentUtils.isWearingBackpack(screen.getMenu().inventory.player) && screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID && !screen.toolSlotsWidget.isCoveringButton())
            {
                this.drawButton(guiGraphics, mouseX, mouseY, TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, 57, 19, 38, 19);
            }
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        if(TravelersBackpack.enableCurios() && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.toolSlotsWidget.isCoveringButton())
        {
            if(AttachmentUtils.isWearingBackpack(screen.getMenu().inventory.player) && screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID)
            {
                if(this.inButton(mouseX, mouseY))
                {
                    guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.unequip_integration"), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(!screen.container.hasBlockEntity())
        {
            if(!TravelersBackpack.enableCurios())
            {
                if(AttachmentUtils.isWearingBackpack(screen.getMenu().inventory.player) && screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.toolSlotsWidget.isCoveringButton())
                {
                    if(this.inButton((int)mouseX, (int)mouseY))
                    {
                        PacketDistributor.SERVER.noArg().send(new ServerboundEquipBackpackPacket(false));
                        //TravelersBackpack.NETWORK.send(new ServerboundEquipBackpackPacket(false), PacketDistributor.SERVER.noArg());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}