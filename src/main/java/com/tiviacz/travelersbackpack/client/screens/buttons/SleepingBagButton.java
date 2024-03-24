package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundSleepingBagPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.network.PacketDistributor;

public class SleepingBagButton extends Button
{
    public SleepingBagButton(TravelersBackpackScreen screen)
    {
        super(screen, 5, 42 + screen.container.getYOffset(), 18, 18);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        if(screen.container.hasBlockEntity() && !screen.toolSlotsWidget.isCoveringButton())
        {
            this.drawButton(guiGraphics, mouseX, mouseY, TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, 19, 0, 0, 0);

            //Fill the bed icon with the color of the sleeping bag
            guiGraphics.blit(TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK, screen.getGuiLeft() + x + 1, screen.getGuiTop() + y + 1, getBedIconX(screen.container.getSleepingBagColor()), getBedIconY(screen.container.getSleepingBagColor()), 16, 16);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(screen.container.hasBlockEntity() && !screen.toolSlotsWidget.isCoveringButton())
        {
            if(this.inButton((int) mouseX, (int) mouseY) && !screen.isWidgetVisible(3, screen.leftTankSlotWidget))
            {
                TravelersBackpack.NETWORK.send(new ServerboundSleepingBagPacket(screen.container.getPosition()), PacketDistributor.SERVER.noArg());
                return true;
            }
        }
        return false;
    }

    public int getBedIconX(int colorId)
    {
        return 1 + (colorId <= 7 ? 0 : 19);
    }

    public int getBedIconY(int colorId)
    {
        return 19 + ((colorId % 8) * 17);
    }
}