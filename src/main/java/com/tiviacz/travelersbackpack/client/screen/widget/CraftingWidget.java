package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.compat.craftingtweaks.ICraftingTweaks;
import com.tiviacz.travelersbackpack.compat.craftingtweaks.TravelersBackpackCraftingGridAddition;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class CraftingWidget extends WidgetBase
{
    private static ICraftingTweaks craftingTweaksAddition = ICraftingTweaks.EMPTY;

    public CraftingWidget(TravelersBackpackHandledScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = screen.inventory.getSettingsManager().hasCraftingGrid();
        this.isWidgetActive = screen.inventory.getSettingsManager().showCraftingGrid();
        this.showTooltip = true;

        if(!screen.inventory.getSettingsManager().hasCraftingGrid())
        {
            this.width = 0;
            this.height = 0;
        }

        craftingTweaksAddition.setScreen(screen);

        if(this.isWidgetActive)
        {
            this.height = 107;
            this.width = 66;

            if(isCraftingTweaksAdditionEnabled())
            {
                this.width = 83;
            }

            craftingTweaksAddition.onCraftingSlotsDisplayed();
        }
        else
        {
            this.height = 18;
            this.width = 15;
        }
        this.zOffset = 0;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        if(zOffset != 0)
        {
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0, 0, zOffset);
        }

        RenderSystem.enableDepthTest();
        drawBackground(drawContext, MinecraftClient.getInstance(), mouseX, mouseY);

        if(zOffset != 0)
        {
            drawContext.getMatrices().pop();
        }
    }

    @Override
    protected void drawBackground(DrawContext drawContext, MinecraftClient minecraft, int mouseX, int mouseY)
    {
        if(isVisible())
        {
            drawContext.drawTexture(TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK, isWidgetActive ? x - 3 : x, y, isWidgetActive ? 29 : 48, isWidgetActive ? 41 : 0, width - (isCraftingTweaksAdditionEnabled() && isWidgetActive() ? 17 : 0), height);

            if(isWidgetActive())
            {
                if(TravelersBackpackConfig.getConfig().client.enableLegacyGui)
                {
                    drawLegacyGridAndResult(drawContext, x + 3, y + 17);
                }

                if(isCraftingTweaksAdditionEnabled())
                {
                    drawContext.drawTexture(TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK, x + 60, y, 96, 41, 20, 107);
                }

                if(screen.inventory.getSettingsManager().shiftClickToBackpack())
                {
                    drawContext.drawTexture(TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK, x + 6, y + 88, 16, 73, 10, 8);
                }
                else
                {
                    drawContext.drawTexture(TravelersBackpackHandledScreen.SETTINGS_TRAVELERS_BACKPACK, x + 7, y + 87, 3, 72, 8, 10);
                }
            }
        }
    }

    @Override
    public void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY)
    {
        if(isWidgetActive() && isVisible())
        {
            drawContext.drawText(screen.getTextRenderer(), Text.translatable("container.crafting"), x + 13, y + 6, 4210752, false);
        }

        if(isMouseOver(mouseX, mouseY) && showTooltip && isVisible)
        {
            if(!isWidgetActive())
            {
                drawContext.drawTooltip(screen.getTextRenderer(), Text.translatable("screen.travelersbackpack.crafting"), mouseX, mouseY);
            }
            else
            {
                if(in(mouseX, mouseY, x, y + 3, 13, 11))
                {
                    drawContext.drawTooltip(screen.getTextRenderer(), Text.translatable("screen.travelersbackpack.crafting"), mouseX, mouseY);
                }

                if(mouseX >= x + 6 && mouseY >= y + 87 && mouseX < x + 16 && mouseY < y + 97)
                {
                    if(screen.inventory.getSettingsManager().shiftClickToBackpack())
                    {
                        drawContext.drawTooltip(screen.getTextRenderer(), Text.translatable("screen.travelersbackpack.crafting_to_backpack"), mouseX, mouseY);
                    }
                    else
                    {
                        drawContext.drawTooltip(screen.getTextRenderer(), Text.translatable("screen.travelersbackpack.crafting_to_player"), mouseX, mouseY);
                    }
                }
            }
        }
    }

    @Override
    public void setWidgetStatus(boolean status)
    {
        boolean showCraftingWidget = screen.inventory.getSettingsManager().showCraftingGrid();
        screen.inventory.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)(showCraftingWidget ? 0 : 1));

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(screen.inventory.getScreenID()).writeByte(SettingsManager.CRAFTING).writeInt(SettingsManager.SHOW_CRAFTING_GRID).writeByte((byte)(showCraftingWidget ? 0 : 1));

        ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);

        super.setWidgetStatus(status);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY)
    {
        if(this.isWidgetActive())
        {
            //Crafting Grid and Result Slot
            if(pMouseX >= x + 3 && pMouseY >= y + 16 && pMouseX < x + 57 && pMouseY < y + 70)
            {
                return false;
            }
            if(pMouseX >= x + 21 && pMouseY >= y + 83 && pMouseX < x + 39 && pMouseY < y + 101)
            {
                return false;
            }
        }
        return pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(this.screen.settingsWidget.isWidgetActive()) return false;

        if(!isVisible()) return false;

        //Crafting Tweaks Buttons
        if(isWidgetActive() && isCraftingTweaksAdditionEnabled())
        {
            if(mouseX >= x + 59 && mouseY >= y + 18 && mouseX < x + 75 && mouseY < y + 34)
            {
                return false;
            }

            if(mouseX >= x + 59 && mouseY >= y + 36 && mouseX < x + 75 && mouseY < y + 52)
            {
                return false;
            }

            if(mouseX >= x + 59 && mouseY >= y + 54 && mouseX < x + 75 && mouseY < y + 70)
            {
                return false;
            }
        }

        if(isMouseOver(mouseX, mouseY))
        {
            if(this.isWidgetActive)
            {
                if(mouseX >= x && mouseY >= y + 3 && mouseX < x + 13 && mouseY < y + 15)
                {
                    setWidgetStatus(false);
                    craftingTweaksAddition.onCraftingSlotsHidden();
                    this.screen.playUIClickSound();
                }

                if(mouseX >= x + 6 && mouseY >= y + 87 && mouseX < x + 16 && mouseY < y + 97)
                {
                    boolean shiftClickToBackpack = screen.inventory.getSettingsManager().shiftClickToBackpack();
                    screen.inventory.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.SHIFT_CLICK_TO_BACKPACK, (byte)(shiftClickToBackpack ? 0 : 1));

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeByte(screen.inventory.getScreenID()).writeByte(SettingsManager.CRAFTING).writeInt(SettingsManager.SHIFT_CLICK_TO_BACKPACK).writeByte((byte)(shiftClickToBackpack ? 0 : 1));

                    ClientPlayNetworking.send(ModNetwork.SETTINGS_ID, buf);

                    this.screen.playUIClickSound();
                }
            }
            else
            {
                setWidgetStatus(true);
                craftingTweaksAddition.onCraftingSlotsDisplayed();
                this.screen.playUIClickSound();
            }

            if(this.isWidgetActive)
            {
                this.height = 107;
                this.width = 66;
                if(isCraftingTweaksAdditionEnabled())
                {
                    this.width = 83;
                }
                this.zOffset = 0;
            }

            if(!this.isWidgetActive)
            {
                this.height = 18;
                this.width = 15;
                this.zOffset = 0;
            }
            return true;
        }
        return false;
    }

    public void drawLegacyGridAndResult(DrawContext context, int x, int y)
    {
        //Grid
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                screen.drawSlotLegacy(context, x + (i * 18), y + (j * 18), 213, 19);
            }
        }

        //Result
        screen.drawSlotLegacy(context, x + 18, y + 66, 213, 19);
    }

    public static void setCraftingTweaksAddition(ICraftingTweaks addition)
    {
        craftingTweaksAddition = addition;
    }

    public ICraftingTweaks getCraftingTweaksAddition()
    {
        return craftingTweaksAddition;
    }

    public boolean isCraftingTweaksAdditionEnabled()
    {
        if(!screen.inventory.getSettingsManager().hasCraftingGrid())
        {
            return false;
        }
        return craftingTweaksAddition instanceof TravelersBackpackCraftingGridAddition;
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