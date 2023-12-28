package com.tiviacz.travelersbackpack.client.screen.widget;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.PacketByteBuf;

public class ToolSlotsWidget extends WidgetBase
{
    public ToolSlotsWidget(TravelersBackpackHandledScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = true;
    }

    @Override
    void drawBackground(DrawContext context, MinecraftClient minecraft, int mouseX, int mouseY)
    {
        if(isVisible())
        {
            if(!screen.inventory.getSettingsManager().showToolSlots())
            {
                context.drawTexture(TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK, x, y, 64, 0, width, height);
            }
            else
            {
                context.drawTexture(TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK, x, y, 64, 16, width, height);
            }
        }
    }

    @Override
    void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY)
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
        if(isMouseOver(mouseX, mouseY))
        {
            boolean showToolSlots = screen.inventory.getSettingsManager().showToolSlots();
            screen.inventory.getSettingsManager().set(SettingsManager.TOOL_SLOTS, SettingsManager.SHOW_TOOL_SLOTS, (byte)(showToolSlots ? 0 : 1));

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(screen.inventory.getScreenID()).writeByte(SettingsManager.TOOL_SLOTS).writeInt(SettingsManager.SHOW_TOOL_SLOTS).writeByte((byte)(showToolSlots ? 0 : 1));

            ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);

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
}