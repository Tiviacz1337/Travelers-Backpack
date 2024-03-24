package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundEquipBackpackPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.chat.TranslatableComponent;

public class UnequipButton extends Button
{
    public UnequipButton(TravelersBackpackScreen screen)
    {
        super(screen, 5, 42 + screen.container.getYOffset(), 18, 18);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        if(!screen.container.hasBlockEntity())
        {
            if(CapabilityUtils.isWearingBackpack(screen.getMenu().inventory.player) && screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID && !screen.toolSlotsWidget.isCoveringButton())
            {
                this.drawButton(poseStack, mouseX, mouseY, TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, 57, 19, 38, 19);
            }
        }
    }

    @Override
    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {
        if(TravelersBackpack.enableCurios() && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.toolSlotsWidget.isCoveringButton())
        {
            if(CapabilityUtils.isWearingBackpack(screen.getMenu().inventory.player) && screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID)
            {
                if(this.inButton(mouseX, mouseY))
                {
                    screen.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.unequip_integration"), mouseX, mouseY);
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
                if(CapabilityUtils.isWearingBackpack(screen.getMenu().inventory.player) && screen.container.getScreenID() == Reference.WEARABLE_SCREEN_ID && !screen.isWidgetVisible(3, screen.leftTankSlotWidget) && !screen.toolSlotsWidget.isCoveringButton())
                {
                    if(this.inButton((int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new ServerboundEquipBackpackPacket(false));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}