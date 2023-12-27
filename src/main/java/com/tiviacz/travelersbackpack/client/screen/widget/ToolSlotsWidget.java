package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.network.SSettingsPacket;
import net.minecraft.client.Minecraft;

public class ToolSlotsWidget extends WidgetBase
{
    public ToolSlotsWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = true;
    }

    @Override
    void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY)
    {
        if(isVisible())
        {
            minecraft.getTextureManager().bind(TravelersBackpackScreen.SETTINGS_TRAVELERS_BACKPACK);

            if(!screen.inv.getSettingsManager().showToolSlots())
            {
                blit(matrixStack, x, y, 64, 0, width, height);
            }
            else
            {
                blit(matrixStack, x, y, 64, 16, width, height);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            if(screen.inv.getSettingsManager().showToolSlots())
            {
                screen.inv.getSettingsManager().set(SettingsManager.TOOL_SLOTS, SettingsManager.SHOW_TOOL_SLOTS, (byte)(0));
                TravelersBackpack.NETWORK.sendToServer(new SSettingsPacket(screen.inv.getScreenID(), SettingsManager.TOOL_SLOTS, SettingsManager.SHOW_TOOL_SLOTS, (byte)(0)));
            }
            else
            {
                screen.inv.getSettingsManager().set(SettingsManager.TOOL_SLOTS, SettingsManager.SHOW_TOOL_SLOTS, (byte)(1));
                TravelersBackpack.NETWORK.sendToServer(new SSettingsPacket(screen.inv.getScreenID(), SettingsManager.TOOL_SLOTS, SettingsManager.SHOW_TOOL_SLOTS, (byte)(1)));
            }
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    @Override
    void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {

    }

    @Override
    public boolean isSettingsChild()
    {
        return false;
    }
}