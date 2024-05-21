package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventsHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HudOverlay
{
    public static final ResourceLocation OVERLAY = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");
    private static float animationProgress = 0.0F;

    public static void renderOverlay(Minecraft mc, GuiGraphics guiGraphics)
    {
        Player player = mc.player;
        Window mainWindow = mc.getWindow();

        int scaledWidth = mainWindow.getGuiScaledWidth() - TravelersBackpackConfig.offsetX;
        int scaledHeight = mainWindow.getGuiScaledHeight() - TravelersBackpackConfig.offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackContainer inv = AttachmentUtils.getBackpackInv(player);

        KeyMapping key = ModClientEventsHandler.SWAP_TOOL;
        List<ItemStack> tools = getTools(inv.getToolSlotsHandler());

        if(key.isDown() && tools.size() > 2)
        {
            if(animationProgress < 1.0F)
            {
                animationProgress += 0.05F;
            }

            for(int i = 0; i < getTools(inv.getToolSlotsHandler()).size(); i++)
            {
                drawItemStack(guiGraphics, getTools(inv.getToolSlotsHandler()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
            }
        }
        else if(!tools.isEmpty())
        {
            if(animationProgress > 0.0F)
            {
                for(int i = 0; i < getTools(inv.getToolSlotsHandler()).size(); i++)
                {
                    drawItemStack(guiGraphics, getTools(inv.getToolSlotsHandler()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                }

                animationProgress -= 0.05F;
            }
            else
            {
                if(!inv.getToolSlotsHandler().getStackInSlot(0).isEmpty())
                {
                    drawItemStack(guiGraphics, inv.getToolSlotsHandler().getStackInSlot(0), scaledWidth - 30, scaledHeight - 4);
                }

                if(tools.size() > 1)
                {
                    if(!inv.getToolSlotsHandler().getStackInSlot(tools.size() - 1).isEmpty())
                    {
                        drawItemStack(guiGraphics, inv.getToolSlotsHandler().getStackInSlot(tools.size() - 1), scaledWidth - 30, scaledHeight + 11);
                    }
                }
            }
        }

        if(!inv.getRightTank().getFluid().isEmpty())
        {
            drawGuiTank(guiGraphics, inv.getRightTank(), scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!inv.getLeftTank().getFluid().isEmpty())
        {
            drawGuiTank(guiGraphics, inv.getLeftTank(), scaledWidth - 11, scaledHeight, 21, 8);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if(player != null && player.getMainHandItem().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandItem());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                guiGraphics.blit(OVERLAY, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                guiGraphics.blit(OVERLAY, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                guiGraphics.blit(OVERLAY, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                guiGraphics.blit(OVERLAY, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                guiGraphics.blit(OVERLAY, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                guiGraphics.blit(OVERLAY, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            guiGraphics.blit(OVERLAY, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            guiGraphics.blit(OVERLAY, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public static void drawGuiTank(GuiGraphics guiGraphics, FluidTank tank, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(guiGraphics, tank, startX, startY, 0, height, width);
    }

    private static void drawItemStack(GuiGraphics guiGraphics, ItemStack stack, int x, int y)
    {
        guiGraphics.renderFakeItem(stack, x, y);
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
    }

    public static List<ItemStack> getTools(ItemStackHandler inventory)
    {
        List<ItemStack> tools = new ArrayList<>();

        for(int i = 0; i < inventory.getSlots(); i++)
        {
            if(!inventory.getStackInSlot(i).isEmpty())
            {
                tools.add(inventory.getStackInSlot(i));
            }
        }
        Collections.reverse(tools);
        return tools;
    }
}