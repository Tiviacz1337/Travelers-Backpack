package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;

public class ToolSlotsWidget extends WidgetBase
{
    public ToolSlotsWidget(TravelersBackpackHandledScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = screen.inventory.getToolSlotsInventory().size() > 0;
        this.isWidgetActive = screen.inventory.getSettingsManager().showToolSlots();
    }

    @Override
    void drawBackground(MatrixStack matrices, MinecraftClient minecraft, int mouseX, int mouseY)
    {
        if(isVisible())
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK);

            if(!screen.inventory.getSettingsManager().showToolSlots())
            {
                drawTexture(matrices, x, y, 64, 0, width, height);
            }
            else
            {
                drawTexture(matrices, x, y, 64, 16, width, height);
            }
        }
    }

    @Override
    void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY)
    {

    }

    @Override
    public boolean isSettingsChild()
    {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isVisible() && isMouseOver(mouseX, mouseY))
        {
            boolean showToolSlots = screen.inventory.getSettingsManager().showToolSlots();
            screen.inventory.getSettingsManager().set(SettingsManager.TOOL_SLOTS, SettingsManager.SHOW_TOOL_SLOTS, (byte)(showToolSlots ? 0 : 1));

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screen.inventory.getScreenID()).writeByte(SettingsManager.TOOL_SLOTS).writeInt(SettingsManager.SHOW_TOOL_SLOTS).writeByte((byte)(showToolSlots ? 0 : 1));

            ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);

            setWidgetStatus(!showToolSlots);

            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    public boolean isCoveringButton()
    {
        return Math.max(3, this.screen.inventory.getRows()) <= screen.inventory.getToolSlotsInventory().size() && isWidgetActive();
    }

    public boolean isCoveringAbility()
    {
        if(screen.inventory.getRows() <= 4)
        {
            return screen.inventory.getToolSlotsInventory().size() >= 2 && isWidgetActive();
        }
        else
        {
            return screen.inventory.getToolSlotsInventory().size() >= 3 && isWidgetActive();
        }
    }
}