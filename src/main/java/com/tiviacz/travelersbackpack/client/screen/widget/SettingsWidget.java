package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SettingsWidget extends WidgetBase
{
    public SettingsWidget(TravelersBackpackHandledScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        showTooltip = true;
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, MinecraftClient minecraft, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK);

        if(!this.isWidgetActive)
        {
            drawTexture(matrixStack, x, y, 0, 0, width, height);
        }
        else
        {
            drawTexture(matrixStack, x, y, 0, 19, width, height);
        }
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if(isMouseOver(mouseX, mouseY) && showTooltip)
        {
            if(isWidgetActive())
            {
                screen.renderTooltip(matrixStack, Text.translatable("screen.travelersbackpack.settings_back"), mouseX, mouseY);
            }
            else
            {
                screen.renderTooltip(matrixStack, Text.translatable("screen.travelersbackpack.settings"), mouseX, mouseY);
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
    public void setFocused(boolean focused) {

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