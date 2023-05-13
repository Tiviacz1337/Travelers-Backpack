package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class OverlayScreen
{
    public static void renderOverlay(ForgeGui gui, Minecraft mc, PoseStack poseStack)
    {
        Player player = mc.player;
        Window mainWindow = mc.getWindow();

        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //RenderSystem.disableLighting();
        //RenderSystem.enableAlphaTest();
        //RenderSystem.disableBlend();

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
            drawItemStack(mc.getItemRenderer(), poseStack, inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER)), scaledWidth - 30, scaledHeight - 4);
        }

        if(!inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER)).isEmpty())
        {
            drawItemStack(mc.getItemRenderer(), poseStack, inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER)), scaledWidth - 30, scaledHeight + 11);
        }

        if(!rightTank.getFluid().isEmpty())
        {
            drawGuiTank(poseStack, rightTank, scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!leftTank.getFluid().isEmpty())
        {
            drawGuiTank(poseStack, leftTank, scaledWidth - 11, scaledHeight, 21, 8);
        }

        ResourceLocation texture = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        if(player.getMainHandItem().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandItem());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                GuiComponent.blit(poseStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                GuiComponent.blit(poseStack, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                GuiComponent.blit(poseStack, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                GuiComponent.blit(poseStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                GuiComponent.blit(poseStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                GuiComponent.blit(poseStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            GuiComponent.blit(poseStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            GuiComponent.blit(poseStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public static void drawGuiTank(PoseStack matrixStackIn, FluidTank tank, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(matrixStackIn, tank, startX, startY, height, width);
    }

    //I don't undestand rendering itemstack into gui at all, if I'm missing something crucial PR is appreciated
    private static void drawItemStack(ItemRenderer itemRenderer, PoseStack poseStack, ItemStack stack, int x, int y)
    {
        //RenderHelper.enableStandardItemLighting();
        itemRenderer.m_274336_(poseStack, stack, x, y);
        itemRenderer.m_274412_(poseStack, Minecraft.getInstance().font, stack, x, y);
        //RenderHelper.disableStandardItemLighting();
    }
}