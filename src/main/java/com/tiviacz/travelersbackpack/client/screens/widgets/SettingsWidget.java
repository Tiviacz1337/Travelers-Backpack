package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SettingsWidget extends WidgetBase
{
    public SettingsWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        showTooltip = true;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, Minecraft minecraft, int mouseX, int mouseY)
    {
        if(!this.isWidgetActive)
        {
            guiGraphics.blit(TravelersBackpackScreen.SETTINGS_TRAVELERS_BACKPACK, x, y, 0, 0, width, height);
        }
        else
        {
            guiGraphics.blit(TravelersBackpackScreen.SETTINGS_TRAVELERS_BACKPACK, x, y, 0, 19, width, height);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        if(isMouseOver(mouseX, mouseY) && showTooltip)
        {
            if(isWidgetActive())
            {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.settings_back"), mouseX, mouseY);
            }
            else
            {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.settings"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        if(isMouseOver(pMouseX, pMouseY) && !this.isWidgetActive)
        {
            this.isWidgetActive = true;
            if(!TravelersBackpackConfig.disableCrafting) this.screen.craftingWidget.setVisible(false);
            this.screen.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild()).forEach(w -> ((WidgetBase) w).setVisible(true));
            this.screen.playUIClickSound();
            return true;
        }
        else if(isMouseOver(pMouseX, pMouseY))
        {
            this.isWidgetActive = false;
            if(!TravelersBackpackConfig.disableCrafting) this.screen.craftingWidget.setVisible(true);
            this.screen.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild()).forEach(w -> ((WidgetBase) w).setVisible(false));
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    @Override
    public void setFocused(boolean p_265728_) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public boolean isSettingsChild()
    {
        return false;
    }
}