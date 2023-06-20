package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;

public class SettingsWidget extends WidgetBase
{
    public SettingsWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        showTooltip = true;
    }

    @Override
    protected void renderBg(PoseStack poseStack, Minecraft minecraft, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TravelersBackpackScreen.SETTINGS_TRAVELERS_BACKPACK);

        if(!this.isWidgetActive)
        {
            blit(poseStack, x, y, 0, 0, width, height);
        }
        else
        {
            blit(poseStack, x, y, 0, 19, width, height);
        }
    }

    @Override
    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {
        if(isHovered && showTooltip)
        {
            if(isWidgetActive())
            {
                screen.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.settings_back"), mouseX, mouseY);
            }
            else
            {
                screen.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.settings"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        if(isHovered && !this.isWidgetActive)
        {
            this.isWidgetActive = true;
            this.screen.craftingWidget.setVisible(false);
            this.screen.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild()).forEach(w -> ((WidgetBase) w).setVisible(true));
            this.screen.playUIClickSound();
            return true;
        }
        else if(isHovered)
        {
            this.isWidgetActive = false;
            this.screen.craftingWidget.setVisible(true);
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