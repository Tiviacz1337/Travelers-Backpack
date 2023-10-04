package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.network.ServerboundSettingsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public class ToolSlotsWidget extends WidgetBase
{
    public ToolSlotsWidget(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = true;
    }

    @Override
    void renderBg(PoseStack poseStack, Minecraft minecraft, int mouseX, int mouseY)
    {
        if(isVisible())
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TravelersBackpackScreen.SETTINGS_TRAVELERS_BACKPACK);

            if(!screen.container.getSettingsManager().showToolSlots())
            {
                blit(poseStack, x, y, 64, 0, width, height);
            }
            else
            {
                blit(poseStack, x, y, 64, 16, width, height);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isHovered)
        {
            if(screen.container.getSettingsManager().showToolSlots())
            {
                screen.container.getSettingsManager().set(SettingsManager.TOOL_SLOTS, SettingsManager.SHOW_TOOL_SLOTS, (byte)(0));
                TravelersBackpack.NETWORK.sendToServer(new ServerboundSettingsPacket(screen.container.getScreenID(), SettingsManager.TOOL_SLOTS, SettingsManager.SHOW_TOOL_SLOTS, (byte)(0)));
            }
            else
            {
                screen.container.getSettingsManager().set(SettingsManager.TOOL_SLOTS, SettingsManager.SHOW_TOOL_SLOTS, (byte)(1));
                TravelersBackpack.NETWORK.sendToServer(new ServerboundSettingsPacket(screen.container.getScreenID(), SettingsManager.TOOL_SLOTS, SettingsManager.SHOW_TOOL_SLOTS, (byte)(1)));
            }
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
    void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {

    }

    @Override
    public boolean isSettingsChild()
    {
        return false;
    }
}