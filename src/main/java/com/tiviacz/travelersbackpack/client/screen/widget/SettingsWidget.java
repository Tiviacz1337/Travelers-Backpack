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
import net.minecraft.text.TranslatableText;

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
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
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
                screen.renderTooltip(matrixStack, new TranslatableText("screen.travelersbackpack.settings_back"), mouseX, mouseY);
            }
            else
            {
                screen.renderTooltip(matrixStack, new TranslatableText("screen.travelersbackpack.settings"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        if(isMouseOver(pMouseX, pMouseY) && !this.isWidgetActive)
        {
            this.isWidgetActive = true;
            if(screen.inventory.getSettingsManager().hasCraftingGrid())
            {
                this.screen.craftingWidget.setVisible(false);

                if(this.screen.craftingWidget.isWidgetActive())
                {
                    //Update Crafting Widget, so slots will hide
                    screen.inventory.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)0);

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeByte(screen.inventory.getScreenID()).writeByte(SettingsManager.CRAFTING).writeInt(SettingsManager.SHOW_CRAFTING_GRID).writeByte((byte)0);

                    ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);

                    this.screen.craftingWidget.getCraftingTweaksAddition().onCraftingSlotsHidden();
                }
            }
            this.screen.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild()).forEach(w -> ((WidgetBase) w).setVisible(true));
            this.screen.playUIClickSound();
            return true;
        }
        else if(isMouseOver(pMouseX, pMouseY))
        {
            this.isWidgetActive = false;
            if(screen.inventory.getSettingsManager().hasCraftingGrid())
            {
                this.screen.craftingWidget.setVisible(true);

                if(this.screen.craftingWidget.isWidgetActive())
                {
                    //Update Crafting Widget, so slots will reveal
                    this.screen.inventory.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1);

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeByte(screen.inventory.getScreenID()).writeByte(SettingsManager.CRAFTING).writeInt(SettingsManager.SHOW_CRAFTING_GRID).writeByte((byte)1);

                    ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);

                    this.screen.craftingWidget.getCraftingTweaksAddition().onCraftingSlotsDisplayed();
                }
            }
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