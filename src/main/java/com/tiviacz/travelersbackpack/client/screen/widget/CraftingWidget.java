package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class CraftingWidget extends WidgetBase
{
    public CraftingWidget(TravelersBackpackHandledScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = true;
        this.showTooltip = true;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

        if(zOffset != 0)
        {
            matrixStack.push();
            matrixStack.translate(0, 0, zOffset);
        }

        RenderSystem.enableDepthTest();
        drawBackground(matrixStack, MinecraftClient.getInstance(), mouseX, mouseY);

        if(zOffset != 0)
        {
            matrixStack.pop();
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, MinecraftClient minecraft, int mouseX, int mouseY)
    {
        RenderSystem.setShaderTexture(0, TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK);

        if(isVisible())
        {
            drawTexture(matrixStack, isWidgetActive ? x - 3 : x, y, isWidgetActive ? 29 : 48, isWidgetActive ? 41 : 0, width, height);

            if(isWidgetActive())
            {
                if(in(mouseX, mouseY, x + 14, y + 16, 10, 10))
                {
                    drawTexture(matrixStack, x + 14, y + 16, 11, 83, 10, 10);
                }

                if(screen.inventory.getSettingsManager().isCraftingGridLocked())
                {
                    drawTexture(matrixStack, x + 15, y + 17, 1, 73, 8, 8);
                }

                if(in(mouseX, mouseY, x + 14, y + 28, 10, 10))
                {
                    drawTexture(matrixStack, x + 14, y + 28, 11, 83, 10, 10);
                }

                if(screen.inventory.getSettingsManager().renderOverlay())
                {
                    drawTexture(matrixStack, x + 15, y + 29, 1, 73, 8, 8);
                }
            }
        }
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if(isHovered && showTooltip && isVisible)
        {
            if(!isWidgetActive())
            {
                screen.renderTooltip(matrixStack, new TranslatableText("screen.travelersbackpack.crafting"), mouseX, mouseY);
            }
            else
            {
                if(in(mouseX, mouseY, x, y + 3, 13, 11))
                {
                    screen.renderTooltip(matrixStack, new TranslatableText("screen.travelersbackpack.crafting"), mouseX, mouseY);
                }

                if(in(mouseX, mouseY, x, y + 15, 12, 11))
                {
                    screen.renderTooltip(matrixStack, List.of(new TranslatableText("screen.travelersbackpack.crafting_lock"), new TranslatableText("screen.travelersbackpack.crafting_lock_description")), mouseX, mouseY);
                }

                if(in(mouseX, mouseY, x, y + 27, 12, 11))
                {
                    screen.renderTooltip(matrixStack, List.of(new TranslatableText("screen.travelersbackpack.crafting_overlay"), new TranslatableText("screen.travelersbackpack.crafting_overlay_description")), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public void setWidgetStatus(boolean status)
    {
        super.setWidgetStatus(status);
        //screen.craftingWidget.setTooltipVisible(!status);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isHovered)
        {
            if(this.isWidgetActive)
            {
                if(mouseX >= x && mouseY >= y + 3 && mouseX < x + 13 && mouseY < y + 15)
                {
                    setWidgetStatus(false);
                }

                if(mouseX >= x + 14 && mouseY >= y + 16 && mouseX < x + 24 && mouseY < y + 26)
                {
                    boolean isCraftingLocked = screen.inventory.getSettingsManager().isCraftingGridLocked();
                    screen.inventory.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.LOCK_CRAFTING_GRID, (byte)(isCraftingLocked ? 0 : 1));

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeByte(screen.inventory.getScreenID()).writeByte(SettingsManager.CRAFTING).writeInt(SettingsManager.LOCK_CRAFTING_GRID).writeByte((byte)(isCraftingLocked ? 0 : 1));

                    ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);
                }

                if(mouseX >= x + 14 && mouseY >= y + 28 && mouseX < x + 24 && mouseY < y + 38)
                {
                    boolean renderOverlay = screen.inventory.getSettingsManager().renderOverlay();
                    screen.inventory.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.RENDER_OVERLAY, (byte)(renderOverlay ? 0 : 1));

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeByte(screen.inventory.getScreenID()).writeByte(SettingsManager.CRAFTING).writeInt(SettingsManager.RENDER_OVERLAY).writeByte((byte)(renderOverlay ? 0 : 1));

                    ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);
                }
            }
            else
            {
                setWidgetStatus(true);
            }

            if(this.isWidgetActive)
            {
                this.height = 42;
                this.width = 31;
                this.zOffset = 0;
            }

            if(!this.isWidgetActive)
            {
                this.height = 18;
                this.width = 15;
                this.zOffset = 0;
            }

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

    @Override
    public int[] getWidgetSizeAndPos()
    {
        int[] size = new int[4];
        size[0] = x;
        size[1] = y;
        size[2] = width;
        size[3] = height;
        return size;
    }
}