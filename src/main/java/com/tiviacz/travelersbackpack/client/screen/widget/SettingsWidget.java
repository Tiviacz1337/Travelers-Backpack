package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;

public class SettingsWidget extends WidgetBase
{
    public SettingsWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        showTooltip = true;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY)
    {
        minecraft.getTextureManager().bind(TravelersBackpackScreen.SETTTINGS_TRAVELERS_BACKPACK);

        if(!this.isWidgetActive)
        {
            blit(matrixStack, x, y, 0, 0, width, height);
        }
        else
        {
            blit(matrixStack, x, y, 0, 19, width, height);
        }
    }

    @Override
    public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if(isHovered && showTooltip)
        {
            if(isWidgetActive())
            {
                screen.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.settings_back"), mouseX, mouseY);
            }
            else
            {
                screen.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.settings"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        if(isHovered && !this.isWidgetActive)
        {
            this.isWidgetActive = true;
            this.screen.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild()).forEach(w -> ((WidgetBase) w).setVisible(true));
            this.screen.playUIClickSound();
            return true;
        }
        else if(isHovered)
        {
            this.isWidgetActive = false;
            this.screen.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild()).forEach(w -> ((WidgetBase) w).setVisible(false));
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    @Override
    public boolean isSettingsChild()
    {
        return false;
    }
}