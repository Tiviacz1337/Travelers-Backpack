package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class OverlayScreen
{
    public static void renderOverlay(ForgeGui gui, Minecraft mc, GuiGraphics guiGraphics)
    {
        Player player = mc.player;
        Window mainWindow = mc.getWindow();

        int offsetX = TravelersBackpackConfig.offsetX;
        int offsetY = TravelersBackpackConfig.offsetY;
        int scaledWidth = mainWindow.getGuiScaledWidth() - offsetX;
        int scaledHeight = mainWindow.getGuiScaledHeight() - offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackContainer inv = CapabilityUtils.getBackpackInv(player);
        FluidTank rightTank = inv.getRightTank();
        FluidTank leftTank = inv.getLeftTank();

        if(!inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER)).isEmpty())
        {
            drawItemStack(guiGraphics, inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER)), scaledWidth - 30, scaledHeight - 4);
        }

        if(!inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER)).isEmpty())
        {
            drawItemStack(guiGraphics, inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER)), scaledWidth - 30, scaledHeight + 11);
        }

        if(!rightTank.getFluid().isEmpty())
        {
            drawGuiTank(guiGraphics, rightTank, scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!leftTank.getFluid().isEmpty())
        {
            drawGuiTank(guiGraphics, leftTank, scaledWidth - 11, scaledHeight, 21, 8);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        ResourceLocation texture = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");

        if(player.getMainHandItem().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandItem());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                guiGraphics.blit(texture, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                guiGraphics.blit(texture, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                guiGraphics.blit(texture, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                guiGraphics.blit(texture, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                guiGraphics.blit(texture, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                guiGraphics.blit(texture, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            guiGraphics.blit(texture, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            guiGraphics.blit(texture, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
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
}